package net.byteseek.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 *
 *
 * Created by matt on 26/07/15.
 */
public final class StringUtils {

    private StringUtils() {};

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
