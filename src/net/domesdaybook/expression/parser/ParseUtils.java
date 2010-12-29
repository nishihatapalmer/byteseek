/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.parser;

import java.util.HashSet;
import java.util.Set;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 *
 * @author matt
 */
public class ParseUtils {





    private ParseUtils() {

    }

    public static byte parseHexByte(final String hexByte) {
        return (byte) Integer.parseInt(hexByte, 16);
    }


    public static byte getHexByteValue(final Tree treeNode) {
        return parseHexByte(treeNode.getText());
    }


    public static byte getBitMaskValue(final Tree treeNode) {
        final Tree childNode = treeNode.getChild(0);
        return parseHexByte(childNode.getText());
    }

    public static int getChildIntValue(final Tree treeNode, final int childIndex) {
        final Tree childNode = treeNode.getChild(childIndex);
        return Integer.parseInt(childNode.getText(), 10);
    }


    public static String getChildStringValue(final Tree treeNode, final int childIndex) {
        return treeNode.getChild(childIndex).getText();
    }


}
