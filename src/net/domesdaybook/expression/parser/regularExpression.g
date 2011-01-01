grammar regularExpression;

/*
  Byte-oriented regular expressions
  =================================

  Author: Matt Palmer, November 2010

  An ANLTR (http://www.antlr.org/) grammar to parse regular expressions
  specified over byte sequences, not text:

  Input
  -----
  A regular expression conforming to the syntax described in the file syntax.txt.
  
  Output
  ------
  An Abstract Syntax Tree (AST) encoding the essential structure of the regular expression.
  The AST is a blue-print for building code that recognise the regular expression.
  
  Differences to other regular expression parsers
  ------------------------------------------------
   * Bytes: the language operates over byte sequences rather than text sequences.

   * Trees: Sequences of bytes or other fundamental objects (atoms)
     produce right-associative trees, rather than the more normal left-
     associative form.  This doesn't make any material difference.

   * Lists: In any case, the tree mode is turned off by default.
     Instead of the deeply nested tree structures produced by traditional parsing,
     sequences are represented directly as sequences, with an ordered list of children.

     "sequencesAsTree" defines whether to output sequences as nested trees or lists.
     
  To do
  -----
   * Investigate why I can't use predicates to control whether I build 
     lists or nested trees of alternatives (code too large error).

*/

options { output=AST; }

tokens { SEQUENCE; 
	 ALTERNATE;
	 REPEAT;
         SET;
         INVERTED_SET;
         RANGE;
         ALL_BITMASK; 
         ANY_BITMASK;
         ANY; 
}


@parser::header {
package net.domesdaybook.expression.parser;
}


@parser::members {
	boolean sequencesAsTree = false;
}


@lexer::header{
package net.domesdaybook.expression.parser;
} 

@lexer::members { 
	boolean inRepeat=false;
	int inSet = 0;
        boolean throwExceptionOnError = true;

        @Override
        public void reportError(RecognitionException e) {
            if (throwExceptionOnError) {
                throw new IllegalArgumentException(e);
            } else {
                super.reportError(e);
            }
        }
}



start	:	regex EOF!				
	;


regex	:	// A list of alternative sequences
		sequence 
		(	
			( (ALT sequence)+	-> ^(ALT sequence+ )	)
			|
			( 			->  sequence		)
		)
	// Nested sequences:
	//		( sequence ( ALT^ sequence )* )
	;
	

sequence
	:	{sequencesAsTree}?=>
	(
		( quantified_atom	-> quantified_atom )
		( sequence 		-> ^(SEQUENCE quantified_atom sequence ) )?
	)
	|
		{!sequencesAsTree}?=>
	(
		quantified_atom
		(
			( quantified_atom+	-> ^(SEQUENCE quantified_atom+) )
			| 			
			(			-> ^(quantified_atom)		)
		)
	)
	;


	
quantified_atom
	:	e=atom
	(	quantifier 		-> ^(quantifier $e)
	|				-> ^($e)
	)
	;


atom	:	
	(	hexbyte
	|	any_byte
	|	byte_set
	|	byte_shorthand
	|	set_shorthand
	|	all_bitmask
	|	any_bitmask
	|	case_sensitive_string
	|	case_insensitive_string
	|	group
	) 
	;


hexbyte	:	BYTE
	;


any_byte:	FULL_STOP		-> ANY
	;

	
byte_set
	:	OPEN_SQUARE 
	(	
		( CARET set_specification 	-> ^( INVERTED_SET set_specification ) )
		| 	
		( set_specification 		-> ^( SET set_specification ) )
	)	
		CLOSE_SQUARE
	;

	
set_specification
	:
	(	hexbyte
	|	byte_shorthand
	|	set_shorthand
	|	mnemonic	
	|	case_sensitive_string
	|	case_insensitive_string
	|	byte_range
	|	all_bitmask
	|	any_bitmask
	|	byte_set	
	)+
	;


