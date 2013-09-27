/*
 * Copyright Matt Palmer 2012-2013, All rights reserved.
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

package net.byteseek.parser.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.byteseek.parser.ParseException;
import net.byteseek.parser.Parser;
import net.byteseek.parser.StringParseReader;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;
import net.byteseek.parser.tree.node.BaseNode;
import net.byteseek.parser.tree.node.ByteNode;
import net.byteseek.parser.tree.node.ChildrenNode;
import net.byteseek.parser.tree.node.IntNode;
import net.byteseek.parser.tree.node.StringNode;
import net.byteseek.util.bytes.ByteUtils;

/**
 * A hand-written regular expression parser for byteseek. The syntax is designed
 * to support matching byte structures. 
 * <p>
 * The parser has no state, so it is entirely thread-safe.
 * <p>
  * <strong>Syntax</strong><br/>
  * The syntax is the byteseek regular expression syntax, which is designed to make
  * byte searching easier.  Mostly this involves bytes being directly specified as hexadecimal,
  * with ASCII chars in quoted strings. Matchers for bitmasks (all or any bits) are also provided.  
  * <p>
  * <strong>Comments</strong><br/>
  * Byteseek regular expressions can have comments in them, using a <strong>#</strong> symbol.  
  * All text following the comment symbol is ignored until the next end of line character.
  * <p><blockquote><pre><code>
  * 01 ff c1 # Match byte sequence 0x01, 0xff, 0xc1
  *	</code></pre></blockquote></p>
  * <p>
  * <strong>Whitespace</strong></br>
  * All spaces, tabs, new lines and carriage returns separating syntax elements will
  * be ignored, unless they appear within a quoted string.
  * <p><blockquote><pre><code>
  * 01ffc1               # match bytes 01 ff c1
  * 01       ff       c1 # match bytes 01 ff c1
  * 01 ff 'some text' c1 # match bytes 01 ff, the string 'some text', then the byte c1 
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Bytes</strong></br>
  * Bytes are written as 2 digit hexadecimal numbers (any case allowed).  Spacing between
  * them doesn't matter, as whitespace is ignored.  However, you can't separate the digits of a hex
  * character with whitespace.
  * <p><blockquote><pre><code>
  * 00 FF 1a dE # match byte 00, ff, 1a, de
  *	</code></pre></blockquote></p></p>
  * To specify that you mean the inverse of a byte value (all other byte values), prepend a <strong>^</strong>
  * symbol to the byte value (with no separating whitespace).
  * <p><blockquote><pre><code>
  * 00 ^FF 1a ^dE # match byte 00, any byte but ff, byte 1a and any byte but dE. 
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Any byte</strong></br>
  * Any byte can be matched using the full stop symbol (as in most regular expression languages).
  * <p><blockquote><pre><code>
  * .       # matches any byte
  * 01 . ff # matches 0x01, followed by any byte, then 0xff
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Ranges</strong></br>
  * Ranges of byte values can be specified using a hypen separator to specify a range:
  * <p><blockquote><pre><code>
  * 00-7f # all the ascii bytes
  * 30-39 # all the digits
  *	</code></pre></blockquote></p></p>
  * If you want every byte except within a range, prepend the range with a <strong>^</strong> symbol:
  * <p><blockquote><pre><code>
  * ^00-7f # all the non-ascii bytes
  * ^30-39 # all the bytes which are not ASCII digits
  *	</code></pre></blockquote></p></p>
  * Ranges can also be specified using single quoted, single character strings:
  * <p><blockquote><pre><code>
  * 'a'-'z'  # all the lowercase ASCII letters. 
  * ^'0'-'9' # all the bytes which are not ASCII digits
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Strings</strong></br>
  * Text (ASCII only) is delimited using single quotes:
  * <p><blockquote><pre><code>
  * 'testing testing 123' # the string 'testing testing 123'
  * 01 '01'               # the byte 0x01, followed by the text '01'
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Case insensitive strings</strong></br>
  * Case-insensitive text can be written delimited with `back-ticks`:
  * <p><blockquote><pre><code>
  * `HtMl public`         # match the text HTML PUBLIC case insensitively.
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Bitmasks</strong></br>
  * If you want to match a bitmask, there are two methods of doing so in byteseek.
  * You can match all the bits in a bitmask, specified by the <strong>&</strong> symbol,
  * or you can match any of the bits in a bitmask, specified by the <strong>~</strong> symbol.
  * Prepend the appropriate bitmask symbol to a two digit hex number:
  * <p><blockquote><pre><code>
  * &7F   # match all these bits    01111111
  * &0F   # match all these bits    00001111
  * &81   # match all these bits    10000001
  * ~7F   # match any of these bits 01111111
  * ~0F   # match any of these bits 00001111
  * ~81   # match any of these bits 10000001
  *	</code></pre></blockquote></p></p>
  * The intention of the all bits match is that, to match all the bits
  * in a bitmask, a byte ANDed with the bitmask should equal the bitmask.
  * This means that an 'all bits' bitmask of zero will match all bytes.
  * <p><blockquote><pre><code>
  * byteValue & bitmask == bitmask
  *        01 & 01      == 01      match
  *        ff & 01      == 01      match
  *        02 & 01      == 00      no match
  *        01 & 00      == 00      match
  *        ff & 00      == 00      match
  *	</code></pre></blockquote></p></p>
  * The intention of the any bits match is that, to match any of the bits
  * in a bitmask, a byte ANDed with the bitmask should not be zero.  Note that
  * the result could be negative, as bytes in Java are signed.  This means
  * that an 'any bits' bitmask of zero will match no bytes at all.
  * <p><blockquote><pre><code>
  * byteValue & bitmask != 00 
  *        01 & 01      == 01      match
  *        ff & 01      == 01      match
  *        02 & 01      == 0       no match
  *        01 & 00      == 00      no match
  *        ff & 00      == 00      no match
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Sets</strong></br>
  * Sets of bytes can be specified using square brackets.  Sets can contain bytes, ranges, strings,
  * case insensitive strings, bitmasks and other sets.
  * <p><blockquote><pre><code>
  * [01 02 03]           # match the set of bytes 0x01, 0x02 or 0x03     
  * 'version' [01 02 03] # match the string 'version' followed by any of the bytes 0x01, 0x02 or 0x03. 
  *	</code></pre></blockquote></p></p>
  * Sets can contain strings, which specify that those ASCII bytes are members of the set too, not a 
  * sequence of characters.  Case insensitive strings just specify all the bytes which could match
  * case insensitively.
  * <p><blockquote><pre><code>
  * ['0123456789']       # the set of all the digit bytes.
  * [ff '0123456789' 00] # the set of all the digit bytes and the bytes 0x00 and 0xff
  * [`HTML`]             # The set of bytes which case insensitively match HTML.
  *	</code></pre></blockquote></p></p>
  * If you want to specify the inverse set, prepend the set with a <strong>^</strong> symbol:
  * <p><blockquote><pre><code>
  * ^['0'-'9']            # anything but the set of all the digit bytes.
  * ^[ff '0123456789' 00] # anything but the set of all the digit bytes and the bytes 0x00 and 0xff
  * ^[^'0'-'9']           # an inefficient (double negative) way of specifying all the digit bytes.
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Sub expressions</strong></br>
  * If you need to specify that something only applies to part of the whole expression,
  * then you can group subsequences into sub expressions using round brackets.
  * Sub expressions can also be nested within each other.  
  * <p><blockquote><pre><code>
  * 01 02 (03 04) 05       # this sub expression does nothing - the round brackets are superfluous
  * (01 02 (03 (04)) 05)   # these sub expressions also do nothing.
  *	</code></pre></blockquote></p></p>
  * The above examples show sub expressions which are valid syntactically, but they don't do anything.  
  * The round brackets are entirely unnecessary.  Sub expressions become useful when you need to quantify
  * a part of an expression, for example, to say it repeats between 5 and 10 times.    
  * They are also needed when you want to specify a list of alternative sequences within an expression.   
  * These more interesting uses of sub expressions will become clear in the next sections.
  * <p>
  * <strong>Alternatives</strong></br>
  * Alternatives are written separated by a pipe character:
  * <p><blockquote><pre><code>
  * 'this' | 'that' | 00 FF 1a     # match 'this', 'that', or 0x00 0xFF 0x1a
  * 01 02 ('one'|'two'|'three') 03 # match the sequence 1, 2 followed by ('one', 'two' or 'three'), ending with 3
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Optional elements</strong></br>
  * To specify that an element is optional, append a <strong>?</strong> symbol to the element.
  * If you want an entire expression to be optional, enclose it with round brackets and append
  * the question mark to it.
  * <p><blockquote><pre><code>
  * de?            # optional byte 0xde
  * 'extra fries'? # optional 'extra fries'
  * (01 02 03)?    # optional sequence of bytes (0x01 0x02 0x03)
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Zero to many elements</strong></br>
  * To specify that an element can be repeated from zero to many times, append a <strong>*</strong> symbol
  * to the element.  If you want an entire expression to be repeated zero to many times, enclose it with
  * round brackets and append the * to it.
  * <p><blockquote><pre><code>
  * 10*                               # repeat byte 0x10 from zero to many times.
  * 'stuff'*                          # repeat 'stuff' zero to many times.
  * ('bytes' 00 01 02 'counting')*    # repeat entire expression in brackets from zero to many times.
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>One to many elements</strong></br>
  * To specify that an element can be repeated from one to many times, append a <strong>+</strong> symbol
  * to the element.  If you want an entire expression to be repeated one to many times, enclose it with
  * round brackets and append the + to it.
  * <p><blockquote><pre><code>
  * 10+                               # repeat byte 0x10 from one to many times.
  * 'stuff'+                          # repeat 'stuff' one to many times.
  * ('bytes' 00 01 02 'counting')+    # repeat entire expression in brackets from one to many times.
  *	</code></pre></blockquote></p></p>
  * <p> 
  * <strong>Repeat exactly</strong></br>
  * To specify that an element should be repeated an exact number of times, append a positive integer 
  * enclosed in curly brackets to the end of the element, e.g. <strong>ff{4}</strong>.
  * If you want to repeat an entire expression, enclose the expression in round brackets
  * and append the repeat quantifier to the end.
  * <p><blockquote><pre><code>
  * 10{40}                              # repeat byte 0x10 forty times.
  * 'stuff'{3}                          # repeat 'stuff' three times.
  * ('bytes' 00 01 02 'counting'){9}    # repeat entire expression in brackets nine times.
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Repeat min to max times</strong></br>
  * To specify that an element can be repeated between a minimum and maximum number, append 
  * two positive integers separated by a comma, enclosed in curly brackets to the end of the
  * element , e.g. <strong>ff{2,6}</strong>.  If you want to specify an entire expression,
  * enclose the expression in round brackets and append the repeat quantifier to the end.
  * <p><blockquote><pre><code>
  * 10{10,40}                           # repeat byte 0x10 between 10 and forty times.
  * 'stuff'{3,6}                        # repeat 'stuff' between three and six times
  * ('bytes' 00 01 02 'counting'){1,3}  # repeat entire expression in brackets between 1 and 3 times.
  * ff{0,1}                             # repeat byte 0xff either zero or once - the same as <strong>ff?</strong> 
  *	</code></pre></blockquote></p></p>
  * <p>
  * <strong>Repeat min to many times</strong></br>
  * To specify that an element repeats at least a minimum number of times, but can repeat
  * infinitely afterwards, append a positive integer and star separated by a comma, and enclosed
  * in curly brackets to the end of the element, e.g. <strong>ff{5,*}</strong>.
  * If you want to repeat an entire expression, enclose the expression in round brackets and
  * append the repeat quantifier to the end. 
  * <p><blockquote><pre><code>
  * 10{10,*}                            # repeat byte 0x10 between 10 and infinite times.
  * 'stuff'{3,*}                        # repeat 'stuff' between three and infinite times
  * ('bytes' 00 01 02 'counting'){1,*}  # repeat entire expression in brackets between 1 and infinite times.
  * ff{0,*}                             # repeat byte 0xff from zero to infinite times - the same as <strong>ff*</strong>	
  * ff{1,*}                             # repeat byte 0xff from one to infinite times - the same as <strong>ff+</strong>
  *</code></pre></blockquote></p></p>
  * <p>
  * <strong>Shorthands</strong></br>
  * There are many common bytes and sets of bytes which appear regularly in regular expressions.
  * Most regular expression languages support shorthands, which give you a memorable and often 
  * shorter way to specify them.  The shorthands supported by this parser are listed below.
  * <p><blockquote><pre><code>
  *	\t	 # tab                          09
  * \n	 # newline                      0a
  *	\v 	 # vertical tab                 0b
  *	\f 	 # form feed                    0c 
  *	\r 	 # carriage return              0d
  *	\e 	 # escape                       1b
  *	\d 	 # digit                        ['0'-'9']
  *	\D 	 # not digit                   ^['0'-'9']
  *	\w 	 # word character               ['a'-'z' 'A'-'Z' '0'-'9' '_']
  *	\W 	 # not word character          ^['a'-'z' 'A'-'Z' '0'-'9' '_']
  *	\s	 # white space                  [\t \n \r ' ']
  *	\S	 # not white space             ^\s
  * \i   # ascii characters             00-7f
  * \I   # not ascii characters        ^00-7f
  * \\u   # uppercase ascii characters  'A'-'Z'
  * \U   # not uppercase ascii chars  ^'A'-'Z'
  * \l   # lowercase ascii characters  'a'-'z'
  * \L   # not lowercase ascii chars  ^'a'-'z'  
  *	</code></pre></blockquote></p></p> 
  *
  * @author Matt Palmer
  */
