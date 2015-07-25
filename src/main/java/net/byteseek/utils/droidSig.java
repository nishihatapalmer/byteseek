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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Builds DROID ByteSequence XML from a DROID regular expression.
 *
 * Created by matt on 25/07/15.
 */
public class droidSig {

    public static class ByteSequence {
        public String anchor;
        public List<SubSequence> subSequences = new ArrayList<SubSequence>();

        public String toDROIDXML() throws Exception {
            StringBuilder builder = new StringBuilder(2048);
            builder.append("<ByteSequence Reference=\"").append(anchor).append("\">\n");
            int subSequencePosition = 1;
            for (SubSequence subSequence : subSequences) {
                subSequence.toDROIDXML(builder, subSequencePosition++);
            }
            builder.append("</ByteSequence>\n");
            return builder.toString();
        }
    }

    public static class SubSequence {
        public String mainExpression;
        public int minSeqOffset;
        public int maxSeqOffset;
        public List<Fragment> leftFragments = new ArrayList<Fragment>();
        public List<Fragment> rightFragments = new ArrayList<Fragment>();

        public void toDROIDXML(StringBuilder builder, int position) throws Exception {
            builder.append("\t<SubSequence Position=\"").append(position).append("\" ");
            builder.append("SubSeqMaxOffset=\"").append(maxSeqOffset).append("\" ");
            builder.append("SubSeqMinOffset=\"").append(minSeqOffset).append("\">\n");
            builder.append("\t\t<Sequence>").append(escapeXml(mainExpression)).append("</Sequence>\n");
            for (Fragment fragment : leftFragments) {
                fragment.toDROIDXML(builder, "LeftFragment");
            }
            for (Fragment fragment : rightFragments) {
                fragment.toDROIDXML(builder, "RightFragment");
            }
            builder.append("\t</SubSequence>\n");
        }
    }

    public static class Fragment {
        public int position;
        public String fragmentExpression;
        public int minFragOffset;
        public int maxFragOffset;

        public void toDROIDXML(StringBuilder builder, String elementName) throws Exception {
            builder.append("\t\t<").append(elementName);
            builder.append(" MaxOffset=\"").append(maxFragOffset).append("\" ");
            builder.append(" MinOffset=\"").append(minFragOffset).append("\" ");
            builder.append(" Position=\"").append(position).append("\">");
            builder.append(escapeXml(fragmentExpression));
            builder.append("</").append(elementName).append(">\n");
        }
    }

    public final static String USAGE_HELP =
            "droidSig usage:\n" +
            "---------------\n\n" +
            "droidSig produces DROID Byte Sequence XML fragments from a DROID regular expression.\n\n" +
                    "\t[BOF|EOF|VAR]\t[Optional]\tThe anchoring of the byte sequence.\n" +
                    "\t\t\t\t\tBOF\tBeginning of file.  If not specified, this is the default.\n" +
                    "\t\t\t\t\tEOF\tEnd of file.\n" +
                    "\t\t\t\t\tVAR\tA wildcard search from the beginning of the file.\n" +
                    "\t{expression}\tA DROID signature regular expression.\n\n" +
            "Examples:\n" +
            "\tdroidSig \"01 02 03 04\"\n" +
            "\tdroidSig EOF \"01 02 {4} [00:FF] 05 06 07 08 09 0A {1-4} 0B 0C * 01 02 03 04 05\n\n";

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

    public static void main(final String[] args) throws Exception {

        if (args == null || args.length == 0) {
            System.out.println(USAGE_HELP);
            System.exit(0);
        }

        int paramIndex = 0;
        String firstParam = args[paramIndex];
        String anchor = "BOFoffset";
        if ("BOF".equals(firstParam)) {
            paramIndex++;
        } else if ("EOF".equals(firstParam)) {
            anchor = "EOFoffset";
            paramIndex++;
        } else if ("VAR".equals(firstParam)) {
            anchor = "Variable";
            paramIndex++;
        }

        String result = buildByteSequence(args[paramIndex], anchor).toDROIDXML();
        System.out.println(result);
    }

