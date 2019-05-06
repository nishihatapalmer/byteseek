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
package net.byteseek.utils.lazy;

import net.byteseek.utils.factory.ObjectFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SingleCheckLazyObjectTest {

    private StringFactory factory;
    private LazyObject<String> lazy;

    @Before
    public void setup() {
        factory = new StringFactory();
        lazy = new SingleCheckLazyObject<String>(factory);
    }

    @Test
    public void testGetAndCreated() throws Exception {
        assertFalse(lazy.created());
        String result = lazy.get();
        assertEquals(factory.result, result);
        assertTrue(lazy.created());
    }

    @Test
    public void testToString() throws Exception {
        assertTrue(lazy.toString().contains(SingleCheckLazyObject.class.getSimpleName()));
    }

    private static class StringFactory implements ObjectFactory<String> {

        public String result = "Result";
        private boolean created = false;

        @Override
        public String create() {
            created = true;
            return result;
        }

        public boolean isCreated() {
            return created;
        }
    }


}