public class RegexParser implements Parser<ParseTree> {

	/*
	 * Ideas for future...
	 */
	//FEATURE: syntax for set subtraction: ['a'-'z' -'aeiou'] 
	//FEATURE: syntax to specify <options> which apply to the parsing. 
	//FEATURE: syntax for text encoding options for strings: <::UTF-16BE::>
	//FEATURE: long syntax for common sets e.g. ascii, tab, space, etc. 
	
	/*
	 * Private syntactic character constants
	 */
	private static final char ANY                    	= '.';
	private static final char ALTERNATIVE            	= '|';
	private static final char COMMENT 					= '#';
	private static final char STRING_QUOTE 				= '\'';
	private static final char CASE_INSENSITIVE_QUOTE 	= '`';
	private static final char ALL_BITMASK 				= '&';
	private static final char ANY_BITMASK 				= '~';
	private static final char SHORTHAND_ESCAPE 			= '\\';
	private static final char OPEN_SET 					= '[';
	private static final char INVERT	 				= '^';
	private static final char RANGE_SEPARATOR 			= '-';
	private static final char CLOSE_SET 				= ']';
	private static final char OPTIONAL 					= '?';
	private static final char MANY 						= '*';
	private static final char ONE_TO_MANY 				= '+';
	private static final char OPEN_REPEAT 				= '{';
	private static final char REPEAT_SEPARATOR       	= ',';
	private static final char CLOSE_REPEAT 				= '}';	
	private static final char OPEN_GROUP 				= '(';
	private static final char CLOSE_GROUP 				= ')';
	
