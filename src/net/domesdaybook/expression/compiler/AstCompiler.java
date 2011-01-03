/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.domesdaybook.expression.compiler;

import java.util.HashSet;
import java.util.Set;
import net.domesdaybook.expression.parser.AstParser;
import net.domesdaybook.expression.parser.ParseException;
import net.domesdaybook.expression.parser.ParseUtils;
import net.domesdaybook.expression.parser.regularExpressionParser;
import net.domesdaybook.matcher.singlebyte.BitUtilities;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

/**
 *
 * @author matt
 */
public abstract class AstCompiler<T> implements Compiler<T> {

    protected static final String MANY = "*";

    @Override
    public T compile(String expression) throws ParseException {
        AstParser parser = new AstParser();
        Tree tree = parser.parseToAST(expression);
        CommonTree optimisedAST = (CommonTree) parser.optimiseAST(tree);
        return compile(optimisedAST);
    }

    
    public abstract T compile(final CommonTree ast);

}