byte_range
	:	r1=range_values
		RANGE_SEPARATOR 
		r2=range_values			-> ^(RANGE $r1 $r2)
	;


range_values
	:	BYTE
	|	CASE_SENSITIVE_STRING
	;


all_bitmask
	:	AMPERSAND BYTE		-> ^(ALL_BITMASK BYTE)
	;

any_bitmask
	:	TILDE BYTE		-> ^(ANY_BITMASK BYTE)
	;
	
mnemonic
	:	m=SET_ASCII		-> ^(SET ^(RANGE BYTE[$m,"00"] BYTE[$m,"7f"]))	
	|	m=SET_PRINT		-> ^(SET ^(RANGE BYTE[$m,"' '"] BYTE[$m,"'~'"]))	
	|	m=SET_GRAPH		-> ^(SET ^(RANGE BYTE[$m,"'!'"] BYTE[$m,"'~'"]))	
	|	m=SET_WORD		-> ^(SET ^(RANGE BYTE[$m,"'0'"] BYTE[$m,"'9'"]) ^(RANGE BYTE[$m,"'a'"] BYTE[$m,"'z'"]) ^(RANGE BYTE[$m,"'A'"] BYTE[$m,"'Z'"]) BYTE[$m,"'_'"])
	|	m=SET_ALPHANUM		-> ^(SET ^(RANGE BYTE[$m,"'a'"] BYTE[$m,"'z'"]) ^(RANGE BYTE[$m,"'A'"] BYTE[$m,"'Z'"]) ^(RANGE BYTE[$m,"'0'"] BYTE[$m,"'9'"]))
	|	m=SET_ALPHA		-> ^(SET ^(RANGE BYTE[$m,"'a'"] BYTE[$m,"'z'"]) ^(RANGE BYTE[$m,"'A'"] BYTE[$m,"'Z'"]))
	|	m=SET_UPPER		-> ^(SET ^(RANGE BYTE[$m,"'A'"] BYTE[$m,"'Z'"]))
	|	m=SET_LOWER		-> ^(SET ^(RANGE BYTE[$m,"'a'"] BYTE[$m,"'z'"]))
	|	m=SET_PUNCT		-> ^(SET ^(RANGE BYTE[$m,"'!'"] BYTE[$m,"'/'"]) ^(RANGE BYTE[$m,"':'"] BYTE[$m,"'@'"]) ^(RANGE BYTE[$m,"'['"] BYTE[$m,"'`'"]) ^(RANGE BYTE[$m,"'{'"] BYTE[$m,"'~'"]))
	|	m=SET_HEXDIGIT		-> ^(SET ^(RANGE BYTE[$m,"'0'"] BYTE[$m,"'9'"]) ^(RANGE BYTE[$m,"'a'"] BYTE[$m,"'f'"]) ^(RANGE BYTE[$m,"'A'"] BYTE[$m,"'F'"]))
	|	m=SET_DIGIT		-> ^(SET ^(RANGE BYTE[$m,"'0'"] BYTE[$m,"'9'"]))
	|	m=SET_WHITESPACE	-> ^(SET BYTE[$m,"09"] BYTE[$m,"0a"] BYTE[$m,"0d"] BYTE[$m,"' '"])
	|	m=SET_BLANK		-> ^(SET BYTE[$m,"09"] BYTE[$m,"' '"])
	|	m=SET_SPACE		-> ^(SET BYTE[$m,"09"] BYTE[$m,"0a"] BYTE[$m,"0b"] BYTE[$m,"0c"] BYTE[$m,"0d"] BYTE[$m,"' '"])
	|	m=SET_TAB		-> BYTE[$m,"09"]
	|	m=SET_NEWLINE		-> BYTE[$m,"0a"]
	|	m=SET_RETURN		-> BYTE[$m,"0d"]
	|	m=SET_CONTROL		-> ^(SET ^(RANGE BYTE[$m,"00"] BYTE[$m,"1f"]) BYTE[$m,"7f"])
	;	