	/*
	 * Private general constants
	 */
	private static final boolean INVERTED 		= true;
	private static final boolean NOT_INVERTED	= false;

	/*
	 * Public common byte values as re-usable parse nodes.
	 */
	public static final ParseTree SPACE      		= ByteNode.valueOf((byte) ' '); 
	public static final ParseTree UNDERSCORE 		= ByteNode.valueOf((byte) '_');
	public static final ParseTree TAB				= ByteNode.valueOf((byte) '\t');
	public static final ParseTree NEWLINE			= ByteNode.valueOf((byte) '\n');
	public static final ParseTree CARRIAGE_RETURN 	= ByteNode.valueOf((byte) '\r');
	public static final ParseTree VERTICAL_TAB    	= ByteNode.valueOf((byte) 0x0b);
	public static final ParseTree FORM_FEED	    	= ByteNode.valueOf((byte) 0x0c);
	public static final ParseTree ESCAPE	        = ByteNode.valueOf((byte) 0x1e);
	
	/*
	 * Public common sets of bytes as re-usable parse nodes.
	 */
	public static final ParseTree DIGITS_RANGE, 	NOT_DIGITS_RANGE;
	public static final ParseTree LOWERCASE_RANGE, 	NOT_LOWERCASE_RANGE;
	public static final ParseTree UPPERCASE_RANGE, 	NOT_UPPERCASE_RANGE;
	public static final ParseTree WHITESPACE_SET, 	NOT_WHITESPACE_SET;
	public static final ParseTree WORD_CHAR_SET, 	NOT_WORD_CHAR_SET;
	public static final ParseTree ASCII_RANGE, 		NOT_ASCII_RANGE;
	static {
		DIGITS_RANGE 		= buildRange((byte) '0', (byte) '9', NOT_INVERTED);
		NOT_DIGITS_RANGE 	= buildRange((byte) '0', (byte) '9', INVERTED);
		LOWERCASE_RANGE 	= buildRange((byte) 'a', (byte) 'z', NOT_INVERTED);
		NOT_LOWERCASE_RANGE = buildRange((byte) 'a', (byte) 'z', INVERTED);
		UPPERCASE_RANGE		= buildRange((byte) 'A', (byte) 'Z', NOT_INVERTED);
		NOT_UPPERCASE_RANGE	= buildRange((byte) 'A', (byte) 'Z', INVERTED);
		ASCII_RANGE	        = buildRange((byte) 0x00, (byte) 0x7f, NOT_INVERTED);
		NOT_ASCII_RANGE	    = buildRange((byte) 0x00, (byte) 0x7f, INVERTED);
		WHITESPACE_SET	    = buildSet(SPACE, TAB, NEWLINE, CARRIAGE_RETURN);
		NOT_WHITESPACE_SET  = buildInvertedSet(SPACE, TAB, NEWLINE, CARRIAGE_RETURN);
		WORD_CHAR_SET 		= buildSet(DIGITS_RANGE, LOWERCASE_RANGE, UPPERCASE_RANGE, UNDERSCORE);
		NOT_WORD_CHAR_SET 	= buildInvertedSet(DIGITS_RANGE, LOWERCASE_RANGE, UPPERCASE_RANGE, UNDERSCORE);
	}
	