    private static ByteSequence buildByteSequence(String expression, String anchor) {
        ByteSequence byteSequence = new ByteSequence();
        byteSequence.anchor = anchor;
        List<SubSequence> subSequences = getSubsequences(expression, anchor);
        for (SubSequence subSequence : subSequences) {
            byteSequence.subSequences.add(subSequence);
        }
        return byteSequence;
    }

    private static List<SubSequence> getSubsequences(String expression, String anchor) {
        List<SubSequence> subSequences = new ArrayList<SubSequence>();
        String[] subSequenceExpressions = getSubsequenceExpressions(expression);
        String anchored = anchor;
        for (String subExpression : subSequenceExpressions) {
            subSequences.add(buildSubSequence(subExpression, anchored));
            anchored = null;
        }
        return subSequences;
    }

    private static SubSequence buildSubSequence(String subExpression, String anchor) {
        SubSequence subSequence = new SubSequence();

        // bytes, sets...
        // fixed offset {digit}
        // variable offset {digit:digit}

        // If there are no offsets, the sequence  needs no fragments.
        if (containsNoOffsets(subExpression)) {
            subSequence.mainExpression = subExpression; // just set the expression directly.
        } else {
            // Split into potential fragments and offsets
            List<String> offsetExpressions = splitFragments(subExpression);

            // If the expression is anchored, check for offsets next to the anchor,
            // as these are processed by setting min/max offsets rather than using fragments.
            processAnchoredOffsets(subSequence, anchor, offsetExpressions);

            // Process the fragments of the subsequence, and return the main expression:
            subSequence.mainExpression = processFragments(subSequence, offsetExpressions);
        }

        return subSequence;
    }

    private static String processFragments(SubSequence subSequence, List<String> offsetExpressions) {
        String mainExpression;
        int numExpressions = offsetExpressions.size();
        if (numExpressions == 1) { // can be no fragments with only a single expression.
            mainExpression = offsetExpressions.get(0);
        } else {
            int mainExpressionIndex = getMainExpression(offsetExpressions);
            mainExpression = offsetExpressions.get(mainExpressionIndex);

            if (mainExpressionIndex > 0) { // have left fragments.
                addLeftFragments(subSequence, offsetExpressions, mainExpressionIndex);
            }

            if (mainExpressionIndex < numExpressions - 1) { // have right fragments.
                addRightFragments(subSequence, offsetExpressions, mainExpressionIndex);
            }
        }
        return mainExpression;
    }

    private static void addLeftFragments(SubSequence subSequence, List<String> offsetExpressions, int mainExpressionIndex) {
        int position = 1;
        int fragMinOffset = 0;
        int fragMaxOffset = 0;
        for (int i = mainExpressionIndex -1; i >= 0; i--) {
            String fragmentExpression = offsetExpressions.get(i);
            if (isOffset(fragmentExpression)) {
                fragMinOffset = getMinOffset(fragmentExpression);
                fragMaxOffset = getMaxOffset(fragmentExpression);
            } else if (isAlternatives(fragmentExpression)) {
                List<Fragment> alternatives = getAlternativeFragments(fragmentExpression, position);
                for (Fragment fragment : alternatives) {
                    fragment.minFragOffset = fragMinOffset;
                    fragment.maxFragOffset = fragMaxOffset;
                    subSequence.leftFragments.add(fragment);
                }
                position++;
                fragMinOffset = 0;
                fragMaxOffset = 0;
            } else {
                Fragment fragment = new Fragment();
                fragment.position = position;
                fragment.fragmentExpression = fragmentExpression;
                fragment.minFragOffset = fragMinOffset;
                fragment.maxFragOffset = fragMaxOffset;
                subSequence.leftFragments.add(fragment);
                position++;
                fragMinOffset = 0;
                fragMaxOffset = 0;
            }
        }
    }

