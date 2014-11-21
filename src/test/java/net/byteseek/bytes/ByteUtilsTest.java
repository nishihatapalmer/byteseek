/*
 * Copyright Matt Palmer 2013, All rights reserved.
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

package net.byteseek.bytes;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.byteseek.bytes.ByteUtils;
import net.byteseek.collections.CollUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matt Palmer
 */
public class ByteUtilsTest {

  
    /**
     * 
     */
    public ByteUtilsTest() {
    }

    /**
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
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
     */
    @Before
    public void setUp() {
    }

    /**
     * 
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of countSetBits method, of class ByteUtils.
     */
    @Test
    public void testCountSetBits() {

       // zero bits:
       assertEquals("00000000", 0, ByteUtils.countSetBits((byte) 0x00) );

       // single bits:
       assertEquals("00000001", 1, ByteUtils.countSetBits((byte) 0x01) );
       assertEquals("00000010", 1, ByteUtils.countSetBits((byte) 0x02) );
       assertEquals("00000100", 1, ByteUtils.countSetBits((byte) 0x04) );
       assertEquals("00001000", 1, ByteUtils.countSetBits((byte) 0x08) );
       assertEquals("00010000", 1, ByteUtils.countSetBits((byte) 0x10) );
       assertEquals("00100000", 1, ByteUtils.countSetBits((byte) 0x20) );
       assertEquals("01000000", 1, ByteUtils.countSetBits((byte) 0x40) );
       assertEquals("10000000", 1, ByteUtils.countSetBits((byte) 0x80) );

       // two bits:
       assertEquals("10000001", 2, ByteUtils.countSetBits((byte) 0x81) );
       assertEquals("10000010", 2, ByteUtils.countSetBits((byte) 0x82) );
       assertEquals("10000100", 2, ByteUtils.countSetBits((byte) 0x84) );
       assertEquals("10001000", 2, ByteUtils.countSetBits((byte) 0x88) );
       assertEquals("10010000", 2, ByteUtils.countSetBits((byte) 0x90) );
       assertEquals("10100000", 2, ByteUtils.countSetBits((byte) 0xA0) );
       assertEquals("11000000", 2, ByteUtils.countSetBits((byte) 0xC0) );
       assertEquals("00011000", 2, ByteUtils.countSetBits((byte) 0x18) );

       // three bits:
       assertEquals("10000011", 3, ByteUtils.countSetBits((byte) 0x83) );
       assertEquals("10000110", 3, ByteUtils.countSetBits((byte) 0x86) );
       assertEquals("10001100", 3, ByteUtils.countSetBits((byte) 0x8C) );
       assertEquals("10011000", 3, ByteUtils.countSetBits((byte) 0x98) );
       assertEquals("10010001", 3, ByteUtils.countSetBits((byte) 0x91) );
       assertEquals("00101100", 3, ByteUtils.countSetBits((byte) 0x2C) );
       assertEquals("11000100", 3, ByteUtils.countSetBits((byte) 0xC4) );
       assertEquals("00001011", 3, ByteUtils.countSetBits((byte) 0x0B) );

       // four bits:
       assertEquals("01010101", 4, ByteUtils.countSetBits((byte) 0x55) );
       assertEquals("10101010", 4, ByteUtils.countSetBits((byte) 0xAA) );
       assertEquals("11110000", 4, ByteUtils.countSetBits((byte) 0xF0) );
       assertEquals("00001111", 4, ByteUtils.countSetBits((byte) 0x0F) );
       assertEquals("01100101", 4, ByteUtils.countSetBits((byte) 0x65) );

       // five bits:
       assertEquals("11010101", 5, ByteUtils.countSetBits((byte) 0xD5) );
       assertEquals("10101011", 5, ByteUtils.countSetBits((byte) 0xAB) );
       assertEquals("11110100", 5, ByteUtils.countSetBits((byte) 0xF4) );
       assertEquals("01001111", 5, ByteUtils.countSetBits((byte) 0x4F) );
       assertEquals("01110101", 5, ByteUtils.countSetBits((byte) 0x75) );

       // six bits:
       assertEquals("11011101", 6, ByteUtils.countSetBits((byte) 0xDD) );
       assertEquals("10111110", 6, ByteUtils.countSetBits((byte) 0xBE) );
       assertEquals("11110110", 6, ByteUtils.countSetBits((byte) 0xF6) );
       assertEquals("01101111", 6, ByteUtils.countSetBits((byte) 0x6F) );
       assertEquals("01111101", 6, ByteUtils.countSetBits((byte) 0x7E) );

       // seven bits:
       assertEquals("11111011", 7, ByteUtils.countSetBits((byte) 0xFB) );
       assertEquals("11011111", 7, ByteUtils.countSetBits((byte) 0xDF) );
       assertEquals("00001011", 7, ByteUtils.countSetBits((byte) 0xFE) );
       
       // eight bits:
       assertEquals("11111111", 8, ByteUtils.countSetBits((byte) 0xFF) );
    }


    /**
     * Test of countUnsetBits method, of class ByteUtils.
     */
    @Test
    public void testCountUnsetBits() {
       // eight zero bits:
       assertEquals("00000000", 8, ByteUtils.countUnsetBits((byte) 0x00) );

       // seven zero bits:
       assertEquals("00000001", 7, ByteUtils.countUnsetBits((byte) 0x01) );
       assertEquals("00000010", 7, ByteUtils.countUnsetBits((byte) 0x02) );
       assertEquals("00000100", 7, ByteUtils.countUnsetBits((byte) 0x04) );
       assertEquals("00001000", 7, ByteUtils.countUnsetBits((byte) 0x08) );
       assertEquals("00010000", 7, ByteUtils.countUnsetBits((byte) 0x10) );
       assertEquals("00100000", 7, ByteUtils.countUnsetBits((byte) 0x20) );
       assertEquals("01000000", 7, ByteUtils.countUnsetBits((byte) 0x40) );
       assertEquals("10000000", 7, ByteUtils.countUnsetBits((byte) 0x80) );

       // six zero bits:
       assertEquals("10000001", 6, ByteUtils.countUnsetBits((byte) 0x81) );
       assertEquals("10000010", 6, ByteUtils.countUnsetBits((byte) 0x82) );
       assertEquals("10000100", 6, ByteUtils.countUnsetBits((byte) 0x84) );
       assertEquals("10001000", 6, ByteUtils.countUnsetBits((byte) 0x88) );
       assertEquals("10010000", 6, ByteUtils.countUnsetBits((byte) 0x90) );
       assertEquals("10100000", 6, ByteUtils.countUnsetBits((byte) 0xA0) );
       assertEquals("11000000", 6, ByteUtils.countUnsetBits((byte) 0xC0) );
       assertEquals("00011000", 6, ByteUtils.countUnsetBits((byte) 0x18) );

       // five zero bits:
       assertEquals("10000011", 5, ByteUtils.countUnsetBits((byte) 0x83) );
       assertEquals("10000110", 5, ByteUtils.countUnsetBits((byte) 0x86) );
       assertEquals("10001100", 5, ByteUtils.countUnsetBits((byte) 0x8C) );
       assertEquals("10011000", 5, ByteUtils.countUnsetBits((byte) 0x98) );
       assertEquals("10010001", 5, ByteUtils.countUnsetBits((byte) 0x91) );
       assertEquals("00101100", 5, ByteUtils.countUnsetBits((byte) 0x2C) );
       assertEquals("11000100", 5, ByteUtils.countUnsetBits((byte) 0xC4) );
       assertEquals("00001011", 5, ByteUtils.countUnsetBits((byte) 0x0B) );

       // four zero bits:
       assertEquals("01010101", 4, ByteUtils.countUnsetBits((byte) 0x55) );
       assertEquals("10101010", 4, ByteUtils.countUnsetBits((byte) 0xAA) );
       assertEquals("11110000", 4, ByteUtils.countUnsetBits((byte) 0xF0) );
       assertEquals("00001111", 4, ByteUtils.countUnsetBits((byte) 0x0F) );
       assertEquals("01100101", 4, ByteUtils.countUnsetBits((byte) 0x65) );

       // three zero bits:
       assertEquals("11010101", 3, ByteUtils.countUnsetBits((byte) 0xD5) );
       assertEquals("10101011", 3, ByteUtils.countUnsetBits((byte) 0xAB) );
       assertEquals("11110100", 3, ByteUtils.countUnsetBits((byte) 0xF4) );
       assertEquals("01001111", 3, ByteUtils.countUnsetBits((byte) 0x4F) );
       assertEquals("01110101", 3, ByteUtils.countUnsetBits((byte) 0x75) );

       // two zero bits:
       assertEquals("11011101", 2, ByteUtils.countUnsetBits((byte) 0xDD) );
       assertEquals("10111110", 2, ByteUtils.countUnsetBits((byte) 0xBE) );
       assertEquals("11110110", 2, ByteUtils.countUnsetBits((byte) 0xF6) );
       assertEquals("01101111", 2, ByteUtils.countUnsetBits((byte) 0x6F) );
       assertEquals("01111101", 2, ByteUtils.countUnsetBits((byte) 0x7E) );

       // one zero bit:
       assertEquals("11111011", 1, ByteUtils.countUnsetBits((byte) 0xFB) );
       assertEquals("11011111", 1, ByteUtils.countUnsetBits((byte) 0xDF) );
       assertEquals("00001011", 1, ByteUtils.countUnsetBits((byte) 0xFE) );

       // no zero bits:
       assertEquals("11111111", 0, ByteUtils.countUnsetBits((byte) 0xFF) );
    }

    
    /**
     * 
     */
    @Test
    public void testGetAllBitmaskForBytes() {
        try {
        	ByteUtils.getAllBitMaskForBytes((byte[]) null);
        	fail("Expected an illegal argument exception on null byte array");
        } catch (IllegalArgumentException expected) {};
    	
    	// only one byte mask matches 11111111 - the bitmask is the same as the byte:
        byte[] bytes = new byte[] {(byte) 0xFF};
        Byte expectedValue = Byte.valueOf((byte) 0xFF);
        assertEquals("11111111", expectedValue, ByteUtils.getAllBitMaskForBytes(bytes));

        // Find all bitmask for odd bytes:
        bytes = getOddBytes();
        expectedValue = Byte.valueOf((byte) 0x01);
        assertEquals("Find all bitmask for odd bytes", expectedValue, ByteUtils.getAllBitMaskForBytes(bytes));

        // no bitmask can match only the zero byte:
        bytes = new byte[] {0};
        assertEquals("00000000 to match 0", null, ByteUtils.getAllBitMaskForBytes(bytes));

        // the zero bitmask can match all byte values:
        bytes = ByteUtils.getAllByteValues();
        expectedValue = Byte.valueOf((byte) 0x00);
        assertEquals("00000000 to match all", expectedValue, ByteUtils.getAllBitMaskForBytes(bytes));

        // 2 bytes match: mask 11111110:  11111110 and 11111111
        bytes = new byte[] {(byte) 0xFE, (byte) 0xFF};
        expectedValue = Byte.valueOf((byte) 0xFE);
        assertEquals("11111110", expectedValue,  ByteUtils.getAllBitMaskForBytes(bytes));

        // no bitmask exists for only the 2 bytes: 11111110 and 01111111
        bytes = new byte[] {(byte) 0xFE, (byte) 0x7F};
        assertEquals("11111110 and 01111111", null, ByteUtils.getAllBitMaskForBytes(bytes));

        // 4 bytes match: mask 01111110: 01111110 01111111 11111110 1111111
        bytes = new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0x7F, (byte) 0x7E};
        expectedValue = Byte.valueOf((byte) 0x7E);
        assertEquals("01111110", expectedValue,  ByteUtils.getAllBitMaskForBytes(bytes));
        