	/*
	 * Public static utility methods to build common parse tree nodes.
	 */
	public static final ParseTree buildRange(final byte minByte, final byte maxByte, final boolean inverted) {
		return new ChildrenNode(ParseTreeType.RANGE, 
								 buildList(ByteNode.valueOf(minByte), ByteNode.valueOf(maxByte)), inverted);
	}
	
	public static final ParseTree buildSet(final ParseTree...parseTrees) {
		return new ChildrenNode(ParseTreeType.SET, buildList(parseTrees));
	}
	
	public static final ParseTree buildInvertedSet(final ParseTree...parseTrees) {
		return new ChildrenNode(ParseTreeType.SET, buildList(parseTrees), INVERTED);
	}
	
	private static final List<ParseTree> buildList(final ParseTree...parseTrees) {
		return Arrays.asList(parseTrees);
	}
	

	/*
	 * Public methods
	 */
	
	@Override
	public ParseTree parse(final String expression) throws ParseException {
		if (expression == null || expression.isEmpty()) {
			throw new ParseException("Null or empty expression not allowed.");
		}
		return parseAlternatives(new StringParseReader(expression));
	}
	
	
	/*
	 * Private methods
	 */
	
	private ParseTree parseAlternatives(final StringParseReader expression) throws ParseException {
		final List<ParseTree> alternatives = new ArrayList<ParseTree>(8);
		while (!expression.atEnd() && expression.peekBehind() != CLOSE_GROUP) {
			alternatives.add(parseSequence(expression));
		}
		return optimisedAlternatives(alternatives, expression);
	}
	
	
	private ParseTree optimisedAlternatives(final List<ParseTree> alternatives, 
			                                final StringParseReader expression)
			throws ParseException {
		final int numAlternatives = alternatives.size();
		
		// If there are no alternatives, throw an error:
		if (numAlternatives == 0) {
			throw new ParseException(addContext("No alternatives were found.", expression));
		}
		
		// If there is only a single alternative, then just return the alternative directly.
		if (numAlternatives == 1) {
			return alternatives.get(0);
		}
		
		// See if there is more than one alternative that only matches sequences of length one.
		// If there is, they can be more efficiently represented as a set match for those byte values.
		final ParseTree optimisedSet = optimiseSingleByteAlternatives(alternatives);
		
		// If there are no remaining alternatives (all got put into the set), return the set directly:
		if (alternatives.size() == 0) {
			return optimisedSet;
		}
		
		// If there is now an optimised set and some remaining alternatives, add it to the list of alternatives:
		if (optimisedSet != null) {
			alternatives.add(optimisedSet);
		}
		
		// Return an alternatives type with the alternatives as children:
		return new ChildrenNode(ParseTreeType.ALTERNATIVES, alternatives);
	}
	
	
	/**
	 * Looks at a list of alternatives for alternatives that only match a sequence of length one each.
	 * If there is more than one of them, they can be collapsed into a single SET ParseTree which 
	 * matches all of the alternatives in a single set matching step.
	 * <p>
	 * If it discovers alternatives that can be optimised, it removes them from the list of alternatives
	 * passed in, and returns a SET ParseTree consisting of the alternatives which can be treated as a set.
	 * If there are no optimisations possible, it does not modify the list passed in, and returns null for
	 * the set of alternatives.
	 * 
	 * @param alternatives The list of alternatives to optimise.
	 * @return A SET ParseTree for the optimised alternatives (removing them from the original list passed in),
	 *         or null if there are no optimisations possible.
	 */
	private ParseTree optimiseSingleByteAlternatives(final List<ParseTree> alternatives) {
		// Find out if there is more than one alternative that matches only a sequence of length one:
		int numOptimisableAlternatives = 0;
		for (final ParseTree alternative : alternatives) { 
			if (matchesSingleByteLength(alternative)) {
				numOptimisableAlternatives++;
			}
		}
		
		// If there are, build a list of them, remove them from the original list of alternatives,
		// and return a set node of the optimisable alternatives:
		if (numOptimisableAlternatives > 1) {
			final List<ParseTree> setChildren = new ArrayList<ParseTree>(numOptimisableAlternatives);
			final Iterator<ParseTree> altIterator = alternatives.iterator();
			while (altIterator.hasNext()) {
				final ParseTree currentAlternative = altIterator.next();
				if (matchesSingleByteLength(currentAlternative)) {
					setChildren.add(currentAlternative);
					altIterator.remove();
				}
			}
			return new ChildrenNode(ParseTreeType.SET, setChildren);
		}
		
		// No optimisable alternatives: return null and don't modify the original list of alternatives:
		return null;
	}
	
	
	private boolean matchesSingleByteLength(final ParseTree node) {
		switch (node.getParseTreeType()) {
			case BYTE: 			
			case RANGE:
			case SET:		
			case ANY:
			case ALL_BITMASK:	
			case ANY_BITMASK: {
				return true;
			}
			default : return false;
		}
	}
	

