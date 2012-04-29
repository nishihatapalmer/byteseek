/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.util.bytes;

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
public class ByteUtilitiesTest {

  
    /**
     * 
     */
    public ByteUtilitiesTest() {
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
     * Test of countSetBits method, of class ByteUtilities.
     */
    @Test
    public void testCountSetBits() {

       // zero bits:
       assertEquals("00000000", 0, ByteUtilities.countSetBits((byte) 0x00) );

       // single bits:
       assertEquals("00000001", 1, ByteUtilities.countSetBits((byte) 0x01) );
       assertEquals("00000010", 1, ByteUtilities.countSetBits((byte) 0x02) );
       assertEquals("00000100", 1, ByteUtilities.countSetBits((byte) 0x04) );
       assertEquals("00001000", 1, ByteUtilities.countSetBits((byte) 0x08) );
       assertEquals("00010000", 1, ByteUtilities.countSetBits((byte) 0x10) );
       assertEquals("00100000", 1, ByteUtilities.countSetBits((byte) 0x20) );
       assertEquals("01000000", 1, ByteUtilities.countSetBits((byte) 0x40) );
       assertEquals("10000000", 1, ByteUtilities.countSetBits((byte) 0x80) );

       // two bits:
       assertEquals("10000001", 2, ByteUtilities.countSetBits((byte) 0x81) );
       assertEquals("10000010", 2, ByteUtilities.countSetBits((byte) 0x82) );
       assertEquals("10000100", 2, ByteUtilities.countSetBits((byte) 0x84) );
       assertEquals("10001000", 2, ByteUtilities.countSetBits((byte) 0x88) );
       assertEquals("10010000", 2, ByteUtilities.countSetBits((byte) 0x90) );
       assertEquals("10100000", 2, ByteUtilities.countSetBits((byte) 0xA0) );
       assertEquals("11000000", 2, ByteUtilities.countSetBits((byte) 0xC0) );
       assertEquals("00011000", 2, ByteUtilities.countSetBits((byte) 0x18) );

       // three bits:
       assertEquals("10000011", 3, ByteUtilities.countSetBits((byte) 0x83) );
       assertEquals("10000110", 3, ByteUtilities.countSetBits((byte) 0x86) );
       assertEquals("10001100", 3, ByteUtilities.countSetBits((byte) 0x8C) );
       assertEquals("10011000", 3, ByteUtilities.countSetBits((byte) 0x98) );
       assertEquals("10010001", 3, ByteUtilities.countSetBits((byte) 0x91) );
       assertEquals("00101100", 3, ByteUtilities.countSetBits((byte) 0x2C) );
       assertEquals("11000100", 3, ByteUtilities.countSetBits((byte) 0xC4) );
       assertEquals("00001011", 3, ByteUtilities.countSetBits((byte) 0x0B) );

       // four bits:
       assertEquals("01010101", 4, ByteUtilities.countSetBits((byte) 0x55) );
       assertEquals("10101010", 4, ByteUtilities.countSetBits((byte) 0xAA) );
       assertEquals("11110000", 4, ByteUtilities.countSetBits((byte) 0xF0) );
       assertEquals("00001111", 4, ByteUtilities.countSetBits((byte) 0x0F) );
       assertEquals("01100101", 4, ByteUtilities.countSetBits((byte) 0x65) );

       // five bits:
       assertEquals("11010101", 5, ByteUtilities.countSetBits((byte) 0xD5) );
       assertEquals("10101011", 5, ByteUtilities.countSetBits((byte) 0xAB) );
       assertEquals("11110100", 5, ByteUtilities.countSetBits((byte) 0xF4) );
       assertEquals("01001111", 5, ByteUtilities.countSetBits((byte) 0x4F) );
       assertEquals("01110101", 5, ByteUtilities.countSetBits((byte) 0x75) );

       // six bits:
       assertEquals("11011101", 6, ByteUtilities.countSetBits((byte) 0xDD) );
       assertEquals("10111110", 6, ByteUtilities.countSetBits((byte) 0xBE) );
       assertEquals("11110110", 6, ByteUtilities.countSetBits((byte) 0xF6) );
       assertEquals("01101111", 6, ByteUtilities.countSetBits((byte) 0x6F) );
       assertEquals("01111101", 6, ByteUtilities.countSetBits((byte) 0x7E) );

       // seven bits:
       assertEquals("11111011", 7, ByteUtilities.countSetBits((byte) 0xFB) );
       assertEquals("11011111", 7, ByteUtilities.countSetBits((byte) 0xDF) );
       assertEquals("00001011", 7, ByteUtilities.countSetBits((byte) 0xFE) );
       
       // eight bits:
       assertEquals("11111111", 8, ByteUtilities.countSetBits((byte) 0xFF) );
    }


    /**
     * Test of countUnsetBits method, of class ByteUtilities.
     */
    @Test
    public void testCountUnsetBits() {
       // eight zero bits:
       assertEquals("00000000", 8, ByteUtilities.countUnsetBits((byte) 0x00) );

       // seven zero bits:
       assertEquals("00000001", 7, ByteUtilities.countUnsetBits((byte) 0x01) );
       assertEquals("00000010", 7, ByteUtilities.countUnsetBits((byte) 0x02) );
       assertEquals("00000100", 7, ByteUtilities.countUnsetBits((byte) 0x04) );
       assertEquals("00001000", 7, ByteUtilities.countUnsetBits((byte) 0x08) );
       assertEquals("00010000", 7, ByteUtilities.countUnsetBits((byte) 0x10) );
       assertEquals("00100000", 7, ByteUtilities.countUnsetBits((byte) 0x20) );
       assertEquals("01000000", 7, ByteUtilities.countUnsetBits((byte) 0x40) );
       assertEquals("10000000", 7, ByteUtilities.countUnsetBits((byte) 0x80) );

       // six zero bits:
       assertEquals("10000001", 6, ByteUtilities.countUnsetBits((byte) 0x81) );
       assertEquals("10000010", 6, ByteUtilities.countUnsetBits((byte) 0x82) );
       assertEquals("10000100", 6, ByteUtilities.countUnsetBits((byte) 0x84) );
       assertEquals("10001000", 6, ByteUtilities.countUnsetBits((byte) 0x88) );
       assertEquals("10010000", 6, ByteUtilities.countUnsetBits((byte) 0x90) );
       assertEquals("10100000", 6, ByteUtilities.countUnsetBits((byte) 0xA0) );
       assertEquals("11000000", 6, ByteUtilities.countUnsetBits((byte) 0xC0) );
       assertEquals("00011000", 6, ByteUtilities.countUnsetBits((byte) 0x18) );

       // five zero bits:
       assertEquals("10000011", 5, ByteUtilities.countUnsetBits((byte) 0x83) );
       assertEquals("10000110", 5, ByteUtilities.countUnsetBits((byte) 0x86) );
       assertEquals("10001100", 5, ByteUtilities.countUnsetBits((byte) 0x8C) );
       assertEquals("10011000", 5, ByteUtilities.countUnsetBits((byte) 0x98) );
       assertEquals("10010001", 5, ByteUtilities.countUnsetBits((byte) 0x91) );
       assertEquals("00101100", 5, ByteUtilities.countUnsetBits((byte) 0x2C) );
       assertEquals("11000100", 5, ByteUtilities.countUnsetBits((byte) 0xC4) );
       assertEquals("00001011", 5, ByteUtilities.countUnsetBits((byte) 0x0B) );

       // four zero bits:
       assertEquals("01010101", 4, ByteUtilities.countUnsetBits((byte) 0x55) );
       assertEquals("10101010", 4, ByteUtilities.countUnsetBits((byte) 0xAA) );
       assertEquals("11110000", 4, ByteUtilities.countUnsetBits((byte) 0xF0) );
       assertEquals("00001111", 4, ByteUtilities.countUnsetBits((byte) 0x0F) );
       assertEquals("01100101", 4, ByteUtilities.countUnsetBits((byte) 0x65) );

       // three zero bits:
       assertEquals("11010101", 3, ByteUtilities.countUnsetBits((byte) 0xD5) );
       assertEquals("10101011", 3, ByteUtilities.countUnsetBits((byte) 0xAB) );
       assertEquals("11110100", 3, ByteUtilities.countUnsetBits((byte) 0xF4) );
       assertEquals("01001111", 3, ByteUtilities.countUnsetBits((byte) 0x4F) );
       assertEquals("01110101", 3, ByteUtilities.countUnsetBits((byte) 0x75) );

       // two zero bits:
       assertEquals("11011101", 2, ByteUtilities.countUnsetBits((byte) 0xDD) );
       assertEquals("10111110", 2, ByteUtilities.countUnsetBits((byte) 0xBE) );
       assertEquals("11110110", 2, ByteUtilities.countUnsetBits((byte) 0xF6) );
       assertEquals("01101111", 2, ByteUtilities.countUnsetBits((byte) 0x6F) );
       assertEquals("01111101", 2, ByteUtilities.countUnsetBits((byte) 0x7E) );

       // one zero bit:
       assertEquals("11111011", 1, ByteUtilities.countUnsetBits((byte) 0xFB) );
       assertEquals("11011111", 1, ByteUtilities.countUnsetBits((byte) 0xDF) );
       assertEquals("00001011", 1, ByteUtilities.countUnsetBits((byte) 0xFE) );

       // no zero bits:
       assertEquals("11111111", 0, ByteUtilities.countUnsetBits((byte) 0xFF) );
    }

    
    /**
     * 
     */
    @Test
    public void testGetAllBitmaskForBytes() {
        // only one byte mask matches 11111111 - the bitmask is the same as the byte:
        byte[] bytes = new byte[] {(byte) 0xFF};
        Byte expectedValue = Byte.valueOf((byte) 0xFF);
        assertEquals("11111111", expectedValue, ByteUtilities.getAllBitMaskForBytes(bytes));

        // Find all bitmask for odd bytes:
        bytes = getOddBytes();
        expectedValue = Byte.valueOf((byte) 0x01);
        assertEquals("Find all bitmask for odd bytes", expectedValue, ByteUtilities.getAllBitMaskForBytes(bytes));

        // no bitmask can match only the zero byte:
        bytes = new byte[] {0};
        assertEquals("00000000 to match 0", null, ByteUtilities.getAllBitMaskForBytes(bytes));

        // the zero bitmask can match all byte values:
        bytes = ByteUtilities.getAllByteValues();
        expectedValue = Byte.valueOf((byte) 0x00);
        assertEquals("00000000 to match all", expectedValue, ByteUtilities.getAllBitMaskForBytes(bytes));

        // 2 bytes match: mask 11111110:  11111110 and 11111111
        bytes = new byte[] {(byte) 0xFE, (byte) 0xFF};
        expectedValue = Byte.valueOf((byte) 0xFE);
        assertEquals("11111110", expectedValue,  ByteUtilities.getAllBitMaskForBytes(bytes));

        // no bitmask exists for only the 2 bytes: 11111110 and 01111111
        bytes = new byte[] {(byte) 0xFE, (byte) 0x7F};
        assertEquals("11111110 and 01111111", null, ByteUtilities.getAllBitMaskForBytes(bytes));

        // 4 bytes match: mask 01111110: 01111110 01111111 11111110 1111111
        bytes = new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0x7F, (byte) 0x7E};
        expectedValue = Byte.valueOf((byte) 0x7E);
        assertEquals("01111110", expectedValue,  ByteUtilities.getAllBitMaskForBytes(bytes));
    }


