/*
 * Copyright Matt Palmer, Casey Stella, 2011-2019, All rights reserved.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.byteseek.io.IOIterator;
import net.byteseek.io.IOUtils;
import net.byteseek.io.reader.cache.TestWindow;
import net.byteseek.io.reader.windows.HardWindow;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.cache.AllWindowsCache;
import net.byteseek.io.reader.cache.NoCache;

import static org.junit.Assert.*;

/**
 *
 * @author matt
 */
public class FileReaderTest {

	private final static Random	rand	= new Random();
	RandomAccessFile raf;

	/**
	 *
	 */
	public FileReaderTest() {
	}

	/**
	 *
	 */
	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	/**
	 *
	 */
	@BeforeClass
	public static void setUpClass() throws Exception {
		final long seed = System.currentTimeMillis();
		// final long seed = ?
		rand.setSeed(seed);
		System.out.println("Seeding random number generator with: " + Long.toString(seed));
		System.out.println("To repeat these exact tests, set the seed to the value above.");
	}

	@After
    public void tearDown() throws IOException {
	    if (raf != null) {
	        raf.close();
        }
    }

	@Test
	public void testIterateWindows() throws IOException {
		FileReaderIterator ri = new FileReaderIterator("/TestASCII.txt");
		while (ri.hasNext()) {
			try (WindowReader reader = ri.next()) {
				testIterateReader(reader);
			}
		}

	}

	@Test(expected = UnsupportedOperationException.class)
	public void testNoRemoveIterator() throws IOException {
		FileReaderIterator ri = new FileReaderIterator("/TestASCII.txt");
		try(WindowReader reader = ri.next()) {
			IOIterator<Window> iterator = reader.iterator();
			iterator.remove();
		}
	}

	private void testIterateReader(WindowReader reader) throws IOException {
		long length = 0;
		final IOIterator<Window> iterator = reader.iterator();
		while (iterator.hasNext()) {
			length += iterator.next().length();
		}
		assertEquals("Length is 112280 after iterating all windows", 112280, length);
	}

	/**
	 * Test of length method, of class FileReader.  Also tests that the total length
	 * is correct when the file reader uses different window sizes and cache capacities.
	 */
	@Test
	public void testLength() throws IOException {
		FileReaderIterator iterator;
		try (FileReader reader = new FileReader(getFile("/TestASCII.txt"))){
			assertEquals("length ASCII", 112280, reader.length());

			// no matter how the file reader sets its window sizes or cache strategies,
			// it must make no difference to the total length of the data read.
			iterator = new FileReaderIterator("/TestASCII.txt");
			while (iterator.hasNext()) {
				try (FileReader aReader = iterator.next()) {
                    long totalLength = 0;
                    final IOIterator<Window> winIterator = aReader.iterator();
                    while (winIterator.hasNext()) {
                        totalLength += winIterator.next().length();
                    }
                    assertEquals("sum of window lengths ASCII", 112280, totalLength);
                }
			}
		}
		try(FileReader reader = new FileReader(getFile("/TestASCII.zip"))) {
			assertEquals("length ZIP", 45846, reader.length());

			iterator = new FileReaderIterator("/TestASCII.zip");
			while (iterator.hasNext()) {
				try (FileReader aReader = iterator.next()) {
                    long totalLength = 0;
                    final IOIterator<Window> winIterator = aReader.iterator();
                    while (winIterator.hasNext()) {
                        totalLength += winIterator.next().length();
                    }
                    assertEquals("sum of window lengths ZIP", 45846, totalLength);
                }
			}
		}

		try(FileReader reader = new FileReader(getFile("/TestEmpty.empty"))) {
			assertEquals("length empty", 0, reader.length());

			iterator = new FileReaderIterator("/TestEmpty.empty");
			while (iterator.hasNext()) {
				try (FileReader aReader = iterator.next()) {
                    long totalLength = 0;
                    final IOIterator<Window> winIterator = aReader.iterator();
                    while (winIterator.hasNext()) {
                        totalLength += winIterator.next().length();
                    }
                    assertEquals("sum of window lengths empty file", 0, totalLength);
                }
			}
		}
	}