	private ParseTree parseSequence(final StringParseReader expression) throws ParseException {
		final List<ParseTree> sequenceNodes = new ArrayList<ParseTree>();
		int currentChar = expression.read();
		boolean requireRangeValue = false;
		PARSE_SEQUENCE: while (currentChar >= 0) {
			if (foundQuantifiedAtoms(currentChar, expression, sequenceNodes)) {
				if (requireRangeValue) {
					createRange(sequenceNodes, expression);
					requireRangeValue = false;
				}
			} else if (!foundWhitespaceAndComments(currentChar, expression)) {
				if (requireRangeValue) {
					throw new ParseException(addContext("A range value was expected", expression));
				}
				switch (currentChar) {
					/*
					 * Groups (i.e. another set of alternative sequences):
					 */
					case OPEN_GROUP: {
						sequenceNodes.add(parseAlternatives(expression));
						break;
					}
				
					/*
					 * Alternatives or closing the group ends this sequence.
					 */
					case ALTERNATIVE:
					case CLOSE_GROUP: {
						break PARSE_SEQUENCE;
					}		
					
					/*
					 * A range
					 */
					case RANGE_SEPARATOR: {
						requireRangeValue = true;
						break;
					}
					
					/*
					 * Unexpected character:
					 */
					default: {
						throw new ParseException(addContext("Unexpected character [" + (char) currentChar + ']', expression));
					}
				}
			}
			currentChar = expression.read();
		}
		
		if (requireRangeValue) {
			throw new ParseException(addContext("Cannot have a range without a second value.", expression));
		}
		
		if (sequenceNodes.isEmpty()) {
			throw new ParseException(addContext("Cannot have an empty sequence", expression));
		}
		return sequenceNodes.size() == 1? sequenceNodes.get(0) 
										 : new ChildrenNode(ParseTreeType.SEQUENCE, sequenceNodes);
	}

	
	private void createRange(final List<ParseTree> sequence,
						     final StringParseReader expression) throws ParseException {
		final ParseTree secondRangeValue = popLastNode(sequence, expression);
		if (secondRangeValue.getParseTreeType() != ParseTreeType.BYTE) { 
			throw new ParseException(addContext("The second range value must be of type BYTE: " + secondRangeValue, expression));
		}
		if (secondRangeValue.isValueInverted()) {
			throw new ParseException(addContext("The second value of a range cannot be inverted " + secondRangeValue, expression));
		}
		final ParseTree firstRangeValue  = popLastNode(sequence, expression);
		if (firstRangeValue.getParseTreeType() != ParseTreeType.BYTE) { 
			throw new ParseException(addContext("The first range value must be of type BYTE: " + firstRangeValue, expression));
		}
		sequence.add(new ChildrenNode(ParseTreeType.RANGE, firstRangeValue.isValueInverted(), 
									  ByteNode.valueOf(firstRangeValue.getByteValue()),
									  ByteNode.valueOf(secondRangeValue.getByteValue())));
	}

	
	/**
	 * Removes the last ParseTree node from a List of ParseTrees.
	 */
	private ParseTree popLastNode(final List<ParseTree> sequence,
							 	  final StringParseReader expression) throws ParseException {
		if (sequence.isEmpty()) {
			throw new ParseException(addContext("Tried to remove the last node in a sequence, but it was empty", expression));
		}
		return sequence.remove(sequence.size() - 1);
	}

	
	
