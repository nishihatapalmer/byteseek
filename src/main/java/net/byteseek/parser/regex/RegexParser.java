/*
 * Copyright Matt Palmer 2012-2019, All rights reserved.
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

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.byteseek.parser.*;
import net.byteseek.parser.tree.ParseTree;
import net.byteseek.parser.tree.ParseTreeType;
import net.byteseek.parser.tree.node.BaseNode;
import net.byteseek.parser.tree.node.ByteNode;
import net.byteseek.parser.tree.node.ChildrenNode;
import net.byteseek.parser.tree.node.IntNode;
import net.byteseek.parser.tree.node.StringNode;

/*
 * Ideas for future...
 */

//FEATURE: syntax for set subtraction: ['a'-'z' -['aeiou']] ... confusion with range syntax...?
//         Could insist second element is explicitly a set...?

//FEATURE: is there any use for set intersection?  e.g. [09-1A n ~_D] - only the elements in common?
//         Second element a set again?  [09-1A n [~_D 04]]
//         Freeform: 09-1A n [~_D 04]   It's freer, but possibly harder to read, not obvious both elements
//         are a single set rather than two elements in sequence.  Inside a set, we know this is matching at
//         a single position.

//FEATURE: greedy and non greedy matching with ?

//FEATURE: syntax for encoding of 32 and 64 bit numbers in big and little endian formats.

//FEATURE: "jump to location contained in a match" - e.g. chain addresses together.  would need some kind of
//         arithmetic support too (functions).  could be relative addresses or absolute, might need transforming.
//         This sort of thing can't be normally supported with an NFA or DFA based approach.

//TODO: update javadoc : strings in sets always use ISO-8859-1 charset, no matter what other charsets are
//                       defined for strings outside sets.
//
//                       This feature is syntactic sugar, and only exists to contribute to aid in expressing
//                       the bytes which will appear in a set, in a clear, readable and transparent fashion.
//
//                       Making the expression of bytes in a set dependent on detailed knowledge of multi-byte
//                       character encodings violates the purpose of having strings in sets.
//                       It becomes opaque and dependent on detailed technical knowledge of multi-byte character set
//                       encodings.
//
//                       So - using multi-byte encodings, or multiple encodings, of strings in sets makes it
//                       extremely hard, even for those who possess detailed technical knowledge, to just read
//                       the set definition and know what bytes would be matched by it.
//                       Generally, for encodings of strings, you are interesting in a byte sequence,
//                       rather than trying to match all possible bytes of a string at a
//                       single position (the set) in an expression
//
//                       Therefore, we only permit ISO-8859-1 encoding for set definitions.  It makes sets a bit
//                       more readable in some circumstances.  It is easy to know what the set will match by
//                       just looking at the text representation of it.
//
//                       It is different for strings outside sets, since being able to specify particular character set
//                       encodings of byte sequences makes it much easier to specify strings encoded in a particular
//                       character set.  Without this facility, encoding strings to match in anything other
//                       than ISO-8859-1 would need precise specification of the byte sequence as hex bytes.
//                       This is extremely unreadable and error prone - and couldn't be encoded as a string
//                       in the syntax, or represented by it in a regular expression.
//
//                       Although it makes it hard to know the exact byte sequence which would be matched,
//                       it makes the syntax for specifying such matches much more comprehensible - and indeed
//                       allows the use of strings to match... strings which happen to be encoded in particular
//                       character sets, which will be a common situation.
//
//                       Therefore, allowing the specification of different character set encodings for string
//                       matching in the regular expression massively simplifies the syntax where that is a requirement,
//                       but if that requirement doesn't exist, strings will simply be matched as ISO-8859-1 by
//                       default, adding no further complexity to the syntax of the expression.

