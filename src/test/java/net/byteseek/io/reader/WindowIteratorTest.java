/*
 * Copyright Matt Palmer 2019, All rights reserved.
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

import net.byteseek.io.reader.windows.Window;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class WindowIteratorTest {

    private WindowReader reader;
    private WindowIterator iterator;

    @Before
    public void setup() throws IOException {
        reader = new FileReader(getFile("/TestASCII.txt"));
        iterator = new WindowIterator(reader);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullWindowReader() {
        new WindowIterator(null);
    }

    @Test
    public void hasNext() throws Exception {
        assertTrue(iterator.hasNext());
    }

    @Test
    public void doesNotHasNext() throws Exception {
        while (iterator.hasNext()) {
            iterator.next();
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void next() throws Exception {
        long position = 0;
        while (iterator.hasNext()) {
            Window window = iterator.next();
            assertEquals(position, window.getWindowPosition());
            position = window.getNextWindowPosition();
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void NoNext() throws Exception {
        while (iterator.hasNext()) {
            iterator.next();
        }
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() throws Exception {
        iterator.remove();
    }

    @Test
    public void testToString() {
        assertTrue(iterator.toString().contains(WindowIterator.class.getSimpleName()));
        assertTrue(iterator.toString().contains("reader"));
        assertTrue(iterator.toString().contains("position"));
    }

    private File getFile(final String resourceName) {
        return new File(getFilePath(resourceName));
    }

    private String getFilePath(final String resourceName) {
        return this.getClass().getResource(resourceName).getPath();
    }



}