	/**
	 * Test of readByte method, of class FileReader.
	 */
	@Test
	public void testReadByte() throws IOException {

		File asciifile = getFile("/TestASCII.txt");
		int fileLength = (int) asciifile.length();
		raf = new RandomAccessFile(asciifile, "r");

		FileReaderIterator iterator = new FileReaderIterator("/TestASCII.txt");
		while (iterator.hasNext()) {
			try(FileReader reader = iterator.next()) {

				// testReadByte known positions at and around the specified position:
				testReadByte(reader, 112122, (byte) 0x50);
				testReadByte(reader, 112271, (byte) 0x44);
				testReadByte(reader, 112275, (byte) 0x6d);
				testReadByte(reader, 112277, (byte) 0x2e);

				// testReadByte randomly selected positions:
				testRandomPositions("ascii file:", raf, reader, fileLength);
			}
		}

		raf.close();

		File zipfile = getFile("/TestASCII.zip");
		fileLength = (int) zipfile.length();
		raf = new RandomAccessFile(zipfile, "r");

		iterator = new FileReaderIterator("/TestASCII.zip");
		while (iterator.hasNext()) {
			try(FileReader reader = iterator.next()) {

				// Test known positions at and around the specified position:
				testReadByte(reader, 3, (byte) 0x04);
				testReadByte(reader, 0, (byte) 0x50);
				testReadByte(reader, 1584, (byte) 0xAA);
				testReadByte(reader, 30359, (byte) 0x6F);
				testReadByte(reader, 39898, (byte) 0xFB);

				// testReadByte randomly selected positions:
				testRandomPositions("ascii file:", raf, reader, fileLength);
			}
		}

		raf.close();
	}

	@Test
	public void testCloseBeforeReading() throws Exception {

		File zipfile = getFile("/TestASCII.zip");
		raf = new RandomAccessFile(zipfile, "r");

		Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.zip");

		while (iterator.hasNext()) {
			try (FileReader reader = iterator.next()) {
                reader.close();  // close reader.
                try {
                    reader.getWindow(0);
                    fail("Expected IOException");
                } catch (IOException expected) {}
            };
		}
	}

	@Test
	public void testGetWindowData() throws IOException {

		File zipfile = getFile("/TestASCII.zip");
		raf = new RandomAccessFile(zipfile, "r");

		Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.zip");
		while (iterator.hasNext()) {
			try(WindowReader wr = iterator.next()) {
				testGetWindowData(wr, raf);
			}
		}
	}

	@Test
	public void testRead() throws IOException {
		File readFile = getFile("/TestASCII.txt");
		raf = new RandomAccessFile(readFile, "r");
        Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.txt");
        byte[] buffer = new byte[1024];
        while (iterator.hasNext()) {
            try (WindowReader wr = iterator.next()) {
                testRead(wr, raf);
            }
        }
	}

	private void testRead(WindowReader reader, RandomAccessFile raf) throws IOException {
        final long length = reader.length();
        final byte[] buffer = new byte[1023];
        final byte[] buffer2 = new byte[1023];
        for (int test = 0; test < 100; test++) {
            final long testPos = rand.nextInt((int) length - 24);
            final int testBufferPos = rand.nextInt(buffer.length - 24);
            final int testLength = rand.nextInt(buffer.length - testBufferPos);
            final int bytesCopied = reader.read(testPos, buffer, testBufferPos, testLength);

            raf.seek(testPos);
            raf.read(buffer2, testBufferPos, testLength);
            assertArrayEquals(buffer, buffer2);
        }
    }

	@Test
    public void testReadByteBuffer() throws IOException {
        File readFile = getFile("/TestASCII.txt");
        raf = new RandomAccessFile(readFile, "r");
        Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.txt");
        byte[] buffer = new byte[1024];
        while (iterator.hasNext()) {
            try (WindowReader wr = iterator.next()) {
                testReadBuffer(wr, raf);
            }
        }
	}

	private void testReadBuffer(WindowReader reader, RandomAccessFile raf) throws IOException {
        final long length = reader.length();
        final byte[] buffer = new byte[1023];
        final ByteBuffer bytebuf = ByteBuffer.wrap(buffer);
        final byte[] buffer2 = new byte[1023];
        for (int test = 0; test < 100; test++) {
            final long testPos = rand.nextInt((int) length - 24);
            final int bytesCopied = reader.read(testPos, bytebuf);

            raf.seek(testPos);
            raf.read(buffer2, 0, buffer2.length);
            assertArrayEquals(buffer, buffer2);

            bytebuf.clear();
        }
	}

    @Test
    public void testSetNullFactory() throws IOException {
        Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.txt");
        while (iterator.hasNext()) {
            try (WindowReader wr = iterator.next()) {
                try {
                    wr.setWindowFactory(null);
                    fail("Setting null window factory should give an IllegalArgumentException " + wr);
                } catch (IllegalArgumentException expected) {}
            }
        }
    }