//TODO: update javadoc to reflect byteseek 3.0 syntax - or remove this and reference the syntax text file instead.
/**
 * A hand-written regular expression parser for byteseek. The syntax is designed
 * to support matching byte structures. 
 * <p>&nbsp;
 * The parser has no state, so it is entirely thread-safe.
 * <p>&nbsp;
  * <strong>Syntax</strong><br>
  * The syntax is the byteseek regular expression syntax, which is designed to make
  * byte searching easier.  Mostly this involves bytes being directly specified as hexadecimal,
  * with text in quoted strings.
  * <p>&nbsp;
  * <strong>Comments</strong><br>
  * Byteseek regular expressions can have comments in them, using a <strong>#</strong> symbol.  
  * All text following the comment symbol is ignored until the next end of line character.
  * <p>&nbsp; <blockquote><pre><code>
  * 01 ff c1 # Match byte sequence 0x01, 0xff, 0xc1
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Whitespace</strong><br>
  * All spaces, tabs, new lines and carriage returns separating syntax elements will
  * be ignored, unless they appear within a quoted string.
  *  <p>&nbsp; <blockquote><pre><code>
  * 01ffc1               # match bytes 01 ff c1
  * 01       ff       c1 # match bytes 01 ff c1
  * 01 ff 'some text' c1 # match bytes 01 ff, the string 'some text', then the byte c1 
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Bytes</strong><br>
  * Bytes are written as 2 digit hexadecimal numbers (any case allowed).  Spacing between
  * them doesn't matter, as whitespace is ignored.  However, you can't separate the digits of a hex
  * character with whitespace.
  *  <p>&nbsp; <blockquote><pre><code>
  * 00 FF 1a dE # match byte 00, ff, 1a, de
  *	</code></pre></blockquote> <p>&nbsp;
  * To specify that you mean the inverse of a value (all other byte values), prepend a <strong>^</strong>
  * symbol to the byte value.
  *  <p>&nbsp; <blockquote><pre><code>
  * 00 ^FF 1a ^dE # match byte 00, any byte but ff, byte 1a and any byte but dE. 
  *	</code></pre></blockquote> <p>&nbsp; </p>
  *  <p>&nbsp;
  * <strong>Any byte</strong>
  * Any byte can be matched using the full stop symbol (as in most regular expression languages).
  *  <p>&nbsp; <blockquote><pre><code>
  * .       # matches any byte
  * 01 . ff # matches 0x01, followed by any byte, then 0xff
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Ranges</strong>
  * Ranges of byte values can be specified using a hypen separator to specify a range:
  *  <p>&nbsp; <blockquote><pre><code>
  * 00-7f # all the ascii bytes
  * 30-39 # all the digits
  *	</code></pre></blockquote> <p>&nbsp;
  * If you want every byte except within a range, prepend the range with a <strong>^</strong> symbol:
  *  <p>&nbsp; <blockquote><pre><code>
  * ^00-7f # all the non-ascii bytes
  * ^30-39 # all the bytes which are not ASCII digits
  *	</code></pre></blockquote> <p>&nbsp;
  * Ranges can also be specified using single quoted, single character strings:
  *  <p>&nbsp; <blockquote><pre><code>
  * 'a'-'z'  # all the lowercase ASCII letters. 
  * ^'0'-'9' # all the bytes which are not ASCII digits
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Strings</strong>
  * Text (ASCII only) is delimited using single quotes:
  *  <p>&nbsp; <blockquote><pre><code>
  * 'testing testing 123' # the string 'testing testing 123'
  * 01 '01'               # the byte 0x01, followed by the text '01'
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Case insensitive strings</strong><br>
  * Case-insensitive text can be written delimited with `back-ticks`:
  *  <p>&nbsp; <blockquote><pre><code>
  * `HtMl public`         # match the text HTML PUBLIC case insensitively.
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Bitmasks</strong><br>
  * If you want to match a bitmask, there are two methods of doing so in byteseek.
  * You can match all the bits in a bitmask, specified by the <strong>&amp;</strong> symbol,
  * or you can match any of the bits in a bitmask, specified by the <strong>~</strong> symbol.
  * Prepend the appropriate bitmask symbol to a two digit hex number:
  *  <p>&nbsp; <blockquote><pre><code>
  * &amp;7F   # match all these bits    01111111
  * &amp;0F   # match all these bits    00001111
  * &amp;81   # match all these bits    10000001
  * ~7F   # match any of these bits 01111111
  * ~0F   # match any of these bits 00001111
  * ~81   # match any of these bits 10000001
  *	</code></pre></blockquote> <p>&nbsp;
  * The intention of the all bits match is that, to match all the bits
  * in a bitmask, a byte ANDed with the bitmask should equal the bitmask.
  * This means that an 'all bits' bitmask of zero will match all bytes.
  *  <p>&nbsp; <blockquote><pre><code>
  * byteValue &amp; bitmask == bitmask
  *        01 &amp; 01      == 01      match
  *        ff &amp; 01      == 01      match
  *        02 &amp; 01      == 00      no match
  *        01 &amp; 00      == 00      match
  *        ff &amp; 00      == 00      match
  *	</code></pre></blockquote> <p>&nbsp;
  * The intention of the any bits match is that, to match any of the bits
  * in a bitmask, a byte ANDed with the bitmask should not be zero.  Note that
  * the result could be negative, as bytes in Java are signed.  This means
  * that an 'any bits' bitmask of zero will match no bytes at all.
  *  <p>&nbsp; <blockquote><pre><code>
  * byteValue &amp; bitmask != 00
  *        01 &amp; 01      == 01      match
  *        ff &amp; 01      == 01      match
  *        02 &amp; 01      == 0       no match
  *        01 &amp; 00      == 00      no match
  *        ff &amp; 00      == 00      no match
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Sets</strong><br>
  * Sets of bytes can be specified using square brackets.  Sets can contain bytes, ranges, strings,
  * case insensitive strings, bitmasks and other sets.
  *  <p>&nbsp; <blockquote><pre><code>
  * [01 02 03]           # match the set of bytes 0x01, 0x02 or 0x03     
  * 'version' [01 02 03] # match the string 'version' followed by any of the bytes 0x01, 0x02 or 0x03. 
  *	</code></pre></blockquote> <p>&nbsp;
  * Sets can contain strings, which specify that those ASCII bytes are members of the set too, not a 
  * sequence of characters.  Case insensitive strings just specify all the bytes which could match
  * case insensitively.
  *  <p>&nbsp; <blockquote><pre><code>
  * ['0123456789']       # the set of all the digit bytes.
  * [ff '0123456789' 00] # the set of all the digit bytes and the bytes 0x00 and 0xff
  * [`HTML`]             # The set of bytes which case insensitively match HTML.
  *	</code></pre></blockquote> <p>&nbsp;
  * If you want to specify the inverse set, prepend the set with a <strong>^</strong> symbol:
  *  <p>&nbsp; <blockquote><pre><code>
  * ^['0'-'9']            # anything but the set of all the digit bytes.
  * ^[ff '0123456789' 00] # anything but the set of all the digit bytes and the bytes 0x00 and 0xff
  * ^[^'0'-'9']           # an inefficient (double negative) way of specifying all the digit bytes.
  *	</code></pre></blockquote> <p>&nbsp; </p>
  *  <p>&nbsp;
  * <strong>Sub expressions</strong><br>
  * If you need to specify that something only applies to part of the whole expression,
  * then you can group subsequences into sub expressions using round brackets.
  * Sub expressions can also be nested within each other.  
  *  <p>&nbsp; <blockquote><pre><code>
  * 01 02 (03 04) 05       # this sub expression does nothing - the round brackets are superfluous
  * (01 02 (03 (04)) 05)   # these sub expressions also do nothing.
  *	</code></pre></blockquote> <p>&nbsp;
  * The above examples show sub expressions which are valid syntactically, but they don't do anything.  
  * The round brackets are entirely unnecessary.  Sub expressions become useful when you need to quantify
  * a part of an expression, for example, to say it repeats between 5 and 10 times.    
  * They are also needed when you want to specify a list of alternative sequences within an expression.   
  * These more interesting uses of sub expressions will become clear in the next sections.
  *  <p>&nbsp;
  * <strong>Alternatives</strong><br>
  * Alternatives are written separated by a pipe character:
  *  <p>&nbsp; <blockquote><pre><code>
  * 'this' | 'that' | 00 FF 1a     # match 'this', 'that', or 0x00 0xFF 0x1a
  * 01 02 ('one'|'two'|'three') 03 # match the sequence 1, 2 followed by ('one', 'two' or 'three'), ending with 3
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Optional elements</strong><br>
  * To specify that an element is optional, append a <strong>?</strong> symbol to the element.
  * If you want an entire expression to be optional, enclose it with round brackets and append
  * the question mark to it.
  *  <p>&nbsp; <blockquote><pre><code>
  * de?            # optional byte 0xde
  * 'extra fries'? # optional 'extra fries'
  * (01 02 03)?    # optional sequence of bytes (0x01 0x02 0x03)
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Zero to many elements</strong><br>
  * To specify that an element can be repeated from zero to many times, append a <strong>*</strong> symbol
  * to the element.  If you want an entire expression to be repeated zero to many times, enclose it with
  * round brackets and append the * to it.
  *  <p>&nbsp; <blockquote><pre><code>
  * 10*                               # repeat byte 0x10 from zero to many times.
  * 'stuff'*                          # repeat 'stuff' zero to many times.
  * ('bytes' 00 01 02 'counting')*    # repeat entire expression in brackets from zero to many times.
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>One to many elements</strong><br>
  * To specify that an element can be repeated from one to many times, append a <strong>+</strong> symbol
  * to the element.  If you want an entire expression to be repeated one to many times, enclose it with
  * round brackets and append the + to it.
  *  <p>&nbsp; <blockquote><pre><code>
  * 10+                               # repeat byte 0x10 from one to many times.
  * 'stuff'+                          # repeat 'stuff' one to many times.
  * ('bytes' 00 01 02 'counting')+    # repeat entire expression in brackets from one to many times.
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Repeat exactly</strong><br>
  * To specify that an element should be repeated an exact number of times, append a positive integer 
  * enclosed in curly brackets to the end of the element, e.g. <strong>ff{4}</strong>.
  * If you want to repeat an entire expression, enclose the expression in round brackets
  * and append the repeat quantifier to the end.
  *  <p>&nbsp; <blockquote><pre><code>
  * 10{40}                              # repeat byte 0x10 forty times.
  * 'stuff'{3}                          # repeat 'stuff' three times.
  * ('bytes' 00 01 02 'counting'){9}    # repeat entire expression in brackets nine times.
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Repeat min to max times</strong><br>
  * To specify that an element can be repeated between a minimum and maximum number, append 
  * two positive integers separated by a comma, enclosed in curly brackets to the end of the
  * element , e.g. <strong>ff{2,6}</strong>.  If you want to specify an entire expression,
  * enclose the expression in round brackets and append the repeat quantifier to the end.
  *  <p>&nbsp; <blockquote><pre><code>
  * 10{10,40}                           # repeat byte 0x10 between 10 and forty times.
  * 'stuff'{3,6}                        # repeat 'stuff' between three and six times
  * ('bytes' 00 01 02 'counting'){1,3}  # repeat entire expression in brackets between 1 and 3 times.
  * ff{0,1}                             # repeat byte 0xff either zero or once - the same as <strong>ff?</strong> 
  *	</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Repeat min to many times</strong><br>
  * To specify that an element repeats at least a minimum number of times, but can repeat
  * infinitely afterwards, append a positive integer and star separated by a comma, and enclosed
  * in curly brackets to the end of the element, e.g. <strong>ff{5,*}</strong>.
  * If you want to repeat an entire expression, enclose the expression in round brackets and
  * append the repeat quantifier to the end. 
  *  <p>&nbsp; <blockquote><pre><code>
  * 10{10,*}                            # repeat byte 0x10 between 10 and infinite times.
  * 'stuff'{3,*}                        # repeat 'stuff' between three and infinite times
  * ('bytes' 00 01 02 'counting'){1,*}  # repeat entire expression in brackets between 1 and infinite times.
  * ff{0,*}                             # repeat byte 0xff from zero to infinite times - the same as <strong>ff*</strong>	
  * ff{1,*}                             # repeat byte 0xff from one to infinite times - the same as <strong>ff+</strong>
  *</code></pre></blockquote> <p>&nbsp;
  *  <p>&nbsp;
  * <strong>Shorthands</strong><br>
  * There are many common bytes and sets of bytes which appear regularly in regular expressions.
  * Most regular expression languages support shorthands, which give you a memorable and often 
  * shorter way to specify them.  The shorthands supported by this parser are listed below.
  *  <p>&nbsp; <blockquote><pre><code>
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
  * \\u   # uppercase ascii characters   'A'-'Z'
  * \U   # not uppercase ascii chars   ^'A'-'Z'
  * \l   # lowercase ascii characters   'a'-'z'
  * \L   # not lowercase ascii chars   ^'a'-'z'  
  *	</code></pre></blockquote> <p>&nbsp; </p>
  *
  * @author Matt Palmer
  */
