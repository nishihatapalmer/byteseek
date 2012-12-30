// $ANTLR 3.4 /home/matt/dev/search/byteseek/antlr/AntlrRegex.g 2012-11-03 21:24:07

package net.domesdaybook.parser.regex.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class AntlrRegexLexer extends Lexer {
    public static final int EOF=-1;
    public static final int ALL_BITMASK=4;
    public static final int ALT=5;
    public static final int ALTERNATE=6;
    public static final int AMPERSAND=7;
    public static final int ANY=8;
    public static final int ANY_BITMASK=9;
    public static final int BACK_TICK=10;
    public static final int BYTE=11;
    public static final int CARET=12;
    public static final int CASE_INSENSITIVE_STRING=13;
    public static final int CASE_SENSITIVE_STRING=14;
    public static final int CLOSE=15;
    public static final int CLOSE_CURLY=16;
    public static final int CLOSE_SQUARE=17;
    public static final int COMMENT=18;
    public static final int DIGIT_SHORTHAND=19;
    public static final int ESCAPE=20;
    public static final int ESCAPE_SHORTHAND=21;
    public static final int FORM_FEED_SHORTHAND=22;
    public static final int FULL_STOP=23;
    public static final int HEX_DIGIT=24;
    public static final int INVERTED_SET=25;
    public static final int MANY=26;
    public static final int NEWLINE_SHORTHAND=27;
    public static final int NOT_DIGIT_SHORTHAND=28;
    public static final int NOT_WHITE_SPACE_SHORTHAND=29;
    public static final int NOT_WORD_SHORTHAND=30;
    public static final int NUMBER=31;
    public static final int OPEN=32;
    public static final int OPEN_CURLY=33;
    public static final int OPEN_SQUARE=34;
    public static final int PLUS=35;
    public static final int QUESTION_MARK=36;
    public static final int QUOTE=37;
    public static final int RANGE=38;
    public static final int RANGE_SEPARATOR=39;
    public static final int REPEAT=40;
    public static final int REPEAT_SEPARATOR=41;
    public static final int RETURN_SHORTHAND=42;
    public static final int SEQUENCE=43;
    public static final int SET=44;
    public static final int SET_ALPHA=45;
    public static final int SET_ALPHANUM=46;
    public static final int SET_ASCII=47;
    public static final int SET_BLANK=48;
    public static final int SET_CONTROL=49;
    public static final int SET_DIGIT=50;
    public static final int SET_GRAPH=51;
    public static final int SET_HEXDIGIT=52;
    public static final int SET_LOWER=53;
    public static final int SET_NEWLINE=54;
    public static final int SET_PRINT=55;
    public static final int SET_PUNCT=56;
    public static final int SET_RETURN=57;
    public static final int SET_SPACE=58;
    public static final int SET_TAB=59;
    public static final int SET_UPPER=60;
    public static final int SET_WHITESPACE=61;
    public static final int SET_WORD=62;
    public static final int TAB_SHORTHAND=63;
    public static final int TILDE=64;
    public static final int VERTICAL_TAB_SHORTHAND=65;
    public static final int WHITE_SPACE_SHORTHAND=66;
    public static final int WORD_SHORTHAND=67;
    public static final int WS=68;
     
    	boolean inRepeat=false;
    	int inSet = 0;


    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public AntlrRegexLexer() {} 
    public AntlrRegexLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public AntlrRegexLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "/home/matt/dev/search/byteseek/antlr/AntlrRegex.g"; }

    // $ANTLR start "CASE_SENSITIVE_STRING"
    public final void mCASE_SENSITIVE_STRING() throws RecognitionException {
        try {
            int _type = CASE_SENSITIVE_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:315:2: ( QUOTE (~ ( QUOTE ) )* QUOTE )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:315:4: QUOTE (~ ( QUOTE ) )* QUOTE
            {
            mQUOTE(); 


            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:315:10: (~ ( QUOTE ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= '\u0000' && LA1_0 <= '&')||(LA1_0 >= '(' && LA1_0 <= '\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            mQUOTE(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CASE_SENSITIVE_STRING"

    // $ANTLR start "CASE_INSENSITIVE_STRING"
    public final void mCASE_INSENSITIVE_STRING() throws RecognitionException {
        try {
            int _type = CASE_INSENSITIVE_STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:320:2: ( BACK_TICK (~ ( BACK_TICK ) )* BACK_TICK )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:320:4: BACK_TICK (~ ( BACK_TICK ) )* BACK_TICK
            {
            mBACK_TICK(); 


            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:320:14: (~ ( BACK_TICK ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0 >= '\u0000' && LA2_0 <= '_')||(LA2_0 >= 'a' && LA2_0 <= '\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '_')||(input.LA(1) >= 'a' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            mBACK_TICK(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CASE_INSENSITIVE_STRING"

    // $ANTLR start "QUOTE"
    public final void mQUOTE() throws RecognitionException {
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:326:7: ( '\\'' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:326:9: '\\''
            {
            match('\''); 

            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUOTE"

    // $ANTLR start "BACK_TICK"
    public final void mBACK_TICK() throws RecognitionException {
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:332:2: ( '`' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:332:4: '`'
            {
            match('`'); 

            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BACK_TICK"

    // $ANTLR start "FULL_STOP"
    public final void mFULL_STOP() throws RecognitionException {
        try {
            int _type = FULL_STOP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:335:10: ( '.' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:335:12: '.'
            {
            match('.'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FULL_STOP"

    // $ANTLR start "ALT"
    public final void mALT() throws RecognitionException {
        try {
            int _type = ALT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:339:5: ( '|' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:339:7: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ALT"

    // $ANTLR start "OPEN"
    public final void mOPEN() throws RecognitionException {
        try {
            int _type = OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:343:6: ( '(' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:343:8: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OPEN"

    // $ANTLR start "CLOSE"
    public final void mCLOSE() throws RecognitionException {
        try {
            int _type = CLOSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:347:7: ( ')' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:347:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CLOSE"

    // $ANTLR start "TAB_SHORTHAND"
    public final void mTAB_SHORTHAND() throws RecognitionException {
        try {
            int _type = TAB_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:352:2: ( ESCAPE 't' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:352:4: ESCAPE 't'
            {
            mESCAPE(); 


            match('t'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TAB_SHORTHAND"

    // $ANTLR start "NEWLINE_SHORTHAND"
    public final void mNEWLINE_SHORTHAND() throws RecognitionException {
        try {
            int _type = NEWLINE_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:357:2: ( ESCAPE 'n' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:357:4: ESCAPE 'n'
            {
            mESCAPE(); 


            match('n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NEWLINE_SHORTHAND"

    // $ANTLR start "VERTICAL_TAB_SHORTHAND"
    public final void mVERTICAL_TAB_SHORTHAND() throws RecognitionException {
        try {
            int _type = VERTICAL_TAB_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:362:2: ( ESCAPE 'v' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:362:4: ESCAPE 'v'
            {
            mESCAPE(); 


            match('v'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "VERTICAL_TAB_SHORTHAND"

    // $ANTLR start "FORM_FEED_SHORTHAND"
    public final void mFORM_FEED_SHORTHAND() throws RecognitionException {
        try {
            int _type = FORM_FEED_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:367:2: ( ESCAPE 'f' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:367:4: ESCAPE 'f'
            {
            mESCAPE(); 


            match('f'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FORM_FEED_SHORTHAND"

    // $ANTLR start "RETURN_SHORTHAND"
    public final void mRETURN_SHORTHAND() throws RecognitionException {
        try {
            int _type = RETURN_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:372:2: ( ESCAPE 'r' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:372:4: ESCAPE 'r'
            {
            mESCAPE(); 


            match('r'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RETURN_SHORTHAND"

    // $ANTLR start "ESCAPE_SHORTHAND"
    public final void mESCAPE_SHORTHAND() throws RecognitionException {
        try {
            int _type = ESCAPE_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:377:2: ( ESCAPE 'e' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:377:4: ESCAPE 'e'
            {
            mESCAPE(); 


            match('e'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ESCAPE_SHORTHAND"

    // $ANTLR start "DIGIT_SHORTHAND"
    public final void mDIGIT_SHORTHAND() throws RecognitionException {
        try {
            int _type = DIGIT_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:382:2: ( ESCAPE 'd' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:382:4: ESCAPE 'd'
            {
            mESCAPE(); 


            match('d'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DIGIT_SHORTHAND"

    // $ANTLR start "NOT_DIGIT_SHORTHAND"
    public final void mNOT_DIGIT_SHORTHAND() throws RecognitionException {
        try {
            int _type = NOT_DIGIT_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:387:2: ( ESCAPE 'D' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:387:4: ESCAPE 'D'
            {
            mESCAPE(); 


            match('D'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT_DIGIT_SHORTHAND"

    // $ANTLR start "WORD_SHORTHAND"
    public final void mWORD_SHORTHAND() throws RecognitionException {
        try {
            int _type = WORD_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:392:2: ( ESCAPE 'w' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:392:4: ESCAPE 'w'
            {
            mESCAPE(); 


            match('w'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WORD_SHORTHAND"

    // $ANTLR start "NOT_WORD_SHORTHAND"
    public final void mNOT_WORD_SHORTHAND() throws RecognitionException {
        try {
            int _type = NOT_WORD_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:397:2: ( ESCAPE 'W' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:397:4: ESCAPE 'W'
            {
            mESCAPE(); 


            match('W'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT_WORD_SHORTHAND"

    // $ANTLR start "WHITE_SPACE_SHORTHAND"
    public final void mWHITE_SPACE_SHORTHAND() throws RecognitionException {
        try {
            int _type = WHITE_SPACE_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:402:2: ( ESCAPE 's' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:402:4: ESCAPE 's'
            {
            mESCAPE(); 


            match('s'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHITE_SPACE_SHORTHAND"

    // $ANTLR start "NOT_WHITE_SPACE_SHORTHAND"
    public final void mNOT_WHITE_SPACE_SHORTHAND() throws RecognitionException {
        try {
            int _type = NOT_WHITE_SPACE_SHORTHAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:407:2: ( ESCAPE 'S' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:407:4: ESCAPE 'S'
            {
            mESCAPE(); 


            match('S'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT_WHITE_SPACE_SHORTHAND"

    // $ANTLR start "ESCAPE"
    public final void mESCAPE() throws RecognitionException {
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:414:8: ( '\\\\' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:414:10: '\\\\'
            {
            match('\\'); 

            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ESCAPE"

    // $ANTLR start "OPEN_SQUARE"
    public final void mOPEN_SQUARE() throws RecognitionException {
        try {
            int _type = OPEN_SQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:419:2: ( '[' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:419:4: '['
            {
            match('['); 

            inSet++;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OPEN_SQUARE"

    // $ANTLR start "CARET"
    public final void mCARET() throws RecognitionException {
        try {
            int _type = CARET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:424:2: ({...}? => '^' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:424:4: {...}? => '^'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "CARET", "inSet>0");
            }

            match('^'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CARET"

    // $ANTLR start "RANGE_SEPARATOR"
    public final void mRANGE_SEPARATOR() throws RecognitionException {
        try {
            int _type = RANGE_SEPARATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:429:2: ({...}? => '-' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:429:4: {...}? => '-'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "RANGE_SEPARATOR", "inSet>0");
            }

            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RANGE_SEPARATOR"

    // $ANTLR start "SET_ASCII"
    public final void mSET_ASCII() throws RecognitionException {
        try {
            int _type = SET_ASCII;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:434:2: ({...}? => 'ascii' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:434:4: {...}? => 'ascii'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_ASCII", "inSet>0");
            }

            match("ascii"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_ASCII"

    // $ANTLR start "SET_PRINT"
    public final void mSET_PRINT() throws RecognitionException {
        try {
            int _type = SET_PRINT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:438:2: ({...}? => 'print' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:438:4: {...}? => 'print'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_PRINT", "inSet>0");
            }

            match("print"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_PRINT"

    // $ANTLR start "SET_GRAPH"
    public final void mSET_GRAPH() throws RecognitionException {
        try {
            int _type = SET_GRAPH;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:442:2: ({...}? => 'graph' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:442:4: {...}? => 'graph'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_GRAPH", "inSet>0");
            }

            match("graph"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_GRAPH"

    // $ANTLR start "SET_WORD"
    public final void mSET_WORD() throws RecognitionException {
        try {
            int _type = SET_WORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:445:9: ({...}? => 'word' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:445:11: {...}? => 'word'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_WORD", "inSet>0");
            }

            match("word"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_WORD"

    // $ANTLR start "SET_ALPHANUM"
    public final void mSET_ALPHANUM() throws RecognitionException {
        try {
            int _type = SET_ALPHANUM;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:449:2: ({...}? => 'alnum' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:449:4: {...}? => 'alnum'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_ALPHANUM", "inSet>0");
            }

            match("alnum"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_ALPHANUM"

    // $ANTLR start "SET_ALPHA"
    public final void mSET_ALPHA() throws RecognitionException {
        try {
            int _type = SET_ALPHA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:453:2: ({...}? => 'alpha' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:453:4: {...}? => 'alpha'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_ALPHA", "inSet>0");
            }

            match("alpha"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_ALPHA"

    // $ANTLR start "SET_UPPER"
    public final void mSET_UPPER() throws RecognitionException {
        try {
            int _type = SET_UPPER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:457:2: ({...}? => 'upper' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:457:4: {...}? => 'upper'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_UPPER", "inSet>0");
            }

            match("upper"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_UPPER"

    // $ANTLR start "SET_LOWER"
    public final void mSET_LOWER() throws RecognitionException {
        try {
            int _type = SET_LOWER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:461:2: ({...}? => 'lower' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:461:4: {...}? => 'lower'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_LOWER", "inSet>0");
            }

            match("lower"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_LOWER"

    // $ANTLR start "SET_PUNCT"
    public final void mSET_PUNCT() throws RecognitionException {
        try {
            int _type = SET_PUNCT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:465:2: ({...}? => 'punct' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:465:4: {...}? => 'punct'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_PUNCT", "inSet>0");
            }

            match("punct"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_PUNCT"

    // $ANTLR start "SET_HEXDIGIT"
    public final void mSET_HEXDIGIT() throws RecognitionException {
        try {
            int _type = SET_HEXDIGIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:469:2: ({...}? => 'xdigit' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:469:4: {...}? => 'xdigit'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_HEXDIGIT", "inSet>0");
            }

            match("xdigit"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_HEXDIGIT"

    // $ANTLR start "SET_DIGIT"
    public final void mSET_DIGIT() throws RecognitionException {
        try {
            int _type = SET_DIGIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:473:2: ({...}? => 'digit' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:473:4: {...}? => 'digit'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_DIGIT", "inSet>0");
            }

            match("digit"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_DIGIT"

    // $ANTLR start "SET_WHITESPACE"
    public final void mSET_WHITESPACE() throws RecognitionException {
        try {
            int _type = SET_WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:477:2: ({...}? => 'ws' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:477:4: {...}? => 'ws'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_WHITESPACE", "inSet>0");
            }

            match("ws"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_WHITESPACE"

    // $ANTLR start "SET_BLANK"
    public final void mSET_BLANK() throws RecognitionException {
        try {
            int _type = SET_BLANK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:481:2: ({...}? => 'blank' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:481:4: {...}? => 'blank'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_BLANK", "inSet>0");
            }

            match("blank"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_BLANK"

    // $ANTLR start "SET_SPACE"
    public final void mSET_SPACE() throws RecognitionException {
        try {
            int _type = SET_SPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:485:2: ({...}? => 'space' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:485:4: {...}? => 'space'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_SPACE", "inSet>0");
            }

            match("space"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_SPACE"

    // $ANTLR start "SET_TAB"
    public final void mSET_TAB() throws RecognitionException {
        try {
            int _type = SET_TAB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:488:9: ({...}? => 'tab' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:488:11: {...}? => 'tab'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_TAB", "inSet>0");
            }

            match("tab"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_TAB"

    // $ANTLR start "SET_NEWLINE"
    public final void mSET_NEWLINE() throws RecognitionException {
        try {
            int _type = SET_NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:492:2: ({...}? => 'newline' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:492:4: {...}? => 'newline'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_NEWLINE", "inSet>0");
            }

            match("newline"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_NEWLINE"

    // $ANTLR start "SET_RETURN"
    public final void mSET_RETURN() throws RecognitionException {
        try {
            int _type = SET_RETURN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:497:2: ({...}? => 'return' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:497:4: {...}? => 'return'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_RETURN", "inSet>0");
            }

            match("return"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_RETURN"

    // $ANTLR start "SET_CONTROL"
    public final void mSET_CONTROL() throws RecognitionException {
        try {
            int _type = SET_CONTROL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:502:2: ({...}? => 'ctrl' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:502:4: {...}? => 'ctrl'
            {
            if ( !((inSet>0)) ) {
                throw new FailedPredicateException(input, "SET_CONTROL", "inSet>0");
            }

            match("ctrl"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SET_CONTROL"

    // $ANTLR start "CLOSE_SQUARE"
    public final void mCLOSE_SQUARE() throws RecognitionException {
        try {
            int _type = CLOSE_SQUARE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:507:2: ( ']' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:507:4: ']'
            {
            match(']'); 

            inSet--;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CLOSE_SQUARE"

    // $ANTLR start "AMPERSAND"
    public final void mAMPERSAND() throws RecognitionException {
        try {
            int _type = AMPERSAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:513:2: ( '&' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:513:4: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AMPERSAND"

    // $ANTLR start "TILDE"
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:517:7: ( '~' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:517:9: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TILDE"

    // $ANTLR start "MANY"
    public final void mMANY() throws RecognitionException {
        try {
            int _type = MANY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:522:2: ( '*' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:522:4: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MANY"

    // $ANTLR start "QUESTION_MARK"
    public final void mQUESTION_MARK() throws RecognitionException {
        try {
            int _type = QUESTION_MARK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:527:2: ( '?' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:527:4: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "QUESTION_MARK"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:532:2: ( '+' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:532:4: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "OPEN_CURLY"
    public final void mOPEN_CURLY() throws RecognitionException {
        try {
            int _type = OPEN_CURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:537:2: ( '{' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:537:4: '{'
            {
            match('{'); 

             inRepeat=true; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OPEN_CURLY"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:541:8: ({...}? => ( '0' .. '9' )+ )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:541:10: {...}? => ( '0' .. '9' )+
            {
            if ( !(( inRepeat )) ) {
                throw new FailedPredicateException(input, "NUMBER", " inRepeat ");
            }

            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:541:27: ( '0' .. '9' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( ((LA3_0 >= '0' && LA3_0 <= '9')) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NUMBER"

    // $ANTLR start "REPEAT_SEPARATOR"
    public final void mREPEAT_SEPARATOR() throws RecognitionException {
        try {
            int _type = REPEAT_SEPARATOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:546:2: ({...}? => '-' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:546:4: {...}? => '-'
            {
            if ( !(( inRepeat )) ) {
                throw new FailedPredicateException(input, "REPEAT_SEPARATOR", " inRepeat ");
            }

            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "REPEAT_SEPARATOR"

    // $ANTLR start "CLOSE_CURLY"
    public final void mCLOSE_CURLY() throws RecognitionException {
        try {
            int _type = CLOSE_CURLY;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:551:2: ( '}' )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:551:4: '}'
            {
            match('}'); 

             inRepeat=false; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CLOSE_CURLY"

    // $ANTLR start "BYTE"
    public final void mBYTE() throws RecognitionException {
        try {
            int _type = BYTE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:555:6: ({...}? => HEX_DIGIT HEX_DIGIT )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:555:8: {...}? => HEX_DIGIT HEX_DIGIT
            {
            if ( !(( !inRepeat )) ) {
                throw new FailedPredicateException(input, "BYTE", " !inRepeat ");
            }

            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BYTE"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:562:2: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:566:2: ( '#' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' | EOF ) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:566:4: '#' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' | EOF )
            {
            match('#'); 

            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:566:8: (~ ( '\\n' | '\\r' ) )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0 >= '\u0000' && LA4_0 <= '\t')||(LA4_0 >= '\u000B' && LA4_0 <= '\f')||(LA4_0 >= '\u000E' && LA4_0 <= '\uFFFF')) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:566:22: ( '\\r' )?
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='\r') ) {
                alt5=1;
            }
            switch (alt5) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:566:22: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:566:28: ( '\\n' | EOF )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='\n') ) {
                alt6=1;
            }
            else {
                alt6=2;
            }
            switch (alt6) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:566:29: '\\n'
                    {
                    match('\n'); 

                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:566:36: EOF
                    {
                    match(EOF); 


                    }
                    break;

            }


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:570:4: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:570:6: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    public void mTokens() throws RecognitionException {
        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:8: ( CASE_SENSITIVE_STRING | CASE_INSENSITIVE_STRING | FULL_STOP | ALT | OPEN | CLOSE | TAB_SHORTHAND | NEWLINE_SHORTHAND | VERTICAL_TAB_SHORTHAND | FORM_FEED_SHORTHAND | RETURN_SHORTHAND | ESCAPE_SHORTHAND | DIGIT_SHORTHAND | NOT_DIGIT_SHORTHAND | WORD_SHORTHAND | NOT_WORD_SHORTHAND | WHITE_SPACE_SHORTHAND | NOT_WHITE_SPACE_SHORTHAND | OPEN_SQUARE | CARET | RANGE_SEPARATOR | SET_ASCII | SET_PRINT | SET_GRAPH | SET_WORD | SET_ALPHANUM | SET_ALPHA | SET_UPPER | SET_LOWER | SET_PUNCT | SET_HEXDIGIT | SET_DIGIT | SET_WHITESPACE | SET_BLANK | SET_SPACE | SET_TAB | SET_NEWLINE | SET_RETURN | SET_CONTROL | CLOSE_SQUARE | AMPERSAND | TILDE | MANY | QUESTION_MARK | PLUS | OPEN_CURLY | NUMBER | REPEAT_SEPARATOR | CLOSE_CURLY | BYTE | COMMENT | WS )
        int alt7=52;
        alt7 = dfa7.predict(input);
        switch (alt7) {
            case 1 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:10: CASE_SENSITIVE_STRING
                {
                mCASE_SENSITIVE_STRING(); 


                }
                break;
            case 2 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:32: CASE_INSENSITIVE_STRING
                {
                mCASE_INSENSITIVE_STRING(); 


                }
                break;
            case 3 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:56: FULL_STOP
                {
                mFULL_STOP(); 


                }
                break;
            case 4 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:66: ALT
                {
                mALT(); 


                }
                break;
            case 5 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:70: OPEN
                {
                mOPEN(); 


                }
                break;
            case 6 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:75: CLOSE
                {
                mCLOSE(); 


                }
                break;
            case 7 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:81: TAB_SHORTHAND
                {
                mTAB_SHORTHAND(); 


                }
                break;
            case 8 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:95: NEWLINE_SHORTHAND
                {
                mNEWLINE_SHORTHAND(); 


                }
                break;
            case 9 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:113: VERTICAL_TAB_SHORTHAND
                {
                mVERTICAL_TAB_SHORTHAND(); 


                }
                break;
            case 10 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:136: FORM_FEED_SHORTHAND
                {
                mFORM_FEED_SHORTHAND(); 


                }
                break;
            case 11 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:156: RETURN_SHORTHAND
                {
                mRETURN_SHORTHAND(); 


                }
                break;
            case 12 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:173: ESCAPE_SHORTHAND
                {
                mESCAPE_SHORTHAND(); 


                }
                break;
            case 13 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:190: DIGIT_SHORTHAND
                {
                mDIGIT_SHORTHAND(); 


                }
                break;
            case 14 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:206: NOT_DIGIT_SHORTHAND
                {
                mNOT_DIGIT_SHORTHAND(); 


                }
                break;
            case 15 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:226: WORD_SHORTHAND
                {
                mWORD_SHORTHAND(); 


                }
                break;
            case 16 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:241: NOT_WORD_SHORTHAND
                {
                mNOT_WORD_SHORTHAND(); 


                }
                break;
            case 17 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:260: WHITE_SPACE_SHORTHAND
                {
                mWHITE_SPACE_SHORTHAND(); 


                }
                break;
            case 18 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:282: NOT_WHITE_SPACE_SHORTHAND
                {
                mNOT_WHITE_SPACE_SHORTHAND(); 


                }
                break;
            case 19 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:308: OPEN_SQUARE
                {
                mOPEN_SQUARE(); 


                }
                break;
            case 20 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:320: CARET
                {
                mCARET(); 


                }
                break;
            case 21 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:326: RANGE_SEPARATOR
                {
                mRANGE_SEPARATOR(); 


                }
                break;
            case 22 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:342: SET_ASCII
                {
                mSET_ASCII(); 


                }
                break;
            case 23 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:352: SET_PRINT
                {
                mSET_PRINT(); 


                }
                break;
            case 24 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:362: SET_GRAPH
                {
                mSET_GRAPH(); 


                }
                break;
            case 25 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:372: SET_WORD
                {
                mSET_WORD(); 


                }
                break;
            case 26 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:381: SET_ALPHANUM
                {
                mSET_ALPHANUM(); 


                }
                break;
            case 27 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:394: SET_ALPHA
                {
                mSET_ALPHA(); 


                }
                break;
            case 28 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:404: SET_UPPER
                {
                mSET_UPPER(); 


                }
                break;
            case 29 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:414: SET_LOWER
                {
                mSET_LOWER(); 


                }
                break;
            case 30 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:424: SET_PUNCT
                {
                mSET_PUNCT(); 


                }
                break;
            case 31 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:434: SET_HEXDIGIT
                {
                mSET_HEXDIGIT(); 


                }
                break;
            case 32 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:447: SET_DIGIT
                {
                mSET_DIGIT(); 


                }
                break;
            case 33 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:457: SET_WHITESPACE
                {
                mSET_WHITESPACE(); 


                }
                break;
            case 34 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:472: SET_BLANK
                {
                mSET_BLANK(); 


                }
                break;
            case 35 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:482: SET_SPACE
                {
                mSET_SPACE(); 


                }
                break;
            case 36 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:492: SET_TAB
                {
                mSET_TAB(); 


                }
                break;
            case 37 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:500: SET_NEWLINE
                {
                mSET_NEWLINE(); 


                }
                break;
            case 38 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:512: SET_RETURN
                {
                mSET_RETURN(); 


                }
                break;
            case 39 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:523: SET_CONTROL
                {
                mSET_CONTROL(); 


                }
                break;
            case 40 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:535: CLOSE_SQUARE
                {
                mCLOSE_SQUARE(); 


                }
                break;
            case 41 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:548: AMPERSAND
                {
                mAMPERSAND(); 


                }
                break;
            case 42 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:558: TILDE
                {
                mTILDE(); 


                }
                break;
            case 43 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:564: MANY
                {
                mMANY(); 


                }
                break;
            case 44 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:569: QUESTION_MARK
                {
                mQUESTION_MARK(); 


                }
                break;
            case 45 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:583: PLUS
                {
                mPLUS(); 


                }
                break;
            case 46 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:588: OPEN_CURLY
                {
                mOPEN_CURLY(); 


                }
                break;
            case 47 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:599: NUMBER
                {
                mNUMBER(); 


                }
                break;
            case 48 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:606: REPEAT_SEPARATOR
                {
                mREPEAT_SEPARATOR(); 


                }
                break;
            case 49 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:623: CLOSE_CURLY
                {
                mCLOSE_CURLY(); 


                }
                break;
            case 50 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:635: BYTE
                {
                mBYTE(); 


                }
                break;
            case 51 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:640: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 52 :
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:1:648: WS
                {
                mWS(); 


                }
                break;

        }

    }


    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS =
        "\12\uffff\1\61\25\uffff\1\73\33\uffff\1\101\5\uffff";
    static final String DFA7_eofS =
        "\102\uffff";
    static final String DFA7_minS =
        "\1\11\6\uffff\1\104\2\uffff\1\0\1\60\1\162\1\uffff\1\157\3\uffff"+
        "\2\60\4\uffff\1\60\7\uffff\1\60\20\uffff\1\0\1\uffff\1\156\10\uffff"+
        "\1\60\4\uffff\1\0";
    static final String DFA7_maxS =
        "\1\176\6\uffff\1\167\2\uffff\1\0\1\163\1\165\1\uffff\1\163\3\uffff"+
        "\1\151\1\154\4\uffff\1\164\7\uffff\1\146\20\uffff\1\0\1\uffff\1"+
        "\160\10\uffff\1\71\4\uffff\1\0";
    static final String DFA7_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\uffff\1\23\1\24\3\uffff\1\30"+
        "\1\uffff\1\34\1\35\1\37\2\uffff\1\43\1\44\1\45\1\46\1\uffff\1\50"+
        "\1\51\1\52\1\53\1\54\1\55\1\56\1\uffff\1\61\1\62\1\63\1\64\1\7\1"+
        "\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\uffff\1"+
        "\26\1\uffff\1\27\1\36\1\31\1\41\1\40\1\42\1\47\1\57\1\uffff\1\25"+
        "\1\60\1\32\1\33\1\uffff";
    static final String DFA7_specialS =
        "\1\13\11\uffff\1\4\1\6\1\7\1\uffff\1\5\3\uffff\1\12\1\11\4\uffff"+
        "\1\0\7\uffff\1\2\20\uffff\1\14\1\uffff\1\10\10\uffff\1\3\4\uffff"+
        "\1\1}>";
    static final String[] DFA7_transitionS = {
            "\2\44\2\uffff\1\44\22\uffff\1\44\2\uffff\1\43\2\uffff\1\32\1"+
            "\1\1\5\1\6\1\34\1\36\1\uffff\1\12\1\3\1\uffff\12\40\5\uffff"+
            "\1\35\1\uffff\6\42\24\uffff\1\10\1\7\1\31\1\11\1\uffff\1\2\1"+
            "\13\1\23\1\30\1\22\2\42\1\15\4\uffff\1\20\1\uffff\1\26\1\uffff"+
            "\1\14\1\uffff\1\27\1\24\1\25\1\17\1\uffff\1\16\1\21\2\uffff"+
            "\1\37\1\4\1\41\1\33",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\54\16\uffff\1\60\3\uffff\1\56\14\uffff\1\53\1\52\1\50\7"+
            "\uffff\1\46\3\uffff\1\51\1\57\1\45\1\uffff\1\47\1\55",
            "",
            "",
            "\1\uffff",
            "\12\42\7\uffff\6\42\32\uffff\6\42\5\uffff\1\63\6\uffff\1\62",
            "\1\64\2\uffff\1\65",
            "",
            "\1\66\3\uffff\1\67",
            "",
            "",
            "",
            "\12\42\7\uffff\6\42\32\uffff\6\42\2\uffff\1\70",
            "\12\42\7\uffff\6\42\32\uffff\6\42\5\uffff\1\71",
            "",
            "",
            "",
            "",
            "\12\42\7\uffff\6\42\32\uffff\6\42\15\uffff\1\72",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\74\7\uffff\6\42\32\uffff\6\42",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\uffff",
            "",
            "\1\77\1\uffff\1\100",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\12\73",
            "",
            "",
            "",
            "",
            "\1\uffff"
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( CASE_SENSITIVE_STRING | CASE_INSENSITIVE_STRING | FULL_STOP | ALT | OPEN | CLOSE | TAB_SHORTHAND | NEWLINE_SHORTHAND | VERTICAL_TAB_SHORTHAND | FORM_FEED_SHORTHAND | RETURN_SHORTHAND | ESCAPE_SHORTHAND | DIGIT_SHORTHAND | NOT_DIGIT_SHORTHAND | WORD_SHORTHAND | NOT_WORD_SHORTHAND | WHITE_SPACE_SHORTHAND | NOT_WHITE_SPACE_SHORTHAND | OPEN_SQUARE | CARET | RANGE_SEPARATOR | SET_ASCII | SET_PRINT | SET_GRAPH | SET_WORD | SET_ALPHANUM | SET_ALPHA | SET_UPPER | SET_LOWER | SET_PUNCT | SET_HEXDIGIT | SET_DIGIT | SET_WHITESPACE | SET_BLANK | SET_SPACE | SET_TAB | SET_NEWLINE | SET_RETURN | SET_CONTROL | CLOSE_SQUARE | AMPERSAND | TILDE | MANY | QUESTION_MARK | PLUS | OPEN_CURLY | NUMBER | REPEAT_SEPARATOR | CLOSE_CURLY | BYTE | COMMENT | WS );";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA7_24 = input.LA(1);

                         
                        int index7_24 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA7_24=='t') && ((inSet>0))) {s = 58;}

                        else if ( ((LA7_24 >= '0' && LA7_24 <= '9')||(LA7_24 >= 'A' && LA7_24 <= 'F')||(LA7_24 >= 'a' && LA7_24 <= 'f')) && (( !inRepeat ))) {s = 34;}

                         
                        input.seek(index7_24);

                        if ( s>=0 ) return s;
                        break;

                    case 1 : 
                        int LA7_65 = input.LA(1);

                         
                        int index7_65 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (( inRepeat )) ) {s = 59;}

                        else if ( (( !inRepeat )) ) {s = 34;}

                         
                        input.seek(index7_65);

                        if ( s>=0 ) return s;
                        break;

                    case 2 : 
                        int LA7_32 = input.LA(1);

                         
                        int index7_32 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((LA7_32 >= '0' && LA7_32 <= '9')) && ((( inRepeat )||( !inRepeat )))) {s = 60;}

                        else if ( ((LA7_32 >= 'A' && LA7_32 <= 'F')||(LA7_32 >= 'a' && LA7_32 <= 'f')) && (( !inRepeat ))) {s = 34;}

                        else s = 59;

                         
                        input.seek(index7_32);

                        if ( s>=0 ) return s;
                        break;

                    case 3 : 
                        int LA7_60 = input.LA(1);

                         
                        int index7_60 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((LA7_60 >= '0' && LA7_60 <= '9')) && (( inRepeat ))) {s = 59;}

                        else s = 65;

                         
                        input.seek(index7_60);

                        if ( s>=0 ) return s;
                        break;

                    case 4 : 
                        int LA7_10 = input.LA(1);

                         
                        int index7_10 = input.index();
                        input.rewind();

                        s = -1;
                        s = 49;

                         
                        input.seek(index7_10);

                        if ( s>=0 ) return s;
                        break;

                    case 5 : 
                        int LA7_14 = input.LA(1);

                         
                        int index7_14 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA7_14=='o') && ((inSet>0))) {s = 54;}

                        else if ( (LA7_14=='s') && ((inSet>0))) {s = 55;}

                         
                        input.seek(index7_14);

                        if ( s>=0 ) return s;
                        break;

                    case 6 : 
                        int LA7_11 = input.LA(1);

                         
                        int index7_11 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA7_11=='s') && ((inSet>0))) {s = 50;}

                        else if ( (LA7_11=='l') && ((inSet>0))) {s = 51;}

                        else if ( ((LA7_11 >= '0' && LA7_11 <= '9')||(LA7_11 >= 'A' && LA7_11 <= 'F')||(LA7_11 >= 'a' && LA7_11 <= 'f')) && (( !inRepeat ))) {s = 34;}

                         
                        input.seek(index7_11);

                        if ( s>=0 ) return s;
                        break;

                    case 7 : 
                        int LA7_12 = input.LA(1);

                         
                        int index7_12 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA7_12=='r') && ((inSet>0))) {s = 52;}

                        else if ( (LA7_12=='u') && ((inSet>0))) {s = 53;}

                         
                        input.seek(index7_12);

                        if ( s>=0 ) return s;
                        break;

                    case 8 : 
                        int LA7_51 = input.LA(1);

                         
                        int index7_51 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA7_51=='n') && ((inSet>0))) {s = 63;}

                        else if ( (LA7_51=='p') && ((inSet>0))) {s = 64;}

                         
                        input.seek(index7_51);

                        if ( s>=0 ) return s;
                        break;

                    case 9 : 
                        int LA7_19 = input.LA(1);

                         
                        int index7_19 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA7_19=='l') && ((inSet>0))) {s = 57;}

                        else if ( ((LA7_19 >= '0' && LA7_19 <= '9')||(LA7_19 >= 'A' && LA7_19 <= 'F')||(LA7_19 >= 'a' && LA7_19 <= 'f')) && (( !inRepeat ))) {s = 34;}

                         
                        input.seek(index7_19);

                        if ( s>=0 ) return s;
                        break;

                    case 10 : 
                        int LA7_18 = input.LA(1);

                         
                        int index7_18 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA7_18=='i') && ((inSet>0))) {s = 56;}

                        else if ( ((LA7_18 >= '0' && LA7_18 <= '9')||(LA7_18 >= 'A' && LA7_18 <= 'F')||(LA7_18 >= 'a' && LA7_18 <= 'f')) && (( !inRepeat ))) {s = 34;}

                         
                        input.seek(index7_18);

                        if ( s>=0 ) return s;
                        break;

                    case 11 : 
                        int LA7_0 = input.LA(1);

                         
                        int index7_0 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (LA7_0=='\'') ) {s = 1;}

                        else if ( (LA7_0=='`') ) {s = 2;}

                        else if ( (LA7_0=='.') ) {s = 3;}

                        else if ( (LA7_0=='|') ) {s = 4;}

                        else if ( (LA7_0=='(') ) {s = 5;}

                        else if ( (LA7_0==')') ) {s = 6;}

                        else if ( (LA7_0=='\\') ) {s = 7;}

                        else if ( (LA7_0=='[') ) {s = 8;}

                        else if ( (LA7_0=='^') && ((inSet>0))) {s = 9;}

                        else if ( (LA7_0=='-') && ((( inRepeat )||(inSet>0)))) {s = 10;}

                        else if ( (LA7_0=='a') && (((inSet>0)||( !inRepeat )))) {s = 11;}

                        else if ( (LA7_0=='p') && ((inSet>0))) {s = 12;}

                        else if ( (LA7_0=='g') && ((inSet>0))) {s = 13;}

                        else if ( (LA7_0=='w') && ((inSet>0))) {s = 14;}

                        else if ( (LA7_0=='u') && ((inSet>0))) {s = 15;}

                        else if ( (LA7_0=='l') && ((inSet>0))) {s = 16;}

                        else if ( (LA7_0=='x') && ((inSet>0))) {s = 17;}

                        else if ( (LA7_0=='d') && (((inSet>0)||( !inRepeat )))) {s = 18;}

                        else if ( (LA7_0=='b') && (((inSet>0)||( !inRepeat )))) {s = 19;}

                        else if ( (LA7_0=='s') && ((inSet>0))) {s = 20;}

                        else if ( (LA7_0=='t') && ((inSet>0))) {s = 21;}

                        else if ( (LA7_0=='n') && ((inSet>0))) {s = 22;}

                        else if ( (LA7_0=='r') && ((inSet>0))) {s = 23;}

                        else if ( (LA7_0=='c') && (((inSet>0)||( !inRepeat )))) {s = 24;}

                        else if ( (LA7_0==']') ) {s = 25;}

                        else if ( (LA7_0=='&') ) {s = 26;}

                        else if ( (LA7_0=='~') ) {s = 27;}

                        else if ( (LA7_0=='*') ) {s = 28;}

                        else if ( (LA7_0=='?') ) {s = 29;}

                        else if ( (LA7_0=='+') ) {s = 30;}

                        else if ( (LA7_0=='{') ) {s = 31;}

                        else if ( ((LA7_0 >= '0' && LA7_0 <= '9')) && ((( inRepeat )||( !inRepeat )))) {s = 32;}

                        else if ( (LA7_0=='}') ) {s = 33;}

                        else if ( ((LA7_0 >= 'A' && LA7_0 <= 'F')||(LA7_0 >= 'e' && LA7_0 <= 'f')) && (( !inRepeat ))) {s = 34;}

                        else if ( (LA7_0=='#') ) {s = 35;}

                        else if ( ((LA7_0 >= '\t' && LA7_0 <= '\n')||LA7_0=='\r'||LA7_0==' ') ) {s = 36;}

                         
                        input.seek(index7_0);

                        if ( s>=0 ) return s;
                        break;

                    case 12 : 
                        int LA7_49 = input.LA(1);

                         
                        int index7_49 = input.index();
                        input.rewind();

                        s = -1;
                        if ( ((inSet>0)) ) {s = 61;}

                        else if ( (( inRepeat )) ) {s = 62;}

                         
                        input.seek(index7_49);

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 7, _s, input);
            error(nvae);
            throw nvae;
        }

    }
 

}