    @Test
    public void testReadNegativePosition() throws IOException {
        Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.txt");
        final byte[] buffer = new byte[2014];
        while (iterator.hasNext()) {
            try (WindowReader wr = iterator.next()) {
                final long negpos = - (rand.nextInt(1024)) - 1;
                final int copied = wr.read(negpos, buffer, 0, buffer.length);
                assertEquals(-1, copied);
            }
        }
    }

    @Test
    public void testReadPastEnd() throws IOException {
        Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.txt");
        long length = getFile("/TestASCII.txt").length();
        final byte[] buffer = new byte[2014];
        while (iterator.hasNext()) {
            try (WindowReader wr = iterator.next()) {
                assertEquals(-1, wr.read(length, buffer, 0, buffer.length));
                final long pastEndPos = length + (rand.nextInt(1024));
                assertEquals(-1, wr.read(pastEndPos, buffer, 0, buffer.length));

            }
        }
    }

    @Test
    public void testReadBufferNegativePosition() throws IOException {
        Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.txt");
        final ByteBuffer buf = ByteBuffer.wrap(new byte[2014]);
        while (iterator.hasNext()) {
            try (WindowReader wr = iterator.next()) {
                final long negpos = - (rand.nextInt(1024)) - 1;
                final int copied = wr.read(negpos, buf);
                assertEquals(-1, copied);
            }
        }
    }

    @Test
    public void testReadBufferPastEnd() throws IOException {
        Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.txt");
        long length = getFile("/TestASCII.txt").length();
        final ByteBuffer buf = ByteBuffer.wrap(new byte[2014]);
        while (iterator.hasNext()) {
            try (WindowReader wr = iterator.next()) {
                assertEquals(-1, wr.read(length, buf));
                final long pastEndPos = length + (rand.nextInt(1024));
                assertEquals(-1, wr.read(pastEndPos, buf));
            }
        }
    }

    @Test
    public void testSetWindowFactory() throws IOException {
        Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.txt");
        while (iterator.hasNext()) {
            try (WindowReader wr = iterator.next()) {
                Window window = wr.getWindow(0);
                assertEquals(HardWindow.class, window.getClass());

                wr.setWindowFactory(TestWindow.FACTORY);
                window = wr.getWindow(window.getNextWindowPosition());
                assertEquals(TestWindow.class, window.getClass());
            }
        }
    }


	@Test
	public void testSoftRecovery() throws IOException {
		File zipfile = getFile("/TestASCII.zip");
		raf = new RandomAccessFile(zipfile, "r");
		Iterator<FileReader> iterator = new FileReaderIterator("/TestASCII.zip");
		while (iterator.hasNext()) {
			try(FileReader reader = iterator.next()) {
				final IOIterator<Window> winIterator = reader.iterator();
				while (winIterator.hasNext()) {
					final Window window = winIterator.next();
					byte[] original = window.getArray().clone();
					byte[] recovered = reader.reloadWindowBytes(window);
					assertTrue("Length is enough orig=" + original.length + " win length=" + window.length() + " recovered len=" + recovered.length, recovered.length >= window.length());
					for (int i = 0; i < original.length; i++) {
						if (original[i] != recovered[i]) {
							fail("Bytes not the same at position " + i + " in recovered window: " + window);
						}
					}
				}
			}
		}
	}

	private void testGetWindowData(WindowReader fileReader, RandomAccessFile raf) throws IOException {
		final IOIterator<Window> winIterator = fileReader.iterator();
	    while (winIterator.hasNext()) {
	        final Window window = winIterator.next();
			byte[] fileBytes = new byte[window.length()];
			long windowPosition = window.getWindowPosition();
			raf.seek(windowPosition);
			IOUtils.readBytes(raf, windowPosition, fileBytes);
			assertAllBytesSame(window, fileBytes);
		}
	}

	private void assertAllBytesSame(Window window, byte[] fileBytes) throws IOException {
		byte[] windowArray = window.getArray();
		for (int i = 0; i < fileBytes.length; i++) {
			assertEquals("Bytes identical for window" + window + " at position " + i, fileBytes[i], windowArray[i]);
		}
	}