public class RegexParser implements Parser<ParseTree> {

    /***************************************************************************
     * Constants
     */

    /*
     * Private static constants
     */
    private static final boolean INVERTED 		  = true;
    private static final boolean NOT_INVERTED	  = false;
    private static final ParseTree NO_NODE_TO_ADD = new BaseNode(ParseTreeType.BYTE); // must be a unique node in memory.

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
     * Private syntactic character constants
     */
    private static final char ANY                    	= '.';
    private static final char DONT_CARE                 = '_';
    private static final char ALTERNATIVE            	= '|';
    private static final char COMMENT 					= '#';
    private static final char STRING_QUOTE 				= '\'';
    private static final char CASE_INSENSITIVE_QUOTE 	= '`';
    private static final char SHORTHAND_ESCAPE 			= '\\';
    private static final char OPEN_SET 					= '[';
    private static final char INVERT	 				= '^';
    private static final char RANGE_SEPARATOR 			= '-';
    private static final char ANYBITS                   = '~';
    private static final char CLOSE_SET 				= ']';
    private static final char OPTIONAL 					= '?';
    private static final char MANY 						= '*';
    private static final char ONE_TO_MANY 				= '+';
    private static final char OPEN_REPEAT 				= '{';
    private static final char REPEAT_SEPARATOR       	= ',';
    private static final char CLOSE_REPEAT 				= '}';
    private static final char OPEN_GROUP 				= '(';
    private static final char CLOSE_GROUP 				= ')';
    private static final char ENCODE_STRING             = '*';

	/*
	 * Public static utility methods
	 */

    /**
     * Creates a ParseTree of type RANGE with a minimum, maximum value, and whether the range should be inverted.
     *
     * @param minByte The minimum byte of the range
     * @param maxByte The maximum byte of the range.
     * @param inverted Whether the range should be inverted or not.
     * @return A ParseTree modelling a range of byte values (or its inverse).
     */
	public static final ParseTree buildRange(final byte minByte, final byte maxByte, final boolean inverted) {
		return new ChildrenNode(ParseTreeType.RANGE, 
								 buildList(ByteNode.valueOf(minByte), ByteNode.valueOf(maxByte)), inverted);
	}