    private static void addRightFragments(SubSequence subSequence, List<String> offsetExpressions, int mainExpressionIndex) {
        int position = 1;
        int fragMinOffset = 0;
        int fragMaxOffset = 0;
        for (int i = mainExpressionIndex + 1; i < offsetExpressions.size(); i++) {
            String fragmentExpression = offsetExpressions.get(i);
            if (isOffset(fragmentExpression)) {
                fragMinOffset = getMinOffset(fragmentExpression);
                fragMaxOffset = getMaxOffset(fragmentExpression);
            } else if (isAlternatives(fragmentExpression)) {
                List<Fragment> alternatives = getAlternativeFragments(fragmentExpression, position);
                for (Fragment fragment : alternatives) {
                    fragment.minFragOffset = fragMinOffset;
                    fragment.maxFragOffset = fragMaxOffset;
                    subSequence.rightFragments.add(fragment);
                }
                position++;
                fragMinOffset = 0;
                fragMaxOffset = 0;
            } else {
                Fragment fragment = new Fragment();
                fragment.position = position;
                fragment.fragmentExpression = fragmentExpression;
                fragment.minFragOffset = fragMinOffset;
                fragment.maxFragOffset = fragMaxOffset;
                subSequence.rightFragments.add(fragment);
                position++;
                fragMinOffset = 0;
                fragMaxOffset = 0;
            }
        }
    }

    private static List<Fragment> getAlternativeFragments(String fragmentExpression, int position) {
        List<Fragment> fragments = new ArrayList<Fragment>();
        String stripBrackets = fragmentExpression.substring(1, fragmentExpression.length() - 1);
        String[] alternatives = stripBrackets.split("\\|");
        for (String alternative : alternatives) {
            Fragment fragment = new Fragment();
            fragment.position = position;
            fragment.fragmentExpression = alternative.trim();
            fragments.add(fragment);
        }
        return fragments;
    }

    /**
     * Figures out which of the expressions should be the main expression to search for.
     * The rest of them will be fragments to the left or right of the main expression.
     *
     * @param offsetExpressions
     * @return
     */
    private static int getMainExpression(List<String> offsetExpressions) {
        int mainIndex = -1;
        int longest = 0;
        for (int expressionIndex = 0; expressionIndex < offsetExpressions.size(); expressionIndex++) {
            String expression = offsetExpressions.get(expressionIndex);
            if (expression.charAt(0) != '(' && expression.charAt(0) != '{') {
                int numBytes = getNumBytesInExpression(expression);
                if (numBytes > longest) {
                    mainIndex = expressionIndex;
                    longest = numBytes;
                }
            }
        }

        if (mainIndex == -1) {
            String expressions = "";
            for (String expression : offsetExpressions) {
                expressions = expressions + expression + "\n";
            }
            throw new IllegalArgumentException("No expressions had any bytes which could be searched for:\n" + expressions);
        }

        return mainIndex;
    }

