/*
 * Copyright Matt Palmer 2011-2015, All rights reserved.
 *
 * This code is licensed under a standard 3-clause BSD license:
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * The names of its contributors may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.byteseek.io.reader;

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

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.cache.AllWindowsCache;
import net.byteseek.io.reader.cache.NoCache;

/**
 *
 * @author matt
 */
public class FileReaderTest {

	private final static Random	rand	= new Random();

	/**
	 * 
	 */
	public FileReaderTest() {
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
	 * Test of length method, of class FileReader.  Also tests that the total length
	 * is correct when the file reader uses different window sizes and cache capacities.
	 * @throws IOException 
	 */
	@Test
	public void testLength() throws IOException {
		@SuppressWarnings("resource")
		FileReader reader = new FileReader(getFile("/TestASCII.txt"));
		assertEquals("length ASCII", 112280, reader.length());
	
		// no matter how the file reader sets its window sizes or cache strategies,
		// it must make no difference to the total length of the data read.
		FileReaderIterator iterator = new FileReaderIterator("/TestASCII.txt");
		while (iterator.hasNext()) {
			FileReader aReader = iterator.next();
			long totalLength = 0;
			for (Window window : aReader) {
				totalLength += window.length();
			}
			assertEquals("sum of window lengths ASCII", 112280, totalLength);
		}

		reader = new FileReader(getFile("/TestASCII.zip"));
		assertEquals("length ZIP", 45846, reader.length());

		iterator = new FileReaderIterator("/TestASCII.zip");
		while (iterator.hasNext()) {
			FileReader aReader = iterator.next();
			long totalLength = 0;
			for (Window window : aReader) {
				totalLength += window.length();
			}
			assertEquals("sum of window lengths ZIP", 45846, totalLength);
		}

		reader = new FileReader(getFile("/TestEmpty.empty"));
		assertEquals("length empty", 0, reader.length());

		iterator = new FileReaderIterator("/TestEmpty.empty");
		while (iterator.hasNext()) {
			FileReader aReader = iterator.next();
			long totalLength = 0;
			for (Window window : aReader) {
				totalLength += window.length();
			}
			assertEquals("sum of window lengths empty file", 0, totalLength);
		}
	}

	/**
	 * Test of readByte method, of class FileReader.
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	@Test
	public void testReadByte() throws IOException {

		File asciifile = getFile("/TestASCII.txt");
		int fileLength = (int) asciifile.length();
		RandomAccessFile raf = new RandomAccessFile(asciifile, "r");

		FileReaderIterator iterator = new FileReaderIterator("/TestASCII.txt");
		while (iterator.hasNext()) {
			FileReader reader = iterator.next();

			// testReadByte known positions at and around the specified position:
			testReadByte(reader, 112122, (byte) 0x50);
			testReadByte(reader, 112271, (byte) 0x44);
			testReadByte(reader, 112275, (byte) 0x6d);
			testReadByte(reader, 112277, (byte) 0x2e);

			// testReadByte randomly selected positions:
			testRandomPositions("ascii file:", raf, reader, fileLength);
		}

		raf.close();

		File zipfile = getFile("/TestASCII.zip");
		fileLength = (int) zipfile.length();
		raf = new RandomAccessFile(zipfile, "r");

		iterator = new FileReaderIterator("/TestASCII.zip");
		while (iterator.hasNext()) {
			FileReader reader = iterator.next();

			// Test known positions at and around the specified position:
			testReadByte(reader, 3, (byte) 0x04);
			testReadByte(reader, 0, (byte) 0x50);
			testReadByte(reader, 1584, (byte) 0xAA);
			testReadByte(reader, 30359, (byte) 0x6F);
			testReadByte(reader, 39898, (byte) 0xFB);

			// testReadByte randomly selected positions:
			testRandomPositions("ascii file:", raf, reader, fileLength);
		}

		raf.close();
	}

	/**
	 * Test of createWindow method, of class FileReader.
	 */
	@Test
	public void testCreateWindow() throws Exception {

		fail("The test case is a prototype.");
	}

	/**
	 * Test of getWindow method, of class FileReader.
	 */
	@Test
	public void testGetWindow() throws Exception {

		fail("The test case is a prototype.");
	}

	/**
	 * Test of close method, of class FileReader.
	 */
	@Test
	public void testClose() throws Exception {
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getFile method, of class FileReader.
	 */
	@Test
	public void testGetFile() {
		fail("The test case is a prototype.");
	}


	@Test
	public void testUseSoftWindows() throws Exception {
		fail("The test case is a prototype.");

	}

	@Test
	public void testReloadWindowBytes() throws Exception {
		fail("The test case is a prototype.");

	}


	/*
	 * Private testReadByte methods.
	 */

	private void testRandomPositions(String description, RandomAccessFile raf, WindowReader reader,
									 int fileLength) throws IOException {
		// testReadByte randomly selected positions:
		for (int count = 0; count < 500; count++) {
			final int randomPosition = rand.nextInt(fileLength);
			raf.seek(randomPosition);
			byte fileByte = raf.readByte();
			assertEquals(description + randomPosition, fileByte,
					(byte) reader.readByte(randomPosition));
		}
	}

	private void testReadByte(WindowReader reader, long position, byte value) throws IOException {
		assertEquals("Reader " + reader + " reading at position " + position + " should have value " + value,
				      value, (byte) reader.readByte(position));
	}

	private File getFile(final String resourceName) {
		return new File(getFilePath(resourceName));
	}

	private String getFilePath(final String resourceName) {
		return this.getClass().getResource(resourceName).getPath();
	}


	/**
	 * Provides a variety of FileReaders using different window sizes, cache capacities
	 * and cache strategies.  This enables us to testReadByte FileReader methods on many different
	 * constructions, which should make no difference to the functionality of the file reader,
	 * (except the non-functional requirement of performance).
	 */
	private class FileReaderIterator implements Iterator<FileReader> {

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
		public FileReader next() {
			if (currentReader == 0) {
				currentReader++;
				try {
					return new FileReader(filePath); // pure default
				} catch (FileNotFoundException ex) {
					Logger.getLogger(FileReaderTest.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else if (currentReader == 1) {
				currentReader++;
				try {
					return new FileReader(filePath, new AllWindowsCache()); // all windows cache.
				} catch (FileNotFoundException ex) {
					Logger.getLogger(FileReaderTest.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else if (currentReader == 2) {
				currentReader++;
				try {
					return new FileReader(filePath, new NoCache()); // no cacheing.
				} catch (FileNotFoundException ex) {
					Logger.getLogger(FileReaderTest.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else if (currentReader < numReaders) {
				currentReader++;
				if (currentCapacity < cachecapacity.length) {
					if (currentSize < sizes.length) {
						try {
							return new FileReader(filePath, sizes[currentSize++],
									cachecapacity[currentCapacity]);
						} catch (FileNotFoundException ex) {
							Logger.getLogger(FileReaderTest.class.getName()).log(Level.SEVERE,
									null, ex);
						}

					} else {
						currentSize = 0;
						if (currentCapacity < cachecapacity.length) {
							try {
								return new FileReader(filePath, sizes[currentSize],
										cachecapacity[currentCapacity++]);
							} catch (FileNotFoundException ex) {
								Logger.getLogger(FileReaderTest.class.getName()).log(Level.SEVERE,
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