	private boolean foundWhitespaceAndComments(final int currentChar,
											   final StringParseReader expression) {
		switch (currentChar) {
			case ' '		:	case '\t':	case '\r':	case '\n': {
				return true;
			}
			case COMMENT	: {
				expression.readPastChar('\n');
				return true;
			}
			default: return false;
		}
	}
	
	
	private boolean foundAtoms(final int currentChar,
							   final StringParseReader expression,
							   final List<ParseTree> nodes) throws ParseException {
		boolean inverted = false;
		int charToMatch = currentChar;
		if (charToMatch == INVERT) {
			inverted = true;
			charToMatch = expression.read();
		}
		final ParseTree node = matchAtoms(charToMatch, expression, inverted);
		if (node != null) {
			nodes.add(node);
		}
		return node != null;
	}
	
	
	private boolean foundQuantifiedAtoms(final int currentChar, 
										 final StringParseReader expression, 
									     final List<ParseTree> nodes) throws ParseException {
		boolean inverted = false;
		int charToMatch = currentChar;
		if (charToMatch == INVERT) {
			inverted = true;
			charToMatch = expression.read();
		}
		ParseTree node = matchAtoms(charToMatch, expression, inverted);
		if (node != null) {
			nodes.add(node);
		} else {
			node = matchQuantifiers(charToMatch, expression, nodes);
			if (node != null) {
				nodes.add(node);
			}
		}
		return node != null;
	}
	
	
	private void checkQuantifiable(final ParseTree node,
						           final StringParseReader expression) throws ParseException {
		switch (node.getParseTreeType()) {
			case BYTE: 			case RANGE:			case SET:		case ANY:
			case SEQUENCE:		case ALTERNATIVES:	case STRING:	case CASE_INSENSITIVE_STRING:
			case ALL_BITMASK:	case ANY_BITMASK: {
				return;
			}
		}
		throw new ParseException(addContext("The node: " + node + " is not quantifiable", expression));
	}
	
