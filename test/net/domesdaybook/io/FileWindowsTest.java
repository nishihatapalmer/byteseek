/*
 * Copyright Matt Palmer 2011, All rights reserved.
 *
 */

package net.domesdaybook.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.domesdaybook.io.FileWIndows;
import net.domesdaybook.io.cache.AllWindowsCache;
import net.domesdaybook.io.cache.NoCache;

/**
 *
 * @author matt
 */
public class FileWindowsTest {

	private final static Random	rand	= new Random();

	/**
	 * 
	 */
	public FileWindowsTest() {
	}

	/**
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		final long seed = System.currentTimeMillis();
		// final long seed = ?
		rand.setSeed(seed);
		System.out.println("Seeding random number generator with: " + Long.toString(seed));
		System.out.println("To repeat these exact tests, set the seed to the value above.");
	}

	/**
	 * Test of length method, of class FileWIndows.  Also tests that the total length
	 * is correct when the file reader uses different window sizes and cache capacities.
	 * 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testLength() throws FileNotFoundException {
		FileWIndows reader = new FileWIndows(getFile("/TestASCII.txt"));
		assertEquals("length ASCII", 112280, reader.length());

		// no matter how the file reader sets its window sizes or cache strategies,
		// it must make no difference to the total length of the data read.
		FileReaderIterator iterator = new FileReaderIterator("/TestASCII.txt");
		while (iterator.hasNext()) {
			FileWIndows aReader = iterator.next();
			long totalLength = 0;
			for (Window window : aReader) {
				totalLength += window.length();
			}
			assertEquals("sum of window lengths ASCII", 112280, totalLength);
		}

		reader = new FileWIndows(getFile("/TestASCII.zip"));
		assertEquals("length ZIP", 45846, reader.length());

		iterator = new FileReaderIterator("/TestASCII.zip");
		while (iterator.hasNext()) {
			FileWIndows aReader = iterator.next();
			long totalLength = 0;
			for (Window window : aReader) {
				totalLength += window.length();
			}
			assertEquals("sum of window lengths ZIP", 45846, totalLength);
		}

		reader = new FileWIndows(getFile("/TestEmpty.empty"));
		assertEquals("length empty", 0, reader.length());

		iterator = new FileReaderIterator("/TestEmpty.empty");
		while (iterator.hasNext()) {
			FileWIndows aReader = iterator.next();
			long totalLength = 0;
			for (Window window : aReader) {
				totalLength += window.length();
			}
			assertEquals("sum of window lengths empty file", 0, totalLength);
		}
	}

	/**
	 * Test of readByte method, of class FileWIndows.
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	@Test
	public void testReadByte() throws FileNotFoundException, IOException {

		File asciifile = getFile("/TestASCII.txt");
		int fileLength = (int) asciifile.length();
		RandomAccessFile raf = new RandomAccessFile(asciifile, "r");

		FileReaderIterator iterator = new FileReaderIterator("/TestASCII.txt");
		while (iterator.hasNext()) {
			FileWIndows reader = iterator.next();

			// test known positions at and around the specified position:
			test(reader, 112122, (byte) 0x50);
			test(reader, 112271, (byte) 0x44);
			test(reader, 112275, (byte) 0x6d);
			test(reader, 112277, (byte) 0x2e);

			// test randomly selected positions:
			testRandomPositions("ascii file:", raf, reader, fileLength);
		}

		raf.close();

		File zipfile = getFile("/TestASCII.zip");
		fileLength = (int) zipfile.length();
		raf = new RandomAccessFile(zipfile, "r");

		iterator = new FileReaderIterator("/TestASCII.zip");
		while (iterator.hasNext()) {
			FileWIndows reader = iterator.next();

			// Test known positions at and around the specified position:
			test(reader, 3, (byte) 0x04);
			test(reader, 0, (byte) 0x50);
			test(reader, 1584, (byte) 0xAA);
			test(reader, 30359, (byte) 0x6F);
			test(reader, 39898, (byte) 0xFB);

			// test randomly selected positions:
			testRandomPositions("ascii file:", raf, reader, fileLength);
		}

		raf.close();
	}

	private void testRandomPositions(String description, RandomAccessFile raf, FileWIndows reader,
			int fileLength) throws IOException {
		// test randomly selected positions:
		for (int count = 0; count < 500; count++) {
			final int randomposition = rand.nextInt(fileLength);
			raf.seek(randomposition);
			byte filebyte = raf.readByte();
			assertEquals(description + randomposition, filebyte,
					(byte) reader.readByte(randomposition));
		}
	}

	/**
	 * Test of createWindow method, of class FileWIndows.
	 */
	@Test
	public void testCreateWindow() throws Exception {
		System.out.println("createWindow");
		long windowStart = 0L;
		FileWIndows instance = null;
		Window expResult = null;
		Window result = instance.createWindow(windowStart);
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of close method, of class FileWIndows.
	 */
	@Test
	public void testClose() throws Exception {
		System.out.println("close");
		FileWIndows instance = null;
		instance.close();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getFile method, of class FileWIndows.
	 */
	@Test
	public void testGetFile() {
		System.out.println("getFile");
		FileWIndows instance = null;
		File expResult = null;
		File result = instance.getFile();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	private void test(FileWIndows reader, long position, byte value) throws IOException {
		assertEquals(value, reader.readByte(position));
	}

	private File getFile(final String resourceName) {
		return new File(getFilePath(resourceName));
	}

	private String getFilePath(final String resourceName) {
		return this.getClass().getResource(resourceName).getPath();
	}

	/**
	 * Provides a variety of FileReaders using different window sizes, cache capacities
	 * and cache strategies.  This enables us to test FileWIndows methods on many different
	 * constructions, which should make no difference to the functionality of the file reader,
	 * (except the non-functional requirement of performance).
	 */
	private class FileReaderIterator implements Iterator<FileWIndows> {

		private final String	filePath;

		private final int[]		sizes			= new int[] { 1, 2, 4, 7, 13, 255, 256, 257, 1023,
														1024, 1025, 2047, 2048, 2049, 4095, 4096,
														4097, 32767, 32768, 32769, 65536 };

		private final int[]		cachecapacity	= new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 13,
														31, 32, 33, 63, 64, 65 };

		private int				currentReader	= 0;
		private int				currentSize		= 0;
		private int				currentCapacity	= 0;

		private int				numReaders		= cachecapacity.length * sizes.length + 3;

		public FileReaderIterator(String resourceName) {
			filePath = getFilePath(resourceName);
		}

		@Override
		public boolean hasNext() {
			return currentReader < numReaders;
		}

		@Override
		public FileWIndows next() {
			if (currentReader == 0) {
				currentReader++;
				try {
					return new FileWIndows(filePath); // pure default
				} catch (FileNotFoundException ex) {
					Logger.getLogger(FileWindowsTest.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else if (currentReader == 1) {
				currentReader++;
				try {
					return new FileWIndows(filePath, new AllWindowsCache()); // all windows cache.
				} catch (FileNotFoundException ex) {
					Logger.getLogger(FileWindowsTest.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else if (currentReader == 2) {
				currentReader++;
				try {
					return new FileWIndows(filePath, new NoCache()); // no cacheing.
				} catch (FileNotFoundException ex) {
					Logger.getLogger(FileWindowsTest.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else if (currentReader < numReaders) {
				currentReader++;
				if (currentCapacity < cachecapacity.length) {
					if (currentSize < sizes.length) {
						try {
							return new FileWIndows(filePath, sizes[currentSize++],
									cachecapacity[currentCapacity]);
						} catch (FileNotFoundException ex) {
							Logger.getLogger(FileWindowsTest.class.getName()).log(Level.SEVERE,
									null, ex);
						}

					} else {
						currentSize = 0;
						if (currentCapacity < cachecapacity.length) {
							try {
								return new FileWIndows(filePath, sizes[currentSize],
										cachecapacity[currentCapacity++]);
							} catch (FileNotFoundException ex) {
								Logger.getLogger(FileWindowsTest.class.getName()).log(Level.SEVERE,
										null, ex);
							}
						}
					}
				}
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Not supported.");
		}

	}

}