    /**
     * Test of getAnyBitMaskForBytes method, of class ByteUtilities.
     */
    @Test
    public void testGetAnyBitMaskForBytes() {
        // no one any bitmask matches only 11111111:
        byte[] bytes = new byte[] {(byte) 0xFF};
        assertEquals("no any bitmask matches only 0xFF", null, ByteUtilities.getAnyBitMaskForBytes(bytes));

        // Get any bitmask for odd values:
        bytes = getOddBytes();
        Byte expectedValue = Byte.valueOf((byte) 0x01);
        assertEquals("Find any bitmask for odd values", expectedValue, ByteUtilities.getAnyBitMaskForBytes(bytes));
        
        // no any bit bitmask can match zero:
        bytes = new byte[] {0};
        assertEquals("no any bitmask to match only zero byte", null, ByteUtilities.getAnyBitMaskForBytes(bytes));

        // the zero bitmask matches no bytes:
        bytes = ByteUtilities.getAllByteValues();
        assertEquals("no any bitmask for all byte values.", null, ByteUtilities.getAnyBitMaskForBytes(bytes));

        // no any bitmask for the bytes:  11111110 and 11111111
        bytes = new byte[] {(byte) 0xFE, (byte) 0xFF};
        assertEquals("11111110 and 11111111 have no any bitmask.", null, ByteUtilities.getAnyBitMaskForBytes(bytes));

        // Match all values except zero: 0xFF
        expectedValue = Byte.valueOf((byte) 0xFF);
        bytes = ByteUtilities.getBytesInRange(1, 255);
        assertEquals("All bytes from 1 to 255 match with 0xFF any bitmask", expectedValue, ByteUtilities.getAnyBitMaskForBytes(bytes));
        
        // no bitmask exists for only the 2 bytes: 11111110 and 01111111
        bytes = new byte[] {(byte) 0xFE, (byte) 0x7F};
        assertEquals("11111110 and 01111111", null, ByteUtilities.getAnyBitMaskForBytes(bytes));

        // any bitmask 01111111 (0x7F) matches all except zero and 0x80.
        bytes = new byte[254];
        int position = 0;
        for (int value = 1; value < 256; value++) {
            if (value != 0x80) {
                bytes[position++] = (byte) value;
            }
        }
        expectedValue = Byte.valueOf((byte) 0x7F);
        assertEquals("01111111 matches all except zero and 0x80", expectedValue, ByteUtilities.getAnyBitMaskForBytes(bytes));

        // No any bitmask exists to match only: 01111110 01111111 11111110 1111111
        bytes = new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0x7F, (byte) 0x7E};
        assertEquals("01111110", null, ByteUtilities.getAnyBitMaskForBytes(bytes));
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
     * Test of countBytesMatchingAllBits method, of class ByteUtilities.
     */
    @Test
    public void testCountBytesMatchingAllBits() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }

    /**
     * Test of countBytesMatchingAnyBit method, of class ByteUtilities.
     */
    @Test
    public void testCountBytesMatchingAnyBit() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBytesMatchingAllBitMask method, of class ByteUtilities.
     */
    @Test
    public void testGetBytesMatchingAllBitMask() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }



    /**
     * Test of toSet method, of class ByteUtilities.
     */
    @Test
    public void testToSet() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }

    /**
     * Test of toArray method, of class ByteUtilities.
     */
    @Test
    public void testToArray() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllByteValues method, of class ByteUtilities.
     */
    @Test
    public void testGetAllByteValues() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBytesInRange method, of class ByteUtilities.
     */
    @Test
    public void testGetBytesInRange() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }

    /**
     * Test of invertedSet method, of class ByteUtilities.
     */
    @Test
    public void testInvertedSet() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }



    /**
     * Test of getBitsInCommon method, of class ByteUtilities.
     */
    @Test
    public void testGetBitsInCommon() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBytesMatchingAnyBitMask method, of class ByteUtilities.
     */
    @Test
    public void testGetBytesMatchingAnyBitMask() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }

    /**
     * Test of byteToString method, of class ByteUtilities.
     */
    @Test
    public void testByteToString() {
        //TODO: finish test
        fail("The test case is a prototype.");
    }

}