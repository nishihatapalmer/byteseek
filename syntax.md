# Syntax

The byteseek syntax is very similar to standard regular expression languages, but is oriented towards bytes rather than text.

A pleasant feature is that there is no need to escape characters or bytes in any expression, which contributes to readability.  This is because specifying values to match is explicitly part of the syntax, rather than being implicit in the expression. 

> **Note on compilers** Byteseek provides several diferent compilers for bytes, fixed length sequences and regular expressions, which all use the appropriate sub-set of this syntax.  The full RegexCompiler can use all of the syntax below.  The SequenceMatcherCompiler can use any syntax which results in a fixed length sequence, but not a variable length sequence or one with alternatives.  The ByteMatcherCompiler can use any syntax which results in matching at a single byte position (i.e. a sequence of length one).

## Bytes and byte sequences
Bytes and byte sequences are written as sequences of case insensitive hex digits:

```
  006E015a7f 77 68 65 72 65 66 6f 72 65
```

If you want to match the inverse of a byte - that is, any byte other than the one you specify - prepend a byte value with the not operator ^

```
  ^00                   # anything but 0x00
  00 ^00 00             # 0x00, anything but 0x00, 0x00
```

## Text
ASCII text is written enclosed in single quotes '. 

```
  'wherefore art thou'  # wherefore art thou 
```

Single characters, as they represent a single byte value, can also be inverted with the not operator ^.

```
  ^'Z'                  # not Z
```

Naturally, byte sequences and text can be mixed arbitrarily:

```
   00 'Start' 01 ^3c 5a 'version' 30 ff
```

If back-ticks ` are used instead of single quotes, this specifies a case-insensitive match:

```
   00 `sTaRt` 01 ^3c 5a 'version' 30 ff
```

## Whitespace and comments
All whitespace (space, tab, newline, carriage return) is ignored, unless within quoted text.  Comments can be added using the # character.  All text after a #, until the next new line character, is ignored.

This allows parts of an expression to be separated for clarity and comments to be placed on lines.  For example, the expression:

```
  'BEGIN:'20*[30-39]+20*3A20*[30-39]{1,6}20*7F'END'
```

can also be written:

```
  'BEGIN:'              # BEGIN:
     20*  [30-39]+      # maybe spaces, some digits
     20*  3A            # maybe spaces, a colon  
     20*  [30-39]{1,6}  # maybe spaces, 1 to 6 digits
     20*  7F            # maybe spaces, 0x7F
  'END'                 # END
```

## Any byte
Any byte can be matched with the full stop .

```
   .                    # always matches, as long as there is something to match against.
   'A' . cb             # A, any byte, 0xcb
```

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

## All bitmasks

You can match a byte on whether all of its bits exist in a bitmask you supply.  To specify an All bitmask use the & operator. 

```
   &07                 # 0x07 = 00000111. Match bytes with all first three bits set.
   &7f                 # 0x7f = 01111111. Match bytes with all first seven bits set.
   &00                 # 0x00 = 00000000. Matches any byte.
```

It may be surprising that matching to zero with an all bitmask matches any byte instead of none.  This is because the test for a match is that the bits of the bitmask and'ed with the byte value to match equals the bitmask:

```
   (byte & bitmask) == bitmask
```

If the bitmask is zero, this is always true, so all byte values will match.  You can also invert the result of a match by pre-pending it with the not operator ^.

```
   ^&80                # 0x80 = 10000000. Match bytes without the last bit set.
   ^&87                # 0x87 = 10000111. Match bytes without all of the bitmask bits set.
```

Any bitmasks

You can match a byte on whether any of its bits exist in a bitmask you supply.  To specify an Any bitmask use the ~ operator.  

```
   ~07                 # 0x07 = 00000111. Match bytes with any of the first three bits set.
   ~7f                 # 0x7f = 01111111. Match bytes with any of the first seven bits set.
   ~00                 # 0x00 = 00000000. Match no bytes.
```

It may or may not be surprising  that matching to zero with an any bitmask matches no bytes at all.  This is because the test for a match is that the bits of the bitmask and'ed with the byte value to match does not equal zero:

```
   (byte & bitmask) != 0
```

If the bitmask is zero, this always results in zero and fails to match.  You can also invert the result of a match by pre-pending it with the not operator ^. 

```
   ^~80                # 0x80 = 10000000. Match bytes without the last bit set.
   ^~87                # 0x87 = 10000111. Match bytes without any of the bitmask bits set.
```

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

You can specify that something repeats an exact number of times {n}, from a minimum to a maximum number of times {n,m}, and up to an unlimited number of times {n,*}.  There is also shorthand syntax for the common repeats zero-to-many (*) and one-to-many (+)

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
To specify that something repeats from a minimum number of times to an unlimited number of times, replace the maximum number of the min to max repeats with a *: {n,*}

```
   00{5,*}                                # 0x00 repeated at least 5 times, to an unlimited number of times.
   'green bottle'{1,*}                    # from 1 to infinite green bottles.
```

### Zero to many repeats

To specify that something repeats from zero to many times, you can write {0,*}.  Since this is such a common requirement, this can be replaced by a single * appended to the element to repeat *:
```
  00*                                    # zero to many 0x00
  'green bottle'*                         # zero to many green bottles.
```

### One to many repeats

To specify that something repeats from one to many times, you can write {1,*}.  Since this is such a common requirement, this can be replaced by a single + appended to the element to repeat +:

```
  00+                                    # one to many 0x00
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

   