    /**
     * Creates a ParseTree of type SET, with all the ParseTrees passed in as its children.
     * @param parseTrees ParseTree we want in a set.
     * @return A ParseTree of type SET, with all the ParseTrees passed in as its children.
     */
	public static final ParseTree buildSet(final ParseTree...parseTrees) {
		return new ChildrenNode(ParseTreeType.SET, buildList(parseTrees));
	}

    /**
     * Creates a ParseTree of type SET with inversion, with all the ParseTrees passed in as its children.
     * @param parseTrees ParseTree nodes we want in an inverted set.
     * @return A ParseTree of type SET with inversion, with all the ParseTrees passed in as its children.
     */
	public static final ParseTree buildInvertedSet(final ParseTree...parseTrees) {
		return new ChildrenNode(ParseTreeType.SET, buildList(parseTrees), INVERTED);
	}

    /**
     * Converts ParseTree passed in as parameters into a List of ParseTrees.
     * @param parseTrees The ParseTree nodes we want in a list.
     * @return A List of ParseTree nodes.
     */
	private static List<ParseTree> buildList(final ParseTree...parseTrees) {
		return Arrays.asList(parseTrees);
	}


	/****************************
	 * Private inner classes
	 */

    /**
     * This class is used to pass context around all the other methods -
     * effectively "global" variables needed by many methods, but unique for each call to parse,
     * making the RegExParser class stateless and thread-safe.
     */
	private static class ParseContext {
        public StringParseReader expression;
        public Charset[] encodings;
        public StringParseReader.WildByteSpec byteSpec;

	    public ParseContext(final String expression) {
	        this.expression = new StringParseReader(expression);
	        this.encodings = new Charset[] {Charset.forName("ISO_8859_1")};
	        this.byteSpec = new StringParseReader.WildByteSpec();
        }
    }

	/*******************************************************************************************************************
	 * Public methods
	 */
	
	@Override
	public ParseTree parse(final String expression) throws ParseException {
        //TODO; should this be an IllegalArgumentException instead?
	    if (expression == null || expression.isEmpty()) {
			throw new ParseException("Null or empty expression not allowed.", ParseInfo.NO_INFO);
		}
		return parseAlternatives(new ParseContext(expression));
	}


	/*******************************************************************************************************************
	 * Private methods
	 */

    /**
     * Parses a set of alternative sequences, separated by | symbols.
     * There do not have to be alternatives, but if there are this method handles them.
     * This is the top level parsing method, which can be called recursively from inner methods if they
     * discover a new set of alternative sequences within them.
     *
     * @param context All the data and objects needed to parse the alternatives.
     * @return A ParseTree containing all the alternative sequences as children.
     *
     * @throws ParseException If a syntactic problem is encountered parsing the expression.
     */
	private ParseTree parseAlternatives(final ParseContext context) throws ParseException {
		final List<ParseTree> alternatives = new ArrayList<ParseTree>(8);
		while (hasMoreAlternatives(context)) {
			alternatives.add(parseSequence(context));
		}
		return optimisedAlternatives(alternatives, context.expression);
	}

    /**
     * Returns true if there are more alternative sequences to process.
     *
     * @param context The current state of parsing.
     * @return true if there are more alterantive sequences to process.
     */
	private boolean hasMoreAlternatives(final ParseContext context) {
	    final boolean isFinished = context.expression.peekBehind() == CLOSE_GROUP || // The last char was stop alternatives.
                                   context.expression.atEnd();                       // There is no more string to process.
        return !isFinished; // Return not is finished.
	}