byte_shorthand
	:	sh=TAB_SHORTHAND		-> BYTE[$sh,"09"]
	|	sh=NEWLINE_SHORTHAND		-> BYTE[$sh,"0a"]
	|	sh=VERTICAL_TAB_SHORTHAND	-> BYTE[$sh,"0b"]
	|	sh=FORM_FEED_SHORTHAND		-> BYTE[$sh,"0c"]
	|	sh=RETURN_SHORTHAND		-> BYTE[$sh,"0d"]
	|	sh=ESCAPE_SHORTHAND		-> BYTE[$sh,"1b"]
	;
	
	
set_shorthand
	:	sh=DIGIT_SHORTHAND		-> ^(SET ^(RANGE BYTE[$sh,"'0'"] BYTE[$sh,"'9'"]))
	|	sh=NOT_DIGIT_SHORTHAND		-> ^(INVERTED_SET ^(RANGE BYTE[$sh,"'0'"] BYTE[$sh,"'9'"]))
	|	sh=WORD_SHORTHAND		-> ^(SET ^(RANGE BYTE[$sh,"'0'"] BYTE[$sh,"'9'"]) ^(RANGE BYTE[$sh,"'a'"] BYTE[$sh,"'z'"]) ^(RANGE BYTE[$sh,"'A'"] BYTE[$sh,"'Z'"]) BYTE[$sh,"'_'"])
	|	sh=NOT_WORD_SHORTHAND		-> ^(INVERTED_SET ^(RANGE BYTE[$sh,"'0'"] BYTE[$sh,"'9'"]) ^(RANGE BYTE[$sh,"'a'"] BYTE[$sh,"'z'"]) ^(RANGE BYTE[$sh,"'A'"] BYTE[$sh,"'Z'"]) BYTE[$sh,"'_'"])
	|	sh=WHITE_SPACE_SHORTHAND	-> ^(SET BYTE[$sh,"09"] BYTE[$sh,"0a"] BYTE[$sh,"0d"] BYTE[$sh,"' '"])
	|	sh=NOT_WHITE_SPACE_SHORTHAND	-> ^(INVERTED_SET BYTE[$sh,"09"] BYTE[$sh,"0a"] BYTE[$sh,"0d"] BYTE[$sh,"' '"]) 
	;

case_insensitive_string
	:	CASE_INSENSITIVE_STRING	
	;
	

case_sensitive_string
	:	CASE_SENSITIVE_STRING
	;
	
	
group	:	OPEN regex CLOSE	-> regex 
	;

	
quantifier
	:
	(	optional			
	| 	zero_to_many 	 	
	| 	one_to_many
	| 	repeat 
	)
	;

	
repeat	:	OPEN_CURLY n1=NUMBER 
	(	
		( REPEAT_SEPARATOR n2=NUMBER 	-> ^( REPEAT $n1 $n2 ))
		|
		( REPEAT_SEPARATOR MANY		-> ^( REPEAT $n1 MANY ))
		|
		(				-> ^( REPEAT $n1 $n1 ))
	)	
		CLOSE_CURLY 
	;


optional:	QUESTION_MARK
	;


zero_to_many
	:	MANY
	;

	
one_to_many
	:	PLUS
	;



CASE_SENSITIVE_STRING
	:	QUOTE ~(QUOTE)* QUOTE 
	;


CASE_INSENSITIVE_STRING
	:	BACK_TICK ~(BACK_TICK)* BACK_TICK
	;


fragment
QUOTE	:	'\''
	;


fragment
BACK_TICK
	:	'`'
	;
	

FULL_STOP:	'.'
	;

	
ALT	:	'|'
	;


OPEN	:	'('	 
	;


CLOSE	:	')'
	;


TAB_SHORTHAND
	:	ESCAPE 't'
	;
	
	
NEWLINE_SHORTHAND
	:	ESCAPE 'n'	
	;


VERTICAL_TAB_SHORTHAND		
	:	ESCAPE 'v'	
	;
	
	
