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

package net.byteseek.utils.droid;

/**
 *
 *
 * Created by matt on 26/07/15.
 */
public final class Utils {

    private Utils() {};

    public static int getInt(String value, int from, int to) {
        String intString = value.substring(from, to).trim();
        return Integer.parseInt(intString);
    }

    /**
     * Returns true if the character is a hex digit.
     *
     * @param currentChar
     * @return
     */
    public static boolean isHexDigit(char currentChar) {
        return (currentChar >= '0' && currentChar <= '9') ||
                (currentChar >= 'a' && currentChar <= 'f') ||
                (currentChar >= 'A' && currentChar <= 'F');
    }

    /**
     * Escapes entities for including in an XML document.
     *
     * @param target
     * @return
     */
    public static String escapeXml(String target) {
        final int length = target.length();
        StringBuilder builder = new StringBuilder(length + 128);
        for (int charIndex = 0; charIndex < length; charIndex++) {
            final char theChar = target.charAt(charIndex);
            switch (theChar) {
                case '&'  : builder.append("&amp;");  break;
                case '<'  : builder.append("&lt;");   break;
                case '>'  : builder.append("&gt;");   break;
                case '"'  : builder.append("&quot;"); break;
                case '\'' : builder.append("&apos;"); break;
                default   : builder.append(theChar);  break;
            }
        }
        return builder.toString();

    }
}
