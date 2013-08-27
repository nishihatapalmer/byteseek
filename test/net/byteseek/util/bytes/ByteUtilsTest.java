/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.byteseek.util.bytes;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.byteseek.util.bytes.ByteUtils;

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
    		ByteUtils.repeat(null, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.repeat(new byte[] {(byte) 0x01}, -1);
    		fail("Number of repeats cannot be negative");
    	} catch (IllegalArgumentException expect) {}
    	
    	testRepeatArray((byte) 0x01);
    	testRepeatArray((byte) 0x01, (byte) 0xff, (byte) 0xde, (byte) 0xed);
    }
    
    private void testRepeatArray(byte... values) {
    	for (int repeat = 0; repeat < 10; repeat++) {
    		byte[] result = ByteUtils.repeat(values, repeat);
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
    		ByteUtils.repeat(null, 1, 0, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.repeat(new byte[] {(byte) 0x01}, -1, 0, 1);
    		fail("Number of repeats cannot be negative");
    	} catch (IllegalArgumentException expect) {}
    	
    	testRepeatArraySubsequence((byte) 0x01);
    	testRepeatArraySubsequence((byte) 0x01, (byte) 0xff, (byte) 0xde, (byte) 0xed);
    }
    
    private void testRepeatArraySubsequence(byte... values) {
    	int length = values.length;
    	try {
    		ByteUtils.repeat(values, 1, -1, 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expected) {}
    	try {
    		ByteUtils.repeat(values, 1, 0, length + 1);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expect) {}
    	try {
    		ByteUtils.repeat(values, 1, length, length);
    		fail("Expected an illegal argument exception");
    	} catch (IllegalArgumentException expect) {}
    	
    	for (int subStart = 0; subStart < length; subStart++) {
    		for (int subEnd = subStart + 1; subEnd <= length; subEnd++) {
    			int subLength = subEnd - subStart;
    	    	for (int repeat = 0; repeat < 10; repeat++) {
    	    		byte[] result = ByteUtils.repeat(values, repeat, subStart, subEnd);
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
    
};