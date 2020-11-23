# Syntax

The byteseek syntax is very similar to standard regular expression languages, but is oriented towards bytes rather than text. A pleasant feature is that there is no need to escape characters or bytes in any expression, which contributes to readability.

## Whitespace and comments

Unless they occur within quoted text, the following characters are ignored:
 * Whitespace: space, tab, newline, carriage return.
 * Comments: all characters after a *#*, up to and including the next new line character or the end of the text, whichever comes first.

This allows us to space out our regular expressions  as we wish and add descriptions to aid readability.

## Hex bytes
Bytes and byte sequences are written as sequences of case insensitive hex digits:

```
  00 6E 01 5a 7F 77 68 65 72 65 66 6f 72 65
```

You can also prepend any hex value with **0x**, but this is not required, as hex bytes are first class citizens.

```
  0x00 0x6E 0x01 0x5a 0x7f 0x77 0x68 0x65 0x72 0x65 0x66 0x6f 0x72 0x65
```

The ability to parse hex bytes with 0x prepended is more for convenience, as some tools will provide hex bytes in this format.  It's just easier if byteseek can consume bytes written in this standard way.

## Binary bytes

It is also possible to write byte values in binary rather than hex.  To do this, prepend 8 binary digits with **0i**.

```
  0i00001111 0i10101010 0i10001000
```

**Note**: it might have been more natural to use "0b" to mean a binary value.  Unfortunately, "0b" is also a valid hex value, which would have created ambiguity in the syntax, so "0i" was chosen.

## Wild bits
If you want to match bytes that have certain bits set to a value, and other bits are "wild" - you don't care about them, you can do this using the Wild underscore operator **_** in place of a hex or binary digit.

````
  F_         # The first nibble of the byte is 0xF, but we don't care about the second nibble.
  0xF_       # The first nibble of the byte is 0xF, but we don't care about the second nibble.
  0i1111____ # The first nibble of the byte is 0xF, but we don't care about the second nibble.
  0i0000____ # The first nibble of the byte is 0x0, but we don't care about the second nibble.

  _3         # We don't care about the first nibble of the byte, the second nibble must be 3.
  0x_3       # We don't care about the first nibble of the byte, the second nibble must be 3.
  0i____0011 # We don't care about the first nibble of the byte, the second nibble must be 3.

  0i10__01_1 # We don't care about the bits with underscores, the other bits must match as written.
````

## Any bits
If you want to match bytes where any of the bits have a certain value, and other bits are "wild", then you can use the **~** Any operator:

````
  ~F_         # Match if any of the bits in the first nibble are 1.
  ~0xF_       # Match if any of the bits in the first nibble are 1.
  ~0i1111____ # Match if any of the bits in the first nibble are 1.
  ~0i0000____ # Match if any of the bits in the first nibble are 0.



````

It is also possible to match any bits on a whole byte value, with no wild bits, although it doesn't create a particularly interesting match.  If we look for any of the bits in a byte with no wildbits, all possible bytes will match at least one bit, except the byte which is the bitwise inverse of the byte you specify any matching on.

```
  ~0xFF      # will match all bytes except 0x00, ^00 would match the same thing.
  ~0xF0      # will match all bytes except 0x0F, ^0F would match the same thing.
```

## Inverted matching
If you want to match the inverse of something - that is, all bytes other than the ones you specify - prepend a value with the not operator ^

```
  ^00                   # anything but 0x00
  00 ^00 00             # 0x00, anything but 0x00, 0x00
  ^0i00001111           # anything but 0i00001111.
  ^F_                   # anything but 0xF in the first nibble.
```

## Any byte
Any byte can be matched with the full stop .

```
   .                    # always matches, as long as there is something to match against.
   'A' . cb             # A, any byte, 0xcb
```

Note that is it also equivalent to write all wild hex or binary digits:

````
   __                  # Match any byte.
   0x__                # Match any byte.
   0xi________         # Match any byte.
````

## Ranges
An inclusive range of byte values can be matched by specifying the range of single byte values, separated by a hyphen -

```
   20-7f                # all printable ASCII characters.
   'a' - 'z'            # only single characters can form a range.
   [30-39]              # a digit.  You can use square brackets, but they aren't required.
   20 [30-39] 20        # space digit space.
   20 '0'-'9' 20        # space digit space.
   00-ff                # matches any byte.
```

You can also match the inverse of a range - that is, any byte which isn't in the range - by pre-pending it with the not operator ^:

```
   [^'0'-'9']           # not a digit.  A set containing the inverse of the range 0-9.
   ^['0'-'9']           # not a digit.  The inverse of a set containing the range 0-9.
   ^30-39               # not a digit.  Square brackets are still not actually needed.
   20 ^30-39 20         # space, not a digit, space.
   20 ^['0'-'9'] 20     # space, not a digit, space.
```


## Text
Text is written enclosed in single quotes '.

```
  'wherefore art thou'  # wherefore art thou
```

Single characters, if encoded as a single byte value, can also be inverted with the not operator ^, or you can specify any bit matching for it:

