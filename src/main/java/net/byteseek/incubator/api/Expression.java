/*
 * Copyright Matt Palmer 2020, All rights reserved.
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
package net.byteseek.incubator.api;

import net.byteseek.compiler.CompileException;
import net.byteseek.compiler.Compiler;
import net.byteseek.compiler.matcher.SequenceMatcherCompiler;
import net.byteseek.io.reader.WindowReader;
import net.byteseek.matcher.MatchResult;
import net.byteseek.matcher.Matcher;
import net.byteseek.matcher.sequence.SequenceMatcher;
import net.byteseek.parser.ParseException;
import net.byteseek.parser.Parser;
import net.byteseek.parser.regex.RegexParser;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.searcher.Searcher;
import net.byteseek.searcher.sequence.factory.FastSearcherFactory;
import net.byteseek.searcher.sequence.factory.SequenceSearcherFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Expression is a high level interfdce to byteseek for matching and searching patterns.
 * It will try to select the best matching or searching algorithms automatically based on the expression.
 * It compiles a pattern in byteseek regular expression syntax, or another if you supply a different parser.
 * An expression can match or search for itself in byte arrays, Files, InputStreams, and SeekableByteChannels.
 * Alternative data sources can be implemented if required through the WindowReader interface.
 */
public final class Expression implements Matcher, Searcher {

    private static Parser<ParseTree>                    DEFAULT_PARSER           = RegexParser.PARSER;
    private static Compiler<SequenceMatcher, ParseTree> DEFAULT_COMPILER         = SequenceMatcherCompiler.COMPILER;
    private static SequenceSearcherFactory              DEFAULT_SEARCHER_FACTORY = FastSearcherFactory.SHIFTOR_12_THEN_SIGNEDHASH2;

    private String expression;
    private Matcher matcher;
    private Searcher forwardsSearcher;
    private Searcher backwardsSearcher;

    // Constructors

    public Expression(final String expression) throws CompileException { //TODO: make compile exceptions runtime, like Java Regexes?
        this(expression, DEFAULT_PARSER, DEFAULT_SEARCHER_FACTORY);
    }

    public Expression(final String expression, final SequenceSearcherFactory factory) throws CompileException { //TODO: make compile exceptions runtime, like Java Regexes?
        this(expression, DEFAULT_PARSER, factory);
    }

    public Expression(final String expression, final Parser<ParseTree> parser) throws CompileException { //TODO: make compile exceptions runtime, like Java Regexes?
        this(expression, parser, DEFAULT_SEARCHER_FACTORY);
    }

