package net.byteseek.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
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
     * Escapes text for including in an XML document.
     *
     * @param target
     * @return
     * @throws Exception
     */
    public static String escapeXml(String target) throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Text text = document.createTextNode(target);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(text);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(source, result);
        return writer.toString();
    }
}