	@Test
	public void testGetNegativeWindow() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while(it.hasNext()) {
		    try (WindowReader wr = it.next()) {
                assertNull("No window before 0: ", wr.getWindow(-1));
            }
		}
	}

	@Test
	public void testGetZeroWindow() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader fr = it.next()){
				Window window = fr.getWindow(0);
				assertNotNull("have window at 0: ", window);
				assertEquals("window is at zero:", 0, window.getWindowPosition());
			}
		}
	}

	@Test
	public void testGetMidWindow() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader fr = it.next()) {
				assertNotNull("Have window at 512: ", fr.getWindow(512));
			}
		}
	}

	@Test
	public void testWindowAfterLength() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader fr = it.next()) {
				assertNull("No window after length: ", fr.getWindow(112281));
			}
		}
	}

	@Test
	public void testWindowLongAfterLength() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader fr = it.next()) {
				assertNull("No window after length: ", fr.getWindow(200000));
			}
		}
	}


	@Test
	public void testCreateNegativeWindow() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader fr = it.next()) {
				assertNull("No window after length: ", fr.createWindow(-1));
			}
		}
	}

	@Test
	public void testCreateZeroWindow() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader fr = it.next()) {
				Window window = fr.createWindow(0);
				assertNotNull("have window at 0: ", window);
				assertEquals("window is at zero:", 0, window.getWindowPosition());
			}
		}
	}

	@Test
	public void testCreateMidWindow() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader fr = it.next()) {
				assertNotNull("Have window at 512: ", fr.createWindow(512));
			}
		}
	}

	@Test
	public void testCreateWindowAfterLength() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader fr = it.next()) {
				assertNull("No window after length: ", fr.createWindow(112281));
			}
		}
	}

	@Test
	public void testCreateWindowLongAfterLength() throws Exception {
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader fr = it.next()) {
				assertNull("No window after length: ", fr.createWindow(200000));
			}
		}
	}

	/**
	 * Test of getFile method, of class FileReader.
	 */
	@Test
	public void testGetFile() throws IOException {
		File testFile = getFile("/TestASCII.txt");
		FileReaderIterator it = new FileReaderIterator("/TestASCII.txt");
		while (it.hasNext()) {
			try(FileReader reader = it.next()) {
				assertEquals("Files are correct.", testFile, reader.getFile());
			}
		}
	}

	@Test
	public void testCreateValidFileAndCache() {
		File testFile = getFile("/TestASCII.txt");
		try( FileReader reader = new FileReader(testFile, new NoCache())) {
		}
		catch (Exception anything) {
			fail("Should be no exception creating a valid file reader.");
		}
	}


	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullFile() throws IOException {
		FileReader fr = new FileReader((File) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullCacheFile() throws IOException {
		FileReader fr = new FileReader((File) null, null);
	}

	@Test
	public void testCreateFileWindowSize() {
		File testFile = getFile("/TestASCII.txt");
		try(FileReader reader = new FileReader(testFile, 1024)){
		} catch (Exception e) {
			fail("Should be no Exception from creating a valid FileReader.");
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullFileWindowSize() throws IOException {
		FileReader fr = new FileReader((File) null, 1024);
	}

	@Test
	public void testCreateFileWindowSizeCapacity() {
		File testFile = getFile("/TestASCII.txt");
		try(FileReader reader = new FileReader(testFile, 1024, 32)) {
		} catch (Exception e) {
			fail("Should be no Exception from creating a valid FileReader.");
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullFileWindowSizeCapacity() throws IOException {
		FileReader fr = new FileReader((File) null, 1024, 32);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullPath() throws IOException {
		FileReader fr = new FileReader((String) null);
	}

	@Test
	public void testCreatePathSize() {
		String path = getFilePath("/TestASCII.txt");
		try(FileReader reader = new FileReader(path, 1024)) {
		} catch (Exception e) {
			fail("Should be no Exception from creating a valid FileReader.");
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullPathCache() throws IOException {
		try(FileReader fr = new FileReader((String) null, null)) {}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatePathNullCache() throws IOException {
		try(FileReader fr = new FileReader("/TestASCII.txt", null)) {}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullPathWindowSize() throws IOException {
		try(FileReader fr = new FileReader((String) null, 1024)) {}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullPathWindowSizeCapacity() throws IOException {
		try(FileReader fr = new FileReader((String) null, 1024, 32)) {}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateNullFileWindowSizeCache() throws IOException {
		try (FileReader fr = new FileReader((File) null, 1024, null)) {
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateFileWindowSizeNullCache() throws IOException {
		try(FileReader fr = new FileReader(getFile("/TestASCII.txt"), 1024, null)) {}
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
	private class FileReaderIterator implements Iterator<FileReader>{

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