        // No bitmask matches 3 bytes only:
        bytes = new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0x7F};
        assertNull("3 bytes has no bitmask", ByteUtils.getAllBitMaskForBytes(bytes));
    }


    /**
     * Test of getAnyBitMaskForBytes method, of class ByteUtils.
     */
    @Test
    public void testGetAnyBitMaskForBytes() {
    	// test all valid sets:
    	for (int bitmask = 0; bitmask < 256; bitmask++) {
    		Set<Byte> validSet = new HashSet<Byte>();
    		for (int b = 0; b < 256; b++) {
    			if ((((byte) b) & ((byte) bitmask)) != 0) {
    				validSet.add((byte) b);
    			}
    		}
    		byte[] array = ByteUtils.toArray(validSet);
    		Byte result  = ByteUtils.getAnyBitMaskForBytes(array);
    		assertNotNull("result should not be null for bitmask " + bitmask + " size of set " + validSet.size() + " expected " + validSet, result);
    		assertEquals("Set of bytes for bitmask " + bitmask + " size of set " + validSet.size() + " expected " + validSet, (byte) bitmask, result.byteValue());
    	}
    	
    	// test invalid set:
    	Set<Byte> invalidSet = new HashSet<Byte>();
    	final int LEGITIMATE_SIZE = 224;
    	for (int i = 0; i < 90; i++) {
    		if (((byte) i & 0xAA) != 0) {
    			if (invalidSet.size() < LEGITIMATE_SIZE) {
    				invalidSet.add((byte) i);
    			}
    		}
    	}
    	for (int i = 90; i < 256; i++) {
    		if (((byte) i & 0x17) != 0) {
    			if (invalidSet.size() < LEGITIMATE_SIZE) {
    				invalidSet.add((byte) i);
    			}
    		}
    	}
    	Byte result = ByteUtils.getAnyBitMaskForBytes(ByteUtils.toArray(invalidSet));
    	assertNull("Set made of two bitmasks on different bytes is not a correct set", result);
    	
    	// test a set for which there is a valid bitmask, but which has additional bytes that don't
    	// match the bitmask, with the set being a valid size for one that has an any bitmask can generate.
    	invalidSet.clear();
    	for (int i = 128; i < 256; i++) { // all bytes share the topmost bit 128.  1000 0000
    		invalidSet.add((byte) i);
    	}
    	// the next valid size for an any bitmask set, is 192 bytes.  So we need to add 64 bytes
    	// in chunks that don't match each other.
    	for (int i = 0; i < 64; i +=2) {
    		invalidSet.add((byte) i);
    	}
    	for (int i = 1; i < 64; i += 2) {
    		invalidSet.add((byte) i);
    	}
    	result = ByteUtils.getAnyBitMaskForBytes(ByteUtils.toArray(invalidSet));
    	assertNull("Set made of two bitmasks on different bytes is not a correct set", result);
    	
    	// no one any bitmask matches only 11111111:
        byte[] bytes = new byte[] {(byte) 0xFF};
        assertEquals("no any bitmask matches only 0xFF", null, ByteUtils.getAnyBitMaskForBytes(bytes));

        // Get any bitmask for odd values:
        bytes = getOddBytes();
        Byte expectedValue = Byte.valueOf((byte) 0x01);
        assertEquals("Find any bitmask for odd values", expectedValue, ByteUtils.getAnyBitMaskForBytes(bytes));
        
        // no any bit bitmask can match zero:
        bytes = new byte[] {0};
        assertEquals("no any bitmask to match only zero byte", null, ByteUtils.getAnyBitMaskForBytes(bytes));

        // the zero bitmask matches no bytes:
        bytes = ByteUtils.getAllByteValues();
        assertEquals("no any bitmask for all byte values.", null, ByteUtils.getAnyBitMaskForBytes(bytes));

        // no any bitmask for the bytes:  11111110 and 11111111
        bytes = new byte[] {(byte) 0xFE, (byte) 0xFF};
        assertEquals("11111110 and 11111111 have no any bitmask.", null, ByteUtils.getAnyBitMaskForBytes(bytes));

        // Match all values except zero: 0xFF
        expectedValue = Byte.valueOf((byte) 0xFF);
        bytes = ByteUtils.getBytesInRange(1, 255);
        assertEquals("All bytes from 1 to 255 match with 0xFF any bitmask", expectedValue, ByteUtils.getAnyBitMaskForBytes(bytes));
        
        // no bitmask exists for only the 2 bytes: 11111110 and 01111111
        bytes = new byte[] {(byte) 0xFE, (byte) 0x7F};
        assertEquals("11111110 and 01111111", null, ByteUtils.getAnyBitMaskForBytes(bytes));

        // No bitmask matches 3 bytes only:
        bytes = new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0x7F};
        assertNull("3 bytes has no bitmask", ByteUtils.getAnyBitMaskForBytes(bytes));
        
        // any bitmask 01111111 (0x7F) matches all except zero and 0x80.
        bytes = new byte[254];
        int position = 0;
        for (int value = 1; value < 256; value++) {
            if (value != 0x80) {
                bytes[position++] = (byte) value;
            }
        }
        expectedValue = Byte.valueOf((byte) 0x7F);
        assertEquals("01111111 matches all except zero and 0x80", expectedValue, ByteUtils.getAnyBitMaskForBytes(bytes));

        // No any bitmask exists to match only: 01111110 01111111 11111110 1111111
        bytes = new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0x7F, (byte) 0x7E};
        assertEquals("01111110", null, ByteUtils.getAnyBitMaskForBytes(bytes));
    }


    private byte[] getOddBytes() {
        byte[] bytes = new byte[128];
        int position = 0;
        for (int oddValue = 1; oddValue < 256; oddValue += 2) {
            bytes[position++] = (byte) oddValue;
        }
        return bytes;
    }

    /**
     * Test of countBytesMatchingAllBits method, of class ByteUtils.
     */
    @Test
    public void testCountBytesMatchingAllBits() {
        for (int bitmask = 0; bitmask < 256; bitmask++) {
        	int expected = 0;
        	for (int b = 0; b < 256; b++) {
        		if ((((byte) b) & ((byte) bitmask)) == ((byte) bitmask)) {
        			expected++;
        		}
        	}
        	assertEquals("Bytes matching all bits for bitmask " + bitmask, expected, ByteUtils.countBytesMatchingAllBits((byte) bitmask));
        }
    }

    /**
     * Test of countBytesMatchingAnyBit method, of class ByteUtils.
     */
    @Test
    public void testCountBytesMatchingAnyBit() {
        for (int bitmask = 0; bitmask < 256; bitmask++) {
        	int expected = 0;
        	for (int b = 0; b < 256; b++) {
        		if ((((byte) b) & ((byte) bitmask)) != 0) {
        			expected++;
        		}
        	}
        	assertEquals("Bytes matching any bits for bitmask " + bitmask, expected, ByteUtils.countBytesMatchingAnyBit((byte) bitmask));
        }
    }

    
    /**
     * Test of getBytesMatchingAllBitMask method, of class ByteUtils.
     */
    @Test
    public void testGetBytesMatchingAllBitMask() {
    	for (int bitmask = 0; bitmask < 256; bitmask++) {
    		Set<Byte> expected = new HashSet<Byte>(192);
    		for (int b = 0; b < 256; b++) {
    			if ((((byte) b) & ((byte) bitmask)) == (byte) bitmask) {
    				expected.add((byte) b);
    			}
    		}
    		Set<Byte> result = ByteUtils.toSet(ByteUtils.getBytesMatchingAllBitMask((byte) bitmask));
    		assertEquals("Set of bytes matching all bitmask " + bitmask, expected, result);
    	}
    }
    
    
    /**
     * Test of getBytesNotMatchingAllBitMask method, of class ByteUtils.
     */
    @Test
    public void testGetBytesNotMatchingAllBitMask() {
    	for (int bitmask = 0; bitmask < 256; bitmask++) {
    		Set<Byte> expected = new HashSet<Byte>(192);
    		for (int b = 0; b < 256; b++) {
    			if ((((byte) b) & ((byte) bitmask)) != (byte) bitmask) {
    				expected.add((byte) b);
    			}
    		}
    		Set<Byte> result = ByteUtils.toSet(ByteUtils.getBytesNotMatchingAllBitMask((byte) bitmask));
    		assertEquals("Set of bytes not matching all bitmask " + bitmask, expected, result);
    	}
    }
    
    /**
     * Test of addBytesMatchingAllBitMask method, of class ByteUtils.
     */
    @Test
    public void testaddBytesMatchingAllBitMask() {
    	try {
    		ByteUtils.addBytesMatchingAllBitMask((byte) 0x00, null);
    		fail("Collection passed in cannot be null");
    	} catch (IllegalArgumentException expect) {}
    	
    	for (int bitmask = 0; bitmask < 256; bitmask++) {
    		Set<Byte> expected = new HashSet<Byte>(192);
    		for (int b = 0; b < 256; b++) {
    			if ((((byte) b) & ((byte) bitmask)) == (byte) bitmask) {
    				expected.add((byte) b);
    			}
    		}
    		Set<Byte> result = new HashSet<Byte>(192);
    		ByteUtils.addBytesMatchingAllBitMask((byte) bitmask, result);
    		assertEquals("Set of bytes matching all bitmask " + bitmask, expected, result);
    	}
    }
    
    /**
     * Test of addBytesNotMatchingAllBitMask method, of class ByteUtils.
     */
    @Test
    public void testaddBytesNotMatchingAllBitMask() {
    	try {
    		ByteUtils.addBytesNotMatchingAllBitMask((byte) 0x00, null);
    		fail("Collection passed in cannot be null");
    	} catch (IllegalArgumentException expected) {};
    	
    	for (int bitmask = 0; bitmask < 256; bitmask++) {
    		Set<Byte> expected = new HashSet<Byte>(192);
    		for (int b = 0; b < 256; b++) {
    			if ((((byte) b) & ((byte) bitmask)) != (byte) bitmask) {
    				expected.add((byte) b);
    			}
    		}
    		Set<Byte> result = new HashSet<Byte>(192);
    		ByteUtils.addBytesNotMatchingAllBitMask((byte) bitmask, result);
    		assertEquals("Set of bytes not matching all bitmask " + bitmask, expected, result);
    	}
    }
    
    
    
    /**
     * Test of getBytesMatchingAllBitMask method, of class ByteUtils.
     */
    @Test
    public void testGetBytesMatchingAnyBitMask() {
    	for (int bitmask = 0; bitmask < 256; bitmask++) {
    		Set<Byte> expected = new HashSet<Byte>(192);
    		for (int b = 0; b < 256; b++) {
    			if ((((byte) b) & ((byte) bitmask)) != 0) {
    				expected.add((byte) b);
    			}
    		}
    		Set<Byte> result = ByteUtils.toSet(ByteUtils.getBytesMatchingAnyBitMask((byte) bitmask));
    		assertEquals("Set of bytes matching any bitmask " + bitmask, expected, result);
    	}
    }


    /**
     * Test of getBytesMatchingAllBitMask method, of class ByteUtils.
     */
    @Test
    public void testGetBytesNotMatchingAnyBitMask() {
    	for (int bitmask = 0; bitmask < 256; bitmask++) {
    		Set<Byte> expected = new HashSet<Byte>(192);
    		for (int b = 0; b < 256; b++) {
    			if ((((byte) b) & ((byte) bitmask)) == 0) {
    				expected.add((byte) b);
    			}
    		}
    		Set<Byte> result = ByteUtils.toSet(ByteUtils.getBytesNotMatchingAnyBitMask((byte) bitmask));
    		assertEquals("Set of bytes not matching any bitmask " + bitmask, expected, result);
    	}
    }
    
    
    /**
     * Test of addBytesMatchingAllBitMask method, of class ByteUtils.
     */
    @Test
    public void testaddBytesMatchingAnyBitMask() {
    	try {
    		ByteUtils.addBytesMatchingAnyBitMask((byte) 0x00, null);
    		fail("Collection passed in cannot be null");
    	} catch (IllegalArgumentException expected) {};
    	
    	for (int bitmask = 0; bitmask < 256; bitmask++) {
    		Set<Byte> expected = new HashSet<Byte>(192);
    		for (int b = 0; b < 256; b++) {
    			if ((((byte) b) & ((byte) bitmask)) != 0) {
    				expected.add((byte) b);
    			}
    		}
    		Set<Byte> result = new HashSet<Byte>(192);
    		ByteUtils.addBytesMatchingAnyBitMask((byte) bitmask, result);
    		assertEquals("Set of bytes matching any bitmask " + bitmask, expected, result);
    	}
    }
    
    /**
     * Test of addBytesNotMatchingAllBitMask method, of class ByteUtils.
     */
    @Test
    public void testaddBytesNotMatchingAnyBitMask() {
    	try {
    		ByteUtils.addBytesNotMatchingAnyBitMask((byte) 0x00, null);
    		fail("Collection passed in cannot be null");
    	} catch (IllegalArgumentException expected) {};
    	
    	for (int bitmask = 0; bitmask < 256; bitmask++) {
    		Set<Byte> expected = new HashSet<Byte>(192);
    		for (int b = 0; b < 256; b++) {
    			if ((((byte) b) & ((byte) bitmask)) == 0) {
    				expected.add((byte) b);
    			}
    		}
    		Set<Byte> result = new HashSet<Byte>(192);
    		ByteUtils.addBytesNotMatchingAnyBitMask((byte) bitmask, result);
    		assertEquals("Set of bytes not matching any bitmask " + bitmask, expected, result);
    	}
    }
    
    
    @Test
    public void testAddAllBytes() {
    	try {
    		ByteUtils.addAllBytes(null);
    		fail("Collection passed in cannot be null");
    	} catch (IllegalArgumentException expect) {}
    	
    	Set<Byte> result = new HashSet<Byte>(288);
    	ByteUtils.addAllBytes(result);
    	assertEquals("size of set is 256", 256, result.size());
    	
    	result.remove(Byte.valueOf((byte) 20)); 
    	ByteUtils.addAllBytes(result);
    	assertEquals("size of set is 256", 256, result.size());
    }

    @Test
    public void testToList() {
    	try {
    		ByteUtils.toList(null);
    		fail("Array passed in cannot be null");
    	} catch (IllegalArgumentException expected) {};
    	
    	Random r = new Random();
    	for (int i = 0; i <256; i++) {
    		byte[] test = new byte[i];
    		for (int j = 0; j < i; j++) {
    			test[j] = (byte) r.nextInt(256);
    		}
    		List<Byte> result = ByteUtils.toList(test);
    		assertEquals("List and array are same size", test.length, result.size());
    		for (int k = 0; k < test.length; k++) {
    			assertEquals("List and array values are the same", test[k], result.get(k).byteValue());
    		}
    	}
    }
    
    @Test
    public void testGetBytesInRange() {
    	try {
    		ByteUtils.getBytesInRange(-1, 3);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException expected) {};
    	
    	try {
    		ByteUtils.getBytesInRange(1, -3);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException expected) {};
    	
    	try {
    		ByteUtils.getBytesInRange(1, 257);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException expected) {};
    	
    	try {
    		ByteUtils.getBytesInRange(257, -1);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException expected) {};

    	for (int i = 0; i < 256; i++) {
    		for (int j = 0; j< 256; j++) {
    			final int start = i < j? i : j;
    			final int end   = i < j? j : i;
    			final int length = end - start + 1;
    			final byte[] expected = new byte[length];
    			for (int k = 0; k < length; k++) {
    				expected[k] = (byte) (k + start);
    			}
    			byte[] result = ByteUtils.getBytesInRange(i,  j);
    			assertTrue("Byte range from " + i + " to " + j, Arrays.equals(expected, result));
    		}
    	}
    }
   
    @Test
    public void testAddBytesInRange() {
    	try {
    		ByteUtils.addBytesInRange(-1, 3, null);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException expected) {};
    	
    	List<Byte> expected = new ArrayList<Byte>();
    	try {
    		ByteUtils.addBytesInRange(-1, 3, expected);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException ex) {};
    	
    	try {
    		ByteUtils.addBytesInRange(1, -3, expected);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException ex) {};
    	
    	try {
    		ByteUtils.addBytesInRange(1, 257, expected);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException ex) {};
    	
    	try {
    		ByteUtils.addBytesInRange(257, -1, expected);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException ex) {};

    	for (int i = 0; i < 256; i++) {
    		for (int j = 0; j< 256; j++) {
    			final int start = i < j? i : j;
    			final int end   = i < j? j : i;
    			final int length = end - start + 1;
    			expected.clear();
    			for (int k = 0; k < length; k++) {
    				expected.add((byte) (k + start));
    			}
    			List<Byte> result = new ArrayList<Byte>();
    			ByteUtils.addBytesInRange(i,  j, result);
    			assertTrue("Byte range from " + i + " to " + j, expected.equals(result));
    		}
    	}
    }

    @Test
    public void testAddBytesNotInRange() {
    	try {
    		ByteUtils.addBytesNotInRange(-1, 3, null);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException expected) {};
    	
    	Set<Byte> expected = new HashSet<Byte>();
    	try {
    		ByteUtils.addBytesNotInRange(-1, 3, expected);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException ex) {};
    	
    	try {
    		ByteUtils.addBytesNotInRange(1, -3, expected);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException ex) {};
    	
    	try {
    		ByteUtils.addBytesNotInRange(1, 257, expected);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException ex) {};
    	
    	try {
    		ByteUtils.addBytesNotInRange(257, -1, expected);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException ex) {};

    	for (int i = 0; i < 256; i++) {
    		for (int j = 0; j< 256; j++) {
    			final int start = i < j? i : j;
    			final int end   = i < j? j : i;
    			expected.clear();
    			for (int k = 0; k < start; k++) {
    				expected.add((byte) k);
    			}
    			for (int k = end + 1; k < 256; k++) {
    				expected.add((byte) k);
    			}
    			Set<Byte> result = new HashSet<Byte>();
    			ByteUtils.addBytesNotInRange(i,  j, result);
    			assertTrue("Inverse Byte range from " + i + " to " + j, expected.equals(result));
    		}
    	}
    }

    
    @Test
    public void testAddInvertedByteValues() {
    	try {
    		ByteUtils.addInvertedByteValues((byte) 1, null);
    		fail("Expected illegal argument exception");
    	} catch (IllegalArgumentException expected) {};
    	
    	for (int i = 0; i < 256; i++ ) {
    		Set<Byte> bytes = new HashSet<Byte>();
    		ByteUtils.addInvertedByteValues((byte) i, bytes);
    		assertEquals("Size of set is 255", 255, bytes.size());
    		assertTrue("byte value not in set", !bytes.contains((byte) i));
    	}
    }
    
    @Test
    public void testAddBytes() {
    	List<Byte> bytes = new ArrayList<Byte>();
    	ByteUtils.addBytes(bytes, (byte) 0x01);
    	assertEquals("Size is now one", 1, bytes.size());
    	assertEquals("first byte is one", Byte.valueOf((byte) 0x01), bytes.get(0));
    	
    	ByteUtils.addBytes(bytes, (byte) 0x01, (byte) 0xFF, (byte) 0x80);
    	assertEquals("Size is now 4", 4, bytes.size());
    	assertEquals("first byte is one", Byte.valueOf((byte) 0x01), bytes.get(0));
    	assertEquals("second byte is one", Byte.valueOf((byte) 0x01), bytes.get(1));
    	assertEquals("third byte is 0xFF", Byte.valueOf((byte) 0xFF), bytes.get(2));
    	assertEquals("fourth byte is 0x80", Byte.valueOf((byte) 0x80), bytes.get(3));
    }
    
    
    @Test
    public void testAddStringBytes() throws UnsupportedEncodingException {
    	try {
    		ByteUtils.addStringBytes(null, new ArrayList<Byte>());
    		fail("String passed in cannot be null");
    	} catch (IllegalArgumentException expect) {}
    	try {
    		ByteUtils.addStringBytes("A string", null);
    		fail("Collection passed in cannot be null");
    	} catch (IllegalArgumentException expect) {}

    	testAddStringBytes("");
    	testAddStringBytes("0123456789");
    	testAddStringBytes("ABCXYZ");
    	testAddStringBytes("ABC\u00C0XYZ");
    }
    
    
    private void testAddStringBytes(String string) throws UnsupportedEncodingException {
    	byte[] stringBytes = string.getBytes("ISO-8859-1");
    	List<Byte> result = new ArrayList<Byte>();
    	ByteUtils.addStringBytes(string, result);
    	for (int i = 0; i < string.length(); i++) {
    		assertEquals("String byte is correct " + stringBytes[i], stringBytes[i], result.get(i).byteValue());
    	}
    }
    
    
    @Test
    public void testCaseAddStringBytes() throws UnsupportedEncodingException {
    	try {
    		ByteUtils.addCaseInsensitiveStringBytes(null, new ArrayList<Byte>());
    		fail("String passed in cannot be null");
    	} catch (IllegalArgumentException expect) {}
    	try {
    		ByteUtils.addCaseInsensitiveStringBytes("A string", null);
    		fail("Collection passed in cannot be null");
    	} catch (IllegalArgumentException expect) {}

    	testCaseAddStringBytes("");
    	testCaseAddStringBytes("0123456789");
    	testCaseAddStringBytes("abcxyz");
    	testCaseAddStringBytes("ABC[XYZ");
    	testCaseAddStringBytes("A@BC\u00C0X`{YZ");
    }
    
    
    private void testCaseAddStringBytes(String string) throws UnsupportedEncodingException {
    	byte[] stringBytes = string.getBytes("ISO-8859-1");
    	List<Byte> expected = new ArrayList<Byte>();
    	for (int i = 0; i < stringBytes.length; i++) {
    		final byte byteValue = stringBytes[i];
    		if (byteValue >= 'a' && byteValue <= 'z') {
    			expected.add(Byte.valueOf((byte) (byteValue - 32)));
    		} else
    		if (byteValue >= 'A' && byteValue <= 'Z') {
    			expected.add(Byte.valueOf((byte) (byteValue + 32)));
    		}
    		expected.add(Byte.valueOf(byteValue));
    	}
    	List<Byte> result = new ArrayList<Byte>();
    	ByteUtils.addCaseInsensitiveStringBytes(string, result);
    	assertEquals("Result string " + string + " has correct size " + expected.size(), expected.size(), result.size());
    	for (int i = 0; i < expected.size(); i++) {
    		assertEquals("String byte for string " + string + " is correct " + i + " with value " + expected.get(i), expected.get(i), result.get(i));
    	}
    }
    
    @Test
    public void testToArray() {
    	try {
    		ByteUtils.toArray((List<Byte>) null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	
    	assertTrue("Arrays are equal", Arrays.equals(new byte[] {(byte) 0x01}, ByteUtils.toArray((byte) 0x01)));
    	assertTrue("Arrays are equal", Arrays.equals(new byte[] {(byte) 0xFF, (byte) 0x3c, (byte) 0x02}, ByteUtils.toArray((byte) 0xff, (byte) 0x3c, (byte) 0x02)));
    }
    
    @Test
    public void testReverseArray() {
    	try {
    		ByteUtils.reverseArray(null);
    		fail("Array passed in cannot be null");
    	} catch (IllegalArgumentException expect) {}
    	
    	testReverseArray(new byte[0]);
    	testReverseArray(new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03});
    	testReverseArray(new byte[] {(byte) 0xff, (byte) 0x80, (byte) 0x01});
    }

    private void testReverseArray(byte[] array) {
    	byte[] expected = new byte[array.length];
    	for (int i = 0; i < array.length; i++) {
    		expected[i] = array[array.length - 1 - i];
    	}
    	assertTrue("Arrays are reversed correctly", Arrays.equals(expected, ByteUtils.reverseArray(array)));
    }
    
    @Test
    public void testReverseArraySubsequence() {
    	try {
    		ByteUtils.reverseArraySubsequence(null, 0, 1);
    		fail("Array passed in cannot be null");
    	} catch (IllegalArgumentException expect) {}

    	try {
    		ByteUtils.reverseArraySubsequence(new byte[0], -1, 0);
    		fail("Start index cannot be less than zero");
    	} catch (IllegalArgumentException expect) {}

    	try {
    		ByteUtils.reverseArraySubsequence(new byte[] {(byte) 0x01, (byte) 0x02}, 2, 2);
    		fail("Start index must be less than length");
    	} catch (IllegalArgumentException expect) {}
    	
    	try {
    		ByteUtils.reverseArraySubsequence(new byte[] {(byte) 0x01, (byte) 0x02}, 1, 0);
    		fail("Start index must be less than end index");
    	} catch (IllegalArgumentException expect) {}

    	try {
    		ByteUtils.reverseArraySubsequence(new byte[] {(byte) 0x01, (byte) 0x02}, 0, 4);
    		fail("End index cannot be greater than length");
    	} catch (IllegalArgumentException expect) {}
   	
    	testReverseArraySubsequence(new byte[0]);
    	testReverseArraySubsequence(new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03});
    	testReverseArraySubsequence(new byte[] {(byte) 0xff, (byte) 0x80, (byte) 0x01});
    	testReverseArraySubsequence(new byte[] {(byte) 0xff, (byte) 0x80, (byte) 0x01,
    											(byte) 0x3c, (byte) 0x20, (byte) 0xfe});
    }

    private void testReverseArraySubsequence(byte[] array) {
    	final int length = array.length;
    	for (int i = 0; i < length; i++) {
    		for (int j = i + 1; j <= length; j++) {
    			byte[] reversed = ByteUtils.reverseArraySubsequence(array,  i,  j);
    			final int sublength = j - i;
    			assertEquals("Length of sub sequence is " + sublength, sublength, reversed.length);
    			for (int k = 0; k < reversed.length; k++) {
    				final byte expected = array[j - k - 1];
    				assertEquals("Byte value of reversed sequence is " + expected, expected, reversed[k]);
    			}
    		}
    	}
    }

    @Test
    public void testRepeatByte() {
    	try {
    		ByteUtils.repeat((byte) 0x01, -1);
    		fail("Number of repeats cannot be negative");
    	} catch (IllegalArgumentException expect) {}
    	
    	for (int i = 0; i < 256; i++) {
    		for (int j = 0; j < 256; j++) {
    			byte[] repeated = ByteUtils.repeat((byte) i, j);
    			assertEquals("Repeated size is " + j, j, repeated.length);
    			for (int k = 0; k < j; k++) {
    				assertEquals("Byte value is " + i + " at position " + k, (byte) i, repeated[k]);
    			}
    		}
    	}
    }
    
    @Test
    public void testRepeatArray() {
    	try {
    		ByteUtils.repeat(1, null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.repeat(-1, new byte[] {(byte) 0x01});
    		fail("Number of repeats cannot be negative");
    	} catch (IllegalArgumentException expect) {}
    	
    	testRepeatArray((byte) 0x01);
    	testRepeatArray((byte) 0x01, (byte) 0xff, (byte) 0xde, (byte) 0xed);
    }
    
    private void testRepeatArray(byte... values) {
    	for (int repeat = 0; repeat < 10; repeat++) {
    		byte[] result = ByteUtils.repeat(repeat, values);
    		assertEquals("Length of repeated array is " + repeat * values.length, repeat * values.length, result.length);
    		for (int testRepeat = 0; testRepeat < repeat; testRepeat++) {
    			for (int testRun = 0; testRun < values.length; testRun++) {
    				final int byteValue = values[testRun];
    				assertEquals("Byte value is correct " + byteValue, byteValue, result[testRepeat * values.length + testRun]);
    			}
    		}
    	}
    }
    
    
    @Test
    public void testRepeatArraySubsequence() {
    	try {
    		ByteUtils.repeat(1, null, 0, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.repeat(-1, new byte[] {(byte) 0x01}, 0, 1);
    		fail("Number of repeats cannot be negative");
    	} catch (IllegalArgumentException expect) {}
    	
    	testRepeatArraySubsequence((byte) 0x01);
    	testRepeatArraySubsequence((byte) 0x01, (byte) 0xff, (byte) 0xde, (byte) 0xed);
    }
    
    private void testRepeatArraySubsequence(byte... values) {
    	int length = values.length;
    	try {
    		ByteUtils.repeat(1, values, -1, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.repeat(1, values, 0, length + 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expect) {}
    	try {
    		ByteUtils.repeat(1, values, length, length);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expect) {}
    	
    	for (int subStart = 0; subStart < length; subStart++) {
    		for (int subEnd = subStart + 1; subEnd <= length; subEnd++) {
    			int subLength = subEnd - subStart;
    	    	for (int repeat = 0; repeat < 10; repeat++) {
    	    		byte[] result = ByteUtils.repeat(repeat, values, subStart, subEnd);
    	    		assertEquals("Length of repeated array is " + repeat * subLength, repeat * subLength, result.length);
    	    		int resultPos = 0;
    	    		for (int testRepeat = 0; testRepeat < repeat; testRepeat++) {
    	    			for (int testRun = subStart; testRun < subEnd; testRun++) {
    	    				final int byteValue = values[testRun];
    	    				assertEquals("Byte value is correct " + byteValue + " for pos " + testRun + " length " + subLength +
    	    						     " repeat " + testRepeat + " sub start" + subStart + " sub end " + subEnd,
    	    						     byteValue, result[resultPos++]);
    	    			}
    	    		}
    	    	}
    		}
    	}
    }
    
    @Test
    public void testToIntArray() {
    	// test null byte array
    	try {
    		ByteUtils.toIntArray(null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    	
    	
    	// test byte array of zero length
    	int[] result = ByteUtils.toIntArray(new byte[0]);
    	assertEquals("Length of array is zero", 0, result.length);
    	
    	// test arbitrary byte arrays:
    	testToIntArray((byte) 0x01);
    	testToIntArray((byte) 0xFF, (byte) 0xc3, (byte) 0x00, (byte) 0x4f, (byte) 0xd9);
    }
    
    private void testToIntArray(byte... values) {
    	int[] result = ByteUtils.toIntArray(values);
    	assertEquals("Lengths are the same", values.length, result.length);
    	for (int i = 0; i < values.length; i++) {
    		int resultValue = result[i];
    		int expectedValue = values[i] & 0xFF;
    		assertEquals("Values are correct value for pos " + i, expectedValue, resultValue);
    	}
    }
    
    @Test
    public void testInvertedSet() {
    	// test null set
    	try {
    		ByteUtils.invertedSet(null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}   
    
	    // Test sets from 0 to 255 elements:
	    Set<Byte> testSet = new HashSet<Byte>();
	    for (int i = 0; i < 256; i++) {
	    	Set<Byte> result = ByteUtils.invertedSet(testSet);
	    	assertTrue("Set lengths are complementary", testSet.size() + result.size() == 256);
	    	result.addAll(testSet);
	    	assertEquals("Combining members gives all byte values", 256, result.size());
	    	testSet.add(Byte.valueOf((byte) i));
	    }
    }
    
    @Test
    public void testInverseOf() {
    	// test null sets
    	try {
    		ByteUtils.inverseOf(null, new HashSet<Byte>());
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       

    	try {
    		ByteUtils.inverseOf(new HashSet<Byte>(), null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       
    	
    	try {
    		ByteUtils.inverseOf(null, null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       
    	
    	Set<Byte> testSet = new HashSet<Byte>();
    	for (int i = 0; i < 256; i++) {
    		Set<Byte> inverse = ByteUtils.invertedSet(testSet);
    		assertTrue("Sets are the inverse of each other", ByteUtils.inverseOf(testSet, inverse));
    		assertTrue("Sets are the inverse of each other", ByteUtils.inverseOf(inverse, testSet));
    		
    		testSet.add(Byte.valueOf((byte) i));
    		assertFalse("Sets are the inverse of each other", ByteUtils.inverseOf(testSet, inverse));
    		assertFalse("Sets are the inverse of each other", ByteUtils.inverseOf(inverse, testSet));

    		inverse.remove(Byte.valueOf((byte) ( 255 - i)));
    		assertFalse("Sets are the inverse of each other " + i, ByteUtils.inverseOf(testSet, inverse));
    		assertFalse("Sets are the inverse of each other " + i, ByteUtils.inverseOf(inverse, testSet));
    	}
    	
    }
    
    @Test
    public void testInvertedSetFromByte() {
    	for (int byteValue = 0; byteValue < 256; byteValue++) {
    		Set<Byte> result = ByteUtils.invertedSet((byte) byteValue);
    		assertEquals("Only 255 elements in a set minus one byte " + byteValue, 255, result.size());
    		assertFalse("Set does not contain the byte value", result.contains(Byte.valueOf((byte) byteValue)));
    	}
    }
    
    @Test
    public void testRemoveIntersection1NullSets() {
    	try {
    		ByteUtils.removeIntersection(null, new HashSet<Byte>());
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       

    	try {
    		ByteUtils.removeIntersection(new HashSet<Byte>(), null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       
    	
    	try {
    		ByteUtils.removeIntersection(null, null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    }
    
    @Test
    public void testRemoveIntersection1EmptySets() {
    	Set<Byte> set1 = new HashSet<Byte>();
    	Set<Byte> set2 = new HashSet<Byte>();
    	List<Byte> result = ByteUtils.removeIntersection(set1, set2);
    	assertTrue("Set 1 is still empty", set1.isEmpty());
    	assertTrue("Set 2 is still empty", set2.isEmpty());
    	assertTrue("Intersection is empty", result.isEmpty());
    }
    
    @Test
    public void testRemoveIntersection1NonIntersectingSets() {
    	testRemoveIntersection1NonIntersecting(new HashSet<Byte>(), ByteUtils.toSet(ByteUtils.getAllByteValues()));
    	testRemoveIntersection1NonIntersecting(toSet(1, 2, 3, 4), toSet(5, 6, 7, 8));
    	testRemoveIntersection1NonIntersecting(toSet(255, 254, 1 ,2), toSet( 253, 80, 3, 4, 5, 6, 7, 8));
    }
    
    private void testRemoveIntersection1NonIntersecting(Set<Byte> set1, Set<Byte> set2) {
		Set<Byte> set1Copy = new HashSet<Byte>(set1);
		Set<Byte> set2Copy = new HashSet<Byte>(set2);
    	List<Byte> result = ByteUtils.removeIntersection(set1,  set2);
		assertTrue("No intersecting bytes expected", result.size() == 0);
		assertTrue("Set 1 is unchanged", set1.equals(set1Copy));
		assertTrue("Set 2 is unchanged", set2.equals(set2Copy));
	}

	@Test
    public void testRemoveIntersection1IntersectingSets() {
    	testRemoveIntersection1IntersectionSets(ByteUtils.toSet(ByteUtils.getAllByteValues()),
    											ByteUtils.toSet(ByteUtils.getAllByteValues()),
    											ByteUtils.toList(ByteUtils.getAllByteValues()));
    	testRemoveIntersection1IntersectionSets(toSet(1, 2, 3, 4, 5, 6), 
    											toSet(4, 5, 6, 7, 8),
    											toList(4, 5, 6));
    	testRemoveIntersection1IntersectionSets(toSet(1, 2, 3, 4, 5, 6, 255, 34, 25, 75), 
												toSet(4, 5, 6, 7, 8, 34, 33, 32, 96, 255, 32),
												toList(4, 5, 6, 34, 255));
    }
	
    private void testRemoveIntersection1IntersectionSets(Set<Byte> set1, Set<Byte> set2, List<Byte> expectedIntersection) {
		assertTrue("Set 1 does contain intersection values", CollUtils.containsAny(set1, expectedIntersection));
		assertTrue("Set 2 does contain intersection values", CollUtils.containsAny(set2, expectedIntersection));
    	List<Byte> intersection = ByteUtils.removeIntersection(set1,  set2);
    	assertEquals("Intersection is correct size", expectedIntersection.size(), intersection.size());
		assertTrue("Intersection has correct values", intersection.containsAll(expectedIntersection));
		assertFalse("Set 1 does not contain any intersection values", CollUtils.containsAny(set1, intersection));
		assertFalse("Set 2 does not contain any intersection values", CollUtils.containsAny(set2, intersection));
	}
    
    @Test
    public void testRemoveIntersection2NullSets() {
    	// test null collections
    	try {
    		ByteUtils.removeIntersection(null, new HashSet<Byte>(), new ArrayList<Byte>());
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       

    	try {
    		ByteUtils.removeIntersection(new HashSet<Byte>(), null, new ArrayList<Byte>());
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       
    	
    	try {
    		ByteUtils.removeIntersection(null, null, new ArrayList<Byte>());
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	
    	try {
    		ByteUtils.removeIntersection(new HashSet<Byte>(), new HashSet<Byte>(), null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       
    	
    	try {
    		ByteUtils.removeIntersection(null, new HashSet<Byte>(), null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       

    	try {
    		ByteUtils.removeIntersection(new HashSet<Byte>(), null, null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       
    	
    	try {
    		ByteUtils.removeIntersection(null, null, null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}       	
    }
    
    
    @Test
    public void testRemoveIntersection2EmptySets() {
    	Set<Byte> set1 = new HashSet<Byte>();
    	Set<Byte> set2 = new HashSet<Byte>();
    	List<Byte> intersectionBytes = new ArrayList<Byte>();
    	ByteUtils.removeIntersection(set1, set2, intersectionBytes);
    	assertTrue("Set 1 is still empty", set1.isEmpty());
    	assertTrue("Set 2 is still empty", set2.isEmpty());
    	assertTrue("Intersection is empty", intersectionBytes.isEmpty());
    }
    
    @Test
    public void testRemoveIntersection2NonIntersectingSets() {
    	testRemoveIntersection2NonIntersecting(new HashSet<Byte>(), ByteUtils.toSet(ByteUtils.getAllByteValues()));
    	testRemoveIntersection2NonIntersecting(toSet(1, 2, 3, 4), toSet(5, 6, 7, 8));
    	testRemoveIntersection2NonIntersecting(toSet(255, 254, 1 ,2), toSet( 253, 80, 3, 4, 5, 6, 7, 8));
    }
    
    private void testRemoveIntersection2NonIntersecting(Set<Byte> set1, Set<Byte> set2) {
		List<Byte> intersection = new ArrayList<Byte>();
    	Set<Byte> set1Copy = new HashSet<Byte>(set1);
		Set<Byte> set2Copy = new HashSet<Byte>(set2);
    	ByteUtils.removeIntersection(set1,  set2, intersection);
		assertTrue("No intersecting bytes expected", intersection.size() == 0);
		assertTrue("Set 1 is unchanged", set1.equals(set1Copy));
		assertTrue("Set 2 is unchanged", set2.equals(set2Copy));
	}
    
	@Test
    public void testRemoveIntersection2IntersectingSets() {
    	testRemoveIntersection2IntersectionSets(ByteUtils.toSet(ByteUtils.getAllByteValues()),
    											ByteUtils.toSet(ByteUtils.getAllByteValues()),
    											ByteUtils.toList(ByteUtils.getAllByteValues()));
    	testRemoveIntersection2IntersectionSets(toSet(1, 2, 3, 4, 5, 6), 
    											toSet(4, 5, 6, 7, 8),
    											toList(4, 5, 6));
    	testRemoveIntersection2IntersectionSets(toSet(1, 2, 3, 4, 5, 6, 255, 34, 25, 75), 
												toSet(4, 5, 6, 7, 8, 34, 33, 32, 96, 255, 32),
												toList(4, 5, 6, 34, 255));
    }
	
    private void testRemoveIntersection2IntersectionSets(Set<Byte> set1, Set<Byte> set2, List<Byte> expectedIntersection) {
		assertTrue("Set 1 does contain intersection values", CollUtils.containsAny(set1, expectedIntersection));
		assertTrue("Set 2 does contain intersection values", CollUtils.containsAny(set2, expectedIntersection));
    	List<Byte> intersection = new ArrayList<Byte>();
    	ByteUtils.removeIntersection(set1,  set2, intersection);
    	assertEquals("Intersection is correct size", expectedIntersection.size(), intersection.size());
		assertTrue("Intersection has correct values", intersection.containsAll(expectedIntersection));
		assertFalse("Set 1 does not contain any intersection values", CollUtils.containsAny(set1, intersection));
		assertFalse("Set 2 does not contain any intersection values", CollUtils.containsAny(set2, intersection));
	}
    
    @Test
    public void testFloorLogBase2() {
    	// Number must be positive.
    	try {
    		ByteUtils.floorLogBaseTwo(0);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
    	try {
    		ByteUtils.floorLogBaseTwo(-1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
    	try {
    		ByteUtils.floorLogBaseTwo(-65537);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}        	
    	
    	assertEquals("floor log base 2", 0, ByteUtils.floorLogBaseTwo(1));
    	assertEquals("floor log base 2", 1, ByteUtils.floorLogBaseTwo(2));
    	assertEquals("floor log base 2", 1, ByteUtils.floorLogBaseTwo(3));
    	assertEquals("floor log base 2", 2, ByteUtils.floorLogBaseTwo(4));
    	assertEquals("floor log base 2", 2, ByteUtils.floorLogBaseTwo(5));
    	assertEquals("floor log base 2", 4, ByteUtils.floorLogBaseTwo(31));
    	assertEquals("floor log base 2", 5, ByteUtils.floorLogBaseTwo(32));
    	assertEquals("floor log base 2", 5, ByteUtils.floorLogBaseTwo(33));
    	assertEquals("floor log base 2", 5, ByteUtils.floorLogBaseTwo(63));
    	assertEquals("floor log base 2", 6, ByteUtils.floorLogBaseTwo(64));
    	assertEquals("floor log base 2", 6, ByteUtils.floorLogBaseTwo(65));
    	assertEquals("floor log base 2", 6, ByteUtils.floorLogBaseTwo(127));
    	assertEquals("floor log base 2", 7, ByteUtils.floorLogBaseTwo(128));
    	assertEquals("floor log base 2", 7, ByteUtils.floorLogBaseTwo(129));
    	assertEquals("floor log base 2", 7, ByteUtils.floorLogBaseTwo(255));
    }
    
    
    @Test
    public void testCeilLogBase2() {
    	// Number must be positive.
    	try {
    		ByteUtils.ceilLogBaseTwo(0);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
    	try {
    		ByteUtils.ceilLogBaseTwo(-1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
    	try {
    		ByteUtils.ceilLogBaseTwo(-65537);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}        	
    	
    	assertEquals("ceil log base 2", 0, ByteUtils.ceilLogBaseTwo(1));
    	assertEquals("ceil log base 2", 1, ByteUtils.ceilLogBaseTwo(2));
    	assertEquals("ceil log base 2", 2, ByteUtils.ceilLogBaseTwo(3));
    	assertEquals("ceil log base 2", 2, ByteUtils.ceilLogBaseTwo(4));
    	assertEquals("ceil log base 2", 3, ByteUtils.ceilLogBaseTwo(5));
    	assertEquals("ceil log base 2", 5, ByteUtils.ceilLogBaseTwo(31));
    	assertEquals("ceil log base 2", 5, ByteUtils.ceilLogBaseTwo(32));
    	assertEquals("ceil log base 2", 6, ByteUtils.ceilLogBaseTwo(33));
    	assertEquals("ceil log base 2", 6, ByteUtils.ceilLogBaseTwo(63));
    	assertEquals("ceil log base 2", 6, ByteUtils.ceilLogBaseTwo(64));
    	assertEquals("ceil log base 2", 7, ByteUtils.ceilLogBaseTwo(65));
    	assertEquals("ceil log base 2", 7, ByteUtils.ceilLogBaseTwo(127));
    	assertEquals("ceil log base 2", 7, ByteUtils.ceilLogBaseTwo(128));
    	assertEquals("ceil log base 2", 8, ByteUtils.ceilLogBaseTwo(129));
    	assertEquals("ceil log base 2", 8, ByteUtils.ceilLogBaseTwo(255));
    }
    
    @Test
    public void testIsPowerOfTwo() {
    	int[] powersOfTwo = new int[] {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768,
    			                       65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216,
    			                       33554432, 67108864, 134217728, 268435456, 536870912, 1073741824};
    	
    	for (int power : powersOfTwo) {
    		assertTrue("Number is power of two " + power, ByteUtils.isPowerOfTwo(power));
    		if (power != 2) {
    			assertFalse("Number minus one is not power of two " + power, ByteUtils.isPowerOfTwo(power - 1));
    		}
    		if (power != 1) {
    			assertFalse("Number plus one is not power of two " + power, ByteUtils.isPowerOfTwo(power + 1));
    		}
    	}
    }
    
    @Test
    public void testNextHighestPowerOfTwo() {
    	int[] powersOfTwo = new int[] {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768,
                65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216,
                33554432, 67108864, 134217728, 268435456, 536870912, 1073741824}; 
    	final int HIGHEST_POWER = 1073741824;
    	for (int index = 0; index < powersOfTwo.length; index++) {
    		final int power = powersOfTwo[index];
    		if (power > 1) {
    			assertEquals("Next highest power of two below power " + power, power, ByteUtils.nextHighestPowerOfTwo(power -1));
    		}
    		if (power < HIGHEST_POWER) {
    			assertEquals("Next highest power of power is next entry " + power, powersOfTwo[index+1], ByteUtils.nextHighestPowerOfTwo(power));
    			if (power > 1) {
    				assertEquals("Next highest power of power + 1 is next entry " + power, powersOfTwo[index+1], ByteUtils.nextHighestPowerOfTwo(power + 1));
    			}
    		}
    	}
    }
    
    @Test
    public void testBitsInCommon() {
    	// null collection
    	try {
    		ByteUtils.getBitsInCommon(null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
    	
    	// empty collection
    	assertEquals("Bits in common for empty set is zero", 0, ByteUtils.getBitsInCommon(new ArrayList<Byte>()));
    	
    	// single byte
    	for (int i = 0; i < 256; i++ ) {
    		List<Byte> byteList = new ArrayList<Byte>();
    		byteList.add((byte) i);
    		assertEquals("Bits in common for single byte is the byte", i, ByteUtils.getBitsInCommon(byteList));
    	}
    	
    	// opposite bit patterns 0x55 and 0xAA
    	List<Byte> opposite = new ArrayList<Byte>();
    	opposite.add((byte) 0x55);
    	opposite.add((byte) 0xAA);
    	assertEquals("Opposite bitpatterns have nothing in common", 0, ByteUtils.getBitsInCommon(opposite));
    	
    	opposite.add((byte) 0x5A);
    	opposite.add((byte) 0xA5);
    	assertEquals("Opposite bitpatterns still have nothing in common", 0, ByteUtils.getBitsInCommon(opposite));
    	
    	// All bytes from 128 to 255 - only bit 8 is in common with all of them.
    	List<Byte> highBits = new ArrayList<Byte>();
    	for (int i = 128; i < 256; i++) {
    		highBits.add((byte) i);
    	}
    	assertEquals("High bits only match 128", 128, ByteUtils.getBitsInCommon(highBits));
    }
    
    @Test
    public void testGetBitsSetForAllPossibleBytes() {
       	// null collection
    	try {
    		ByteUtils.getBitsSetForAllPossibleBytes(null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}   
    	
    	// empty collection
    	assertEquals("No bytes gives no bits", 0, ByteUtils.getBitsSetForAllPossibleBytes(new HashSet<Byte>()));
    
    	// all bytes gives FF bitmask:
    	Set<Byte> allPossible = ByteUtils.toSet(ByteUtils.getAllByteValues());
    	assertEquals("All bytes gives 0xFF", 0xFF, ByteUtils.getBitsSetForAllPossibleBytes(allPossible));
    	
    	// 128 to 255 gives 0x80 bitmask:
    	Set<Byte> topBitSet = ByteUtils.toSet(ByteUtils.getBytesInRange(128, 255));
    	assertEquals("128-255 gives 0x80", 0x80, ByteUtils.getBitsSetForAllPossibleBytes(topBitSet));
    	
    	topBitSet.add(Byte.valueOf((byte) 1));
    	assertEquals("128-255, 1 gives 0x80", 0x80, ByteUtils.getBitsSetForAllPossibleBytes(topBitSet));
    }
    
    
    @Test
    public void testByteFromHex() {
    	expectIllegalArgumentHexByte(null);
    	expectIllegalArgumentHexByte("");
    	expectIllegalArgumentHexByte("A");
    	expectIllegalArgumentHexByte(" A3");
    	expectIllegalArgumentHexByte("A3 ");
    	expectIllegalArgumentHexByte("xx");
    	expectIllegalArgumentHexByte("deadbeef");
    	
    	assertCorrectHexByte("00", (byte) 0);
    	assertCorrectHexByte("01", (byte) 1);
    	assertCorrectHexByte("aa", (byte) 0xaa);
    	assertCorrectHexByte("FF", (byte) 0xFF);
    	assertCorrectHexByte("3c", (byte) 0x3c);
    	assertCorrectHexByte("Dc", (byte) 0xdC);
    	assertCorrectHexByte("DC", (byte) 0xdC);
    	assertCorrectHexByte("ab", (byte) 0xab);
    	assertCorrectHexByte("cd", (byte) 0xcd);
    	assertCorrectHexByte("ef", (byte) 0xef);
    	assertCorrectHexByte("AB", (byte) 0xab);
    	assertCorrectHexByte("CD", (byte) 0xcd);
    	assertCorrectHexByte("EF", (byte) 0xef);
    	assertCorrectHexByte("Ab", (byte) 0xab);
    	assertCorrectHexByte("Cd", (byte) 0xcd);
    	assertCorrectHexByte("Ef", (byte) 0xef);
    	assertCorrectHexByte("aB", (byte) 0xab);
    	assertCorrectHexByte("cD", (byte) 0xcd);
    	assertCorrectHexByte("eF", (byte) 0xef);
    }
    
    private void expectIllegalArgumentHexByte(String hex) {
    	try {
    		ByteUtils.byteFromHex(hex);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}   
    }
    
    private void assertCorrectHexByte(String hex, byte value) {
    	assertEquals("Byte value is correct for " + hex, value, ByteUtils.byteFromHex(hex));
    }
    
    @Test
    public void testByteToString() {
    	for (int i = 0; i < 256; i++) {
    		assertEquals("Simple byte is just hex value " + i,   String.format("%02x", i),    ByteUtils.byteToString(false, i)); 
    		if (i >= 32 && i <= 126 && i != 39) { 
    			assertEquals("Pretty print ASCII char " + i,     String.format("'%c'",  i), ByteUtils.byteToString(true,  i));
    		} else {
    			assertEquals("Pretty print non ASCII char " + i, String.format("%02x",  i), ByteUtils.byteToString(true,  i));
    		}
    	}
    }
    
    @Test
    public void testNullBytesToString() {
    	try {
    		ByteUtils.bytesToString(false, (byte[]) null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
       	try {
    		ByteUtils.bytesToString(true, (byte[]) null);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}  
    }
       	
     @Test
     public void testEmptyBytesToString() {
    	try {
    		ByteUtils.bytesToString(false, new byte[0]);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
       	try {
    		ByteUtils.bytesToString(true, new byte[0]);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {} 
    }
    
    @Test
    public void testSingleBytesToString() {
       	// simple bytes
    	testBytesToString("one byte array", "00", "00",  (byte) 0x00);
    	testBytesToString("one byte array", "01", "01",  (byte) 0x01);
    	testBytesToString("one byte array", "ff", "ff",  (byte) 0xff);
    	testBytesToString("one byte array", "81", "81",  (byte) 0x81);
    	testBytesToString("ascii char",     "41", "'A'", (byte) 0x41);
    	testBytesToString("quote char",     "27", "27",  (byte) 0x27);
    }
    
    private void testBytesToString(String description, String byteString, String prettyString, byte... array) {
       	assertEquals(description, byteString, 				ByteUtils.bytesToString(false, array));
       	assertEquals(description + " pretty", prettyString, ByteUtils.bytesToString(true, array));
    }
    
    @Test
    public void testMultipleBytesToString() {
       	// multiple non ASCII bytes
    	testBytesToString("two byte sequence", "0305", "03 05", (byte) 0x03, (byte) 0x05);
        testBytesToString("two byte sequence", "039f", "03 9f", (byte) 0x03, (byte) 0x9f);
               	
       	// pretty print string
       	testBytesToString("ASCII digits", "30313233343536373839", "'0123456789'", 
       			          (byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34,  
       			          (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x39);
       	testBytesToString("ASCII characters", "2021227a7b", "' !\"z{'",
			          (byte) 0x20, (byte) 0x21, (byte) 0x22, (byte) 0x7a, (byte) 0x7B);

       	
       	// bytes then string
       	testBytesToString("bytes then ASCII characters", "1a1b2021227a7b", "1a 1b ' !\"z{'",
		          (byte) 0x1a, (byte) 0x1b, (byte) 0x20, (byte) 0x21, (byte) 0x22, (byte) 0x7a, (byte) 0x7B);
       	
       	// string then bytes
       	testBytesToString("bytes then ASCII characters", "2021227a7b1a1b", "' !\"z{' 1a 1b",
		          (byte) 0x20, (byte) 0x21, (byte) 0x22, (byte) 0x7a, (byte) 0x7B, (byte) 0x1a, (byte) 0x1b);
       	
       	// mixed string and bytes
       	testBytesToString("bytes, ASCII, bytes", "152021227a7b1a", "15 ' !\"z{' 1a",
		          (byte) 0x15, (byte) 0x20, (byte) 0x21, (byte) 0x22, (byte) 0x7a, (byte) 0x7B, (byte) 0x1a);
       	testBytesToString("ASCII, bytes, ASCII", "202115227a7b", "' !' 15 '\"z{'",
		          (byte) 0x20, (byte) 0x21, (byte) 0x15, (byte) 0x22, (byte) 0x7a, (byte) 0x7B);
    }
    
    @Test
    public void testNullBytesToStringSub() {
    	try {
    		ByteUtils.bytesToString(false, null, 0, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
       	try {
    		ByteUtils.bytesToString(true, null, 0, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}  
    }
       	
     @Test
     public void testEmptyBytesToStringSub() {
    	try {
    		ByteUtils.bytesToString(false, new byte[0], 0, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
       	try {
    		ByteUtils.bytesToString(true, new byte[0], 0, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {} 
    }
     
    @Test
    public void testOutOfBoundsStringSub() {
    	try {
    		ByteUtils.bytesToString(false, new byte[0], 0, 0);
    		fail("0:0,0 Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.bytesToString(false, new byte[] {(byte) 0x01}, 0, 0);
    		fail("1:0,0 Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
    	try {
    		ByteUtils.bytesToString(false, new byte[] {(byte) 0x01}, -1, 0);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
    	try {
    		ByteUtils.bytesToString(false, new byte[] {(byte) 0x01}, -1, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
    	try {
    		ByteUtils.bytesToString(false, new byte[] {(byte) 0x01, (byte) 0xff}, 0, 3);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.bytesToString(false, new byte[] {(byte) 0x01, (byte) 0xff}, 1, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.bytesToString(false, new byte[] {(byte) 0x01, (byte) 0xff}, 2, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.bytesToString(false, new byte[] {(byte) 0x01, (byte) 0xff}, 2, 2);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}    
    }
    
    @Test
    public void testBytesToStringSubsequence() {
       	// simple bytes
    	testBytesToStringSub("one byte array",  0, 1, "00", "00",  (byte) 0x00);
    	testBytesToStringSub("one byte array",  0, 1, "01", "01",  (byte) 0x01);
    	testBytesToStringSub("one byte array",  0, 1, "ff", "ff",  (byte) 0xff);
    	testBytesToStringSub("one byte array",  0, 1, "81", "81",  (byte) 0x81);
    	testBytesToStringSub("ascii char",      0, 1, "41", "'A'", (byte) 0x41);
    	testBytesToStringSub("quote char",      0, 1, "27", "27",  (byte) 0x27);
    }
    
    private void testBytesToStringSub(String description, int start, int end, String byteString, String prettyString, byte... array) {
       	assertEquals(description, byteString, 				ByteUtils.bytesToString(false, array, start, end));
       	assertEquals(description + " pretty", prettyString, ByteUtils.bytesToString(true, array, start, end));
    }
    
    @Test
    public void testMultipleBytesToStringSub() {
       	// multiple non ASCII bytes
    	testBytesToStringSub("two byte sequence", 0, 2, "0305", "03 05", (byte) 0x03, (byte) 0x05, (byte) 0x07);
    	testBytesToStringSub("two byte sequence", 0, 1, "03", "03",      (byte) 0x03, (byte) 0x05, (byte) 0x07);
    	testBytesToStringSub("two byte sequence", 1, 3, "039f", "03 9f", (byte) 0x07, (byte) 0x03, (byte) 0x9f);
    	testBytesToStringSub("two byte sequence", 2, 3, "9f", "9f",      (byte) 0x07, (byte) 0x03, (byte) 0x9f);
               	
       	// pretty print string
    	testBytesToStringSub("ASCII digits", 1, 9, "3132333435363738", "'12345678'", 
       			          (byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34,  
       			          (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x39);
    	testBytesToStringSub("ASCII characters", 2, 5, "227a7b", "'\"z{'",
			             (byte) 0x20, (byte) 0x21, (byte) 0x22, (byte) 0x7a, (byte) 0x7B);

       	
       	// bytes then string
    	testBytesToStringSub("bytes then ASCII characters", 1, 4, "1b2021", "1b ' !'",
		          (byte) 0x1a, (byte) 0x1b, (byte) 0x20, (byte) 0x21, (byte) 0x22, (byte) 0x7a, (byte) 0x7B);
       	
       	// string then bytes
    	testBytesToStringSub("bytes then ASCII characters", 2, 7, "227a7b1a1b", "'\"z{' 1a 1b",
		          (byte) 0x20, (byte) 0x21, (byte) 0x22, (byte) 0x7a, (byte) 0x7B, (byte) 0x1a, (byte) 0x1b);
       	
       	// mixed string and bytes
    	testBytesToStringSub("bytes, ASCII, bytes", 0, 8, "152021227a7b1a1c", "15 ' !\"z{' 1a 1c",
		          (byte) 0x15, (byte) 0x20, (byte) 0x21, (byte) 0x22, (byte) 0x7a, (byte) 0x7B, (byte) 0x1a, (byte) 0x1c);
    	testBytesToStringSub("ASCII, bytes, ASCII", 1, 5, "2115227a", "'!' 15 '\"z'",
		          (byte) 0x20, (byte) 0x21, (byte) 0x15, (byte) 0x22, (byte) 0x7a, (byte) 0x7B);
    }
    

    /*
     * convenience methods to build collections quickly from variable parameters
     */
    
    private Set<Byte> toSet(int...integers) {
    	Set<Byte> set = new HashSet<Byte>((int) (integers.length / 0.75));
    	for (int i = 0; i < integers.length; i++) {
    		final int value = integers[i];
    		if (value < 0 || value > 255) {
    			throw new IllegalArgumentException("Integer value " + value + " at position " + i + " is not between 0 and 255 inclusive");
    		}
    		set.add(Byte.valueOf((byte) value));
    	}
    	return set;
    }
    
    private List<Byte> toList(int... integers) {
    	List<Byte> list = new ArrayList<Byte>(integers.length);
    	for (int i = 0; i < integers.length; i++) {
    		final int value = integers[i];
    		if (value < 0 || value > 255) {
    			throw new IllegalArgumentException("Integer value " + value + " at position " + i + " is not between 0 and 255 inclusive");
    		}
    		list.add(Byte.valueOf((byte) value));
    	}
    	return list;    	
    }
    
};