FORM_FEED_SHORTHAND
	:	ESCAPE 'f'	
	;
	
	
RETURN_SHORTHAND
	:	ESCAPE 'r'	
	;
	
	
ESCAPE_SHORTHAND
	:	ESCAPE 'e'	
	;


DIGIT_SHORTHAND
	:	ESCAPE 'd'	
	;
	
	
NOT_DIGIT_SHORTHAND
	:	ESCAPE 'D'	// [^ '0'-'9' ]
	;
	

WORD_SHORTHAND
	:	ESCAPE 'w'	// [ 'a'-'z' 'A'-'Z' '0'-'9' '_' ]
	;

	
NOT_WORD_SHORTHAND
	:	ESCAPE 'W'	//[^ 'a'-'z' 'A'-'Z' '0'-'9' '_' ]
	;

	
WHITE_SPACE_SHORTHAND
	:	ESCAPE 's'	//[ 09 0a 0d 20 ]
	;

	
NOT_WHITE_SPACE_SHORTHAND
	:	ESCAPE 'S'	//[^ 09 0a 0d 20 ]
	;
	


fragment
ESCAPE	:	'\\'
	;



OPEN_SQUARE
	:	'[' 		{inSet++;} //{ inSet=true; }
	;


CARET
	:	{inSet>0}?=>	'^' 
	;


RANGE_SEPARATOR
	:	{inSet>0}?=>	'-' 
	;


SET_ASCII
	:	{inSet>0}?=>	'ascii'
	;

SET_PRINT
	:	{inSet>0}?=>	'print'
	;
	
SET_GRAPH
	:	{inSet>0}?=>	'graph'
	;
	
SET_WORD:	{inSet>0}?=>	'word'
	;

SET_ALPHANUM
	:	{inSet>0}?=>	'alnum'
	;

SET_ALPHA
	:	{inSet>0}?=>	'alpha'
	;

SET_UPPER
	:	{inSet>0}?=>	'upper'
	;

SET_LOWER
	:	{inSet>0}?=>	'lower'
	;
	
SET_PUNCT
	:	{inSet>0}?=>	'punct'
	;

SET_HEXDIGIT
	:	{inSet>0}?=>	'xdigit'
	;
	
SET_DIGIT
	:	{inSet>0}?=>	'digit'
	;
	
SET_WHITESPACE
	:	{inSet>0}?=>	'ws'
	;
	
SET_BLANK
	:	{inSet>0}?=>	'blank'
	;

SET_SPACE
	:	{inSet>0}?=>	'space'
	;
	
SET_TAB	:	{inSet>0}?=>	'tab'
	;
	
SET_NEWLINE
	:	{inSet>0}?=>	'newline'
	;


SET_RETURN
	:	{inSet>0}?=>	'return'
	;


SET_CONTROL
	:	{inSet>0}?=>	'ctrl'
	;	
	

CLOSE_SQUARE
	:	']'		{inSet--;} //{inSet=false;}
	;



AMPERSAND
	:	'&' 
	;


TILDE	:	'~'
	;
	

MANY
	:	'*'
	;


QUESTION_MARK
	:	'?'	
	;


PLUS
	:	'+'	
	;

	
OPEN_CURLY
	:	'{'			{ inRepeat=true; }
	;


NUMBER	:	{ inRepeat }?=>		('0'..'9')+
	;


REPEAT_SEPARATOR
	:	{ inRepeat }?=>		'-'
	;


CLOSE_CURLY	
	:	'}'			{ inRepeat=false; }
	;


BYTE	:	{ !inRepeat }?=>	HEX_DIGIT HEX_DIGIT 
	;
	

fragment
HEX_DIGIT 
	:	('0'..'9'|'a'..'f'|'A'..'F')
	;


COMMENT
	:	'#' ~('\n'|'\r')* '\r'? '\n'			{$channel=HIDDEN;}
	;


WS	:	( ' ' | '\t' | '\r' | '\n' )			{$channel=HIDDEN;}
	;
