// $ANTLR 3.4 /home/matt/dev/search/byteseek/antlr/AntlrRegex.g 2012-11-03 21:24:05

package net.domesdaybook.parser.regex.antlr;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.tree.*;


@SuppressWarnings({"all", "warnings", "unchecked"})
public class AntlrRegexParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ALL_BITMASK", "ALT", "ALTERNATE", "AMPERSAND", "ANY", "ANY_BITMASK", "BACK_TICK", "BYTE", "CARET", "CASE_INSENSITIVE_STRING", "CASE_SENSITIVE_STRING", "CLOSE", "CLOSE_CURLY", "CLOSE_SQUARE", "COMMENT", "DIGIT_SHORTHAND", "ESCAPE", "ESCAPE_SHORTHAND", "FORM_FEED_SHORTHAND", "FULL_STOP", "HEX_DIGIT", "INVERTED_SET", "MANY", "NEWLINE_SHORTHAND", "NOT_DIGIT_SHORTHAND", "NOT_WHITE_SPACE_SHORTHAND", "NOT_WORD_SHORTHAND", "NUMBER", "OPEN", "OPEN_CURLY", "OPEN_SQUARE", "PLUS", "QUESTION_MARK", "QUOTE", "RANGE", "RANGE_SEPARATOR", "REPEAT", "REPEAT_SEPARATOR", "RETURN_SHORTHAND", "SEQUENCE", "SET", "SET_ALPHA", "SET_ALPHANUM", "SET_ASCII", "SET_BLANK", "SET_CONTROL", "SET_DIGIT", "SET_GRAPH", "SET_HEXDIGIT", "SET_LOWER", "SET_NEWLINE", "SET_PRINT", "SET_PUNCT", "SET_RETURN", "SET_SPACE", "SET_TAB", "SET_UPPER", "SET_WHITESPACE", "SET_WORD", "TAB_SHORTHAND", "TILDE", "VERTICAL_TAB_SHORTHAND", "WHITE_SPACE_SHORTHAND", "WORD_SHORTHAND", "WS"
    };

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

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public AntlrRegexParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public AntlrRegexParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

protected TreeAdaptor adaptor = new CommonTreeAdaptor();