	private ParseTree matchAtoms(final int currentChar, 
								 final StringParseReader expression,
								 final boolean inverted) throws ParseException {
		switch (currentChar) {
			case '0': 	case '1':	case '2':	case '3':	case '4':	
			case '5': 	case '6':	case '7':	case '8':	case '9':
			case 'a': 	case 'b':	case 'c':	case 'd':	case 'e':	case 'f':	
			case 'A':	case 'B':	case 'C':	case 'D':	case 'E':	case 'F': {
				return ByteNode.valueOf(expression.readHexByte(currentChar), inverted);
			}
											
			case ANY: {
				return BaseNode.ANY_NODE;
			}
			
			case ALL_BITMASK: {
				return new ByteNode(ParseTreeType.ALL_BITMASK, expression.readHexByte(), inverted);
			}
			
			case ANY_BITMASK: {
				return new ByteNode(ParseTreeType.ANY_BITMASK, expression.readHexByte(), inverted);
			}
			
			case STRING_QUOTE:	{
				final String stringValue = expression.readString(STRING_QUOTE);
				if (stringValue.length() == 1) {
					return ByteNode.valueOf(ByteUtils.getBytes(stringValue)[0], inverted);
				}
				if (inverted) {
					throw new ParseException(addContext("Strings cannot be inverted", expression));
				}
				return new StringNode(stringValue);
			}
			
			case CASE_INSENSITIVE_QUOTE: {
				final String stringValue = expression.readString(CASE_INSENSITIVE_QUOTE);
				if (inverted) {
					throw new ParseException(addContext("Case insensitive strings cannot be inverted", expression));
				}
				return new StringNode(stringValue, ParseTreeType.CASE_INSENSITIVE_STRING);
			}
						               							   
			case SHORTHAND_ESCAPE:	{
				return parseShorthand(expression, inverted);
			}
			
			case OPEN_SET: 	{
				return parseSet(expression, inverted);
			}
			
			default: return null;
		}
	}
	
	
	private ParseTree parseShorthand(final StringParseReader expression,
						              final boolean inverted) throws ParseException {
		final int character = expression.read();
		switch (character) {
			case 't': return inverted? ByteNode.valueOf((byte) '\t', INVERTED) 	: TAB;
			case 'n': return inverted? ByteNode.valueOf((byte) '\n', INVERTED) 	: NEWLINE;
			case 'r': return inverted? ByteNode.valueOf((byte) '\r', INVERTED) 	: CARRIAGE_RETURN;
			case 'v': return inverted? ByteNode.valueOf((byte) 0x0b, INVERTED) 	: VERTICAL_TAB;
			case 'f': return inverted? ByteNode.valueOf((byte) 0x0c, INVERTED) 	: FORM_FEED;
			case 'e': return inverted? ByteNode.valueOf((byte) 0x1e, INVERTED)	: ESCAPE;
			case 'd': return inverted? NOT_DIGITS_RANGE    						: DIGITS_RANGE;
			case 'D': return inverted? DIGITS_RANGE        						: NOT_DIGITS_RANGE;
			case 'w': return inverted? NOT_WORD_CHAR_SET  						: WORD_CHAR_SET;
			case 'W': return inverted? WORD_CHAR_SET       						: NOT_WORD_CHAR_SET;
			case 's': return inverted? NOT_WHITESPACE_SET  						: WHITESPACE_SET;
			case 'S': return inverted? WHITESPACE_SET      						: NOT_WHITESPACE_SET;
			case 'l': return inverted? NOT_LOWERCASE_RANGE 						: LOWERCASE_RANGE;
			case 'L': return inverted? LOWERCASE_RANGE     						: NOT_LOWERCASE_RANGE;
			case 'u': return inverted? NOT_UPPERCASE_RANGE 						: UPPERCASE_RANGE;
			case 'U': return inverted? UPPERCASE_RANGE    						: NOT_UPPERCASE_RANGE;
			case 'i': return inverted? NOT_ASCII_RANGE     						: ASCII_RANGE;
			case 'I': return inverted? ASCII_RANGE         						: NOT_ASCII_RANGE;
			default: throw new ParseException(addContext("Unexpected shorthand character [" + 
														   (char) character + ']', expression));
		}
	}
	
	
	private ParseTree parseSet(final StringParseReader expression,
				                final boolean inverted ) throws ParseException {
		final List<ParseTree> setNodes = new ArrayList<ParseTree>();
		int currentChar = expression.read();
		boolean requireRangeValue = false;
		PARSE_SET: while (currentChar >= 0) {
			if (foundAtoms(currentChar, expression, setNodes)) {
				if (requireRangeValue) {
					createRange(setNodes, expression);
					requireRangeValue = false;
				}
			} else if (!foundWhitespaceAndComments(currentChar, expression)) {
				if (currentChar == CLOSE_SET) {
					break PARSE_SET;
				}
				if (currentChar == RANGE_SEPARATOR) {
					requireRangeValue = true;
				} else {
					throw new ParseException(addContext("Unexpected character [" + (char) currentChar + ']', expression));
				}
			}
			currentChar = expression.read();
		}
		
		if (requireRangeValue) {
			throw new ParseException(addContext("Cannot have a range without a second value.", expression));
		}
		if (currentChar < 0) {
			throw new ParseException(addContext("The expression ended without closing the set", expression));
		}
		if (setNodes.isEmpty()) {
			throw new ParseException(addContext("Cannot have an empty set", expression));
		}
		// We do not try to optimise a set with a single child by just returning the child directly, as we
		// do for sequences and alternatives.
		// If the set is inverted, then the child should be inverted too from its current inversion status.
		// We don't currently have an easy way to invert a node (could add a method to do so to the interface).
		// Not all children have the same meaning outside of a set as inside one.  E.g. strings are interpreted
		// as a set of bytes to match, not a sequence.  So for simplicity we leave sets exactly as they are
		// syntactically defined.  Compilers must deal with nested sets or sets which aren't really needed.
		return new ChildrenNode(ParseTreeType.SET, setNodes, inverted);
	}
	
	
	private ParseTree matchQuantifiers(final int currentChar, 
									   final StringParseReader expression,
									   final List<ParseTree> nodes) throws ParseException {
		ParseTree quantifier = null;
		if (nodes.size() > 0) {
			final ParseTree nodeToQuantify = nodes.get(nodes.size() - 1);
			switch (currentChar) {
				case MANY: {
					quantifier = new ChildrenNode(ParseTreeType.ZERO_TO_MANY, nodeToQuantify);
					break;
				}
				case ONE_TO_MANY: {
					quantifier = new ChildrenNode(ParseTreeType.ONE_TO_MANY, nodeToQuantify);
					break;
				}
				case OPEN_REPEAT: {
					quantifier = parseRepeat(expression, nodeToQuantify);
					break;
				}
				case OPTIONAL: { 		
					quantifier = new ChildrenNode(ParseTreeType.OPTIONAL, nodeToQuantify);
					break;
				}
			}
			if (quantifier != null) {
				checkQuantifiable(nodeToQuantify, expression);			
				nodes.remove(nodes.size() - 1);
			}
		}
		return quantifier;
	}
	

