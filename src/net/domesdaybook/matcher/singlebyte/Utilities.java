/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

/**
 *
 * @author matt
 */
public class Utilities {

    private static int[] MASK = {0x55, 0x33, 0x0F};

    private Utilities() {
    }

    public static int countSetBits(byte b) {
        int bits = (int) b;
        int result = bits - ((bits >>> 1) & MASK[0]);
        result = ((result >>> 2) & MASK[1]) + (result & MASK[1]);
        result = ((result >>> 4) + result) & MASK[2];
        return result;
    }
    
}