    /**
     * Parses a sequence of values and other expressions.
     *
     * @param context All the data and objects needed to parse the alternatives.
     * @return A ParseTree containing a sequence of ParseTree nodes forming the sequence.
     * @throws ParseException If there was any syntactic problem with parsing the expression.
     */
	private ParseTree parseSequence(final ParseContext context) throws ParseException {

	    // Setup default collections and values:
	    final List<ParseTree> sequenceNodes = new ArrayList<ParseTree>();
        boolean inverted = false;
        boolean expectSecondRangeValue = false;
        boolean expectAnybitsValue     = false;

        // Parse the expression
        final StringParseReader expression = context.expression;
        int currentChar;
        PARSE_SEQUENCE: while ((currentChar = expression.read()) >= 0) {
            final ParseTree nextNode;
            switch (currentChar) {

                /*********************************************************************************
                 *  Whitespace and comments - just ignore and go round again.
                 */
                case ' ':	case '\t':	case '\r': case '\n': {
                    nextNode = NO_NODE_TO_ADD;
                    break;
                }
                case COMMENT: {
                    expression.readPastChar('\n');
                    nextNode = NO_NODE_TO_ADD;
                    break;
                }

                /* ********************************************************************************
                 * Single byte matching atoms, which are invertible.
                 */

                // A byte value:
                case '0': 	case '1':	case '2':	case '3':	case '4':
                case '5': 	case '6':	case '7':	case '8':	case '9':
                case 'a': 	case 'b':	case 'c':	case 'd':	case 'e':	case 'f':
                case 'A':	case 'B':	case 'C':	case 'D':	case 'E':	case 'F':   case DONT_CARE: {
                    nextNode = parseWildByte(currentChar, inverted, expression, context.byteSpec);
                    break;
                }
                case SHORTHAND_ESCAPE:	{
                    nextNode = parseShorthand(expression, inverted);
                    break;
                }
                case OPEN_SET: 	{
                    nextNode = parseSet(context, inverted);
                    break;
                }

                /* **********************************************************************************************
                 * Value modifiers (invert next value or make the next value part of a range with the preceding value.
                 * They must throw a ParseException if inversion is on for them, or a range value is already expected.
                 */

                // inversion:
                case INVERT: {
                    checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                    nextNode = NO_NODE_TO_ADD;
                    inverted = true;
                    break;
                }
                // a range of values:
                case RANGE_SEPARATOR: {
                    checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                    nextNode = NO_NODE_TO_ADD;
                    expectSecondRangeValue = true; // set an expectation that we get another range value on the next round.
                    break;
                }
                case ANYBITS: { // can be inverted, can't be expecting a range value.
                    checkNoRangeValueExpected(expectSecondRangeValue, expression); // can't have a range separator if there's already one waiting.
                    checkNoAnybitsValueExpected(expectAnybitsValue, expression);
                    nextNode = NO_NODE_TO_ADD;
                    expectAnybitsValue = true;
                    break;
                }

                /* **********************************************************************************************
                 * Atoms which are not invertible.  They must throw a ParseException if inversion is on for them.
                 */

                case ANY: {
                    checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                    nextNode = BaseNode.ANY_NODE;
                    break;
                }
                case STRING_QUOTE:	{ // SPECIAL CASE: single character, single byte strings can be inverted (just a byte value), others not.
                    final String stringValue = expression.readString(STRING_QUOTE);
                    if (stringValue.length() == 1 && context.encodings.length == 1) {
                        final byte[] charEncoding = stringValue.getBytes(context.encodings[0]);
                        if (charEncoding.length == 1) {
                            nextNode = ByteNode.valueOf(charEncoding[0], inverted);
                            break;
                        }
                    }
                    checkNotInverted(inverted, "Strings cannot be inverted", expression);
                    if (context.encodings.length == 1) {
                        nextNode = new StringNode(expression, stringValue, context.encodings[0]);
                    } else {
                        final List<ParseTree> alternativeEncodings = new ArrayList<ParseTree>();
                        for (Charset charset : context.encodings) {
                            alternativeEncodings.add(new StringNode(expression, stringValue, charset));
                        }
                        nextNode = new ChildrenNode(ParseTreeType.ALTERNATIVES, alternativeEncodings);
                    }
                    break;
                }
                case CASE_INSENSITIVE_QUOTE: {
                    checkNotInverted(inverted, "Case insensitive strings cannot be inverted", expression);
                    final String value = expression.readString(CASE_INSENSITIVE_QUOTE);
                    if (context.encodings.length == 1) {
                        nextNode = new StringNode(expression, value, context.encodings[0], ParseTreeType.CASE_INSENSITIVE_STRING);
                    } else {
                        final List<ParseTree> alternativeEncodings = new ArrayList<ParseTree>();
                        for (Charset charset : context.encodings) {
                            alternativeEncodings.add(new StringNode(expression, value, charset, ParseTreeType.CASE_INSENSITIVE_STRING));
                        }
                        nextNode = new ChildrenNode(ParseTreeType.ALTERNATIVES, alternativeEncodings);
                    }
                    break;
                }

                /*************************************************************************************
                 * Start of a new group of alternative sequences, or the start of a string encoding statement.
                 * Parse the alternatives so they can be added to this sequence.
                 * Can't be inverted.
                 */
                case OPEN_GROUP: {
                	if (expression.peekAhead() == ENCODE_STRING) {
                	    parseEncodings(context);
                	    nextNode = NO_NODE_TO_ADD;
                    } else {
                        checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                        nextNode = parseAlternatives(context);
                    }
                    break;
                }

                /**************************************************************************************
                 * End of the existing sequence or group - stop processing more sequence nodes.
                 */
                case ALTERNATIVE:
                case CLOSE_GROUP: {
                    break PARSE_SEQUENCE;
                }

                /***************************************************************************************
                 * Value quantifiers.
                 * These pop the last node off the sequence and make it the child of a quantifier node.
                 * These should throw a ParseException if inversion is on for them,
                 * and also if the previous node in the sequence is not quantifiable.
                 */

                // value quantifiers:
                case MANY: {
                    checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                    nextNode = quantifyLastNode(sequenceNodes, ParseTreeType.ZERO_TO_MANY, expression);
                    break;
                }
                case ONE_TO_MANY: {
                    checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                    nextNode = quantifyLastNode(sequenceNodes, ParseTreeType.ONE_TO_MANY, expression);
                    break;
                }
                case OPTIONAL: {
                    checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                    nextNode = quantifyLastNode(sequenceNodes, ParseTreeType.OPTIONAL, expression);
                    break;
                }
                case OPEN_REPEAT: {
                    checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                    nextNode = parseRepeat(sequenceNodes, expression);
                    break;
                }

                /*****************************************************************************************
                 * Unexpected character, throw a ParseException.
                 */
                default: {
                    throw new ParseException("Unexpected character [" + (char) currentChar + ']', expression);
                }
            }

            // If there's a node to add, add it to our sequence of nodes.
            if (nextNode != NO_NODE_TO_ADD) {
                sequenceNodes.add(nextNode);

                // If we expect the last node added to be the second range value needed, then process as a range.
                if (expectSecondRangeValue) {
                    createRange(sequenceNodes, expression);
                    expectSecondRangeValue = false;
                }

                if (expectAnybitsValue) {
                    createAnybits(sequenceNodes, expression);
                    expectAnybitsValue = false;
                }

                // Reset inverted to false after adding a node.
                inverted = false;
            }
        }

        // Can't exit a sequence with an outstanding value still expected:
        checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);