public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
}
public TreeAdaptor getTreeAdaptor() {
    return adaptor;
}
    public String[] getTokenNames() { return AntlrRegexParser.tokenNames; }
    public String getGrammarFileName() { return "/home/matt/dev/search/byteseek/antlr/AntlrRegex.g"; }


    	boolean sequencesAsTree = false;


    public static class start_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "start"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:107:1: start : regex EOF !;
    public final AntlrRegexParser.start_return start() throws RecognitionException {
        AntlrRegexParser.start_return retval = new AntlrRegexParser.start_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token EOF2=null;
        AntlrRegexParser.regex_return regex1 =null;


        Object EOF2_tree=null;

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:107:7: ( regex EOF !)
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:107:9: regex EOF !
            {
            root_0 = (Object)adaptor.nil();


            pushFollow(FOLLOW_regex_in_start157);
            regex1=regex();

            state._fsp--;

            adaptor.addChild(root_0, regex1.getTree());

            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_start159); 

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "start"


    public static class regex_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "regex"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:111:1: regex : sequence ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) ) ;
    public final AntlrRegexParser.regex_return regex() throws RecognitionException {
        AntlrRegexParser.regex_return retval = new AntlrRegexParser.regex_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token ALT4=null;
        AntlrRegexParser.sequence_return sequence3 =null;

        AntlrRegexParser.sequence_return sequence5 =null;


        Object ALT4_tree=null;
        RewriteRuleTokenStream stream_ALT=new RewriteRuleTokenStream(adaptor,"token ALT");
        RewriteRuleSubtreeStream stream_sequence=new RewriteRuleSubtreeStream(adaptor,"rule sequence");
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:111:7: ( sequence ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) ) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:112:3: sequence ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) )
            {
            pushFollow(FOLLOW_sequence_in_regex178);
            sequence3=sequence();

            state._fsp--;

            stream_sequence.add(sequence3.getTree());

            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:113:3: ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==ALT) ) {
                alt2=1;
            }
            else if ( (LA2_0==EOF||LA2_0==CLOSE) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }
            switch (alt2) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:114:4: ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) )
                    {
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:114:4: ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:114:6: ( ALT sequence )+
                    {
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:114:6: ( ALT sequence )+
                    int cnt1=0;
                    loop1:
                    do {
                        int alt1=2;
                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==ALT) ) {
                            alt1=1;
                        }


                        switch (alt1) {
                    	case 1 :
                    	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:114:7: ALT sequence
                    	    {
                    	    ALT4=(Token)match(input,ALT,FOLLOW_ALT_in_regex192);  
                    	    stream_ALT.add(ALT4);


                    	    pushFollow(FOLLOW_sequence_in_regex194);
                    	    sequence5=sequence();

                    	    state._fsp--;

                    	    stream_sequence.add(sequence5.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt1 >= 1 ) break loop1;
                                EarlyExitException eee =
                                    new EarlyExitException(1, input);
                                throw eee;
                        }
                        cnt1++;
                    } while (true);


                    // AST REWRITE
                    // elements: ALT, sequence
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 114:22: -> ^( ALT ( sequence )+ )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:114:25: ^( ALT ( sequence )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        stream_ALT.nextNode()
                        , root_1);

                        if ( !(stream_sequence.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_sequence.hasNext() ) {
                            adaptor.addChild(root_1, stream_sequence.nextTree());

                        }
                        stream_sequence.reset();

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }


                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:116:4: ( -> sequence )
                    {
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:116:4: ( -> sequence )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:116:9: 
                    {
                    // AST REWRITE
                    // elements: sequence
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 116:9: -> sequence
                    {
                        adaptor.addChild(root_0, stream_sequence.nextTree());

                    }


                    retval.tree = root_0;

                    }


                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "regex"


    public static class sequence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "sequence"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:123:1: sequence : ({...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? ) |{...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) ) );
    public final AntlrRegexParser.sequence_return sequence() throws RecognitionException {
        AntlrRegexParser.sequence_return retval = new AntlrRegexParser.sequence_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        AntlrRegexParser.quantified_atom_return quantified_atom6 =null;

        AntlrRegexParser.sequence_return sequence7 =null;

        AntlrRegexParser.quantified_atom_return quantified_atom8 =null;

        AntlrRegexParser.quantified_atom_return quantified_atom9 =null;


        RewriteRuleSubtreeStream stream_quantified_atom=new RewriteRuleSubtreeStream(adaptor,"rule quantified_atom");
        RewriteRuleSubtreeStream stream_sequence=new RewriteRuleSubtreeStream(adaptor,"rule sequence");
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:124:2: ({...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? ) |{...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) ) )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==BYTE) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_1 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==FULL_STOP) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_2 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 2, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==OPEN_SQUARE) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_3 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 3, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==TAB_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_4 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 4, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==NEWLINE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_5 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 5, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==VERTICAL_TAB_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_6 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 6, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==FORM_FEED_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_7 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 7, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==RETURN_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_8 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 8, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==ESCAPE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_9 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 9, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==DIGIT_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_10 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 10, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==NOT_DIGIT_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_11 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 11, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==WORD_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_12 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 12, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==NOT_WORD_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_13 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 13, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==WHITE_SPACE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_14 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 14, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==NOT_WHITE_SPACE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_15 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 15, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==AMPERSAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_16 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 16, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==TILDE) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_17 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 17, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==CASE_SENSITIVE_STRING) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_18 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 18, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==CASE_INSENSITIVE_STRING) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_19 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 19, input);

                    throw nvae;

                }
            }
            else if ( (LA6_0==OPEN) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                int LA6_20 = input.LA(2);

                if ( ((sequencesAsTree)) ) {
                    alt6=1;
                }
                else if ( ((!sequencesAsTree)) ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 20, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }
            switch (alt6) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:124:4: {...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? )
                    {
                    if ( !((sequencesAsTree)) ) {
                        throw new FailedPredicateException(input, "sequence", "sequencesAsTree");
                    }

                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:125:2: ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:126:3: ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )?
                    {
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:126:3: ( quantified_atom -> quantified_atom )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:126:5: quantified_atom
                    {
                    pushFollow(FOLLOW_quantified_atom_in_sequence260);
                    quantified_atom6=quantified_atom();

                    state._fsp--;

                    stream_quantified_atom.add(quantified_atom6.getTree());

                    // AST REWRITE
                    // elements: quantified_atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 126:21: -> quantified_atom
                    {
                        adaptor.addChild(root_0, stream_quantified_atom.nextTree());

                    }


                    retval.tree = root_0;

                    }


                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:127:3: ( sequence -> ^( SEQUENCE quantified_atom sequence ) )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0==AMPERSAND||LA3_0==BYTE||(LA3_0 >= CASE_INSENSITIVE_STRING && LA3_0 <= CASE_SENSITIVE_STRING)||LA3_0==DIGIT_SHORTHAND||(LA3_0 >= ESCAPE_SHORTHAND && LA3_0 <= FULL_STOP)||(LA3_0 >= NEWLINE_SHORTHAND && LA3_0 <= NOT_WORD_SHORTHAND)||LA3_0==OPEN||LA3_0==OPEN_SQUARE||LA3_0==RETURN_SHORTHAND||(LA3_0 >= TAB_SHORTHAND && LA3_0 <= WORD_SHORTHAND)) && (((!sequencesAsTree)||(sequencesAsTree)))) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:127:5: sequence
                            {
                            pushFollow(FOLLOW_sequence_in_sequence272);
                            sequence7=sequence();

                            state._fsp--;

                            stream_sequence.add(sequence7.getTree());

                            // AST REWRITE
                            // elements: sequence, quantified_atom
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 127:16: -> ^( SEQUENCE quantified_atom sequence )
                            {
                                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:127:19: ^( SEQUENCE quantified_atom sequence )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                root_1 = (Object)adaptor.becomeRoot(
                                (Object)adaptor.create(SEQUENCE, "SEQUENCE")
                                , root_1);

                                adaptor.addChild(root_1, stream_quantified_atom.nextTree());

                                adaptor.addChild(root_1, stream_sequence.nextTree());

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:130:3: {...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) )
                    {
                    if ( !((!sequencesAsTree)) ) {
                        throw new FailedPredicateException(input, "sequence", "!sequencesAsTree");
                    }

                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:131:2: ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:132:3: quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) )
                    {
                    pushFollow(FOLLOW_quantified_atom_in_sequence306);
                    quantified_atom8=quantified_atom();

                    state._fsp--;

                    stream_quantified_atom.add(quantified_atom8.getTree());

                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:133:3: ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) )
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==AMPERSAND||LA5_0==BYTE||(LA5_0 >= CASE_INSENSITIVE_STRING && LA5_0 <= CASE_SENSITIVE_STRING)||LA5_0==DIGIT_SHORTHAND||(LA5_0 >= ESCAPE_SHORTHAND && LA5_0 <= FULL_STOP)||(LA5_0 >= NEWLINE_SHORTHAND && LA5_0 <= NOT_WORD_SHORTHAND)||LA5_0==OPEN||LA5_0==OPEN_SQUARE||LA5_0==RETURN_SHORTHAND||(LA5_0 >= TAB_SHORTHAND && LA5_0 <= WORD_SHORTHAND)) ) {
                        alt5=1;
                    }
                    else if ( (LA5_0==EOF||LA5_0==ALT||LA5_0==CLOSE) ) {
                        alt5=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 0, input);

                        throw nvae;

                    }
                    switch (alt5) {
                        case 1 :
                            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:134:4: ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) )
                            {
                            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:134:4: ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) )
                            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:134:6: ( quantified_atom )+
                            {
                            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:134:6: ( quantified_atom )+
                            int cnt4=0;
                            loop4:
                            do {
                                int alt4=2;
                                int LA4_0 = input.LA(1);

                                if ( (LA4_0==AMPERSAND||LA4_0==BYTE||(LA4_0 >= CASE_INSENSITIVE_STRING && LA4_0 <= CASE_SENSITIVE_STRING)||LA4_0==DIGIT_SHORTHAND||(LA4_0 >= ESCAPE_SHORTHAND && LA4_0 <= FULL_STOP)||(LA4_0 >= NEWLINE_SHORTHAND && LA4_0 <= NOT_WORD_SHORTHAND)||LA4_0==OPEN||LA4_0==OPEN_SQUARE||LA4_0==RETURN_SHORTHAND||(LA4_0 >= TAB_SHORTHAND && LA4_0 <= WORD_SHORTHAND)) ) {
                                    alt4=1;
                                }


                                switch (alt4) {
                            	case 1 :
                            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:134:6: quantified_atom
                            	    {
                            	    pushFollow(FOLLOW_quantified_atom_in_sequence317);
                            	    quantified_atom9=quantified_atom();

                            	    state._fsp--;

                            	    stream_quantified_atom.add(quantified_atom9.getTree());

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt4 >= 1 ) break loop4;
                                        EarlyExitException eee =
                                            new EarlyExitException(4, input);
                                        throw eee;
                                }
                                cnt4++;
                            } while (true);


                            // AST REWRITE
                            // elements: quantified_atom
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 134:23: -> ^( SEQUENCE ( quantified_atom )+ )
                            {
                                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:134:26: ^( SEQUENCE ( quantified_atom )+ )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                root_1 = (Object)adaptor.becomeRoot(
                                (Object)adaptor.create(SEQUENCE, "SEQUENCE")
                                , root_1);

                                if ( !(stream_quantified_atom.hasNext()) ) {
                                    throw new RewriteEarlyExitException();
                                }
                                while ( stream_quantified_atom.hasNext() ) {
                                    adaptor.addChild(root_1, stream_quantified_atom.nextTree());

                                }
                                stream_quantified_atom.reset();

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;

                            }


                            }
                            break;
                        case 2 :
                            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:136:4: ( -> ^( quantified_atom ) )
                            {
                            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:136:4: ( -> ^( quantified_atom ) )
                            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:136:8: 
                            {
                            // AST REWRITE
                            // elements: quantified_atom
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 136:8: -> ^( quantified_atom )
                            {
                                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:136:11: ^( quantified_atom )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                root_1 = (Object)adaptor.becomeRoot(stream_quantified_atom.nextNode(), root_1);

                                adaptor.addChild(root_0, root_1);
                                }

                            }


                            retval.tree = root_0;

                            }


                            }
                            break;

                    }


                    }


                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "sequence"


    public static class quantified_atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "quantified_atom"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:143:1: quantified_atom : e= atom ( quantifier -> ^( quantifier $e) | -> ^( $e) ) ;
    public final AntlrRegexParser.quantified_atom_return quantified_atom() throws RecognitionException {
        AntlrRegexParser.quantified_atom_return retval = new AntlrRegexParser.quantified_atom_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        AntlrRegexParser.atom_return e =null;

        AntlrRegexParser.quantifier_return quantifier10 =null;


        RewriteRuleSubtreeStream stream_quantifier=new RewriteRuleSubtreeStream(adaptor,"rule quantifier");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:144:2: (e= atom ( quantifier -> ^( quantifier $e) | -> ^( $e) ) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:144:4: e= atom ( quantifier -> ^( quantifier $e) | -> ^( $e) )
            {
            pushFollow(FOLLOW_atom_in_quantified_atom377);
            e=atom();

            state._fsp--;

            stream_atom.add(e.getTree());

            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:145:2: ( quantifier -> ^( quantifier $e) | -> ^( $e) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==MANY||LA7_0==OPEN_CURLY||(LA7_0 >= PLUS && LA7_0 <= QUESTION_MARK)) ) {
                alt7=1;
            }
            else if ( (LA7_0==EOF||LA7_0==ALT||LA7_0==AMPERSAND||LA7_0==BYTE||(LA7_0 >= CASE_INSENSITIVE_STRING && LA7_0 <= CLOSE)||LA7_0==DIGIT_SHORTHAND||(LA7_0 >= ESCAPE_SHORTHAND && LA7_0 <= FULL_STOP)||(LA7_0 >= NEWLINE_SHORTHAND && LA7_0 <= NOT_WORD_SHORTHAND)||LA7_0==OPEN||LA7_0==OPEN_SQUARE||LA7_0==RETURN_SHORTHAND||(LA7_0 >= TAB_SHORTHAND && LA7_0 <= WORD_SHORTHAND)) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }
            switch (alt7) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:145:4: quantifier
                    {
                    pushFollow(FOLLOW_quantifier_in_quantified_atom382);
                    quantifier10=quantifier();

                    state._fsp--;

                    stream_quantifier.add(quantifier10.getTree());

                    // AST REWRITE
                    // elements: e, quantifier
                    // token labels: 
                    // rule labels: retval, e
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 145:17: -> ^( quantifier $e)
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:145:20: ^( quantifier $e)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_quantifier.nextNode(), root_1);

                        adaptor.addChild(root_1, stream_e.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:146:7: 
                    {
                    // AST REWRITE
                    // elements: e
                    // token labels: 
                    // rule labels: retval, e
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"rule e",e!=null?e.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 146:7: -> ^( $e)
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:146:10: ^( $e)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_e.nextNode(), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "quantified_atom"


    public static class atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "atom"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:151:1: atom : ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | all_bitmask | any_bitmask | case_sensitive_string | case_insensitive_string | group ) ;
    public final AntlrRegexParser.atom_return atom() throws RecognitionException {
        AntlrRegexParser.atom_return retval = new AntlrRegexParser.atom_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        AntlrRegexParser.hexbyte_return hexbyte11 =null;

        AntlrRegexParser.any_byte_return any_byte12 =null;

        AntlrRegexParser.byte_set_return byte_set13 =null;

        AntlrRegexParser.byte_shorthand_return byte_shorthand14 =null;

        AntlrRegexParser.set_shorthand_return set_shorthand15 =null;

        AntlrRegexParser.all_bitmask_return all_bitmask16 =null;

        AntlrRegexParser.any_bitmask_return any_bitmask17 =null;

        AntlrRegexParser.case_sensitive_string_return case_sensitive_string18 =null;

        AntlrRegexParser.case_insensitive_string_return case_insensitive_string19 =null;

        AntlrRegexParser.group_return group20 =null;



        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:151:6: ( ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | all_bitmask | any_bitmask | case_sensitive_string | case_insensitive_string | group ) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:152:2: ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | all_bitmask | any_bitmask | case_sensitive_string | case_insensitive_string | group )
            {
            root_0 = (Object)adaptor.nil();


            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:152:2: ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | all_bitmask | any_bitmask | case_sensitive_string | case_insensitive_string | group )
            int alt8=10;
            switch ( input.LA(1) ) {
            case BYTE:
                {
                alt8=1;
                }
                break;
            case FULL_STOP:
                {
                alt8=2;
                }
                break;
            case OPEN_SQUARE:
                {
                alt8=3;
                }
                break;
            case ESCAPE_SHORTHAND:
            case FORM_FEED_SHORTHAND:
            case NEWLINE_SHORTHAND:
            case RETURN_SHORTHAND:
            case TAB_SHORTHAND:
            case VERTICAL_TAB_SHORTHAND:
                {
                alt8=4;
                }
                break;
            case DIGIT_SHORTHAND:
            case NOT_DIGIT_SHORTHAND:
            case NOT_WHITE_SPACE_SHORTHAND:
            case NOT_WORD_SHORTHAND:
            case WHITE_SPACE_SHORTHAND:
            case WORD_SHORTHAND:
                {
                alt8=5;
                }
                break;
            case AMPERSAND:
                {
                alt8=6;
                }
                break;
            case TILDE:
                {
                alt8=7;
                }
                break;
            case CASE_SENSITIVE_STRING:
                {
                alt8=8;
                }
                break;
            case CASE_INSENSITIVE_STRING:
                {
                alt8=9;
                }
                break;
            case OPEN:
                {
                alt8=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:152:4: hexbyte
                    {
                    pushFollow(FOLLOW_hexbyte_in_atom424);
                    hexbyte11=hexbyte();

                    state._fsp--;

                    adaptor.addChild(root_0, hexbyte11.getTree());

                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:153:4: any_byte
                    {
                    pushFollow(FOLLOW_any_byte_in_atom429);
                    any_byte12=any_byte();

                    state._fsp--;

                    adaptor.addChild(root_0, any_byte12.getTree());

                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:154:4: byte_set
                    {
                    pushFollow(FOLLOW_byte_set_in_atom434);
                    byte_set13=byte_set();

                    state._fsp--;

                    adaptor.addChild(root_0, byte_set13.getTree());

                    }
                    break;
                case 4 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:155:4: byte_shorthand
                    {
                    pushFollow(FOLLOW_byte_shorthand_in_atom439);
                    byte_shorthand14=byte_shorthand();

                    state._fsp--;

                    adaptor.addChild(root_0, byte_shorthand14.getTree());

                    }
                    break;
                case 5 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:156:4: set_shorthand
                    {
                    pushFollow(FOLLOW_set_shorthand_in_atom444);
                    set_shorthand15=set_shorthand();

                    state._fsp--;

                    adaptor.addChild(root_0, set_shorthand15.getTree());

                    }
                    break;
                case 6 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:157:4: all_bitmask
                    {
                    pushFollow(FOLLOW_all_bitmask_in_atom449);
                    all_bitmask16=all_bitmask();

                    state._fsp--;

                    adaptor.addChild(root_0, all_bitmask16.getTree());

                    }
                    break;
                case 7 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:158:4: any_bitmask
                    {
                    pushFollow(FOLLOW_any_bitmask_in_atom454);
                    any_bitmask17=any_bitmask();

                    state._fsp--;

                    adaptor.addChild(root_0, any_bitmask17.getTree());

                    }
                    break;
                case 8 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:159:4: case_sensitive_string
                    {
                    pushFollow(FOLLOW_case_sensitive_string_in_atom459);
                    case_sensitive_string18=case_sensitive_string();

                    state._fsp--;

                    adaptor.addChild(root_0, case_sensitive_string18.getTree());

                    }
                    break;
                case 9 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:160:4: case_insensitive_string
                    {
                    pushFollow(FOLLOW_case_insensitive_string_in_atom464);
                    case_insensitive_string19=case_insensitive_string();

                    state._fsp--;

                    adaptor.addChild(root_0, case_insensitive_string19.getTree());

                    }
                    break;
                case 10 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:161:4: group
                    {
                    pushFollow(FOLLOW_group_in_atom469);
                    group20=group();

                    state._fsp--;

                    adaptor.addChild(root_0, group20.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "atom"


    public static class hexbyte_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "hexbyte"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:166:1: hexbyte : BYTE ;
    public final AntlrRegexParser.hexbyte_return hexbyte() throws RecognitionException {
        AntlrRegexParser.hexbyte_return retval = new AntlrRegexParser.hexbyte_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token BYTE21=null;

        Object BYTE21_tree=null;

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:166:9: ( BYTE )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:166:11: BYTE
            {
            root_0 = (Object)adaptor.nil();


            BYTE21=(Token)match(input,BYTE,FOLLOW_BYTE_in_hexbyte484); 
            BYTE21_tree = 
            (Object)adaptor.create(BYTE21)
            ;
            adaptor.addChild(root_0, BYTE21_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "hexbyte"


    public static class any_byte_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "any_byte"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:170:1: any_byte : FULL_STOP -> ANY ;
    public final AntlrRegexParser.any_byte_return any_byte() throws RecognitionException {
        AntlrRegexParser.any_byte_return retval = new AntlrRegexParser.any_byte_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token FULL_STOP22=null;

        Object FULL_STOP22_tree=null;
        RewriteRuleTokenStream stream_FULL_STOP=new RewriteRuleTokenStream(adaptor,"token FULL_STOP");

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:170:9: ( FULL_STOP -> ANY )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:170:11: FULL_STOP
            {
            FULL_STOP22=(Token)match(input,FULL_STOP,FOLLOW_FULL_STOP_in_any_byte494);  
            stream_FULL_STOP.add(FULL_STOP22);


            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 170:22: -> ANY
            {
                adaptor.addChild(root_0, 
                (Object)adaptor.create(ANY, "ANY")
                );

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "any_byte"


    public static class byte_set_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "byte_set"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:174:1: byte_set : OPEN_SQUARE ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) ) CLOSE_SQUARE ;
    public final AntlrRegexParser.byte_set_return byte_set() throws RecognitionException {
        AntlrRegexParser.byte_set_return retval = new AntlrRegexParser.byte_set_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token OPEN_SQUARE23=null;
        Token CARET24=null;
        Token CLOSE_SQUARE27=null;
        AntlrRegexParser.set_specification_return set_specification25 =null;

        AntlrRegexParser.set_specification_return set_specification26 =null;


        Object OPEN_SQUARE23_tree=null;
        Object CARET24_tree=null;
        Object CLOSE_SQUARE27_tree=null;
        RewriteRuleTokenStream stream_OPEN_SQUARE=new RewriteRuleTokenStream(adaptor,"token OPEN_SQUARE");
        RewriteRuleTokenStream stream_CLOSE_SQUARE=new RewriteRuleTokenStream(adaptor,"token CLOSE_SQUARE");
        RewriteRuleTokenStream stream_CARET=new RewriteRuleTokenStream(adaptor,"token CARET");
        RewriteRuleSubtreeStream stream_set_specification=new RewriteRuleSubtreeStream(adaptor,"rule set_specification");
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:175:2: ( OPEN_SQUARE ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) ) CLOSE_SQUARE )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:175:4: OPEN_SQUARE ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) ) CLOSE_SQUARE
            {
            OPEN_SQUARE23=(Token)match(input,OPEN_SQUARE,FOLLOW_OPEN_SQUARE_in_byte_set512);  
            stream_OPEN_SQUARE.add(OPEN_SQUARE23);


            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:176:2: ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==CARET) ) {
                alt9=1;
            }
            else if ( (LA9_0==AMPERSAND||LA9_0==BYTE||(LA9_0 >= CASE_INSENSITIVE_STRING && LA9_0 <= CASE_SENSITIVE_STRING)||LA9_0==DIGIT_SHORTHAND||(LA9_0 >= ESCAPE_SHORTHAND && LA9_0 <= FORM_FEED_SHORTHAND)||(LA9_0 >= NEWLINE_SHORTHAND && LA9_0 <= NOT_WORD_SHORTHAND)||LA9_0==OPEN_SQUARE||LA9_0==RETURN_SHORTHAND||(LA9_0 >= SET_ALPHA && LA9_0 <= WORD_SHORTHAND)) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }
            switch (alt9) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:177:3: ( CARET set_specification -> ^( INVERTED_SET set_specification ) )
                    {
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:177:3: ( CARET set_specification -> ^( INVERTED_SET set_specification ) )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:177:5: CARET set_specification
                    {
                    CARET24=(Token)match(input,CARET,FOLLOW_CARET_in_byte_set523);  
                    stream_CARET.add(CARET24);


                    pushFollow(FOLLOW_set_specification_in_byte_set525);
                    set_specification25=set_specification();

                    state._fsp--;

                    stream_set_specification.add(set_specification25.getTree());

                    // AST REWRITE
                    // elements: set_specification
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 177:30: -> ^( INVERTED_SET set_specification )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:177:33: ^( INVERTED_SET set_specification )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(INVERTED_SET, "INVERTED_SET")
                        , root_1);

                        adaptor.addChild(root_1, stream_set_specification.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }


                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:179:3: ( set_specification -> ^( SET set_specification ) )
                    {
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:179:3: ( set_specification -> ^( SET set_specification ) )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:179:5: set_specification
                    {
                    pushFollow(FOLLOW_set_specification_in_byte_set550);
                    set_specification26=set_specification();

                    state._fsp--;

                    stream_set_specification.add(set_specification26.getTree());

                    // AST REWRITE
                    // elements: set_specification
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 179:25: -> ^( SET set_specification )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:179:28: ^( SET set_specification )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        adaptor.addChild(root_1, stream_set_specification.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }


                    }
                    break;

            }


            CLOSE_SQUARE27=(Token)match(input,CLOSE_SQUARE,FOLLOW_CLOSE_SQUARE_in_byte_set572);  
            stream_CLOSE_SQUARE.add(CLOSE_SQUARE27);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "byte_set"


    public static class set_specification_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "set_specification"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:185:1: set_specification : ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | all_bitmask | any_bitmask | byte_set )+ ;
    public final AntlrRegexParser.set_specification_return set_specification() throws RecognitionException {
        AntlrRegexParser.set_specification_return retval = new AntlrRegexParser.set_specification_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        AntlrRegexParser.hexbyte_return hexbyte28 =null;

        AntlrRegexParser.byte_shorthand_return byte_shorthand29 =null;

        AntlrRegexParser.set_shorthand_return set_shorthand30 =null;

        AntlrRegexParser.mnemonic_return mnemonic31 =null;

        AntlrRegexParser.case_sensitive_string_return case_sensitive_string32 =null;

        AntlrRegexParser.case_insensitive_string_return case_insensitive_string33 =null;

        AntlrRegexParser.byte_range_return byte_range34 =null;

        AntlrRegexParser.all_bitmask_return all_bitmask35 =null;

        AntlrRegexParser.any_bitmask_return any_bitmask36 =null;

        AntlrRegexParser.byte_set_return byte_set37 =null;



        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:186:2: ( ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | all_bitmask | any_bitmask | byte_set )+ )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:187:2: ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | all_bitmask | any_bitmask | byte_set )+
            {
            root_0 = (Object)adaptor.nil();


            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:187:2: ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | all_bitmask | any_bitmask | byte_set )+
            int cnt10=0;
            loop10:
            do {
                int alt10=11;
                switch ( input.LA(1) ) {
                case BYTE:
                    {
                    int LA10_2 = input.LA(2);

                    if ( (LA10_2==AMPERSAND||LA10_2==BYTE||(LA10_2 >= CASE_INSENSITIVE_STRING && LA10_2 <= CASE_SENSITIVE_STRING)||LA10_2==CLOSE_SQUARE||LA10_2==DIGIT_SHORTHAND||(LA10_2 >= ESCAPE_SHORTHAND && LA10_2 <= FORM_FEED_SHORTHAND)||(LA10_2 >= NEWLINE_SHORTHAND && LA10_2 <= NOT_WORD_SHORTHAND)||LA10_2==OPEN_SQUARE||LA10_2==RETURN_SHORTHAND||(LA10_2 >= SET_ALPHA && LA10_2 <= WORD_SHORTHAND)) ) {
                        alt10=1;
                    }
                    else if ( (LA10_2==RANGE_SEPARATOR) ) {
                        alt10=7;
                    }


                    }
                    break;
                case ESCAPE_SHORTHAND:
                case FORM_FEED_SHORTHAND:
                case NEWLINE_SHORTHAND:
                case RETURN_SHORTHAND:
                case TAB_SHORTHAND:
                case VERTICAL_TAB_SHORTHAND:
                    {
                    alt10=2;
                    }
                    break;
                case DIGIT_SHORTHAND:
                case NOT_DIGIT_SHORTHAND:
                case NOT_WHITE_SPACE_SHORTHAND:
                case NOT_WORD_SHORTHAND:
                case WHITE_SPACE_SHORTHAND:
                case WORD_SHORTHAND:
                    {
                    alt10=3;
                    }
                    break;
                case SET_ALPHA:
                case SET_ALPHANUM:
                case SET_ASCII:
                case SET_BLANK:
                case SET_CONTROL:
                case SET_DIGIT:
                case SET_GRAPH:
                case SET_HEXDIGIT:
                case SET_LOWER:
                case SET_NEWLINE:
                case SET_PRINT:
                case SET_PUNCT:
                case SET_RETURN:
                case SET_SPACE:
                case SET_TAB:
                case SET_UPPER:
                case SET_WHITESPACE:
                case SET_WORD:
                    {
                    alt10=4;
                    }
                    break;
                case CASE_SENSITIVE_STRING:
                    {
                    int LA10_6 = input.LA(2);

                    if ( (LA10_6==AMPERSAND||LA10_6==BYTE||(LA10_6 >= CASE_INSENSITIVE_STRING && LA10_6 <= CASE_SENSITIVE_STRING)||LA10_6==CLOSE_SQUARE||LA10_6==DIGIT_SHORTHAND||(LA10_6 >= ESCAPE_SHORTHAND && LA10_6 <= FORM_FEED_SHORTHAND)||(LA10_6 >= NEWLINE_SHORTHAND && LA10_6 <= NOT_WORD_SHORTHAND)||LA10_6==OPEN_SQUARE||LA10_6==RETURN_SHORTHAND||(LA10_6 >= SET_ALPHA && LA10_6 <= WORD_SHORTHAND)) ) {
                        alt10=5;
                    }
                    else if ( (LA10_6==RANGE_SEPARATOR) ) {
                        alt10=7;
                    }


                    }
                    break;
                case CASE_INSENSITIVE_STRING:
                    {
                    alt10=6;
                    }
                    break;
                case AMPERSAND:
                    {
                    alt10=8;
                    }
                    break;
                case TILDE:
                    {
                    alt10=9;
                    }
                    break;
                case OPEN_SQUARE:
                    {
                    alt10=10;
                    }
                    break;

                }

                switch (alt10) {
            	case 1 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:187:4: hexbyte
            	    {
            	    pushFollow(FOLLOW_hexbyte_in_set_specification588);
            	    hexbyte28=hexbyte();

            	    state._fsp--;

            	    adaptor.addChild(root_0, hexbyte28.getTree());

            	    }
            	    break;
            	case 2 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:188:4: byte_shorthand
            	    {
            	    pushFollow(FOLLOW_byte_shorthand_in_set_specification593);
            	    byte_shorthand29=byte_shorthand();

            	    state._fsp--;

            	    adaptor.addChild(root_0, byte_shorthand29.getTree());

            	    }
            	    break;
            	case 3 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:189:4: set_shorthand
            	    {
            	    pushFollow(FOLLOW_set_shorthand_in_set_specification598);
            	    set_shorthand30=set_shorthand();

            	    state._fsp--;

            	    adaptor.addChild(root_0, set_shorthand30.getTree());

            	    }
            	    break;
            	case 4 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:190:4: mnemonic
            	    {
            	    pushFollow(FOLLOW_mnemonic_in_set_specification603);
            	    mnemonic31=mnemonic();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mnemonic31.getTree());

            	    }
            	    break;
            	case 5 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:191:4: case_sensitive_string
            	    {
            	    pushFollow(FOLLOW_case_sensitive_string_in_set_specification609);
            	    case_sensitive_string32=case_sensitive_string();

            	    state._fsp--;

            	    adaptor.addChild(root_0, case_sensitive_string32.getTree());

            	    }
            	    break;
            	case 6 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:192:4: case_insensitive_string
            	    {
            	    pushFollow(FOLLOW_case_insensitive_string_in_set_specification614);
            	    case_insensitive_string33=case_insensitive_string();

            	    state._fsp--;

            	    adaptor.addChild(root_0, case_insensitive_string33.getTree());

            	    }
            	    break;
            	case 7 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:193:4: byte_range
            	    {
            	    pushFollow(FOLLOW_byte_range_in_set_specification619);
            	    byte_range34=byte_range();

            	    state._fsp--;

            	    adaptor.addChild(root_0, byte_range34.getTree());

            	    }
            	    break;
            	case 8 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:194:4: all_bitmask
            	    {
            	    pushFollow(FOLLOW_all_bitmask_in_set_specification624);
            	    all_bitmask35=all_bitmask();

            	    state._fsp--;

            	    adaptor.addChild(root_0, all_bitmask35.getTree());

            	    }
            	    break;
            	case 9 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:195:4: any_bitmask
            	    {
            	    pushFollow(FOLLOW_any_bitmask_in_set_specification629);
            	    any_bitmask36=any_bitmask();

            	    state._fsp--;

            	    adaptor.addChild(root_0, any_bitmask36.getTree());

            	    }
            	    break;
            	case 10 :
            	    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:196:4: byte_set
            	    {
            	    pushFollow(FOLLOW_byte_set_in_set_specification634);
            	    byte_set37=byte_set();

            	    state._fsp--;

            	    adaptor.addChild(root_0, byte_set37.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "set_specification"


    public static class byte_range_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "byte_range"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:201:1: byte_range : r1= range_values RANGE_SEPARATOR r2= range_values -> ^( RANGE $r1 $r2) ;
    public final AntlrRegexParser.byte_range_return byte_range() throws RecognitionException {
        AntlrRegexParser.byte_range_return retval = new AntlrRegexParser.byte_range_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token RANGE_SEPARATOR38=null;
        AntlrRegexParser.range_values_return r1 =null;

        AntlrRegexParser.range_values_return r2 =null;


        Object RANGE_SEPARATOR38_tree=null;
        RewriteRuleTokenStream stream_RANGE_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token RANGE_SEPARATOR");
        RewriteRuleSubtreeStream stream_range_values=new RewriteRuleSubtreeStream(adaptor,"rule range_values");
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:202:2: (r1= range_values RANGE_SEPARATOR r2= range_values -> ^( RANGE $r1 $r2) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:202:4: r1= range_values RANGE_SEPARATOR r2= range_values
            {
            pushFollow(FOLLOW_range_values_in_byte_range653);
            r1=range_values();

            state._fsp--;

            stream_range_values.add(r1.getTree());

            RANGE_SEPARATOR38=(Token)match(input,RANGE_SEPARATOR,FOLLOW_RANGE_SEPARATOR_in_byte_range657);  
            stream_RANGE_SEPARATOR.add(RANGE_SEPARATOR38);


            pushFollow(FOLLOW_range_values_in_byte_range664);
            r2=range_values();

            state._fsp--;

            stream_range_values.add(r2.getTree());

            // AST REWRITE
            // elements: r1, r2
            // token labels: 
            // rule labels: retval, r1, r2
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_r1=new RewriteRuleSubtreeStream(adaptor,"rule r1",r1!=null?r1.tree:null);
            RewriteRuleSubtreeStream stream_r2=new RewriteRuleSubtreeStream(adaptor,"rule r2",r2!=null?r2.tree:null);

            root_0 = (Object)adaptor.nil();
            // 204:21: -> ^( RANGE $r1 $r2)
            {
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:204:24: ^( RANGE $r1 $r2)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(RANGE, "RANGE")
                , root_1);

                adaptor.addChild(root_1, stream_r1.nextTree());

                adaptor.addChild(root_1, stream_r2.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "byte_range"


    public static class range_values_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "range_values"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:208:1: range_values : ( BYTE | CASE_SENSITIVE_STRING );
    public final AntlrRegexParser.range_values_return range_values() throws RecognitionException {
        AntlrRegexParser.range_values_return retval = new AntlrRegexParser.range_values_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token set39=null;

        Object set39_tree=null;

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:209:2: ( BYTE | CASE_SENSITIVE_STRING )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:
            {
            root_0 = (Object)adaptor.nil();


            set39=(Token)input.LT(1);

            if ( input.LA(1)==BYTE||input.LA(1)==CASE_SENSITIVE_STRING ) {
                input.consume();
                adaptor.addChild(root_0, 
                (Object)adaptor.create(set39)
                );
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "range_values"


    public static class all_bitmask_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "all_bitmask"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:214:1: all_bitmask : AMPERSAND BYTE -> ^( ALL_BITMASK BYTE ) ;
    public final AntlrRegexParser.all_bitmask_return all_bitmask() throws RecognitionException {
        AntlrRegexParser.all_bitmask_return retval = new AntlrRegexParser.all_bitmask_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token AMPERSAND40=null;
        Token BYTE41=null;

        Object AMPERSAND40_tree=null;
        Object BYTE41_tree=null;
        RewriteRuleTokenStream stream_AMPERSAND=new RewriteRuleTokenStream(adaptor,"token AMPERSAND");
        RewriteRuleTokenStream stream_BYTE=new RewriteRuleTokenStream(adaptor,"token BYTE");

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:215:2: ( AMPERSAND BYTE -> ^( ALL_BITMASK BYTE ) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:215:4: AMPERSAND BYTE
            {
            AMPERSAND40=(Token)match(input,AMPERSAND,FOLLOW_AMPERSAND_in_all_bitmask707);  
            stream_AMPERSAND.add(AMPERSAND40);


            BYTE41=(Token)match(input,BYTE,FOLLOW_BYTE_in_all_bitmask709);  
            stream_BYTE.add(BYTE41);


            // AST REWRITE
            // elements: BYTE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 215:20: -> ^( ALL_BITMASK BYTE )
            {
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:215:23: ^( ALL_BITMASK BYTE )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(ALL_BITMASK, "ALL_BITMASK")
                , root_1);

                adaptor.addChild(root_1, 
                stream_BYTE.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "all_bitmask"


    public static class any_bitmask_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "any_bitmask"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:218:1: any_bitmask : TILDE BYTE -> ^( ANY_BITMASK BYTE ) ;
    public final AntlrRegexParser.any_bitmask_return any_bitmask() throws RecognitionException {
        AntlrRegexParser.any_bitmask_return retval = new AntlrRegexParser.any_bitmask_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token TILDE42=null;
        Token BYTE43=null;

        Object TILDE42_tree=null;
        Object BYTE43_tree=null;
        RewriteRuleTokenStream stream_BYTE=new RewriteRuleTokenStream(adaptor,"token BYTE");
        RewriteRuleTokenStream stream_TILDE=new RewriteRuleTokenStream(adaptor,"token TILDE");

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:219:2: ( TILDE BYTE -> ^( ANY_BITMASK BYTE ) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:219:4: TILDE BYTE
            {
            TILDE42=(Token)match(input,TILDE,FOLLOW_TILDE_in_any_bitmask729);  
            stream_TILDE.add(TILDE42);


            BYTE43=(Token)match(input,BYTE,FOLLOW_BYTE_in_any_bitmask731);  
            stream_BYTE.add(BYTE43);


            // AST REWRITE
            // elements: BYTE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 219:16: -> ^( ANY_BITMASK BYTE )
            {
                // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:219:19: ^( ANY_BITMASK BYTE )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot(
                (Object)adaptor.create(ANY_BITMASK, "ANY_BITMASK")
                , root_1);

                adaptor.addChild(root_1, 
                stream_BYTE.nextNode()
                );

                adaptor.addChild(root_0, root_1);
                }

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "any_bitmask"


    public static class mnemonic_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "mnemonic"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:222:1: mnemonic : (m= SET_ASCII -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) ) |m= SET_PRINT -> ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) ) |m= SET_GRAPH -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) ) |m= SET_WORD -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] ) |m= SET_ALPHANUM -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) |m= SET_ALPHA -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) |m= SET_UPPER -> ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) |m= SET_LOWER -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ) |m= SET_PUNCT -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) ) |m= SET_HEXDIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) ) |m= SET_DIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) |m= SET_WHITESPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) |m= SET_BLANK -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] ) |m= SET_SPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) |m= SET_TAB -> BYTE[$m,\"09\"] |m= SET_NEWLINE -> BYTE[$m,\"0a\"] |m= SET_RETURN -> BYTE[$m,\"0d\"] |m= SET_CONTROL -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] ) );
    public final AntlrRegexParser.mnemonic_return mnemonic() throws RecognitionException {
        AntlrRegexParser.mnemonic_return retval = new AntlrRegexParser.mnemonic_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token m=null;

        Object m_tree=null;
        RewriteRuleTokenStream stream_SET_WORD=new RewriteRuleTokenStream(adaptor,"token SET_WORD");
        RewriteRuleTokenStream stream_SET_ALPHANUM=new RewriteRuleTokenStream(adaptor,"token SET_ALPHANUM");
        RewriteRuleTokenStream stream_SET_WHITESPACE=new RewriteRuleTokenStream(adaptor,"token SET_WHITESPACE");
        RewriteRuleTokenStream stream_SET_RETURN=new RewriteRuleTokenStream(adaptor,"token SET_RETURN");
        RewriteRuleTokenStream stream_SET_NEWLINE=new RewriteRuleTokenStream(adaptor,"token SET_NEWLINE");
        RewriteRuleTokenStream stream_SET_LOWER=new RewriteRuleTokenStream(adaptor,"token SET_LOWER");
        RewriteRuleTokenStream stream_SET_GRAPH=new RewriteRuleTokenStream(adaptor,"token SET_GRAPH");
        RewriteRuleTokenStream stream_SET_ASCII=new RewriteRuleTokenStream(adaptor,"token SET_ASCII");
        RewriteRuleTokenStream stream_SET_ALPHA=new RewriteRuleTokenStream(adaptor,"token SET_ALPHA");
        RewriteRuleTokenStream stream_SET_PRINT=new RewriteRuleTokenStream(adaptor,"token SET_PRINT");
        RewriteRuleTokenStream stream_SET_TAB=new RewriteRuleTokenStream(adaptor,"token SET_TAB");
        RewriteRuleTokenStream stream_SET_SPACE=new RewriteRuleTokenStream(adaptor,"token SET_SPACE");
        RewriteRuleTokenStream stream_SET_UPPER=new RewriteRuleTokenStream(adaptor,"token SET_UPPER");
        RewriteRuleTokenStream stream_SET_BLANK=new RewriteRuleTokenStream(adaptor,"token SET_BLANK");
        RewriteRuleTokenStream stream_SET_HEXDIGIT=new RewriteRuleTokenStream(adaptor,"token SET_HEXDIGIT");
        RewriteRuleTokenStream stream_SET_PUNCT=new RewriteRuleTokenStream(adaptor,"token SET_PUNCT");
        RewriteRuleTokenStream stream_SET_CONTROL=new RewriteRuleTokenStream(adaptor,"token SET_CONTROL");
        RewriteRuleTokenStream stream_SET_DIGIT=new RewriteRuleTokenStream(adaptor,"token SET_DIGIT");

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:223:2: (m= SET_ASCII -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) ) |m= SET_PRINT -> ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) ) |m= SET_GRAPH -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) ) |m= SET_WORD -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] ) |m= SET_ALPHANUM -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) |m= SET_ALPHA -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) |m= SET_UPPER -> ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) |m= SET_LOWER -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ) |m= SET_PUNCT -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) ) |m= SET_HEXDIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) ) |m= SET_DIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) |m= SET_WHITESPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) |m= SET_BLANK -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] ) |m= SET_SPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) |m= SET_TAB -> BYTE[$m,\"09\"] |m= SET_NEWLINE -> BYTE[$m,\"0a\"] |m= SET_RETURN -> BYTE[$m,\"0d\"] |m= SET_CONTROL -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] ) )
            int alt11=18;
            switch ( input.LA(1) ) {
            case SET_ASCII:
                {
                alt11=1;
                }
                break;
            case SET_PRINT:
                {
                alt11=2;
                }
                break;
            case SET_GRAPH:
                {
                alt11=3;
                }
                break;
            case SET_WORD:
                {
                alt11=4;
                }
                break;
            case SET_ALPHANUM:
                {
                alt11=5;
                }
                break;
            case SET_ALPHA:
                {
                alt11=6;
                }
                break;
            case SET_UPPER:
                {
                alt11=7;
                }
                break;
            case SET_LOWER:
                {
                alt11=8;
                }
                break;
            case SET_PUNCT:
                {
                alt11=9;
                }
                break;
            case SET_HEXDIGIT:
                {
                alt11=10;
                }
                break;
            case SET_DIGIT:
                {
                alt11=11;
                }
                break;
            case SET_WHITESPACE:
                {
                alt11=12;
                }
                break;
            case SET_BLANK:
                {
                alt11=13;
                }
                break;
            case SET_SPACE:
                {
                alt11=14;
                }
                break;
            case SET_TAB:
                {
                alt11=15;
                }
                break;
            case SET_NEWLINE:
                {
                alt11=16;
                }
                break;
            case SET_RETURN:
                {
                alt11=17;
                }
                break;
            case SET_CONTROL:
                {
                alt11=18;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }

            switch (alt11) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:223:4: m= SET_ASCII
                    {
                    m=(Token)match(input,SET_ASCII,FOLLOW_SET_ASCII_in_mnemonic754);  
                    stream_SET_ASCII.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 223:17: -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:223:20: ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:223:26: ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "00")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "7f")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:224:4: m= SET_PRINT
                    {
                    m=(Token)match(input,SET_PRINT,FOLLOW_SET_PRINT_in_mnemonic779);  
                    stream_SET_PRINT.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 224:17: -> ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:224:20: ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:224:26: ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "' '")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'~'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:225:4: m= SET_GRAPH
                    {
                    m=(Token)match(input,SET_GRAPH,FOLLOW_SET_GRAPH_in_mnemonic804);  
                    stream_SET_GRAPH.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 225:17: -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:225:20: ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:225:26: ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'!'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'~'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 4 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:226:4: m= SET_WORD
                    {
                    m=(Token)match(input,SET_WORD,FOLLOW_SET_WORD_in_mnemonic829);  
                    stream_SET_WORD.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 226:16: -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:226:19: ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:226:25: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'0'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'9'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:226:64: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'a'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:226:103: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'A'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'Z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "'_'")
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 5 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:227:4: m= SET_ALPHANUM
                    {
                    m=(Token)match(input,SET_ALPHANUM,FOLLOW_SET_ALPHANUM_in_mnemonic876);  
                    stream_SET_ALPHANUM.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 227:20: -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:227:23: ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:227:29: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'a'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:227:68: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'A'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'Z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:227:107: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'0'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'9'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 6 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:228:4: m= SET_ALPHA
                    {
                    m=(Token)match(input,SET_ALPHA,FOLLOW_SET_ALPHA_in_mnemonic920);  
                    stream_SET_ALPHA.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 228:17: -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:228:20: ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:228:26: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'a'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:228:65: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'A'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'Z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 7 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:229:4: m= SET_UPPER
                    {
                    m=(Token)match(input,SET_UPPER,FOLLOW_SET_UPPER_in_mnemonic954);  
                    stream_SET_UPPER.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 229:17: -> ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:229:20: ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:229:26: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'A'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'Z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 8 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:230:4: m= SET_LOWER
                    {
                    m=(Token)match(input,SET_LOWER,FOLLOW_SET_LOWER_in_mnemonic978);  
                    stream_SET_LOWER.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 230:17: -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:230:20: ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:230:26: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'a'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 9 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:231:4: m= SET_PUNCT
                    {
                    m=(Token)match(input,SET_PUNCT,FOLLOW_SET_PUNCT_in_mnemonic1002);  
                    stream_SET_PUNCT.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 231:17: -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:231:20: ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:231:26: ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'!'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'/'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:231:65: ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "':'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'@'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:231:104: ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'['")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'`'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:231:143: ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'{'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'~'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 10 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:232:4: m= SET_HEXDIGIT
                    {
                    m=(Token)match(input,SET_HEXDIGIT,FOLLOW_SET_HEXDIGIT_in_mnemonic1056);  
                    stream_SET_HEXDIGIT.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 232:20: -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:232:23: ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:232:29: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'0'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'9'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:232:68: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'a'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'f'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:232:107: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'A'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'F'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 11 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:233:4: m= SET_DIGIT
                    {
                    m=(Token)match(input,SET_DIGIT,FOLLOW_SET_DIGIT_in_mnemonic1100);  
                    stream_SET_DIGIT.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 233:17: -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:233:20: ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:233:26: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'0'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "'9'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 12 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:234:4: m= SET_WHITESPACE
                    {
                    m=(Token)match(input,SET_WHITESPACE,FOLLOW_SET_WHITESPACE_in_mnemonic1124);  
                    stream_SET_WHITESPACE.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 234:21: -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:234:24: ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "09")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "0a")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "0d")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "' '")
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 13 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:235:4: m= SET_BLANK
                    {
                    m=(Token)match(input,SET_BLANK,FOLLOW_SET_BLANK_in_mnemonic1149);  
                    stream_SET_BLANK.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 235:17: -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:235:20: ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "09")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "' '")
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 14 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:236:4: m= SET_SPACE
                    {
                    m=(Token)match(input,SET_SPACE,FOLLOW_SET_SPACE_in_mnemonic1169);  
                    stream_SET_SPACE.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 236:17: -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:236:20: ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "09")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "0a")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "0b")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "0c")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "0d")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "' '")
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 15 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:237:4: m= SET_TAB
                    {
                    m=(Token)match(input,SET_TAB,FOLLOW_SET_TAB_in_mnemonic1201);  
                    stream_SET_TAB.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 237:15: -> BYTE[$m,\"09\"]
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(BYTE, m, "09")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 16 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:238:4: m= SET_NEWLINE
                    {
                    m=(Token)match(input,SET_NEWLINE,FOLLOW_SET_NEWLINE_in_mnemonic1214);  
                    stream_SET_NEWLINE.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 238:19: -> BYTE[$m,\"0a\"]
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(BYTE, m, "0a")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 17 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:239:4: m= SET_RETURN
                    {
                    m=(Token)match(input,SET_RETURN,FOLLOW_SET_RETURN_in_mnemonic1227);  
                    stream_SET_RETURN.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 239:18: -> BYTE[$m,\"0d\"]
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(BYTE, m, "0d")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 18 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:240:4: m= SET_CONTROL
                    {
                    m=(Token)match(input,SET_CONTROL,FOLLOW_SET_CONTROL_in_mnemonic1240);  
                    stream_SET_CONTROL.add(m);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 240:19: -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:240:22: ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:240:28: ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "00")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, m, "1f")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, m, "7f")
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "mnemonic"


    public static class byte_shorthand_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "byte_shorthand"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:244:1: byte_shorthand : (sh= TAB_SHORTHAND -> BYTE[$sh,\"09\"] |sh= NEWLINE_SHORTHAND -> BYTE[$sh,\"0a\"] |sh= VERTICAL_TAB_SHORTHAND -> BYTE[$sh,\"0b\"] |sh= FORM_FEED_SHORTHAND -> BYTE[$sh,\"0c\"] |sh= RETURN_SHORTHAND -> BYTE[$sh,\"0d\"] |sh= ESCAPE_SHORTHAND -> BYTE[$sh,\"1b\"] );
    public final AntlrRegexParser.byte_shorthand_return byte_shorthand() throws RecognitionException {
        AntlrRegexParser.byte_shorthand_return retval = new AntlrRegexParser.byte_shorthand_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token sh=null;

        Object sh_tree=null;
        RewriteRuleTokenStream stream_FORM_FEED_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token FORM_FEED_SHORTHAND");
        RewriteRuleTokenStream stream_NEWLINE_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token NEWLINE_SHORTHAND");
        RewriteRuleTokenStream stream_RETURN_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token RETURN_SHORTHAND");
        RewriteRuleTokenStream stream_ESCAPE_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token ESCAPE_SHORTHAND");
        RewriteRuleTokenStream stream_VERTICAL_TAB_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token VERTICAL_TAB_SHORTHAND");
        RewriteRuleTokenStream stream_TAB_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token TAB_SHORTHAND");

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:245:2: (sh= TAB_SHORTHAND -> BYTE[$sh,\"09\"] |sh= NEWLINE_SHORTHAND -> BYTE[$sh,\"0a\"] |sh= VERTICAL_TAB_SHORTHAND -> BYTE[$sh,\"0b\"] |sh= FORM_FEED_SHORTHAND -> BYTE[$sh,\"0c\"] |sh= RETURN_SHORTHAND -> BYTE[$sh,\"0d\"] |sh= ESCAPE_SHORTHAND -> BYTE[$sh,\"1b\"] )
            int alt12=6;
            switch ( input.LA(1) ) {
            case TAB_SHORTHAND:
                {
                alt12=1;
                }
                break;
            case NEWLINE_SHORTHAND:
                {
                alt12=2;
                }
                break;
            case VERTICAL_TAB_SHORTHAND:
                {
                alt12=3;
                }
                break;
            case FORM_FEED_SHORTHAND:
                {
                alt12=4;
                }
                break;
            case RETURN_SHORTHAND:
                {
                alt12=5;
                }
                break;
            case ESCAPE_SHORTHAND:
                {
                alt12=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:245:4: sh= TAB_SHORTHAND
                    {
                    sh=(Token)match(input,TAB_SHORTHAND,FOLLOW_TAB_SHORTHAND_in_byte_shorthand1275);  
                    stream_TAB_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 245:22: -> BYTE[$sh,\"09\"]
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(BYTE, sh, "09")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:246:4: sh= NEWLINE_SHORTHAND
                    {
                    sh=(Token)match(input,NEWLINE_SHORTHAND,FOLLOW_NEWLINE_SHORTHAND_in_byte_shorthand1288);  
                    stream_NEWLINE_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 246:26: -> BYTE[$sh,\"0a\"]
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(BYTE, sh, "0a")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:247:4: sh= VERTICAL_TAB_SHORTHAND
                    {
                    sh=(Token)match(input,VERTICAL_TAB_SHORTHAND,FOLLOW_VERTICAL_TAB_SHORTHAND_in_byte_shorthand1301);  
                    stream_VERTICAL_TAB_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 247:30: -> BYTE[$sh,\"0b\"]
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(BYTE, sh, "0b")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 4 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:248:4: sh= FORM_FEED_SHORTHAND
                    {
                    sh=(Token)match(input,FORM_FEED_SHORTHAND,FOLLOW_FORM_FEED_SHORTHAND_in_byte_shorthand1313);  
                    stream_FORM_FEED_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 248:28: -> BYTE[$sh,\"0c\"]
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(BYTE, sh, "0c")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 5 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:249:4: sh= RETURN_SHORTHAND
                    {
                    sh=(Token)match(input,RETURN_SHORTHAND,FOLLOW_RETURN_SHORTHAND_in_byte_shorthand1326);  
                    stream_RETURN_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 249:25: -> BYTE[$sh,\"0d\"]
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(BYTE, sh, "0d")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 6 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:250:4: sh= ESCAPE_SHORTHAND
                    {
                    sh=(Token)match(input,ESCAPE_SHORTHAND,FOLLOW_ESCAPE_SHORTHAND_in_byte_shorthand1339);  
                    stream_ESCAPE_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 250:25: -> BYTE[$sh,\"1b\"]
                    {
                        adaptor.addChild(root_0, 
                        (Object)adaptor.create(BYTE, sh, "1b")
                        );

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "byte_shorthand"


    public static class set_shorthand_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "set_shorthand"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:254:1: set_shorthand : (sh= DIGIT_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) |sh= NOT_DIGIT_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) |sh= WORD_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) |sh= NOT_WORD_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) |sh= WHITE_SPACE_SHORTHAND -> ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) |sh= NOT_WHITE_SPACE_SHORTHAND -> ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) );
    public final AntlrRegexParser.set_shorthand_return set_shorthand() throws RecognitionException {
        AntlrRegexParser.set_shorthand_return retval = new AntlrRegexParser.set_shorthand_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token sh=null;

        Object sh_tree=null;
        RewriteRuleTokenStream stream_NOT_WHITE_SPACE_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token NOT_WHITE_SPACE_SHORTHAND");
        RewriteRuleTokenStream stream_WORD_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token WORD_SHORTHAND");
        RewriteRuleTokenStream stream_DIGIT_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token DIGIT_SHORTHAND");
        RewriteRuleTokenStream stream_NOT_WORD_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token NOT_WORD_SHORTHAND");
        RewriteRuleTokenStream stream_WHITE_SPACE_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token WHITE_SPACE_SHORTHAND");
        RewriteRuleTokenStream stream_NOT_DIGIT_SHORTHAND=new RewriteRuleTokenStream(adaptor,"token NOT_DIGIT_SHORTHAND");

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:255:2: (sh= DIGIT_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) |sh= NOT_DIGIT_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) |sh= WORD_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) |sh= NOT_WORD_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) |sh= WHITE_SPACE_SHORTHAND -> ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) |sh= NOT_WHITE_SPACE_SHORTHAND -> ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) )
            int alt13=6;
            switch ( input.LA(1) ) {
            case DIGIT_SHORTHAND:
                {
                alt13=1;
                }
                break;
            case NOT_DIGIT_SHORTHAND:
                {
                alt13=2;
                }
                break;
            case WORD_SHORTHAND:
                {
                alt13=3;
                }
                break;
            case NOT_WORD_SHORTHAND:
                {
                alt13=4;
                }
                break;
            case WHITE_SPACE_SHORTHAND:
                {
                alt13=5;
                }
                break;
            case NOT_WHITE_SPACE_SHORTHAND:
                {
                alt13=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:255:4: sh= DIGIT_SHORTHAND
                    {
                    sh=(Token)match(input,DIGIT_SHORTHAND,FOLLOW_DIGIT_SHORTHAND_in_set_shorthand1361);  
                    stream_DIGIT_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 255:24: -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:255:27: ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:255:33: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'0'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'9'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:256:4: sh= NOT_DIGIT_SHORTHAND
                    {
                    sh=(Token)match(input,NOT_DIGIT_SHORTHAND,FOLLOW_NOT_DIGIT_SHORTHAND_in_set_shorthand1385);  
                    stream_NOT_DIGIT_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 256:28: -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:256:31: ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(INVERTED_SET, "INVERTED_SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:256:46: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'0'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'9'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:257:4: sh= WORD_SHORTHAND
                    {
                    sh=(Token)match(input,WORD_SHORTHAND,FOLLOW_WORD_SHORTHAND_in_set_shorthand1409);  
                    stream_WORD_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 257:23: -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:257:26: ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:257:32: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'0'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'9'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:257:73: ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'a'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:257:114: ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'A'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'Z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "'_'")
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 4 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:258:4: sh= NOT_WORD_SHORTHAND
                    {
                    sh=(Token)match(input,NOT_WORD_SHORTHAND,FOLLOW_NOT_WORD_SHORTHAND_in_set_shorthand1456);  
                    stream_NOT_WORD_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 258:27: -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:258:30: ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(INVERTED_SET, "INVERTED_SET")
                        , root_1);

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:258:45: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'0'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'9'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:258:86: ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'a'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:258:127: ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(RANGE, "RANGE")
                        , root_2);

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'A'")
                        );

                        adaptor.addChild(root_2, 
                        (Object)adaptor.create(BYTE, sh, "'Z'")
                        );

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "'_'")
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 5 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:259:4: sh= WHITE_SPACE_SHORTHAND
                    {
                    sh=(Token)match(input,WHITE_SPACE_SHORTHAND,FOLLOW_WHITE_SPACE_SHORTHAND_in_set_shorthand1503);  
                    stream_WHITE_SPACE_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 259:29: -> ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:259:32: ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(SET, "SET")
                        , root_1);

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "09")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "0a")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "0d")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "' '")
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;
                case 6 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:260:4: sh= NOT_WHITE_SPACE_SHORTHAND
                    {
                    sh=(Token)match(input,NOT_WHITE_SPACE_SHORTHAND,FOLLOW_NOT_WHITE_SPACE_SHORTHAND_in_set_shorthand1528);  
                    stream_NOT_WHITE_SPACE_SHORTHAND.add(sh);


                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 260:33: -> ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:260:36: ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(INVERTED_SET, "INVERTED_SET")
                        , root_1);

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "09")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "0a")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "0d")
                        );

                        adaptor.addChild(root_1, 
                        (Object)adaptor.create(BYTE, sh, "' '")
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }
                    break;

            }
            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "set_shorthand"


    public static class case_insensitive_string_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "case_insensitive_string"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:263:1: case_insensitive_string : CASE_INSENSITIVE_STRING ;
    public final AntlrRegexParser.case_insensitive_string_return case_insensitive_string() throws RecognitionException {
        AntlrRegexParser.case_insensitive_string_return retval = new AntlrRegexParser.case_insensitive_string_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token CASE_INSENSITIVE_STRING44=null;

        Object CASE_INSENSITIVE_STRING44_tree=null;

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:264:2: ( CASE_INSENSITIVE_STRING )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:264:4: CASE_INSENSITIVE_STRING
            {
            root_0 = (Object)adaptor.nil();


            CASE_INSENSITIVE_STRING44=(Token)match(input,CASE_INSENSITIVE_STRING,FOLLOW_CASE_INSENSITIVE_STRING_in_case_insensitive_string1558); 
            CASE_INSENSITIVE_STRING44_tree = 
            (Object)adaptor.create(CASE_INSENSITIVE_STRING44)
            ;
            adaptor.addChild(root_0, CASE_INSENSITIVE_STRING44_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "case_insensitive_string"


    public static class case_sensitive_string_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "case_sensitive_string"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:268:1: case_sensitive_string : CASE_SENSITIVE_STRING ;
    public final AntlrRegexParser.case_sensitive_string_return case_sensitive_string() throws RecognitionException {
        AntlrRegexParser.case_sensitive_string_return retval = new AntlrRegexParser.case_sensitive_string_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token CASE_SENSITIVE_STRING45=null;

        Object CASE_SENSITIVE_STRING45_tree=null;

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:269:2: ( CASE_SENSITIVE_STRING )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:269:4: CASE_SENSITIVE_STRING
            {
            root_0 = (Object)adaptor.nil();


            CASE_SENSITIVE_STRING45=(Token)match(input,CASE_SENSITIVE_STRING,FOLLOW_CASE_SENSITIVE_STRING_in_case_sensitive_string1572); 
            CASE_SENSITIVE_STRING45_tree = 
            (Object)adaptor.create(CASE_SENSITIVE_STRING45)
            ;
            adaptor.addChild(root_0, CASE_SENSITIVE_STRING45_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "case_sensitive_string"


    public static class group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "group"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:273:1: group : OPEN regex CLOSE -> regex ;
    public final AntlrRegexParser.group_return group() throws RecognitionException {
        AntlrRegexParser.group_return retval = new AntlrRegexParser.group_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token OPEN46=null;
        Token CLOSE48=null;
        AntlrRegexParser.regex_return regex47 =null;


        Object OPEN46_tree=null;
        Object CLOSE48_tree=null;
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleTokenStream stream_CLOSE=new RewriteRuleTokenStream(adaptor,"token CLOSE");
        RewriteRuleSubtreeStream stream_regex=new RewriteRuleSubtreeStream(adaptor,"rule regex");
        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:273:7: ( OPEN regex CLOSE -> regex )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:273:9: OPEN regex CLOSE
            {
            OPEN46=(Token)match(input,OPEN,FOLLOW_OPEN_in_group1585);  
            stream_OPEN.add(OPEN46);


            pushFollow(FOLLOW_regex_in_group1587);
            regex47=regex();

            state._fsp--;

            stream_regex.add(regex47.getTree());

            CLOSE48=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_group1589);  
            stream_CLOSE.add(CLOSE48);


            // AST REWRITE
            // elements: regex
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 273:26: -> regex
            {
                adaptor.addChild(root_0, stream_regex.nextTree());

            }


            retval.tree = root_0;

            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "group"


    public static class quantifier_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "quantifier"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:277:1: quantifier : ( optional | zero_to_many | one_to_many | repeat ) ;
    public final AntlrRegexParser.quantifier_return quantifier() throws RecognitionException {
        AntlrRegexParser.quantifier_return retval = new AntlrRegexParser.quantifier_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        AntlrRegexParser.optional_return optional49 =null;

        AntlrRegexParser.zero_to_many_return zero_to_many50 =null;

        AntlrRegexParser.one_to_many_return one_to_many51 =null;

        AntlrRegexParser.repeat_return repeat52 =null;



        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:278:2: ( ( optional | zero_to_many | one_to_many | repeat ) )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:279:2: ( optional | zero_to_many | one_to_many | repeat )
            {
            root_0 = (Object)adaptor.nil();


            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:279:2: ( optional | zero_to_many | one_to_many | repeat )
            int alt14=4;
            switch ( input.LA(1) ) {
            case QUESTION_MARK:
                {
                alt14=1;
                }
                break;
            case MANY:
                {
                alt14=2;
                }
                break;
            case PLUS:
                {
                alt14=3;
                }
                break;
            case OPEN_CURLY:
                {
                alt14=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }

            switch (alt14) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:279:4: optional
                    {
                    pushFollow(FOLLOW_optional_in_quantifier1610);
                    optional49=optional();

                    state._fsp--;

                    adaptor.addChild(root_0, optional49.getTree());

                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:280:5: zero_to_many
                    {
                    pushFollow(FOLLOW_zero_to_many_in_quantifier1619);
                    zero_to_many50=zero_to_many();

                    state._fsp--;

                    adaptor.addChild(root_0, zero_to_many50.getTree());

                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:281:5: one_to_many
                    {
                    pushFollow(FOLLOW_one_to_many_in_quantifier1629);
                    one_to_many51=one_to_many();

                    state._fsp--;

                    adaptor.addChild(root_0, one_to_many51.getTree());

                    }
                    break;
                case 4 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:282:5: repeat
                    {
                    pushFollow(FOLLOW_repeat_in_quantifier1635);
                    repeat52=repeat();

                    state._fsp--;

                    adaptor.addChild(root_0, repeat52.getTree());

                    }
                    break;

            }


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "quantifier"


    public static class repeat_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "repeat"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:287:1: repeat : OPEN_CURLY n1= NUMBER ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) ) CLOSE_CURLY ;
    public final AntlrRegexParser.repeat_return repeat() throws RecognitionException {
        AntlrRegexParser.repeat_return retval = new AntlrRegexParser.repeat_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token n1=null;
        Token n2=null;
        Token OPEN_CURLY53=null;
        Token REPEAT_SEPARATOR54=null;
        Token REPEAT_SEPARATOR55=null;
        Token MANY56=null;
        Token CLOSE_CURLY57=null;

        Object n1_tree=null;
        Object n2_tree=null;
        Object OPEN_CURLY53_tree=null;
        Object REPEAT_SEPARATOR54_tree=null;
        Object REPEAT_SEPARATOR55_tree=null;
        Object MANY56_tree=null;
        Object CLOSE_CURLY57_tree=null;
        RewriteRuleTokenStream stream_OPEN_CURLY=new RewriteRuleTokenStream(adaptor,"token OPEN_CURLY");
        RewriteRuleTokenStream stream_REPEAT_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token REPEAT_SEPARATOR");
        RewriteRuleTokenStream stream_CLOSE_CURLY=new RewriteRuleTokenStream(adaptor,"token CLOSE_CURLY");
        RewriteRuleTokenStream stream_MANY=new RewriteRuleTokenStream(adaptor,"token MANY");
        RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:287:8: ( OPEN_CURLY n1= NUMBER ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) ) CLOSE_CURLY )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:287:10: OPEN_CURLY n1= NUMBER ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) ) CLOSE_CURLY
            {
            OPEN_CURLY53=(Token)match(input,OPEN_CURLY,FOLLOW_OPEN_CURLY_in_repeat1651);  
            stream_OPEN_CURLY.add(OPEN_CURLY53);


            n1=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_repeat1655);  
            stream_NUMBER.add(n1);


            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:288:2: ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) )
            int alt15=3;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==REPEAT_SEPARATOR) ) {
                int LA15_1 = input.LA(2);

                if ( (LA15_1==NUMBER) ) {
                    alt15=1;
                }
                else if ( (LA15_1==MANY) ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA15_0==CLOSE_CURLY) ) {
                alt15=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }
            switch (alt15) {
                case 1 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:289:3: ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) )
                    {
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:289:3: ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:289:5: REPEAT_SEPARATOR n2= NUMBER
                    {
                    REPEAT_SEPARATOR54=(Token)match(input,REPEAT_SEPARATOR,FOLLOW_REPEAT_SEPARATOR_in_repeat1666);  
                    stream_REPEAT_SEPARATOR.add(REPEAT_SEPARATOR54);


                    n2=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_repeat1670);  
                    stream_NUMBER.add(n2);


                    // AST REWRITE
                    // elements: n2, n1
                    // token labels: n1, n2
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_n1=new RewriteRuleTokenStream(adaptor,"token n1",n1);
                    RewriteRuleTokenStream stream_n2=new RewriteRuleTokenStream(adaptor,"token n2",n2);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 289:33: -> ^( REPEAT $n1 $n2)
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:289:36: ^( REPEAT $n1 $n2)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(REPEAT, "REPEAT")
                        , root_1);

                        adaptor.addChild(root_1, stream_n1.nextNode());

                        adaptor.addChild(root_1, stream_n2.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }


                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:291:3: ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) )
                    {
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:291:3: ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:291:5: REPEAT_SEPARATOR MANY
                    {
                    REPEAT_SEPARATOR55=(Token)match(input,REPEAT_SEPARATOR,FOLLOW_REPEAT_SEPARATOR_in_repeat1696);  
                    stream_REPEAT_SEPARATOR.add(REPEAT_SEPARATOR55);


                    MANY56=(Token)match(input,MANY,FOLLOW_MANY_in_repeat1698);  
                    stream_MANY.add(MANY56);


                    // AST REWRITE
                    // elements: MANY, n1
                    // token labels: n1
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_n1=new RewriteRuleTokenStream(adaptor,"token n1",n1);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 291:28: -> ^( REPEAT $n1 MANY )
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:291:31: ^( REPEAT $n1 MANY )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(REPEAT, "REPEAT")
                        , root_1);

                        adaptor.addChild(root_1, stream_n1.nextNode());

                        adaptor.addChild(root_1, 
                        stream_MANY.nextNode()
                        );

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }


                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:293:3: ( -> ^( REPEAT $n1 $n1) )
                    {
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:293:3: ( -> ^( REPEAT $n1 $n1) )
                    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:293:8: 
                    {
                    // AST REWRITE
                    // elements: n1, n1
                    // token labels: n1
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_n1=new RewriteRuleTokenStream(adaptor,"token n1",n1);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 293:8: -> ^( REPEAT $n1 $n1)
                    {
                        // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:293:11: ^( REPEAT $n1 $n1)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(
                        (Object)adaptor.create(REPEAT, "REPEAT")
                        , root_1);

                        adaptor.addChild(root_1, stream_n1.nextNode());

                        adaptor.addChild(root_1, stream_n1.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }


                    retval.tree = root_0;

                    }


                    }
                    break;

            }


            CLOSE_CURLY57=(Token)match(input,CLOSE_CURLY,FOLLOW_CLOSE_CURLY_in_repeat1747);  
            stream_CLOSE_CURLY.add(CLOSE_CURLY57);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "repeat"


    public static class optional_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "optional"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:299:1: optional : QUESTION_MARK ;
    public final AntlrRegexParser.optional_return optional() throws RecognitionException {
        AntlrRegexParser.optional_return retval = new AntlrRegexParser.optional_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token QUESTION_MARK58=null;

        Object QUESTION_MARK58_tree=null;

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:299:9: ( QUESTION_MARK )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:299:11: QUESTION_MARK
            {
            root_0 = (Object)adaptor.nil();


            QUESTION_MARK58=(Token)match(input,QUESTION_MARK,FOLLOW_QUESTION_MARK_in_optional1758); 
            QUESTION_MARK58_tree = 
            (Object)adaptor.create(QUESTION_MARK58)
            ;
            adaptor.addChild(root_0, QUESTION_MARK58_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "optional"


    public static class zero_to_many_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "zero_to_many"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:303:1: zero_to_many : MANY ;
    public final AntlrRegexParser.zero_to_many_return zero_to_many() throws RecognitionException {
        AntlrRegexParser.zero_to_many_return retval = new AntlrRegexParser.zero_to_many_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token MANY59=null;

        Object MANY59_tree=null;

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:304:2: ( MANY )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:304:4: MANY
            {
            root_0 = (Object)adaptor.nil();


            MANY59=(Token)match(input,MANY,FOLLOW_MANY_in_zero_to_many1770); 
            MANY59_tree = 
            (Object)adaptor.create(MANY59)
            ;
            adaptor.addChild(root_0, MANY59_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "zero_to_many"


    public static class one_to_many_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };


    // $ANTLR start "one_to_many"
    // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:308:1: one_to_many : PLUS ;
    public final AntlrRegexParser.one_to_many_return one_to_many() throws RecognitionException {
        AntlrRegexParser.one_to_many_return retval = new AntlrRegexParser.one_to_many_return();
        retval.start = input.LT(1);


        Object root_0 = null;

        Token PLUS60=null;

        Object PLUS60_tree=null;

        try {
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:309:2: ( PLUS )
            // /home/matt/dev/search/byteseek/antlr/AntlrRegex.g:309:4: PLUS
            {
            root_0 = (Object)adaptor.nil();


            PLUS60=(Token)match(input,PLUS,FOLLOW_PLUS_in_one_to_many1783); 
            PLUS60_tree = 
            (Object)adaptor.create(PLUS60)
            ;
            adaptor.addChild(root_0, PLUS60_tree);


            }

            retval.stop = input.LT(-1);


            retval.tree = (Object)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (Object)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }

        finally {
        	// do for sure before leaving
        }
        return retval;
    }
    // $ANTLR end "one_to_many"

    // Delegated rules


 

    public static final BitSet FOLLOW_regex_in_start157 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_start159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_regex178 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_ALT_in_regex192 = new BitSet(new long[]{0x8000040578E86880L,0x000000000000000FL});
    public static final BitSet FOLLOW_sequence_in_regex194 = new BitSet(new long[]{0x0000000000000022L});
    public static final BitSet FOLLOW_quantified_atom_in_sequence260 = new BitSet(new long[]{0x8000040578E86882L,0x000000000000000FL});
    public static final BitSet FOLLOW_sequence_in_sequence272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_quantified_atom_in_sequence306 = new BitSet(new long[]{0x8000040578E86882L,0x000000000000000FL});
    public static final BitSet FOLLOW_quantified_atom_in_sequence317 = new BitSet(new long[]{0x8000040578E86882L,0x000000000000000FL});
    public static final BitSet FOLLOW_atom_in_quantified_atom377 = new BitSet(new long[]{0x0000001A04000002L});
    public static final BitSet FOLLOW_quantifier_in_quantified_atom382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hexbyte_in_atom424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_any_byte_in_atom429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_set_in_atom434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_shorthand_in_atom439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_shorthand_in_atom444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_bitmask_in_atom449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_any_bitmask_in_atom454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_sensitive_string_in_atom459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_insensitive_string_in_atom464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_group_in_atom469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BYTE_in_hexbyte484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FULL_STOP_in_any_byte494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_SQUARE_in_byte_set512 = new BitSet(new long[]{0xFFFFE40478687880L,0x000000000000000FL});
    public static final BitSet FOLLOW_CARET_in_byte_set523 = new BitSet(new long[]{0xFFFFE40478686880L,0x000000000000000FL});
    public static final BitSet FOLLOW_set_specification_in_byte_set525 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_set_specification_in_byte_set550 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_SQUARE_in_byte_set572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hexbyte_in_set_specification588 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_byte_shorthand_in_set_specification593 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_set_shorthand_in_set_specification598 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_mnemonic_in_set_specification603 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_case_sensitive_string_in_set_specification609 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_case_insensitive_string_in_set_specification614 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_byte_range_in_set_specification619 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_all_bitmask_in_set_specification624 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_any_bitmask_in_set_specification629 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_byte_set_in_set_specification634 = new BitSet(new long[]{0xFFFFE40478686882L,0x000000000000000FL});
    public static final BitSet FOLLOW_range_values_in_byte_range653 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_RANGE_SEPARATOR_in_byte_range657 = new BitSet(new long[]{0x0000000000004800L});
    public static final BitSet FOLLOW_range_values_in_byte_range664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AMPERSAND_in_all_bitmask707 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_BYTE_in_all_bitmask709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_any_bitmask729 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_BYTE_in_any_bitmask731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_ASCII_in_mnemonic754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_PRINT_in_mnemonic779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_GRAPH_in_mnemonic804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_WORD_in_mnemonic829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_ALPHANUM_in_mnemonic876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_ALPHA_in_mnemonic920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_UPPER_in_mnemonic954 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_LOWER_in_mnemonic978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_PUNCT_in_mnemonic1002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_HEXDIGIT_in_mnemonic1056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_DIGIT_in_mnemonic1100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_WHITESPACE_in_mnemonic1124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_BLANK_in_mnemonic1149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_SPACE_in_mnemonic1169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_TAB_in_mnemonic1201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_NEWLINE_in_mnemonic1214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_RETURN_in_mnemonic1227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_CONTROL_in_mnemonic1240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TAB_SHORTHAND_in_byte_shorthand1275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEWLINE_SHORTHAND_in_byte_shorthand1288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VERTICAL_TAB_SHORTHAND_in_byte_shorthand1301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORM_FEED_SHORTHAND_in_byte_shorthand1313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_SHORTHAND_in_byte_shorthand1326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_SHORTHAND_in_byte_shorthand1339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIGIT_SHORTHAND_in_set_shorthand1361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_DIGIT_SHORTHAND_in_set_shorthand1385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WORD_SHORTHAND_in_set_shorthand1409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_WORD_SHORTHAND_in_set_shorthand1456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHITE_SPACE_SHORTHAND_in_set_shorthand1503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_WHITE_SPACE_SHORTHAND_in_set_shorthand1528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_INSENSITIVE_STRING_in_case_insensitive_string1558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_SENSITIVE_STRING_in_case_sensitive_string1572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_group1585 = new BitSet(new long[]{0x8000040578E86880L,0x000000000000000FL});
    public static final BitSet FOLLOW_regex_in_group1587 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_CLOSE_in_group1589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optional_in_quantifier1610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_zero_to_many_in_quantifier1619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_one_to_many_in_quantifier1629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_repeat_in_quantifier1635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_CURLY_in_repeat1651 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_NUMBER_in_repeat1655 = new BitSet(new long[]{0x0000020000010000L});
    public static final BitSet FOLLOW_REPEAT_SEPARATOR_in_repeat1666 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_NUMBER_in_repeat1670 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_REPEAT_SEPARATOR_in_repeat1696 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_MANY_in_repeat1698 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_CLOSE_CURLY_in_repeat1747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_MARK_in_optional1758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MANY_in_zero_to_many1770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_one_to_many1783 = new BitSet(new long[]{0x0000000000000002L});

}