    public Expression(final String expression, final Parser<ParseTree> parser, final SequenceSearcherFactory factory) throws CompileException {
        final SequenceMatcher matcher;
        try {
            matcher = DEFAULT_COMPILER.compile(parser.parse(expression));
        } catch (ParseException e) {
            throw new CompileException(e.getMessage(), e);
        }
        this.expression = expression;
        this.matcher = matcher;
        //TODO: defer until we need these.
        this.forwardsSearcher = factory.createForwards(matcher);
        this.backwardsSearcher = factory.createBackwards(matcher);
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

    @Override
    public int searchForwards(WindowReader reader, long fromPosition, long toPosition, Collection<MatchResult> results) throws IOException {
        return forwardsSearcher.searchForwards(reader, fromPosition, toPosition, results);
    }

    @Override
    public List<MatchResult> searchForwards(WindowReader reader, long fromPosition, long toPosition) throws IOException {
        return forwardsSearcher.searchForwards(reader, fromPosition, toPosition);
    }

    @Override
    public int searchForwards(WindowReader reader, long fromPosition, Collection<MatchResult> results) throws IOException {
        return forwardsSearcher.searchForwards(reader, fromPosition, results);
    }

    @Override
    public List<MatchResult> searchForwards(WindowReader reader, long fromPosition) throws IOException {
        return forwardsSearcher.searchForwards(reader, fromPosition);
    }

    @Override
    public int searchForwards(WindowReader reader, Collection<MatchResult> results) throws IOException {
        return forwardsSearcher.searchForwards(reader, results);
    }

    @Override
    public List<MatchResult> searchForwards(WindowReader reader) throws IOException {
        return forwardsSearcher.searchForwards(reader);
    }

    @Override
    public int searchForwards(byte[] bytes, int fromPosition, int toPosition, Collection<MatchResult> results) {
        return forwardsSearcher.searchForwards(bytes, fromPosition, toPosition, results);
    }

    @Override
    public List<MatchResult> searchForwards(byte[] bytes, int fromPosition, int toPosition) {
        return forwardsSearcher.searchForwards(bytes, fromPosition, toPosition);
    }

    @Override
    public int searchForwards(byte[] bytes, int fromPosition, Collection<MatchResult> results) {
        return forwardsSearcher.searchForwards(bytes, fromPosition, results);
    }

    @Override
    public List<MatchResult> searchForwards(byte[] bytes, int fromPosition) {
        return forwardsSearcher.searchForwards(bytes, fromPosition);
    }

    @Override
    public int searchForwards(byte[] bytes, Collection<MatchResult> results) {
        return forwardsSearcher.searchForwards(bytes, results);
    }

    @Override
    public List<MatchResult> searchForwards(byte[] bytes) {
        return forwardsSearcher.searchForwards(bytes);
    }

    @Override
    public int searchBackwards(WindowReader reader, long fromPosition, long toPosition, Collection<MatchResult> results) throws IOException {
        return backwardsSearcher.searchBackwards(reader, fromPosition, toPosition, results);
    }

    @Override
    public List<MatchResult> searchBackwards(WindowReader reader, long fromPosition, long toPosition) throws IOException {
        return backwardsSearcher.searchBackwards(reader, fromPosition, toPosition);
    }

    @Override
    public int searchBackwards(WindowReader reader, long fromPosition, Collection<MatchResult> results) throws IOException {
        return backwardsSearcher.searchBackwards(reader, fromPosition, results);
    }

    @Override
    public List<MatchResult> searchBackwards(WindowReader reader, long fromPosition) throws IOException {
        return backwardsSearcher.searchBackwards(reader, fromPosition);
    }

    @Override
    public int searchBackwards(WindowReader reader, Collection<MatchResult> results) throws IOException {
        return backwardsSearcher.searchBackwards(reader, results);
    }

    @Override
    public List<MatchResult> searchBackwards(WindowReader reader) throws IOException {
        return backwardsSearcher.searchBackwards(reader);
    }

    @Override
    public int searchBackwards(byte[] bytes, int fromPosition, int toPosition, Collection<MatchResult> results) {
        return backwardsSearcher.searchBackwards(bytes, fromPosition, toPosition, results);
    }

    @Override
    public List<MatchResult> searchBackwards(byte[] bytes, int fromPosition, int toPosition) {
        return backwardsSearcher.searchBackwards(bytes, fromPosition, toPosition);
    }

    @Override
    public int searchBackwards(byte[] bytes, int fromPosition, Collection<MatchResult> results) {
        return backwardsSearcher.searchBackwards(bytes, fromPosition, results);
    }

    @Override
    public List<MatchResult> searchBackwards(byte[] bytes, int fromPosition) {
        return backwardsSearcher.searchBackwards(bytes, fromPosition);
    }

    @Override
    public int searchBackwards(byte[] bytes, Collection<MatchResult> results) {
        return backwardsSearcher.searchBackwards(bytes, results);
    }

    @Override
    public List<MatchResult> searchBackwards(byte[] bytes) {
        return backwardsSearcher.searchBackwards(bytes);
    }

    @Override
    public void prepareForwards() {
        forwardsSearcher.prepareForwards();
    }

    @Override
    public void prepareBackwards() {
        backwardsSearcher.prepareBackwards();
    }

    // Accessor methods

    public String expression() {
        return expression;
    }

    // Standard methods

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + expression + ')';
    }

}