        // Return the appropriate ParseTree or throw a ParseException depending on how many nodes in the sequence:
        switch (sequenceNodes.size()) {
            case 0:  throw new ParseException("Cannot have an empty sequence", expression);
            case 1:  return sequenceNodes.get(0);
            default: return new ChildrenNode(expression, ParseTreeType.SEQUENCE, sequenceNodes);
        }
    }

    private void parseEncodings(final ParseContext context) throws ParseException {
        context.expression.read(); // read past the encode string character (*)
        final String encodingString = context.expression.readString(CLOSE_GROUP); // read up to the next closing round bracket.
        final String[] encodings = encodingString.split(",");
        final Charset[] charsets = new Charset[encodings.length];
        int charSetIndex = 0;
        for (String encoding : encodings) {
            final String trimmed = encoding.trim(); // strip off any spaces from the start or end.
            final Charset charset;
            try {
                charset = Charset.forName(trimmed);
            } catch (IllegalCharsetNameException | UnsupportedCharsetException noSuchName) {
                throw new ParseException("The charset name provided: " + trimmed + " was not a supported charset.",
                                         context.expression);
            }
            charsets[charSetIndex++] = charset;
        }
        context.encodings = charsets;
    }

    private ParseTree parseSet(final ParseContext context,
                               final boolean invertFinalSet) throws ParseException {
        // Set defaults:
	    final List<ParseTree> setNodes = new ArrayList<ParseTree>();
        boolean inverted               = false;
        boolean expectSecondRangeValue = false;
        boolean expectAnybitsValue     = false;

        // Parse the set:
        final StringParseReader expression = context.expression;
        int currentChar;
        PARSE_SET: while ((currentChar = expression.read()) >= 0) {
            final ParseTree nodeToAdd;
            switch (currentChar) {

                /*********************************************************************************
                 *  Whitespace and comments - just ignore and go round again.
                 */
                case ' ':   case '\t':   case '\r':   case '\n': {
                    nodeToAdd = NO_NODE_TO_ADD;
                    break;
                }
                case COMMENT: {
                    expression.readPastChar('\n');
                    nodeToAdd = NO_NODE_TO_ADD;
                    break;
                }

                /* ********************************************************************************
                 * Single byte matching atoms, which are invertible.
                 */

                // A byte value:
                case '0':   case '1':   case '2':   case '3':   case '4':   case '5':   case '6':   case '7':
                case '8':   case '9':   case 'a':   case 'b':   case 'c':   case 'd':   case 'e':   case 'f':
                case 'A':   case 'B':   case 'C':   case 'D':   case 'E':   case 'F':   case DONT_CARE: {
                    nodeToAdd = parseWildByte(currentChar, inverted, expression, context.byteSpec);
                    break;
                }
                case SHORTHAND_ESCAPE: {
                    nodeToAdd = parseShorthand(expression, inverted);
                    break;
                }
                case OPEN_SET: {
                    nodeToAdd = parseSet(context, inverted);
                    break;
                }

                /* **********************************************************************************************
                 * Value modifiers (invert next value or make the next value part of a range with the preceding value.
                 * They must throw a ParseException if inversion is on for them, or a range value is already expected.
                 */

                // inversion:
                case INVERT: {
                    checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                    nodeToAdd = NO_NODE_TO_ADD;
                    inverted = true;
                    break;
                }
                // a range of values:
                case RANGE_SEPARATOR: {
                    checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
                    nodeToAdd = NO_NODE_TO_ADD;
                    expectSecondRangeValue = true; // set an expectation that we get another range value on the next round.
                    break;
                }
                case ANYBITS: { // can be inverted, can't be expecting a range value.
                    checkNoRangeValueExpected(expectSecondRangeValue, expression); // can't have a range separator if there's already one waiting.
                    checkNoAnybitsValueExpected(expectAnybitsValue, expression);
                    nodeToAdd = NO_NODE_TO_ADD;
                    expectAnybitsValue = true;
                    break;
                }

                /* **********************************************************************************************
                 * Atoms which are not invertible.  They must throw a ParseException if inversion is on for them.
                 */

                case ANY: {
                    checkNotInverted(inverted, "Any bytes can't be inverted - it would match nothing.", expression);
                    nodeToAdd = BaseNode.ANY_NODE;
                    break;
                }
                case STRING_QUOTE: {
                    // SPECIAL CASE: single character, singly encoded, single byte strings can be inverted (just a byte value), others not.
                    final String stringValue = expression.readString(STRING_QUOTE);
                    if (stringValue.length() == 1) {
                        final byte[] charEncoding = stringValue.getBytes(StandardCharsets.ISO_8859_1);
                        nodeToAdd = ByteNode.valueOf(charEncoding[0], inverted);
                        break;
                    }

                    checkNotInverted(inverted, "Strings or strings cannot be inverted", expression);
                    nodeToAdd = new StringNode(expression, stringValue, StandardCharsets.ISO_8859_1);
                    break;
                }
                case CASE_INSENSITIVE_QUOTE: {
                    checkNotInverted(inverted, "Case insensitive strings cannot be inverted", expression);
                    nodeToAdd = new StringNode(expression, expression.readString(CASE_INSENSITIVE_QUOTE),
                            StandardCharsets.ISO_8859_1, ParseTreeType.CASE_INSENSITIVE_STRING);
                    break;
                }

                /*************************************************************************************
                 * Start of a string encoding statement.
                 */
                case OPEN_GROUP: {
                    if (expression.peekAhead() == ENCODE_STRING) {
                        parseEncodings(context);
                        nodeToAdd = NO_NODE_TO_ADD;
                        break;
                    }
                    throw new ParseException("Cannot open a group within a set definition",
                                             context.expression);
                }

                /**************************************************************************************
                 * End of the set - stop processing more set nodes.
                 */
                case CLOSE_SET: {
                    break PARSE_SET;
                }

                /*****************************************************************************************
                 * Unexpected character, throw a ParseException.
                 */
                default: {
                    throw new ParseException("Unexpected character [" + (char) currentChar + ']', expression);
                }
            }

            // If there's a node to add, add it to our sequence of nodes.
            if (nodeToAdd != NO_NODE_TO_ADD) {
                setNodes.add(nodeToAdd);

                // If we expect the last node added to be the second range value needed, then process as a range.
                if (expectSecondRangeValue) {
                    createRange(setNodes, expression);
                    expectSecondRangeValue = false;
                }

                // If we expect the last node added to be something we're applying any matching to, create an anybits node.
                if (expectAnybitsValue) {
                    createAnybits(setNodes, expression);
                    expectAnybitsValue = false;
                }

                // Reset inverted to false after adding a node.
                inverted = false;
            }
        }

        // Can't exit a set with a value still expected, or if the set wasn't closed.
        checkNoValuesExpected(inverted, expectSecondRangeValue, expectAnybitsValue, expression);
        if (currentChar < 0) {
            throw new ParseException("Set was not closed before the expression ended.", expression);
        }

        // Return the appropriate ParseTree or throw a ParseException depending on how many nodes in the set:
        switch (setNodes.size()) {
            //TODO: what about an inverted set of size 0 = 256!
            case 0:  throw new ParseException("Cannot have an empty set", expression);
            //case 1:  return setNodes.get(0); //TODO: this is wrong in some circumstances - a set with a single child
            // isn't always directly returnable - i.e. if the set contains a string or case insensitive string,
            // it's still a set of those values, not the string itself.  Some sets could be optimised like this.
            default: return new ChildrenNode(expression, ParseTreeType.SET, setNodes, invertFinalSet);
        }
    }

    private ParseTree parseWildByte(final int firstByteChar, final boolean inverted,
                                    final StringParseReader expression,
                                    final StringParseReader.WildByteSpec parseSpec) throws ParseException {
	    // If reading a byte spec has wild bits, then create a WildBit node, possibly inverted.
	    if (expression.readWildByte(firstByteChar, parseSpec)) {
	        final ParseTree valueNode = ByteNode.valueOf(parseSpec.value);
	        final ParseTree maskNode  = ByteNode.valueOf(parseSpec.mask);
	        return new ChildrenNode(expression, ParseTreeType.WILDBIT, buildList(maskNode, valueNode), inverted);
        }
        // No wild bits - just return a straight byte value (possibly inverted).
        return ByteNode.valueOf(parseSpec.value, inverted);
    }

	private ParseTree parseShorthand(final StringParseReader expression,
						              final boolean inverted) throws ParseException {
		final int character = expression.read();
		switch (character) {
			case 't': return inverted? ByteNode.valueOf((byte) '\t', INVERTED)  : TAB;
			case 'n': return inverted? ByteNode.valueOf((byte) '\n', INVERTED)  : NEWLINE;
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
			default: throw new ParseException("Unexpected shorthand character [" + (char) character + ']',
                                              expression);
		}
	}

    /**
     * Parses the repeat syntax {n,m} which says how many times the last node in the sequence so far should be repeated.
     *
     * @param sequenceNodes The sequence of nodes so far, for which the last node is repeated.
     * @param expression The expression being parsed.
     * @return A ParseTree repeat node containing the last node of the sequence passed in (which is removed from the sequence).
     * @throws ParseException If there is any syntactic problem parsing the repeat.
     */
    private ParseTree parseRepeat(final List<ParseTree> sequenceNodes,
                                  final StringParseReader expression) throws ParseException {
        final ParseTree nodeToRepeat = popLastNode(sequenceNodes, expression);
        final int firstValue = expression.readInt();
        int nextToken = expression.read();
        if (nextToken == CLOSE_REPEAT) {
            if (firstValue == 0) {
                throw new ParseException("Single repeat value cannot be zero", expression);
            }
            return new ChildrenNode(expression, ParseTreeType.REPEAT,
                                    new IntNode(expression, firstValue),
                                    nodeToRepeat);
        }
        if (nextToken == REPEAT_SEPARATOR) {
            ParseTree repeatNode;
            if (expression.peekAhead() == MANY) {
                expression.read();
                repeatNode = new ChildrenNode(expression, ParseTreeType.REPEAT_MIN_TO_MANY,
                        new IntNode(expression, firstValue), nodeToRepeat);
            } else {
                repeatNode = new ChildrenNode(expression, ParseTreeType.REPEAT_MIN_TO_MAX,
                        new IntNode(expression, firstValue),
                        new IntNode(expression, expression.readInt()),
                        nodeToRepeat);
            }
            nextToken = expression.read();
            if (nextToken == CLOSE_REPEAT) {
                return repeatNode;
            }
            throw new ParseException("No closing } for repeat instruction " + repeatNode, expression);
        }
        throw new ParseException("No closing } for repeat instruction with firstValue " + firstValue, expression);
    }

    private void createRange(final List<ParseTree> sequence,
                             final StringParseReader expression) throws ParseException {
        final ParseTree secondRangeValue = popLastNode(sequence, expression);
        if (secondRangeValue.getParseTreeType() != ParseTreeType.BYTE) {
            throw new ParseException("The second range value must be of type BYTE: " + secondRangeValue, expression);
        }
        if (secondRangeValue.isValueInverted()) {
            throw new ParseException("The second value of a range cannot be inverted " + secondRangeValue, expression);
        }
        final ParseTree firstRangeValue  = popLastNode(sequence, expression);
        if (firstRangeValue.getParseTreeType() != ParseTreeType.BYTE) {
            throw new ParseException("The first range value must be of type BYTE: " + firstRangeValue, expression);
        }
        sequence.add(new ChildrenNode(expression, ParseTreeType.RANGE, firstRangeValue.isValueInverted(),
                ByteNode.valueOf(firstRangeValue.getByteValue()),
                ByteNode.valueOf(secondRangeValue.getByteValue())));
    }

    private void createAnybits(final List<ParseTree> sequence,
                               final StringParseReader expression) throws ParseException {
        final ParseTree lastNode = popLastNode(sequence, expression);
        final boolean inverted = lastNode.isValueInverted();
        switch (lastNode.getParseTreeType()) {
            case BYTE: {
                final ParseTree maskNode = ByteNode.valueOf((byte) 0xFF); // a mask of 0xFF means no wild bits.
                sequence.add(new ChildrenNode(expression, ParseTreeType.ANYBITS, buildList(maskNode, lastNode), inverted));
                break;
            }
            case WILDBIT: {
                final ParseTree maskNode  = lastNode.getChild(0);
                final ParseTree valueNode = lastNode.getChild(1);
                sequence.add(new ChildrenNode(expression, ParseTreeType.ANYBITS, buildList(maskNode, valueNode), inverted));
                break;
            }
            default: throw new ParseException("Any ~ matching needs a BYTE or WILDBIT value, value was: " + lastNode, expression);
        }
    }

    /**
     * Applies simple optimisations for alternative sequences.
     *  <p>&nbsp; 1. If there is only a single alternative, return just that.
     *  <p>&nbsp; 2. If all of the alternatives are single bytes, they can be combined into a single set byte matcher.
     *  <p>&nbsp; 3. If some of the alternatives are single bytes, they are combined into a single set byte matcher and added to the other alternatives.
     *
     * @param alternatives A ParseTree containing a list of alternatives to be optimised.
     * @param expression The expression being parsed, to add context to any exception messages.
     * @return An optimised ParseTree.
     * @throws ParseException If there are no alternatives to optimise.
     */
    private ParseTree optimisedAlternatives(final List<ParseTree> alternatives,
                                            final StringParseReader expression) throws ParseException {

        // If there are no alternatives, throw an error (this is impossible in the code as currently written,
        // but we keep the test in case of changes that invalidate this assumption).
        if (alternatives.isEmpty()) {
            throw new ParseException("No alternatives were found.", expression);
        }

        // If there is only a single alternative, then just return the alternative directly.
        if (alternatives.size() == 1) {
            return alternatives.get(0);
        }

        // See if there is more than one alternative that only matches sequences of length one.
        // If there is, they can be more efficiently represented as a set match for those byte values.
        final ParseTree optimisedSet = optimiseSingleByteAlternatives(alternatives, expression);

        // If there are no remaining alternatives (all got put into the set), return the set directly:
        if (alternatives.isEmpty()) {
            return optimisedSet;
        }

        // If there is now an optimised set and some remaining alternatives, add it to the list of alternatives:
        if (optimisedSet != null) {
            alternatives.add(optimisedSet);
        }

        // Return an alternatives type with the alternatives as children:
        return new ChildrenNode(expression, ParseTreeType.ALTERNATIVES, alternatives);
    }

    /**
     * Looks at a list of alternatives for alternatives that only match a sequence of length one each.
     * If there is more than one of them, they can be collapsed into a single SET ParseTree which
     * matches all of the alternatives in a single set matching step.
     * <p>&nbsp;
     * If it discovers alternatives that can be optimised, it removes them from the list of alternatives
     * passed in, and returns a SET ParseTree consisting of the alternatives which can be treated as a set.
     * If there are no optimisations possible, it does not modify the list passed in, and returns null for
     * the set of alternatives.
     *
     * @param alternatives The list of alternatives to optimise.
     * @return A SET ParseTree for the optimised alternatives (removing them from the original list passed in),
     *         or null if there are no optimisations possible.
     */
    private ParseTree optimiseSingleByteAlternatives(final List<ParseTree> alternatives,
                                                     final StringParseReader expression) {
        // Find out if there is more than one alternative that matches only a sequence of length one:
        int numOptimisableAlternatives = 0;
        for (final ParseTree alternative : alternatives) {
            if (matchesSingleByteLength(alternative)) {
                numOptimisableAlternatives++;
            }
        }

        // If there aren't at least two alternatives, return null and don't modify original collections.
        if (numOptimisableAlternatives < 2) {
            return null;
        }

        // If there are, build a list of them, remove them from the original list of alternatives,
        // and return a set node of the optimisable alternatives:
        final List<ParseTree> setChildren = new ArrayList<ParseTree>(numOptimisableAlternatives);
        final Iterator<ParseTree> altIterator = alternatives.iterator();
        while (altIterator.hasNext()) {
            final ParseTree currentAlternative = altIterator.next();
            if (matchesSingleByteLength(currentAlternative)) {
                setChildren.add(currentAlternative);
                altIterator.remove();
            }
        }
        return new ChildrenNode(expression, ParseTreeType.SET, setChildren);
    }

    /**
     * Removes the last ParseTree node from a List of ParseTrees.
     *
     * @param sequence The sequence to pop a node from.
     * @param expression The expression we are parsing (used to add context to an exception message)
     * @return The last node of the sequence (which is removed from the sequence).
     * @throws ParseException If the sequence was empty.
     */
    private ParseTree popLastNode(final List<ParseTree> sequence,
                                  final StringParseReader expression) throws ParseException {
        if (sequence.isEmpty()) {
            throw new ParseException("Tried to remove the last node in a sequence, but it was empty", expression);
        }
        return sequence.remove(sequence.size() - 1);
    }

    /**
     * Makes the last node of a sequence the child of a quantify type node.
     *
     * @param sequenceNodes The sequence to process.
     * @param quantifyType The type of the quantify node to create
     * @param expression The expression being parsed.
     * @return A new node of type quantifyType with the last node of the sequence as it's child.
     * @throws ParseException If the last node of the sequence is not quantifiable.
     */
    private ParseTree quantifyLastNode(final List<ParseTree> sequenceNodes, final ParseTreeType quantifyType, final StringParseReader expression) throws ParseException {
        final ParseTree lastNode = popLastNode(sequenceNodes, expression);
        checkQuantifiable(lastNode, expression);
        return new ChildrenNode(lastNode, quantifyType, lastNode);
    }

    /**
     * Returns true if the type of the ParseTree node matches just a single byte length.
     * @param node The node to test.
     * @return true if the type of the ParseTree node matches just a single byte length.
     */
    private boolean matchesSingleByteLength(final ParseTree node) {
        switch (node.getParseTreeType()) {
            case BYTE:
            case RANGE:
            case SET:
            case ANY:
            case WILDBIT: {
                return true;
            }
            default : return false;
        }
    }


    private void checkNoValuesExpected(final boolean inverted,
                                       final boolean rangeValueExpected,
                                       final boolean anybitsValueExpected,
                                       final StringParseReader expression) throws ParseException {
        checkNotInverted(inverted, "", expression); //TODO: add messages to this.
        checkNoRangeValueExpected(rangeValueExpected, expression);
        checkNoAnybitsValueExpected(anybitsValueExpected, expression);
    }

    /**
     * Throws a ParseException if inverted is true.
     *
     * @param inverted The status of inversion.
     * @param message  A message if an exception is thrown.
     * @param expression The expression we are parsing (used to add context to an exception message).
     *
     * @throws ParseException If inverted is true.
     */
    private void checkNotInverted(final boolean inverted, final String message, final StringParseReader expression) throws ParseException {
        if (inverted) {
            throw new ParseException(message, expression);
        }
    }

    /**
     * Throws an exception if a range value is expected.
     *
     * @param rangeValueExpected Whether a range value is expected.
     * @param expression  The expression we are parsing (used to add context to an exception message)
     *
     * @throws ParseException If rangeValueExpected is true.
     */
    private void checkNoRangeValueExpected(final boolean rangeValueExpected, final StringParseReader expression) throws ParseException {
        if (rangeValueExpected) {
            throw new ParseException("A range value was expected at this point in the expression.", expression);
        }
    }

    /**
     * Throws an exception if any bits are expected.
     *
     * @param anybitsExpected Whether any bits value is expected.
     * @param expression The expression we are parsing (used to add context to an exception message)
     *
     * @throws ParseException If anybitsExpected is true.
     */
    private void checkNoAnybitsValueExpected(final boolean anybitsExpected, final StringParseReader expression) throws ParseException {
        if (anybitsExpected) {
            throw new ParseException("A byte or wildbit value was expected to follow an any bits modifier ~ modifier",
                                     expression);
        }
    }

    /**
     * Throws a ParseException if the node passed in is not quantifiable.
     * Any node you could repeat for a while is quantifiable.
     *
     * @param node The node to check.
     * @param expression  The expression we are parsing (used to add context to an exception message)
     *
     * @throws ParseException if the node to check is not a quantifiable type.
     */
    private void checkQuantifiable(final ParseTree node,
                                   final StringParseReader expression) throws ParseException {
        switch (node.getParseTreeType()) {
            case BYTE: 			case RANGE:			case SET:		case ANY:
            case SEQUENCE:		case ALTERNATIVES:	case STRING:	case CASE_INSENSITIVE_STRING:
            case WILDBIT:       case ANYBITS: {
                return;
            }
        }
        throw new ParseException("The node: " + node + " is not quantifiable", expression);
    }

}