```
  ^'Z'                  # not Z
  ~'Z'                  # all bytes which match at least one bit from 'Z'.
```

Naturally, byte sequences and text can be mixed arbitrarily:

```
   00 'Start' 01 ^3c 5a 'version' 30 ff
```

## Case insensitive ASCII

If back-ticks ` are used instead of single quotes, this specifies a case-insensitive match, for ASCII alphabetic characters only.

```
   00 `sTaRt` 01 ^3c 5a 'version' 30 ff
```

## Text encoding
Text is, by default, encoded in the ISO_8859_1 charset, which is a single byte encoding.
It is possible to specify other character sets (and thus byte encodings) for text, using the encoding operator.  The character sets must be specified inside round brackets, with an asterisk immediately following the open round bracket.  The name can be any Charset supported by Java.

````
   (*UTF-8)    'In UTF-8'
   (*UTF-16BE) 'In UTF-16BE'
````

These can be placed anywhere in a regular expression, and as many times as you like.  All text following it will have the specified encoding applied to it, until another text encoding is specified, which will override the last one.

It is also possible to specify multiple encodings, if the encodings are separated by commas.  Spaces are ignored (although tabs, newlines, carriage returns and comments are not permitted). This will result in all following strings being encoded in all the specified encodings, which creates multiple byte sequences, possibly of different lengths, to match at that point.

````
   (*UTF-8, UTF-16BE)  'This text will be encoded in both UTF-8 and UTF16BE'
````

**Note** It is important to emphasise that byteseek does not understand different text encodings when matching, it works on bytes.  So while it is possible to encode text you are looking for in UTF-16BE, it will only match the exact bytes which this encodes to using the charset.  It is possible to write the same unicode text with different underlying byte encodings.  Byteseek will only match the literal byte encoding it gets.


## Sets

You can specify an arbitrary set of bytes to match at some position by enclosing the byte values with square brackets [], creating a set.  Sets can be inverted by pre-pending with the not operator ^, in which case they match any byte not in the set.

```
  [09 0a 0d 20]       # whitespace - tab, newline, carriage return, space
  ^[09 0a 0d 20]      # not whitespace - anything but tab, newline, carriage return, space
```

> *Note*
> Most regular expression languages place the set inversion character immediately after the left square bracket, not before it.  In these languages, sets are the only things which can be inverted, so the inversion syntax is peculiar to them.
> In byteseek the not operator is more general, and can apply to anything that specifies a byte value or values to match, by pre-pending it with ^.

Sets can contain anything that specifies a byte value or values, including bytes, characters, ranges, bitmasks, other sets and their respective inversions.  You can also specify a set of characters using quoted strings or case insensitive strings.  In this case, the byte values in the strings are added to the set.

```
   [09 0A 0D 20 '0'-'9' 'a'-'z' 'A'-'Z']   # whitespace or alphanumerics.
   ^[09 0A 0D 20 '0'-'9' 'a'-'z' 'A'-'Z']  # not whitespace or alphanumerics.
   [20-7f [82 83 84 85]]                   # sets can be nested, although there is no advantage to doing so.
   ['aeiou']                               # vowels.
   ['a'-'z' 'A'-'Z' &80]                   # letters and high bit set.
   ['a'-'z' 'A'-'Z' ~81]                   # letters and high or low bit set.
   [^~0f 01]                               # without any of the first 4 bits set and 0x01
```

Note that the last item shows a not operator within the set.  This is legal - but it applies only to the ~0f element in the set, not the entire set.

## Repetition

You can specify that something repeats:

 * an exact number of times `{n}`
 * from a minimum to a maximum number of times `{n,m}`
 * up to an unlimited number of times `{n,*}`

There is also shorthand syntax for the common repeats:

* zero-to-many `*`
* one-to-many `+`
*  optionality `?`

### Exact repeats

To specify that something repeats an exact number of times, append the number enclosed in curly brackets after the element to repeat {n}:
```
  00{10}                                   # 0x00 repeated ten times.
  'green bottle'{100}                      # one hundred green bottles.
```

### Min to max repeats

To specify that something repeats from a minimum number of times up to some maximum number, append the two numbers to the element to repeat, separated by a comma, and enclosed in curly brackets {n,m}:

```
   00{5,25}                                # 0x00 repeated from 5 to 25 times.
   'green bottle'{1,100}                   # from 1 to 100 green bottles.
```

### Min to many repeats
To specify that something repeats from a minimum number of times to an unlimited number of times, replace the maximum number of the min to max repeats with a *: {n,\*}

```
   00{5,*}                                # 0x00 repeated at least 5 times, to an unlimited number of times.
   'green bottle'{1,*}                    # from 1 to infinite green bottles.
```

### Zero to many repeats

To specify that something repeats from zero to many times, you can write {0,*}.  Since this is such a common requirement, this can be replaced by a single * appended to the element to repeat:
```
  00*                                     # zero to many 0x00
  'green bottle'*                         # zero to many green bottles.
```

### One to many repeats

To specify that something repeats from one to many times, you can write {1,*}.  Since this is such a common requirement, this can be replaced by a single + appended to the element to repeat:

```
  00+                                     # one to many 0x00
  'green bottle'+                         # one to many green bottles.
