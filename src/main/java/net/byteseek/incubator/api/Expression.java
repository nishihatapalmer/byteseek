package net.byteseek.incubator.api;

import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.matcher.Matcher;
import net.byteseek.parser.ParseException;
import net.byteseek.parser.Parser;
import net.byteseek.parser.regex.RegexParser;
import net.byteseek.parser.tree.ParseTree;

import java.io.IOException;
import java.util.Collection;

public final class Expression implements Matcher {

    private static Parser<ParseTree> DEFAULT_PARSER = new RegexParser();

    private String expression;
    private ParseTree parseTree;
    private Matcher matcher; //TODO: compile appropriate matcher.

    // Constructors

    public Expression(final String expression) throws ParseException { //TODO: make parse exceptions runtime, like Java Regexes?
        this(expression, DEFAULT_PARSER);
    }

    public Expression(final String expression, final Parser<ParseTree> parser) throws ParseException {
        this.parseTree = parser.parse(expression);
        this.expression = expression;
    }

    // Matching methods

    @Override
    public boolean matches(final byte[] bytes, final int matchPosition) {
        return matcher.matches(bytes, matchPosition);
    }

    @Override
    public boolean matches(final WindowReader reader, final long matchPosition) throws IOException {
        return matcher.matches(reader, matchPosition);
    }

    @Override
    public int matches(final byte[] bytes, int matchPosition, final Collection<MatchResult> results) {
        return matcher.matches(bytes, matchPosition, results);
    }

    @Override
    public int matches(final WindowReader reader, final long matchPosition, final Collection<MatchResult> results) throws IOException {
        return matcher.matches(reader, matchPosition, results);
    }


    // Accessor methods

    public String expression() {
        return expression;
    }

    public ParseTree parseTree() {
        return parseTree;
    }

    // Standard methods

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + expression + ')';
    }


}
