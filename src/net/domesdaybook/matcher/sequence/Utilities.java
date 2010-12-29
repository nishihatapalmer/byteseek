/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */
package net.domesdaybook.matcher.sequence;

/**
 *
 * @author matt
 */
public class Utilities {

    public static String bytesToString(final boolean prettyPrint, byte[] bytes) {
        final int QUOTE_CHARACTER_VALUE = 39; // 34;
        StringBuilder hexString = new StringBuilder();
        boolean inString = false;
        for (int byteIndex=0, byteLength = bytes.length; byteIndex<byteLength; byteIndex++) {
            int byteValue = 0xFF & bytes[byteIndex];
            if ( prettyPrint && byteValue > 31 && byteValue < 127 &&
                 byteValue != QUOTE_CHARACTER_VALUE) {
                final String formatString = inString? "%c" : " '%c";
                hexString.append( String.format( formatString, (char) byteValue ));
                inString = true;
            } else {
                 final String formatString = prettyPrint? inString? "' %02x" : "%02x" : "%02x";
                hexString.append( String.format(formatString, byteValue ));
                inString = false;
            }
        }
        if ( prettyPrint && inString ) {
            hexString.append( "' ");
        }
        return hexString.toString();
    }


    public static String byteValueToString(final boolean prettyPrint, final int byteValue) {
        final byte theByte = (byte) (0xFF & byteValue);
        final byte[] singleByte = new byte[] {theByte};
        //singleByte[0] = theByte;
        return bytesToString(prettyPrint, singleByte);
    }

}
