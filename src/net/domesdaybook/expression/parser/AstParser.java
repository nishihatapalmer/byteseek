/*
 * Copyright Matt Palmer 2009-2010, All rights reserved.
 *
 */

package net.domesdaybook.expression.parser;

import java.util.ArrayList;
import java.util.List;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
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
    public Tree parseToAST(final String expression) {
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
    public Tree optimiseAST(final Tree treeNode) {
        
        // Recursively invoke on children of tree node, to walk the tree:
        for (int childIndex = 0; childIndex < treeNode.getChildCount(); childIndex++) {
            optimiseAST(treeNode.getChild(childIndex));
        }

        // If the current node is an alternative node:
        if (treeNode.getType() == regularExpressionParser.ALT) {
            optimiseSingleByteAlternatives(treeNode);
        }

        return treeNode;
    }


    private void optimiseSingleByteAlternatives(Tree treeNode) {

        // Determine which children can be optimised:
        List<Integer> childrenToMerge = new ArrayList<Integer>();
        for (int childIndex = treeNode.getChildCount()-1; childIndex >= 0 ; childIndex--) {
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
                final Tree mergeNode = treeNode.getChild(childIndex);
                mergeSet.addChild(mergeNode);
                treeNode.deleteChild(childIndex);
            }
            mergeSet.setParent(treeNode);
        }
    }


    private CommonTree createNode(int type) {
        return new CommonTree(new CommonToken(type));
    }

    
    private boolean isSingleByteNode(Tree node) {
        final int nodeType = node.getType();
        return    nodeType == regularExpressionParser.BYTE
               || nodeType == regularExpressionParser.SET
               || nodeType == regularExpressionParser.BITMASK
               || ((   nodeType == regularExpressionParser.CASE_SENSITIVE_STRING
                    || nodeType == regularExpressionParser.CASE_INSENSITIVE_STRING)
                   && node.getText().length() == 1);
    }


    private CommonTree parseToAbstractSyntaxTree(final String expression) throws RecognitionException {
        ANTLRStringStream input = new ANTLRStringStream(expression);
        regularExpressionLexer lexer = new regularExpressionLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        regularExpressionParser parser = new regularExpressionParser(tokens) {
            @Override
            public void emitErrorMessage(String msg) {
                throw new ParseException(msg);
            }
        };
        CommonTreeAdaptor adaptor = new CommonTreeAdaptor();
        parser.setTreeAdaptor(adaptor);
        regularExpressionParser.start_return ret = parser.start();
        final CommonTree tree = (CommonTree) ret.getTree();
        return tree;
    }

}
