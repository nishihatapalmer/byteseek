/*
 * Copyright Matt Palmer 2017, All rights reserved.
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
package net.byteseek.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.fail;

/**
 * Tests for the ArgUtils utility class methods.
 *
 * Created by matt on 07/08/17.
 */
public class ArgUtilsTest {

    private static final Collection<?> EMPTY_COLLECTION = Collections.EMPTY_LIST;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final String EMPTY_STRING = "";

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullObject() throws Exception {
        ArgUtils.checkNullObject(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullCollection() throws Exception {
        ArgUtils.checkNullCollection(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullOrEmptyCollectionNull() throws Exception {
        ArgUtils.checkNullOrEmptyCollection(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullOrEmptyCollectionEmpty() throws Exception {
        ArgUtils.checkNullOrEmptyCollection(EMPTY_COLLECTION);
    }

    public void testExceptionCheckCollectionSize() throws Exception {
        Collection<Object> collection = new ArrayList<Object>();
        for (int i = 0; i < 100; i++) {
            ArgUtils.checkCollectionSize(collection, i);
            collection.add(EMPTY_STRING);
            try {
                ArgUtils.checkCollectionSize(collection, i);
                fail("collection is one bigger than " + i);
            } catch (IllegalArgumentException ia) {
            }
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullByteArray() throws Exception {
        ArgUtils.checkNullByteArray(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullIntArray() throws Exception {
        ArgUtils.checkNullIntArray(null, "int array");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullOrEmptyByteArrayNull() throws Exception {
        ArgUtils.checkNullOrEmptyByteArray(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullOrEmptyByteArrayNullDescription() throws Exception {
        ArgUtils.checkNullOrEmptyByteArray(null, "description");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullOrEmptyByteArrayEmpty() throws Exception {
        ArgUtils.checkNullOrEmptyByteArray(EMPTY_BYTE_ARRAY);
    }


    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullOrEmptyByteArrayEmptyDescription() throws Exception {
        ArgUtils.checkNullOrEmptyByteArray(EMPTY_BYTE_ARRAY, "description");
    }
    @Test
    public void testExceptionCheckNullOrEmptyByteArrayEmptyDescription2() throws Exception {
        byte[] array = new byte[1];
        try {
            ArgUtils.checkNullOrEmptyByteArray(array, "description");
        } catch (Exception ex) {
            fail("should not fail if byte array is not empty");
        }
    }


    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullString() throws Exception {
        ArgUtils.checkNullString(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullStringDescription() throws Exception {
        ArgUtils.checkNullString(null, "description");
    }


    @Test
    public void testExceptionCheckNotNullStringDescription() throws Exception {
        try {
            ArgUtils.checkNullString("string", "description");
        } catch (Exception ex) {
            fail("should not fail if string is not null");
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullOrEmptyStringNull() throws Exception {
        ArgUtils.checkNullOrEmptyString(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckNullOrEmptyStringEmpty() throws Exception {
        ArgUtils.checkNullOrEmptyString(EMPTY_STRING);
    }

    @Test
    public void testExceptionCheckNullOrEmptyStringNotEmpty() throws Exception {
        try {
            ArgUtils.checkNullOrEmptyString("string");
        } catch (Exception ex) {
            fail("should not fail if string is not empty");
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckPositive() throws Exception {
        ArgUtils.checkPositive(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testExceptionCheckPositiveZero() throws Exception {
        ArgUtils.checkPositive(0);
    }

    @Test
    public void testExceptionCheckPositiveOK() throws Exception {
        for (int i = 1; i < 1000; i++) {
            ArgUtils.checkPositive(i);
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullCollectionElements() {
        ArgUtils.checkNullCollectionElements(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullCollectionElementsDescription() {
        ArgUtils.checkNullCollectionElements(null, "description");
    }

    @Test
    public void testCheckNullCollectionElementsNullElements() {
        Collection<Integer> test = new ArrayList<Integer>();
        try {
            ArgUtils.checkNullCollectionElements(test);
        } catch (Exception ex) {
            fail("Should not generate exception with empty collection.");
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullCollectionElementsNullElementsDescription() {
        Collection<Integer> test = new ArrayList<Integer>();
        test.add(1);
        test.add(null);
        ArgUtils.checkNullCollectionElements(test, "description");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullCollectionElementsEmptyCollection() {
        Collection<Integer> test = new ArrayList<Integer>();
        test.add(1);
        test.add(null);
        ArgUtils.checkNullCollectionElements(test);
    }

    @Test
    public void testCheckNullCollectionElementsEmptyCollectionDescription() {
        Collection<Integer> test = new ArrayList<Integer>();
        try {
            ArgUtils.checkNullCollectionElements(test, "description");
        } catch (Exception ex) {
            fail("Should not generate exception with empty collection.");
        }
    }

    @Test
    public void testCheckNullCollectionElementsGoodCollection() {
        Collection<Integer> test = new ArrayList<Integer>();
        test.add(1);
        test.add(2);
        try {
            ArgUtils.checkNullCollectionElements(test);
        } catch (Exception ex) {
            fail("Should not generate exception with no null elements.");
        }
    }

    @Test
    public void testCheckNullCollectionElementsGoodCollectionDescription() {
        Collection<Integer> test = new ArrayList<Integer>();
        test.add(1);
        test.add(2);
        try {
            ArgUtils.checkNullCollectionElements(test, "description");
        } catch (Exception ex) {
            fail("Should not generate exception with no null elements.");
        }
    }

    //-------

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullOrEmptyCollectionElements() {
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullOrEmptyCollectionElementsDescription() {
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(null, "description");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullOrEmptyCollectionElementsNullElements() {
        Collection<Integer> test = new ArrayList<Integer>();
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(test);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullOrEmptyCollectionElementsNullElementsDescription() {
        Collection<Integer> test = new ArrayList<Integer>();
        test.add(1);
        test.add(null);
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(test, "description");

    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullOrEmptyCollectionElementsEmptyCollection() {
        Collection<Integer> test = new ArrayList<Integer>();
        test.add(1);
        test.add(null);
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(test);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCheckNullOrEmptyCollectionElementsEmptyCollectionDescription() {
        Collection<Integer> test = new ArrayList<Integer>();
        ArgUtils.checkNullOrEmptyCollectionNoNullElements(test, "description");
    }

    @Test
    public void testCheckNullOrEmptyCollectionElementsGoodCollection() {
        Collection<Integer> test = new ArrayList<Integer>();
        test.add(1);
        test.add(2);
        try {
            ArgUtils.checkNullOrEmptyCollectionNoNullElements(test);
        } catch (Exception ex) {
            fail("Should not generate exception with no null elements.");
        }
    }

    @Test
    public void testCheckNullOrEmptyCollectionElementsGoodCollectionDescription() {
        Collection<Integer> test = new ArrayList<Integer>();
        test.add(1);
        test.add(2);
        try {
            ArgUtils.checkNullOrEmptyCollectionNoNullElements(test, "description");
        } catch (Exception ex) {
            fail("Should not generate exception with no null elements.");
        }
    }

    //----------

    @Test(expected = IllegalArgumentException.class)
    public void testNullByteArray() {
        ArgUtils.checkNullByteArray(null);
    }

    @Test
    public void testNullByteArrayNotNull() {
        try {
            ArgUtils.checkNullByteArray(new byte[1]);
        } catch (Exception ex) {
            fail("should not generate exception with a non null array");
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullByteArrayDescription() {
        ArgUtils.checkNullByteArray(null, "description");
    }

    @Test
    public void testNullByteArrayNotNullDescription() {
        try {
            ArgUtils.checkNullByteArray(new byte[1], "description");
        } catch (Exception ex) {
            fail("should not generate exception with a non null array");
        }
    }

    //----------

    @Test(expected = IllegalArgumentException.class)
    public void testNullIntArray() {
        ArgUtils.checkNullIntArray(null, "description");
    }

    @Test
    public void testNullIntArrayNotNull() {
        try {
            ArgUtils.checkNullIntArray(new int[1], "description");
        } catch (Exception ex) {
            fail("should not generate exception with a non null array");
        }
    }

    //---------

    @Test(expected = IllegalArgumentException.class)
    public void checkNullArrayDescription() {
        ArgUtils.checkNullArray(null, "description");
    }

    @Test
    public void checkNullArrayNotNullDescription() {
        Integer[] array = new Integer[1];
        try {
            ArgUtils.checkNullArray(array, "description");
        } catch (Exception ex) {
            fail("should not fail if array is not null, even if array is empty");
        }
    }

    //---------

    @Test(expected = IllegalArgumentException.class)
    public void checkNullOrEmptyArrayDescription() {
        ArgUtils.checkNullOrEmptyArray(null, "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNullEmptyArrayDescription() {
        Integer[] array = new Integer[0];
        ArgUtils.checkNullOrEmptyArray(array, "description");
    }

    @Test
    public void checkNullNotEmptyArrayDescription() {
        Integer[] array = new Integer[1];
        try {
            ArgUtils.checkNullOrEmptyArray(array, "description");
        } catch (Exception ex) {
            fail("should not throw exception if array is not empty");
        }
    }

    //-----


    @Test(expected = IllegalArgumentException.class)
    public void checkNullOrEmptyArrayNoNullElementsDescription() {
        ArgUtils.checkNullOrEmptyArrayNoNullElements(null, "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNullEmptyArrayNoNullElementsDescription() {
        Integer[] array = new Integer[0];
        ArgUtils.checkNullOrEmptyArrayNoNullElements(array, "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNullEmptyArrayNoNullElementsDescription2() {
        Integer[] array = new Integer[1];
        ArgUtils.checkNullOrEmptyArrayNoNullElements(array, "description");
    }

    @Test
    public void checkNullNotEmptyArrayNoNullElementsDescription() {
        Integer[] array = new Integer[1];
        array[0] = new Integer(0);
        try {
            ArgUtils.checkNullOrEmptyArrayNoNullElements(array, "description");
        } catch (Exception ex) {
            fail("should not throw exception if array is not empty");
        }
    }

    @Test
    public void checkNullNotEmptyArrayNoNullElements2Description() {
        Integer[] array = new Integer[2];
        array[0] = new Integer(1);
        array[1] = new Integer(0);
        try {
            ArgUtils.checkNullOrEmptyArrayNoNullElements(array, "description");
        } catch (Exception ex) {
            fail("should not throw exception if array has no null elements");
        }
    }

    //-----------
    @Test(expected = IllegalArgumentException.class)
    public void checkRangeInclusiveBefore() {
        ArgUtils.checkRangeInclusive(0, 1, 2, "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkRangeInclusiveAfter() {
        ArgUtils.checkRangeInclusive(3, 1, 2, "description");
    }

    @Test
    public void checkRangeInclusiveAtStart() {
        try {
            ArgUtils.checkRangeInclusive(1, 1, 2, "description");
        } catch (Exception ex) {
            fail("should not fail if at start");
        }
    }


    @Test
    public void checkRangeInclusiveAtEnd() {
        try {
            ArgUtils.checkRangeInclusive(2, 1, 2, "description");
        } catch (Exception ex) {
            fail("should not fail if at end");
        }
    }

    @Test
    public void checkRangeInclusiveInside() {
        try {
            ArgUtils.checkRangeInclusive(2, 1, 3, "description");
        } catch (Exception ex) {
            fail("should not fail if inside range");
        }
    }

    //------
    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOutOfBoundsPositionNegative() {
        ArgUtils.checkIndexOutOfBounds(0, -1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOutOfBoundsPositionAtLength() {
        ArgUtils.checkIndexOutOfBounds(1, 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOutOfBoundsPositionPastLength() {
        ArgUtils.checkIndexOutOfBounds(1, 2);
    }

    @Test
    public void checkIndexOutOfBoundsPositionAtStart() {
        try {
            ArgUtils.checkIndexOutOfBounds(1, 0);
        } catch (Exception ex) {
            fail("should not fail if at start of index");
        }
    }

    @Test
    public void checkIndexOutOfBoundsPositionWithin() {
        try {
            ArgUtils.checkIndexOutOfBounds(2, 1);
        } catch (Exception ex) {
            fail("should not fail if inside");
        }
    }

    //-----------
    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOutOfBoundsRangeStartIndexNegative() {
        ArgUtils.checkIndexOutOfBounds(10, -1, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOutOfBoundsRangeStartIndexEqualsEndIndex() {
        ArgUtils.checkIndexOutOfBounds(10, 2, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOutOfBoundsRangeStartIndexPastEndIndex() {
        ArgUtils.checkIndexOutOfBounds(10, 3, 2);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOutOfBoundsRangeStartIndexEqualsLength() {
        ArgUtils.checkIndexOutOfBounds(10, 10, 11);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOutOfBoundsRangeStartIndexPastLength() {
        ArgUtils.checkIndexOutOfBounds(10, 11, 12);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void checkIndexOutOfBoundsRangeEndIndexPastLength() {
        ArgUtils.checkIndexOutOfBounds(10, 1, 11);
    }

    @Test
    public void checkIndexOutOfBoundsRangeEndIndexEqualsLength() {
        try {
            ArgUtils.checkIndexOutOfBounds(10, 2, 10);
        } catch (Exception ex) {
            fail("should not fail if end index equals length");
        }
    }

    @Test
    public void checkIndexOutOfBoundsRangeStartIndexZero() {
        try {
            ArgUtils.checkIndexOutOfBounds(1, 0, 1);
        } catch (Exception ex) {
            fail("should not fail if start index is zero");
        }
    }

    //----------
    @Test(expected = IllegalArgumentException.class)
    public void checkNotNegative() {
        ArgUtils.checkNotNegative(-1);
    }

    @Test
    public void checkNotNegativeIfZero() {
        try {
            ArgUtils.checkNotNegative(0);
        } catch (Exception ex) {
            fail("should not fail for zero");
        }
    }

    @Test
    public void checkNotNegativeIfPositive() {
        try {
            ArgUtils.checkNotNegative(1);
        } catch (Exception ex) {
            fail("should not fail for 1");
        }
    }

}
