/*
 * Copyright Matt Palmer 2009-2011, All rights reserved.
 *
 */

package net.domesdaybook.expression.parser;

import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.Tree;

/**
 *
 * @author matt palmer
 */
public class AstParser {

    /**
     * Returns an (unoptimised) abstract syntax tree from a regular
     * expression string.
     */
    public Tree parseToAST(final String expression) throws ParseException {
        try {
            return parseToAbstractSyntaxTree(expression);
        } catch (RecognitionException ex) {
            throw new ParseException(ex);
        }
    }

    
    /**
     * Optimises AST tree structures:
     * 
     * 1)  Looks for alternate lists with more than one single byte alternatives.
     *     These can be more efficiently be represented as a set of bytes.
     * 2)  If there are existing alternate sets of bytes, they can also be merged.
     * 3)  If all the alternatives are single bytes, then the entire alternative
     *     can be replaced by the set.
     *
     * @param treeNode The abstract syntax tree root to optimise.
     * @return Tree an AST with the alternatives optimised.
     */
    public Tree optimiseAST(final Tree treeNode) throws ParseException {
        Tree result = treeNode;
        // Recursively invoke on children of tree node, to walk the tree:
        for (int childIndex = 0; childIndex < treeNode.getChildCount(); childIndex++) {
            Tree childNode = treeNode.getChild(childIndex);

            // If a child is exactly equivalent to its parent, then
            // replace it with its own children, unless it is a repeat node, which can
            // have repeats of repeats
            //TODO: optimise repeats of repeats.
            if (equivalent(treeNode, childNode) && treeNode.getType() != regularExpressionParser.REPEAT) {
                treeNode.replaceChildren(childIndex, childIndex, getChildList(childNode));
                childNode = treeNode.getChild(childIndex);
            }

            Tree resultNode = optimiseAST(childNode);
            if (resultNode != childNode) {
                treeNode.setChild(childIndex, resultNode);
            }
        }

        // If the current node is an alternative node:
        if (treeNode.getType() == regularExpressionParser.ALT) {
            result = optimiseSingleByteAlternatives(treeNode);
        }

        return result;
    }




    private CommonTree parseToAbstractSyntaxTree(final String expression) throws ParseException, RecognitionException {
        CommonTree tree;
        final ANTLRStringStream input = new ANTLRStringStream(expression);
        final regularExpressionLexer lexer = new regularExpressionLexer(input);
        if (lexer.getNumberOfSyntaxErrors() == 0) {

            final CommonTokenStream tokens = new CommonTokenStream(lexer);
            regularExpressionParser parser = new regularExpressionParser(tokens) {
                @Override
                public void emitErrorMessage(String msg) {
                    throw new ParseErrorException(msg);
                }
            };
            try {
                final CommonTreeAdaptor adaptor = new CommonTreeAdaptor();
                parser.setTreeAdaptor(adaptor);
                regularExpressionParser.start_return ret = parser.start();
                tree = (CommonTree) ret.getTree();
            } catch (ParseErrorException e) {
                throw new ParseException(e.getMessage());
            }
        } else {
            throw new ParseException(String.format("Parse error: %d syntax errors in %s", lexer.getNumberOfSyntaxErrors(), expression));
        }
        return tree;
    }


    private Tree optimiseSingleByteAlternatives(Tree treeNode) {

        Tree result = treeNode;

        // Determine which children can be optimised:
        List<Integer> childrenToMerge = new ArrayList<Integer>();
        final int childCount = treeNode.getChildCount();
        for (int childIndex = childCount-1; childIndex >= 0 ; childIndex--) {
            if (isSingleByteNode(treeNode.getChild(childIndex))) {
                childrenToMerge.add(childIndex);
            }
        }

        // If there is more than one candidate child node to merge, then merge them
        // into a set-based byte test, rather than different alternatives:
        if (childrenToMerge.size() > 1) {
            
            CommonTree mergeSet = createNode(regularExpressionParser.SET);
            for (int mergeIndex = 0; mergeIndex < childrenToMerge.size(); mergeIndex++) {
                final int childIndex = childrenToMerge.get(mergeIndex);
                Tree mergeNode = treeNode.getChild(childIndex);

                // If any of the children of the alternative we are changing
                // to a set is also a set, merge its children into the set
                // instead of adding a set child to a set.
                if (mergeNode.getType() == regularExpressionParser.SET) {
                    mergeNode = getChildList(mergeNode);
                }
                mergeSet.addChild(mergeNode);
                treeNode.deleteChild(childIndex);
            }

            // If all the children are merged, the entire alternative
            // is really a single set - change the type of the node:
            if (childrenToMerge.size() == childCount) {
                result = mergeSet;
            } else { // just add the set to the alternative node:
                treeNode.addChild(mergeSet);
            }
        }

        return result;
    }


    private CommonTree createNode(int type) {
        String text = regularExpressionParser.tokenNames[type];
        return new CommonTree(new CommonToken(type, text));
    }

    private boolean equivalent(Tree node1, Tree node2) {
        return node1.getType() == node2.getType() &&
               node1.getText().equals(node2.getText());
    }

    private Tree getChildList(Tree parent) {
        // nodes with no token are "nil" nodes in antlr,
        // which act as lists of children.
        Tree listNode = new CommonTree();
        for (int childIndex = 0; childIndex < parent.getChildCount(); childIndex++) {
            listNode.addChild(parent.getChild(childIndex));
        }
        return listNode;
    }

    
    private boolean isSingleByteNode(Tree node) {
        final int nodeType = node.getType();
        return    nodeType == regularExpressionParser.BYTE
               || nodeType == regularExpressionParser.SET
               || nodeType == regularExpressionParser.ALL_BITMASK
               || nodeType == regularExpressionParser.ANY_BITMASK
               || ((   nodeType == regularExpressionParser.CASE_SENSITIVE_STRING
                    || nodeType == regularExpressionParser.CASE_INSENSITIVE_STRING)
                   && node.getText().length() == 1);
    }

    private class ParseErrorException extends RuntimeException {
        public ParseErrorException(String message) {
            super(message);
        }
    }



}