    /**
     * Processes an expression to count the number of bytes represented by it.
     *
     * @param expression
     * @return
     */
    private static int getNumBytesInExpression(String expression) {
        int numBytes = 0;
        int hexCount = 0;
        boolean inSet = false;
        boolean inString = false;
        boolean inCaseString = false;
        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);
            if (inSet) {
                if (currentChar == ']') {
                    inSet = false;
                    numBytes++;
                }
            } else if (inString) {
                if (currentChar == '\'') {
                    inString = false;
                } else {
                    numBytes++;
                }
            } else if (inCaseString) {
                if (currentChar == '`') {
                    inCaseString = false;
                } else {
                    numBytes++;
                }
            } else {
                if (isHexDigit(currentChar)) {
                    hexCount++;
                    if (hexCount == 2) {
                        numBytes++;
                        hexCount = 0;
                    }
                } else if (hexCount == 1) {
                    throw new IllegalArgumentException("A hex digit was split in two in the expression: " + expression);
                } else if (currentChar == '[') {
                    inSet = true;
                } else if (currentChar == '\'') {
                    inString = true;
                } else if (currentChar == '`') {
                    inCaseString = true;
                }
            }
        }
        return numBytes;
    }

    private static boolean isHexDigit(char currentChar) {
        return (currentChar >= '0' && currentChar <= '9') ||
               (currentChar >= 'a' && currentChar <= 'f') ||
               (currentChar >= 'A' && currentChar <= 'F');
    }

    private static void processAnchoredOffsets(SubSequence subSequence, String anchor, List<String> offsetExpressions) {
        String firstOffset = offsetExpressions.get(0);
        int lastIndex      = offsetExpressions.size() - 1;
        String lastOffset  = offsetExpressions.get(lastIndex);

        if ("BOFoffset".equals(anchor) && isOffsetExpression(firstOffset)) {
            setSequenceOffsets(subSequence, firstOffset);
            offsetExpressions.remove(0);
        } else if ("EOFoffset".equals(anchor) && isOffsetExpression(lastOffset)) {
            setSequenceOffsets(subSequence, lastOffset);
            offsetExpressions.remove(lastIndex);
        } else if ("Variable".equals(anchor) && isOffsetExpression(offsetExpressions.get(0))) {
            offsetExpressions.remove(0); // an offset expression starting a variable sequence means nothing - it's already doing a wildcard search.
        }
    }

    private static int getMinOffset(String fragmentExpression) {
        if (fragmentExpression.contains("-")) {
            int rangePosition = fragmentExpression.indexOf("-");
            return getInt(fragmentExpression, 1, rangePosition);
        } else {
            return getInt(fragmentExpression, 1, fragmentExpression.length() - 1);
        }
    }

    private static int getMaxOffset(String fragmentExpression) {
        if (fragmentExpression.contains("-")) {
            int rangePosition = fragmentExpression.indexOf("-");
            return getInt(fragmentExpression, rangePosition + 1, fragmentExpression.length() - 1);
        } else {
            return getInt(fragmentExpression, 1, fragmentExpression.length() - 1);
        }
    }

    private static void setSequenceOffsets(SubSequence subSequence, String offset) {
        if (offset.contains("-")) {
            int rangePosition = offset.indexOf("-");
            subSequence.minSeqOffset = getInt(offset, 1, rangePosition);
            subSequence.maxSeqOffset = getInt(offset, rangePosition + 1, offset.length() - 1);
        } else {
            int offsetValue = getInt(offset, 1, offset.length() - 1);
            subSequence.minSeqOffset = offsetValue;
            subSequence.maxSeqOffset = offsetValue;
        }
    }

    private static int getInt(String value, int from, int to) {
        String intString = value.substring(from, to).trim();
        return Integer.parseInt(intString);
    }


    private static boolean isOffsetExpression(String s) {
        return s.startsWith("{");
    }

    private static boolean isAlternatives(String fragmentExpression) {
        return fragmentExpression.startsWith("(");
    }

    private static boolean isOffset(String fragmentExpression) {
        return fragmentExpression.startsWith("{");
    }

    private static List<String> splitFragments(String subExpression) {
        List<String> offsets = new ArrayList<String>();
        int expressionStart = 0;
        for (int charIndex = 0; charIndex < subExpression.length(); charIndex++) {
            char currentChar = subExpression.charAt(charIndex);
            if (currentChar == '{') {
                if (expressionStart < charIndex - 1) {
                    offsets.add(subExpression.substring(expressionStart, charIndex).trim());
                }
                expressionStart = charIndex;
            } else if (currentChar == '}') {
                offsets.add(subExpression.substring(expressionStart, charIndex + 1).trim());
                expressionStart = charIndex + 1;
            } else if (currentChar == '(') {
                if (expressionStart < charIndex - 1) {
                    offsets.add(subExpression.substring(expressionStart, charIndex).trim());
                }
                expressionStart = charIndex;
            } else if (currentChar == ')') {
                offsets.add(subExpression.substring(expressionStart, charIndex + 1).trim());
                expressionStart = charIndex + 1;
            }
        }
        if (expressionStart < subExpression.length() -1) {
            offsets.add(subExpression.substring(expressionStart).trim());
        }
        return offsets;
    }

    private static boolean containsNoOffsets(String subExpression) {
        return !subExpression.contains("{");
    }


    private static String[] getSubsequenceExpressions(String expression) {
        // Split expression into separate bytes sequences, separated by wildcards:
        String[] sequences = expression.split("\\*");

        // Trim spaces from start and end of sequences:
        for (int i = 0; i < sequences.length; i++) {
            sequences[i] = sequences[i].trim();
        }

        return sequences;
    }


}