	private ParseTree parseRepeat(final StringParseReader expression,
								  final ParseTree nodeToRepeat) throws ParseException {
		final int firstValue = expression.readInt();
		int nextToken = expression.read();
		if (nextToken == CLOSE_REPEAT) {
			if (firstValue == 0) {
				throw new ParseException(addContext("Single repeat value cannot be zero", expression));
			}
			return new ChildrenNode(ParseTreeType.REPEAT, new IntNode(firstValue), nodeToRepeat);
		}
		if (nextToken == REPEAT_SEPARATOR) {
			ParseTree repeatNode;
			if (expression.peekAhead() == MANY) {
				expression.read();
				repeatNode = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MANY, 
											  new IntNode(firstValue), nodeToRepeat);
			} else {
  			    repeatNode = new ChildrenNode(ParseTreeType.REPEAT_MIN_TO_MAX, 
  			    							  new IntNode(firstValue), 
  			    							  new IntNode(expression.readInt()),
  			    							  nodeToRepeat);
			}
			nextToken = expression.read();
			if (nextToken == CLOSE_REPEAT) {
				return repeatNode;
			}
			throw new ParseException(addContext("No closing } for repeat instruction " + repeatNode, expression));
		}
		throw new ParseException(addContext("No closing } for repeat instruction with firstValue " + firstValue, expression));
	}
	
	
	private String addContext(String description, StringParseReader expression) {
		return description + ".  Error occurred at position [" +
				expression.getPosition() +
				"] in expression [" + expression + ']';
	}
	

}
