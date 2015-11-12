/*
 * Copyright Matt Palmer 2015, All rights reserved.
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

import net.byteseek.io.reader.windows.SoftWindow;
import net.byteseek.io.reader.windows.SoftWindowRecovery;
import net.byteseek.io.reader.windows.Window;
import net.byteseek.io.reader.windows.WindowMissingException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class InputStreamReaderTest {

    private InputStreamReader[] readers = new InputStreamReader[7];

    @Before
    public void setup() {
        byte[] array = new byte[1024];
        // set up readers using different window sizes to flush out boundary conditions.
        InputStream in = new ByteArrayInputStream(array);
        readers[0] = new InputStreamReader(in, 512);
        in = new ByteArrayInputStream(array);
        readers[1] = new InputStreamReader(in, 1022);
        in = new ByteArrayInputStream(array);
        readers[2] = new InputStreamReader(in, 1023);
        in = new ByteArrayInputStream(array);
        readers[3] = new InputStreamReader(in, 1024);
        in = new ByteArrayInputStream(array);
        readers[4] = new InputStreamReader(in, 1025);
        in = new ByteArrayInputStream(array);
        readers[5] = new InputStreamReader(in, 1026);
        in = new ByteArrayInputStream(array);
        readers[6] = new InputStreamReader(in, 4096);
    }

    @Test
    public void testGetNegativeWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window before 0: " + i, readers[i].getWindow(-1));
        }
    }

    @Test
    public void testGetZeroWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            Window window = readers[i].getWindow(0);
            assertNotNull("have window at 0: " + i, window);
            assertEquals("window is at zero:" + i, 0, window.getWindowPosition());
        }
    }

    @Test
    public void testGetMidWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNotNull("Have window at 512: " + i, readers[i].getWindow(512));
        }
    }

    @Test
    public void testWindowAfterLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].getWindow(1025));
        }
    }

    @Test
    public void testWindowLongAfterLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].getWindow(200000));
        }
    }


    @Test
    public void testCreateNegativeWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].createWindow(-1));
        }
    }

    @Test
    public void testCreateZeroWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            Window window = readers[i].createWindow(0);
            assertNotNull("have window at 0: " + i, window);
            assertEquals("window is at zero:" + i, 0, window.getWindowPosition());
        }
    }

    @Test
    public void testCreateMidWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNotNull("Have window at 512: " + i, readers[i].createWindow(512));
        }
    }

    @Test
    public void testCreateWindowAfterLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].createWindow(1025));
        }
    }

    @Test
    public void testCreateWindowLongAfterLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertNull("No window after length: " + i, readers[i].createWindow(200000));
        }
    }

    @Test
    public void testLength() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            assertEquals("reader length is 1024", 1024, readers[i].length());
        }
    }

    @Test
    public void testCloseAfterReading() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            readers[i].length(); // force read of stream
            readers[i].close();  // close reader.
            try {
                readers[i].getWindow(0);
                fail("Expected WindowMissingException");
            } catch (WindowMissingException expected) {}
        }
    }

    /* Closing a ByteArrayInputStream doesn't do anything, so this
       test doesn't test anything.
    @Test
    public void testCloseBeforeReading() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            readers[i].close();  // close reader.
            try {
                readers[i].getWindow(0);
                fail("Expected WindowMissingException");
            } catch (WindowMissingException expected) {}
        }
    }
    */

    @Test
    public void testSetSoftWindowRecoveryGetWindow() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            readers[i].setSoftWindowRecovery(
                    new SoftWindowRecovery() {
                        @Override
                        public byte[] reloadWindowBytes(Window window) throws IOException {
                            return new byte[1024];
                        }
                    });
            assertEquals("Soft windows are returned", SoftWindow.class, readers[i].getWindow(0).getClass());
        }
    }

    @Test
    public void testSetSoftWindowRecovery() throws Exception {
        for (int i = 0; i < readers.length; i++) {
            readers[i].setSoftWindowRecovery(
                    new SoftWindowRecovery() {
                        @Override
                        public byte[] reloadWindowBytes(Window window) throws IOException {
                            return new byte[1024];
                        }
                    });
            readers[i].length(); // read entire stream so everything is in cache.
            assertEquals("Soft windows are returned", SoftWindow.class, readers[i].getWindow(0).getClass());
        }
    }


}