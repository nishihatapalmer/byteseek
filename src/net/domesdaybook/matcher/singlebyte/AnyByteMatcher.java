/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.matcher.singlebyte;

import net.domesdaybook.reader.ByteReader;

/**
 *
 * @author matt
 */
public class AnyByteMatcher implements SingleByteMatcher {

    public final boolean matches(byte theByte) {
        return true;
    }

    public final byte[] getMatchingBytes() {
        byte[] bytes = new byte[256];
        for (int count = 255; count >= 0; count--) {
            bytes[count] = (byte) count;
        }
        return bytes;
    }

    public final String toRegularExpression(boolean prettyPrint) {
        return prettyPrint ? " . " : ".";
    }

    public final boolean matches(ByteReader reader, long matchFrom) {
        return matches(reader.getByte(matchFrom));
    }

    public int getNumberOfMatchingBytes() {
        return 256;
    }

}