```

### Optionality

If some element is optional, append a question mark to it ? Optionality is the same as repeating from 0 to 1 times {0,1}.

```
   7f?                                    # maybe 0x7f
   00 01 02? 03 04                        # matches 00 01 02 03 04 and 00 01 03 04
   'definitely this' 'maybe this'?        # definitely this, maybe this is optional.
```

## Alternatives

To match different sequences, separate the alternatives with the vertical bar |.

```
   01 02 | '123' | 01 ^02 20-7f            # 0x01 0x02 | 123 | 0x01, not 0x02, printable ASCII
```

If you want to make it clear where alternatives begin and end in a sequence, enclose the set of alternatives in round brackets ():

```
  00 (01 02|'123'|01 ^02 20-7f) ff        # starts with 0x00 and ends with 0xff, the middle can vary.
```

You can also nest alternatives inside each other:

```
   5e ('XX'77(039c7f|929c|949c)|'YY'92)    # 0x5e followed by XX 0x77 then three alternatives, or YY 0x92
```

## Sub expressions

To define a smaller part of an expression surround the sub expression with round brackets (). Sub expressions are useful to define where a list of alternatives starts and stops (as in the example above), or so you can apply some operation only to the sub-expression.  For example, you may want to repeat part of an expression, or to specify that it is optional.

```
   00 01 ('start'|'begin') 20+            # defines where alternatives begin and end.
   5c 6e (7f 'abdef' 51)+ 9c              # defines a sub expression to repeat one to many times.
   7e 14 (09 'fd' 09){2,10} 8e 83 92 ff   # defines a sub expression to repeat 2 to 10 times.
   'num' ('a'-'z'+ ' '*)? '0'-'9'+        # defines a sub expression which is optional.
```

## Shorthands
Common shorthands are provided for bytes and sets of bytes.  They can appear anywhere a byte value or values are specified.  However, they are not valid within quoted strings or case insensitive strings.  Everything written inside quotes is part of the literal string text.


 | Shorthand	|  Description    |  Matches        | Example
 |------------|-----------------|-----------------|---------
 | \t	        | Tab	            | 09	            | 'field' \t 'value'  # tab separated
 | \n	        | New line   	    | 0a	            | 3939 \n 313030 \n   # new lines for numbers
 | \v	        | Vertical tab    | 0b	            | 'some' \v 'thing'   # not widely seen...
 | \f      	  | Form feed       | 0c	            | 'END' \f            # feed the form at end
 | \r	        | Carriage return | 0d	            | \r|\r\n|\n\r|\n     # to be sure try all
 | \e	        | Escape	        | 1e	            | \e                  # escape
 | \d	        | Digit	          | '0' - '9'	      | \d \d \d            # three digits
 | \D      	  | not Digit    	  | ^'0' - '9'	    | \D \D \D            # not three digits
 | \l      	  | Lowercase	      | 'a' - 'z'       | \l+                 # one or more lowercase letters.
 | \L	        | not Lowercase   | ^'a' - 'z'	    | \L+                 # one or more non-lowercase bytes.
 | \u	        | Uppercase	      | 'A' - 'Z'	      | \u \l+              # uppercase followed by lowercase
 | \U	        | not Uppercase 	|  ^'A' - 'Z'	    | \U{2,10}            # two to ten non uppercase bytes
 | \i      	  | ASCII chars     | 00-7f           | \i+                 # one or more ASCII chars
 | \I	        | not ASCII chars | ^00-7f	        | \I+                 # one or more bytes with high bit set.
 | \s	        | Whitespace      | [\t \n \r ' ']  | \s*                 # all whitespace if it exists
 | \S	        | not Whitespace	|  ^[\t \n \r ' ']|	\S+                 # some non whitespace bytes
 | \w	        | Word            | [\d \l \u '_']  | \w+                 # some alphanumerics or underscores
 | \W	        | not Word       	| ^[\d \l \u '_']	| \W+                 # some bytes which aren't Words.


# Compilers
**A note on byteseek compilers**

Byteseek provides several diferent compilers for bytes, fixed length sequences and regular expressions, which all use the appropriate sub-set of this syntax.

The ByteMatcherCompiler can use any syntax which results in matching at a single position (i.e. a sequence of length one). Searchers for these cannot do better than examine every position to be searched, so performance linearly depends on the efficiency of the ByteMatcher.

The SequenceMatcherCompiler can use any syntax which results in a fixed length sequence, but not a variable length sequence or one with alternatives.  Searchers for these are fast if the right searcher is used.  There are searcher factories which will select the right searcher to use in most circumstances, given the pattern to be searched for.

The RegexCompiler can use all of the syntax below, but only produces Non Deterministic Finite State Automata matchers which are currently slow to search with.  There are techniques to search for regular expressions faster than just trying every position, but they are someway off in byteseek planning.  One technique is to look for necessary factors of the expression - sequences which must appear for the expression to be present, and use a multi-pattern search for those factors.  When one is found, the rest of the regular expression has to be evaluated forwards and backwards from the sequence.
