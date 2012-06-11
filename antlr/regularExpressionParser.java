// $ANTLR 3.1 /home/matt/dev/search/byteseek/antlr/regularExpression.g 2012-06-08 00:11:32

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.antlr.runtime.debug.*;
import java.io.IOException;

import org.antlr.runtime.tree.*;

public class regularExpressionParser extends DebugParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEQUENCE", "ALTERNATE", "REPEAT", "SET", "INVERTED_SET", "RANGE", "ALL_BITMASK", "ANY_BITMASK", "ANY", "ALT", "BYTE", "FULL_STOP", "OPEN_SQUARE", "CARET", "CLOSE_SQUARE", "RANGE_SEPARATOR", "CASE_SENSITIVE_STRING", "AMPERSAND", "TILDE", "SET_ASCII", "SET_PRINT", "SET_GRAPH", "SET_WORD", "SET_ALPHANUM", "SET_ALPHA", "SET_UPPER", "SET_LOWER", "SET_PUNCT", "SET_HEXDIGIT", "SET_DIGIT", "SET_WHITESPACE", "SET_BLANK", "SET_SPACE", "SET_TAB", "SET_NEWLINE", "SET_RETURN", "SET_CONTROL", "TAB_SHORTHAND", "NEWLINE_SHORTHAND", "VERTICAL_TAB_SHORTHAND", "FORM_FEED_SHORTHAND", "RETURN_SHORTHAND", "ESCAPE_SHORTHAND", "DIGIT_SHORTHAND", "NOT_DIGIT_SHORTHAND", "WORD_SHORTHAND", "NOT_WORD_SHORTHAND", "WHITE_SPACE_SHORTHAND", "NOT_WHITE_SPACE_SHORTHAND", "CASE_INSENSITIVE_STRING", "OPEN", "CLOSE", "OPEN_CURLY", "NUMBER", "REPEAT_SEPARATOR", "MANY", "CLOSE_CURLY", "QUESTION_MARK", "PLUS", "QUOTE", "BACK_TICK", "ESCAPE", "HEX_DIGIT", "COMMENT", "WS"
    };
    public static final int CLOSE_CURLY=60;
    public static final int SET_NEWLINE=38;
    public static final int SET_LOWER=30;
    public static final int SET_GRAPH=25;
    public static final int SET_ASCII=23;
    public static final int TAB_SHORTHAND=41;
    public static final int DIGIT_SHORTHAND=47;
    public static final int CASE_SENSITIVE_STRING=20;
    public static final int SET_PUNCT=31;
    public static final int EOF=-1;
    public static final int SET_DIGIT=33;
    public static final int RANGE_SEPARATOR=19;
    public static final int NEWLINE_SHORTHAND=42;
    public static final int QUOTE=63;
    public static final int ALT=13;
    public static final int SET_ALPHANUM=27;
    public static final int ESCAPE=65;
    public static final int ESCAPE_SHORTHAND=46;
    public static final int SET_WHITESPACE=34;
    public static final int SET_RETURN=39;
    public static final int CARET=17;
    public static final int QUESTION_MARK=61;
    public static final int TILDE=22;
    public static final int OPEN_SQUARE=16;
    public static final int BACK_TICK=64;
    public static final int PLUS=62;
    public static final int ANY_BITMASK=11;
    public static final int RETURN_SHORTHAND=45;
    public static final int SET_UPPER=29;
    public static final int SET_TAB=37;
    public static final int SET_BLANK=35;
    public static final int COMMENT=67;
    public static final int REPEAT_SEPARATOR=58;
    public static final int CLOSE_SQUARE=18;
    public static final int ALL_BITMASK=10;
    public static final int FORM_FEED_SHORTHAND=44;
    public static final int BYTE=14;
    public static final int FULL_STOP=15;
    public static final int VERTICAL_TAB_SHORTHAND=43;
    public static final int INVERTED_SET=8;
    public static final int NUMBER=57;
    public static final int AMPERSAND=21;
    public static final int HEX_DIGIT=66;
    public static final int RANGE=9;
    public static final int SET=7;
    public static final int OPEN_CURLY=56;
    public static final int SET_SPACE=36;
    public static final int SET_HEXDIGIT=32;
    public static final int MANY=59;
    public static final int SET_CONTROL=40;
    public static final int ALTERNATE=5;
    public static final int OPEN=54;
    public static final int SEQUENCE=4;
    public static final int SET_WORD=26;
    public static final int WS=68;
    public static final int ANY=12;
    public static final int CLOSE=55;
    public static final int CASE_INSENSITIVE_STRING=53;
    public static final int NOT_WORD_SHORTHAND=50;
    public static final int NOT_WHITE_SPACE_SHORTHAND=52;
    public static final int WORD_SHORTHAND=49;
    public static final int SET_ALPHA=28;
    public static final int REPEAT=6;
    public static final int SET_PRINT=24;
    public static final int WHITE_SPACE_SHORTHAND=51;
    public static final int NOT_DIGIT_SHORTHAND=48;

    // delegates
    // delegators

    public static final String[] ruleNames = new String[] {
        "invalidRule", "optional", "set_shorthand", "byte_range", "case_insensitive_string", 
        "byte_set", "one_to_many", "quantified_atom", "any_bitmask", "case_sensitive_string", 
        "sequence", "regex", "set_specification", "group", "byte_shorthand", 
        "zero_to_many", "atom", "hexbyte", "start", "all_bitmask", "repeat", 
        "quantifier", "mnemonic", "any_byte", "range_values"
    };
     
        public int ruleLevel = 0;
        public int getRuleLevel() { return ruleLevel; }
        public void incRuleLevel() { ruleLevel++; }
        public void decRuleLevel() { ruleLevel--; }
        public regularExpressionParser(TokenStream input) {
            this(input, DebugEventSocketProxy.DEFAULT_DEBUGGER_PORT, new RecognizerSharedState());
        }
        public regularExpressionParser(TokenStream input, int port, RecognizerSharedState state) {
            super(input, state);
            DebugEventSocketProxy proxy =
                new DebugEventSocketProxy(this,port,adaptor);
            setDebugListener(proxy);
            setTokenStream(new DebugTokenStream(input,proxy));
            try {
                proxy.handshake();
            }
            catch (IOException ioe) {
                reportError(ioe);
            }
            TreeAdaptor adap = new CommonTreeAdaptor();
            setTreeAdaptor(adap);
            proxy.setTreeAdaptor(adap);
        }
    public regularExpressionParser(TokenStream input, DebugEventListener dbg) {
        super(input, dbg);

         
        TreeAdaptor adap = new CommonTreeAdaptor();
        setTreeAdaptor(adap);

    }
    protected boolean evalPredicate(boolean result, String predicate) {
        dbg.semanticPredicate(result, predicate);
        return result;
    }

    protected DebugTreeAdaptor adaptor;
    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = new DebugTreeAdaptor(dbg,adaptor);

    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }


    public String[] getTokenNames() { return regularExpressionParser.tokenNames; }
    public String getGrammarFileName() { return "/home/matt/dev/search/byteseek/antlr/regularExpression.g"; }


    	boolean sequencesAsTree = false;


    public static class start_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "start"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:29:1: start : regex EOF ;
    public final regularExpressionParser.start_return start() throws RecognitionException {
        regularExpressionParser.start_return retval = new regularExpressionParser.start_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF2=null;
        regularExpressionParser.regex_return regex1 = null;


        Object EOF2_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "start");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(29, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:29:7: ( regex EOF )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:29:9: regex EOF
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(29,9);
            pushFollow(FOLLOW_regex_in_start134);
            regex1=regex();

            state._fsp--;

            adaptor.addChild(root_0, regex1.getTree());
            dbg.location(29,18);
            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_start136); 

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
        }
        dbg.location(30, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "start");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "start"

    public static class regex_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "regex"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:33:1: regex : sequence ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) ) ;
    public final regularExpressionParser.regex_return regex() throws RecognitionException {
        regularExpressionParser.regex_return retval = new regularExpressionParser.regex_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token ALT4=null;
        regularExpressionParser.sequence_return sequence3 = null;

        regularExpressionParser.sequence_return sequence5 = null;


        Object ALT4_tree=null;
        RewriteRuleTokenStream stream_ALT=new RewriteRuleTokenStream(adaptor,"token ALT");
        RewriteRuleSubtreeStream stream_sequence=new RewriteRuleSubtreeStream(adaptor,"rule sequence");
        try { dbg.enterRule(getGrammarFileName(), "regex");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(33, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:33:7: ( sequence ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) ) )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:34:3: sequence ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) )
            {
            dbg.location(34,3);
            pushFollow(FOLLOW_sequence_in_regex155);
            sequence3=sequence();

            state._fsp--;

            stream_sequence.add(sequence3.getTree());
            dbg.location(35,3);
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:35:3: ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) )
            int alt2=2;
            try { dbg.enterSubRule(2);
            try { dbg.enterDecision(2);

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

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(2);}

            switch (alt2) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:36:4: ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) )
                    {
                    dbg.location(36,4);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:36:4: ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:36:6: ( ALT sequence )+
                    {
                    dbg.location(36,6);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:36:6: ( ALT sequence )+
                    int cnt1=0;
                    try { dbg.enterSubRule(1);

                    loop1:
                    do {
                        int alt1=2;
                        try { dbg.enterDecision(1);

                        int LA1_0 = input.LA(1);

                        if ( (LA1_0==ALT) ) {
                            alt1=1;
                        }


                        } finally {dbg.exitDecision(1);}

                        switch (alt1) {
                    	case 1 :
                    	    dbg.enterAlt(1);

                    	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:36:7: ALT sequence
                    	    {
                    	    dbg.location(36,7);
                    	    ALT4=(Token)match(input,ALT,FOLLOW_ALT_in_regex169);  
                    	    stream_ALT.add(ALT4);

                    	    dbg.location(36,11);
                    	    pushFollow(FOLLOW_sequence_in_regex171);
                    	    sequence5=sequence();

                    	    state._fsp--;

                    	    stream_sequence.add(sequence5.getTree());

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt1 >= 1 ) break loop1;
                                EarlyExitException eee =
                                    new EarlyExitException(1, input);
                                dbg.recognitionException(eee);

                                throw eee;
                        }
                        cnt1++;
                    } while (true);
                    } finally {dbg.exitSubRule(1);}



                    // AST REWRITE
                    // elements: sequence, ALT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 36:22: -> ^( ALT ( sequence )+ )
                    {
                        dbg.location(36,25);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:36:25: ^( ALT ( sequence )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(36,27);
                        root_1 = (Object)adaptor.becomeRoot(stream_ALT.nextNode(), root_1);

                        dbg.location(36,31);
                        if ( !(stream_sequence.hasNext()) ) {
                            throw new RewriteEarlyExitException();
                        }
                        while ( stream_sequence.hasNext() ) {
                            dbg.location(36,31);
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
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:38:4: ( -> sequence )
                    {
                    dbg.location(38,4);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:38:4: ( -> sequence )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:38:9: 
                    {

                    // AST REWRITE
                    // elements: sequence
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 38:9: -> sequence
                    {
                        dbg.location(38,13);
                        adaptor.addChild(root_0, stream_sequence.nextTree());

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;

            }
            } finally {dbg.exitSubRule(2);}


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
        }
        dbg.location(42, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "regex");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "regex"

    public static class sequence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sequence"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:45:1: sequence : ({...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? ) | {...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) ) );
    public final regularExpressionParser.sequence_return sequence() throws RecognitionException {
        regularExpressionParser.sequence_return retval = new regularExpressionParser.sequence_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        regularExpressionParser.quantified_atom_return quantified_atom6 = null;

        regularExpressionParser.sequence_return sequence7 = null;

        regularExpressionParser.quantified_atom_return quantified_atom8 = null;

        regularExpressionParser.quantified_atom_return quantified_atom9 = null;


        RewriteRuleSubtreeStream stream_quantified_atom=new RewriteRuleSubtreeStream(adaptor,"rule quantified_atom");
        RewriteRuleSubtreeStream stream_sequence=new RewriteRuleSubtreeStream(adaptor,"rule sequence");
        try { dbg.enterRule(getGrammarFileName(), "sequence");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(45, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:46:2: ({...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? ) | {...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) ) )
            int alt6=2;
            try { dbg.enterDecision(6);

            try {
                isCyclicDecision = true;
                alt6 = dfa6.predict(input);
            }
            catch (NoViableAltException nvae) {
                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(6);}

            switch (alt6) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:46:4: {...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? )
                    {
                    dbg.location(46,4);
                    if ( !(evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {
                        throw new FailedPredicateException(input, "sequence", "sequencesAsTree");
                    }
                    dbg.location(47,2);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:47:2: ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:48:3: ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )?
                    {
                    dbg.location(48,3);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:48:3: ( quantified_atom -> quantified_atom )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:48:5: quantified_atom
                    {
                    dbg.location(48,5);
                    pushFollow(FOLLOW_quantified_atom_in_sequence237);
                    quantified_atom6=quantified_atom();

                    state._fsp--;

                    stream_quantified_atom.add(quantified_atom6.getTree());


                    // AST REWRITE
                    // elements: quantified_atom
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 48:21: -> quantified_atom
                    {
                        dbg.location(48,24);
                        adaptor.addChild(root_0, stream_quantified_atom.nextTree());

                    }

                    retval.tree = root_0;
                    }

                    dbg.location(49,3);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:49:3: ( sequence -> ^( SEQUENCE quantified_atom sequence ) )?
                    int alt3=2;
                    try { dbg.enterSubRule(3);
                    try { dbg.enterDecision(3);

                    try {
                        isCyclicDecision = true;
                        alt3 = dfa3.predict(input);
                    }
                    catch (NoViableAltException nvae) {
                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                    } finally {dbg.exitDecision(3);}

                    switch (alt3) {
                        case 1 :
                            dbg.enterAlt(1);

                            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:49:5: sequence
                            {
                            dbg.location(49,5);
                            pushFollow(FOLLOW_sequence_in_sequence249);
                            sequence7=sequence();

                            state._fsp--;

                            stream_sequence.add(sequence7.getTree());


                            // AST REWRITE
                            // elements: sequence, quantified_atom
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 49:16: -> ^( SEQUENCE quantified_atom sequence )
                            {
                                dbg.location(49,19);
                                // /home/matt/dev/search/byteseek/antlr/regularExpression.g:49:19: ^( SEQUENCE quantified_atom sequence )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                dbg.location(49,21);
                                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SEQUENCE, "SEQUENCE"), root_1);

                                dbg.location(49,30);
                                adaptor.addChild(root_1, stream_quantified_atom.nextTree());
                                dbg.location(49,46);
                                adaptor.addChild(root_1, stream_sequence.nextTree());

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;
                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(3);}


                    }


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:52:3: {...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) )
                    {
                    dbg.location(52,3);
                    if ( !(evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {
                        throw new FailedPredicateException(input, "sequence", "!sequencesAsTree");
                    }
                    dbg.location(53,2);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:53:2: ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:54:3: quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) )
                    {
                    dbg.location(54,3);
                    pushFollow(FOLLOW_quantified_atom_in_sequence283);
                    quantified_atom8=quantified_atom();

                    state._fsp--;

                    stream_quantified_atom.add(quantified_atom8.getTree());
                    dbg.location(55,3);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:55:3: ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) )
                    int alt5=2;
                    try { dbg.enterSubRule(5);
                    try { dbg.enterDecision(5);

                    int LA5_0 = input.LA(1);

                    if ( ((LA5_0>=BYTE && LA5_0<=OPEN_SQUARE)||(LA5_0>=CASE_SENSITIVE_STRING && LA5_0<=TILDE)||(LA5_0>=TAB_SHORTHAND && LA5_0<=OPEN)) ) {
                        alt5=1;
                    }
                    else if ( (LA5_0==EOF||LA5_0==ALT||LA5_0==CLOSE) ) {
                        alt5=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 5, 0, input);

                        dbg.recognitionException(nvae);
                        throw nvae;
                    }
                    } finally {dbg.exitDecision(5);}

                    switch (alt5) {
                        case 1 :
                            dbg.enterAlt(1);

                            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:56:4: ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) )
                            {
                            dbg.location(56,4);
                            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:56:4: ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) )
                            dbg.enterAlt(1);

                            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:56:6: ( quantified_atom )+
                            {
                            dbg.location(56,6);
                            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:56:6: ( quantified_atom )+
                            int cnt4=0;
                            try { dbg.enterSubRule(4);

                            loop4:
                            do {
                                int alt4=2;
                                try { dbg.enterDecision(4);

                                int LA4_0 = input.LA(1);

                                if ( ((LA4_0>=BYTE && LA4_0<=OPEN_SQUARE)||(LA4_0>=CASE_SENSITIVE_STRING && LA4_0<=TILDE)||(LA4_0>=TAB_SHORTHAND && LA4_0<=OPEN)) ) {
                                    alt4=1;
                                }


                                } finally {dbg.exitDecision(4);}

                                switch (alt4) {
                            	case 1 :
                            	    dbg.enterAlt(1);

                            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:56:6: quantified_atom
                            	    {
                            	    dbg.location(56,6);
                            	    pushFollow(FOLLOW_quantified_atom_in_sequence294);
                            	    quantified_atom9=quantified_atom();

                            	    state._fsp--;

                            	    stream_quantified_atom.add(quantified_atom9.getTree());

                            	    }
                            	    break;

                            	default :
                            	    if ( cnt4 >= 1 ) break loop4;
                                        EarlyExitException eee =
                                            new EarlyExitException(4, input);
                                        dbg.recognitionException(eee);

                                        throw eee;
                                }
                                cnt4++;
                            } while (true);
                            } finally {dbg.exitSubRule(4);}



                            // AST REWRITE
                            // elements: quantified_atom
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 56:23: -> ^( SEQUENCE ( quantified_atom )+ )
                            {
                                dbg.location(56,26);
                                // /home/matt/dev/search/byteseek/antlr/regularExpression.g:56:26: ^( SEQUENCE ( quantified_atom )+ )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                dbg.location(56,28);
                                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SEQUENCE, "SEQUENCE"), root_1);

                                dbg.location(56,37);
                                if ( !(stream_quantified_atom.hasNext()) ) {
                                    throw new RewriteEarlyExitException();
                                }
                                while ( stream_quantified_atom.hasNext() ) {
                                    dbg.location(56,37);
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
                            dbg.enterAlt(2);

                            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:58:4: ( -> ^( quantified_atom ) )
                            {
                            dbg.location(58,4);
                            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:58:4: ( -> ^( quantified_atom ) )
                            dbg.enterAlt(1);

                            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:58:8: 
                            {

                            // AST REWRITE
                            // elements: quantified_atom
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 58:8: -> ^( quantified_atom )
                            {
                                dbg.location(58,11);
                                // /home/matt/dev/search/byteseek/antlr/regularExpression.g:58:11: ^( quantified_atom )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                dbg.location(58,13);
                                root_1 = (Object)adaptor.becomeRoot(stream_quantified_atom.nextNode(), root_1);

                                adaptor.addChild(root_0, root_1);
                                }

                            }

                            retval.tree = root_0;
                            }


                            }
                            break;

                    }
                    } finally {dbg.exitSubRule(5);}


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
        }
        dbg.location(61, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "sequence");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "sequence"

    public static class quantified_atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "quantified_atom"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:65:1: quantified_atom : e= atom ( quantifier -> ^( quantifier $e) | -> ^( $e) ) ;
    public final regularExpressionParser.quantified_atom_return quantified_atom() throws RecognitionException {
        regularExpressionParser.quantified_atom_return retval = new regularExpressionParser.quantified_atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        regularExpressionParser.atom_return e = null;

        regularExpressionParser.quantifier_return quantifier10 = null;


        RewriteRuleSubtreeStream stream_quantifier=new RewriteRuleSubtreeStream(adaptor,"rule quantifier");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        try { dbg.enterRule(getGrammarFileName(), "quantified_atom");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(65, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:66:2: (e= atom ( quantifier -> ^( quantifier $e) | -> ^( $e) ) )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:66:4: e= atom ( quantifier -> ^( quantifier $e) | -> ^( $e) )
            {
            dbg.location(66,5);
            pushFollow(FOLLOW_atom_in_quantified_atom354);
            e=atom();

            state._fsp--;

            stream_atom.add(e.getTree());
            dbg.location(67,2);
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:67:2: ( quantifier -> ^( quantifier $e) | -> ^( $e) )
            int alt7=2;
            try { dbg.enterSubRule(7);
            try { dbg.enterDecision(7);

            int LA7_0 = input.LA(1);

            if ( (LA7_0==OPEN_CURLY||LA7_0==MANY||(LA7_0>=QUESTION_MARK && LA7_0<=PLUS)) ) {
                alt7=1;
            }
            else if ( (LA7_0==EOF||(LA7_0>=ALT && LA7_0<=OPEN_SQUARE)||(LA7_0>=CASE_SENSITIVE_STRING && LA7_0<=TILDE)||(LA7_0>=TAB_SHORTHAND && LA7_0<=CLOSE)) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(7);}

            switch (alt7) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:67:4: quantifier
                    {
                    dbg.location(67,4);
                    pushFollow(FOLLOW_quantifier_in_quantified_atom359);
                    quantifier10=quantifier();

                    state._fsp--;

                    stream_quantifier.add(quantifier10.getTree());


                    // AST REWRITE
                    // elements: quantifier, e
                    // token labels: 
                    // rule labels: retval, e
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"token e",e!=null?e.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 67:17: -> ^( quantifier $e)
                    {
                        dbg.location(67,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:67:20: ^( quantifier $e)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(67,22);
                        root_1 = (Object)adaptor.becomeRoot(stream_quantifier.nextNode(), root_1);

                        dbg.location(67,33);
                        adaptor.addChild(root_1, stream_e.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:68:7: 
                    {

                    // AST REWRITE
                    // elements: e
                    // token labels: 
                    // rule labels: retval, e
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
                    RewriteRuleSubtreeStream stream_e=new RewriteRuleSubtreeStream(adaptor,"token e",e!=null?e.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 68:7: -> ^( $e)
                    {
                        dbg.location(68,10);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:68:10: ^( $e)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(68,12);
                        root_1 = (Object)adaptor.becomeRoot(stream_e.nextNode(), root_1);

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            } finally {dbg.exitSubRule(7);}


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
        }
        dbg.location(70, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "quantified_atom");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "quantified_atom"

    public static class atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:73:1: atom : ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | all_bitmask | any_bitmask | case_sensitive_string | case_insensitive_string | group ) ;
    public final regularExpressionParser.atom_return atom() throws RecognitionException {
        regularExpressionParser.atom_return retval = new regularExpressionParser.atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        regularExpressionParser.hexbyte_return hexbyte11 = null;

        regularExpressionParser.any_byte_return any_byte12 = null;

        regularExpressionParser.byte_set_return byte_set13 = null;

        regularExpressionParser.byte_shorthand_return byte_shorthand14 = null;

        regularExpressionParser.set_shorthand_return set_shorthand15 = null;

        regularExpressionParser.all_bitmask_return all_bitmask16 = null;

        regularExpressionParser.any_bitmask_return any_bitmask17 = null;

        regularExpressionParser.case_sensitive_string_return case_sensitive_string18 = null;

        regularExpressionParser.case_insensitive_string_return case_insensitive_string19 = null;

        regularExpressionParser.group_return group20 = null;



        try { dbg.enterRule(getGrammarFileName(), "atom");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(73, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:73:6: ( ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | all_bitmask | any_bitmask | case_sensitive_string | case_insensitive_string | group ) )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:74:2: ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | all_bitmask | any_bitmask | case_sensitive_string | case_insensitive_string | group )
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(74,2);
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:74:2: ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | all_bitmask | any_bitmask | case_sensitive_string | case_insensitive_string | group )
            int alt8=10;
            try { dbg.enterSubRule(8);
            try { dbg.enterDecision(8);

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
            case TAB_SHORTHAND:
            case NEWLINE_SHORTHAND:
            case VERTICAL_TAB_SHORTHAND:
            case FORM_FEED_SHORTHAND:
            case RETURN_SHORTHAND:
            case ESCAPE_SHORTHAND:
                {
                alt8=4;
                }
                break;
            case DIGIT_SHORTHAND:
            case NOT_DIGIT_SHORTHAND:
            case WORD_SHORTHAND:
            case NOT_WORD_SHORTHAND:
            case WHITE_SPACE_SHORTHAND:
            case NOT_WHITE_SPACE_SHORTHAND:
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

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(8);}

            switch (alt8) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:74:4: hexbyte
                    {
                    dbg.location(74,4);
                    pushFollow(FOLLOW_hexbyte_in_atom401);
                    hexbyte11=hexbyte();

                    state._fsp--;

                    adaptor.addChild(root_0, hexbyte11.getTree());

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:75:4: any_byte
                    {
                    dbg.location(75,4);
                    pushFollow(FOLLOW_any_byte_in_atom406);
                    any_byte12=any_byte();

                    state._fsp--;

                    adaptor.addChild(root_0, any_byte12.getTree());

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:76:4: byte_set
                    {
                    dbg.location(76,4);
                    pushFollow(FOLLOW_byte_set_in_atom411);
                    byte_set13=byte_set();

                    state._fsp--;

                    adaptor.addChild(root_0, byte_set13.getTree());

                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:77:4: byte_shorthand
                    {
                    dbg.location(77,4);
                    pushFollow(FOLLOW_byte_shorthand_in_atom416);
                    byte_shorthand14=byte_shorthand();

                    state._fsp--;

                    adaptor.addChild(root_0, byte_shorthand14.getTree());

                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:78:4: set_shorthand
                    {
                    dbg.location(78,4);
                    pushFollow(FOLLOW_set_shorthand_in_atom421);
                    set_shorthand15=set_shorthand();

                    state._fsp--;

                    adaptor.addChild(root_0, set_shorthand15.getTree());

                    }
                    break;
                case 6 :
                    dbg.enterAlt(6);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:79:4: all_bitmask
                    {
                    dbg.location(79,4);
                    pushFollow(FOLLOW_all_bitmask_in_atom426);
                    all_bitmask16=all_bitmask();

                    state._fsp--;

                    adaptor.addChild(root_0, all_bitmask16.getTree());

                    }
                    break;
                case 7 :
                    dbg.enterAlt(7);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:80:4: any_bitmask
                    {
                    dbg.location(80,4);
                    pushFollow(FOLLOW_any_bitmask_in_atom431);
                    any_bitmask17=any_bitmask();

                    state._fsp--;

                    adaptor.addChild(root_0, any_bitmask17.getTree());

                    }
                    break;
                case 8 :
                    dbg.enterAlt(8);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:81:4: case_sensitive_string
                    {
                    dbg.location(81,4);
                    pushFollow(FOLLOW_case_sensitive_string_in_atom436);
                    case_sensitive_string18=case_sensitive_string();

                    state._fsp--;

                    adaptor.addChild(root_0, case_sensitive_string18.getTree());

                    }
                    break;
                case 9 :
                    dbg.enterAlt(9);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:82:4: case_insensitive_string
                    {
                    dbg.location(82,4);
                    pushFollow(FOLLOW_case_insensitive_string_in_atom441);
                    case_insensitive_string19=case_insensitive_string();

                    state._fsp--;

                    adaptor.addChild(root_0, case_insensitive_string19.getTree());

                    }
                    break;
                case 10 :
                    dbg.enterAlt(10);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:83:4: group
                    {
                    dbg.location(83,4);
                    pushFollow(FOLLOW_group_in_atom446);
                    group20=group();

                    state._fsp--;

                    adaptor.addChild(root_0, group20.getTree());

                    }
                    break;

            }
            } finally {dbg.exitSubRule(8);}


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
        }
        dbg.location(85, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "atom");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "atom"

    public static class hexbyte_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "hexbyte"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:88:1: hexbyte : BYTE ;
    public final regularExpressionParser.hexbyte_return hexbyte() throws RecognitionException {
        regularExpressionParser.hexbyte_return retval = new regularExpressionParser.hexbyte_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BYTE21=null;

        Object BYTE21_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "hexbyte");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(88, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:88:9: ( BYTE )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:88:11: BYTE
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(88,11);
            BYTE21=(Token)match(input,BYTE,FOLLOW_BYTE_in_hexbyte461); 
            BYTE21_tree = (Object)adaptor.create(BYTE21);
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
        }
        dbg.location(89, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "hexbyte");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "hexbyte"

    public static class any_byte_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "any_byte"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:92:1: any_byte : FULL_STOP -> ANY ;
    public final regularExpressionParser.any_byte_return any_byte() throws RecognitionException {
        regularExpressionParser.any_byte_return retval = new regularExpressionParser.any_byte_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FULL_STOP22=null;

        Object FULL_STOP22_tree=null;
        RewriteRuleTokenStream stream_FULL_STOP=new RewriteRuleTokenStream(adaptor,"token FULL_STOP");

        try { dbg.enterRule(getGrammarFileName(), "any_byte");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(92, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:92:9: ( FULL_STOP -> ANY )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:92:11: FULL_STOP
            {
            dbg.location(92,11);
            FULL_STOP22=(Token)match(input,FULL_STOP,FOLLOW_FULL_STOP_in_any_byte471);  
            stream_FULL_STOP.add(FULL_STOP22);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 92:22: -> ANY
            {
                dbg.location(92,25);
                adaptor.addChild(root_0, (Object)adaptor.create(ANY, "ANY"));

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
        }
        dbg.location(93, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "any_byte");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "any_byte"

    public static class byte_set_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "byte_set"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:96:1: byte_set : OPEN_SQUARE ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) ) CLOSE_SQUARE ;
    public final regularExpressionParser.byte_set_return byte_set() throws RecognitionException {
        regularExpressionParser.byte_set_return retval = new regularExpressionParser.byte_set_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OPEN_SQUARE23=null;
        Token CARET24=null;
        Token CLOSE_SQUARE27=null;
        regularExpressionParser.set_specification_return set_specification25 = null;

        regularExpressionParser.set_specification_return set_specification26 = null;


        Object OPEN_SQUARE23_tree=null;
        Object CARET24_tree=null;
        Object CLOSE_SQUARE27_tree=null;
        RewriteRuleTokenStream stream_OPEN_SQUARE=new RewriteRuleTokenStream(adaptor,"token OPEN_SQUARE");
        RewriteRuleTokenStream stream_CLOSE_SQUARE=new RewriteRuleTokenStream(adaptor,"token CLOSE_SQUARE");
        RewriteRuleTokenStream stream_CARET=new RewriteRuleTokenStream(adaptor,"token CARET");
        RewriteRuleSubtreeStream stream_set_specification=new RewriteRuleSubtreeStream(adaptor,"rule set_specification");
        try { dbg.enterRule(getGrammarFileName(), "byte_set");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(96, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:97:2: ( OPEN_SQUARE ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) ) CLOSE_SQUARE )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:97:4: OPEN_SQUARE ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) ) CLOSE_SQUARE
            {
            dbg.location(97,4);
            OPEN_SQUARE23=(Token)match(input,OPEN_SQUARE,FOLLOW_OPEN_SQUARE_in_byte_set489);  
            stream_OPEN_SQUARE.add(OPEN_SQUARE23);

            dbg.location(98,2);
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:98:2: ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) )
            int alt9=2;
            try { dbg.enterSubRule(9);
            try { dbg.enterDecision(9);

            int LA9_0 = input.LA(1);

            if ( (LA9_0==CARET) ) {
                alt9=1;
            }
            else if ( (LA9_0==BYTE||LA9_0==OPEN_SQUARE||(LA9_0>=CASE_SENSITIVE_STRING && LA9_0<=CASE_INSENSITIVE_STRING)) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(9);}

            switch (alt9) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:99:3: ( CARET set_specification -> ^( INVERTED_SET set_specification ) )
                    {
                    dbg.location(99,3);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:99:3: ( CARET set_specification -> ^( INVERTED_SET set_specification ) )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:99:5: CARET set_specification
                    {
                    dbg.location(99,5);
                    CARET24=(Token)match(input,CARET,FOLLOW_CARET_in_byte_set500);  
                    stream_CARET.add(CARET24);

                    dbg.location(99,11);
                    pushFollow(FOLLOW_set_specification_in_byte_set502);
                    set_specification25=set_specification();

                    state._fsp--;

                    stream_set_specification.add(set_specification25.getTree());


                    // AST REWRITE
                    // elements: set_specification
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 99:30: -> ^( INVERTED_SET set_specification )
                    {
                        dbg.location(99,33);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:99:33: ^( INVERTED_SET set_specification )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(99,36);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INVERTED_SET, "INVERTED_SET"), root_1);

                        dbg.location(99,49);
                        adaptor.addChild(root_1, stream_set_specification.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:101:3: ( set_specification -> ^( SET set_specification ) )
                    {
                    dbg.location(101,3);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:101:3: ( set_specification -> ^( SET set_specification ) )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:101:5: set_specification
                    {
                    dbg.location(101,5);
                    pushFollow(FOLLOW_set_specification_in_byte_set527);
                    set_specification26=set_specification();

                    state._fsp--;

                    stream_set_specification.add(set_specification26.getTree());


                    // AST REWRITE
                    // elements: set_specification
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 101:25: -> ^( SET set_specification )
                    {
                        dbg.location(101,28);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:101:28: ^( SET set_specification )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(101,31);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(101,35);
                        adaptor.addChild(root_1, stream_set_specification.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;

            }
            } finally {dbg.exitSubRule(9);}

            dbg.location(103,3);
            CLOSE_SQUARE27=(Token)match(input,CLOSE_SQUARE,FOLLOW_CLOSE_SQUARE_in_byte_set549);  
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
        }
        dbg.location(104, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "byte_set");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "byte_set"

    public static class set_specification_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "set_specification"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:107:1: set_specification : ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | all_bitmask | any_bitmask | byte_set )+ ;
    public final regularExpressionParser.set_specification_return set_specification() throws RecognitionException {
        regularExpressionParser.set_specification_return retval = new regularExpressionParser.set_specification_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        regularExpressionParser.hexbyte_return hexbyte28 = null;

        regularExpressionParser.byte_shorthand_return byte_shorthand29 = null;

        regularExpressionParser.set_shorthand_return set_shorthand30 = null;

        regularExpressionParser.mnemonic_return mnemonic31 = null;

        regularExpressionParser.case_sensitive_string_return case_sensitive_string32 = null;

        regularExpressionParser.case_insensitive_string_return case_insensitive_string33 = null;

        regularExpressionParser.byte_range_return byte_range34 = null;

        regularExpressionParser.all_bitmask_return all_bitmask35 = null;

        regularExpressionParser.any_bitmask_return any_bitmask36 = null;

        regularExpressionParser.byte_set_return byte_set37 = null;



        try { dbg.enterRule(getGrammarFileName(), "set_specification");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(107, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:108:2: ( ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | all_bitmask | any_bitmask | byte_set )+ )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:109:2: ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | all_bitmask | any_bitmask | byte_set )+
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(109,2);
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:109:2: ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | all_bitmask | any_bitmask | byte_set )+
            int cnt10=0;
            try { dbg.enterSubRule(10);

            loop10:
            do {
                int alt10=11;
                try { dbg.enterDecision(10);

                try {
                    isCyclicDecision = true;
                    alt10 = dfa10.predict(input);
                }
                catch (NoViableAltException nvae) {
                    dbg.recognitionException(nvae);
                    throw nvae;
                }
                } finally {dbg.exitDecision(10);}

                switch (alt10) {
            	case 1 :
            	    dbg.enterAlt(1);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:109:4: hexbyte
            	    {
            	    dbg.location(109,4);
            	    pushFollow(FOLLOW_hexbyte_in_set_specification565);
            	    hexbyte28=hexbyte();

            	    state._fsp--;

            	    adaptor.addChild(root_0, hexbyte28.getTree());

            	    }
            	    break;
            	case 2 :
            	    dbg.enterAlt(2);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:110:4: byte_shorthand
            	    {
            	    dbg.location(110,4);
            	    pushFollow(FOLLOW_byte_shorthand_in_set_specification570);
            	    byte_shorthand29=byte_shorthand();

            	    state._fsp--;

            	    adaptor.addChild(root_0, byte_shorthand29.getTree());

            	    }
            	    break;
            	case 3 :
            	    dbg.enterAlt(3);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:111:4: set_shorthand
            	    {
            	    dbg.location(111,4);
            	    pushFollow(FOLLOW_set_shorthand_in_set_specification575);
            	    set_shorthand30=set_shorthand();

            	    state._fsp--;

            	    adaptor.addChild(root_0, set_shorthand30.getTree());

            	    }
            	    break;
            	case 4 :
            	    dbg.enterAlt(4);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:112:4: mnemonic
            	    {
            	    dbg.location(112,4);
            	    pushFollow(FOLLOW_mnemonic_in_set_specification580);
            	    mnemonic31=mnemonic();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mnemonic31.getTree());

            	    }
            	    break;
            	case 5 :
            	    dbg.enterAlt(5);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:113:4: case_sensitive_string
            	    {
            	    dbg.location(113,4);
            	    pushFollow(FOLLOW_case_sensitive_string_in_set_specification586);
            	    case_sensitive_string32=case_sensitive_string();

            	    state._fsp--;

            	    adaptor.addChild(root_0, case_sensitive_string32.getTree());

            	    }
            	    break;
            	case 6 :
            	    dbg.enterAlt(6);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:114:4: case_insensitive_string
            	    {
            	    dbg.location(114,4);
            	    pushFollow(FOLLOW_case_insensitive_string_in_set_specification591);
            	    case_insensitive_string33=case_insensitive_string();

            	    state._fsp--;

            	    adaptor.addChild(root_0, case_insensitive_string33.getTree());

            	    }
            	    break;
            	case 7 :
            	    dbg.enterAlt(7);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:115:4: byte_range
            	    {
            	    dbg.location(115,4);
            	    pushFollow(FOLLOW_byte_range_in_set_specification596);
            	    byte_range34=byte_range();

            	    state._fsp--;

            	    adaptor.addChild(root_0, byte_range34.getTree());

            	    }
            	    break;
            	case 8 :
            	    dbg.enterAlt(8);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:116:4: all_bitmask
            	    {
            	    dbg.location(116,4);
            	    pushFollow(FOLLOW_all_bitmask_in_set_specification601);
            	    all_bitmask35=all_bitmask();

            	    state._fsp--;

            	    adaptor.addChild(root_0, all_bitmask35.getTree());

            	    }
            	    break;
            	case 9 :
            	    dbg.enterAlt(9);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:117:4: any_bitmask
            	    {
            	    dbg.location(117,4);
            	    pushFollow(FOLLOW_any_bitmask_in_set_specification606);
            	    any_bitmask36=any_bitmask();

            	    state._fsp--;

            	    adaptor.addChild(root_0, any_bitmask36.getTree());

            	    }
            	    break;
            	case 10 :
            	    dbg.enterAlt(10);

            	    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:118:4: byte_set
            	    {
            	    dbg.location(118,4);
            	    pushFollow(FOLLOW_byte_set_in_set_specification611);
            	    byte_set37=byte_set();

            	    state._fsp--;

            	    adaptor.addChild(root_0, byte_set37.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        dbg.recognitionException(eee);

                        throw eee;
                }
                cnt10++;
            } while (true);
            } finally {dbg.exitSubRule(10);}


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
        }
        dbg.location(120, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "set_specification");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "set_specification"

    public static class byte_range_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "byte_range"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:123:1: byte_range : r1= range_values RANGE_SEPARATOR r2= range_values -> ^( RANGE $r1 $r2) ;
    public final regularExpressionParser.byte_range_return byte_range() throws RecognitionException {
        regularExpressionParser.byte_range_return retval = new regularExpressionParser.byte_range_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token RANGE_SEPARATOR38=null;
        regularExpressionParser.range_values_return r1 = null;

        regularExpressionParser.range_values_return r2 = null;


        Object RANGE_SEPARATOR38_tree=null;
        RewriteRuleTokenStream stream_RANGE_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token RANGE_SEPARATOR");
        RewriteRuleSubtreeStream stream_range_values=new RewriteRuleSubtreeStream(adaptor,"rule range_values");
        try { dbg.enterRule(getGrammarFileName(), "byte_range");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(123, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:124:2: (r1= range_values RANGE_SEPARATOR r2= range_values -> ^( RANGE $r1 $r2) )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:124:4: r1= range_values RANGE_SEPARATOR r2= range_values
            {
            dbg.location(124,6);
            pushFollow(FOLLOW_range_values_in_byte_range630);
            r1=range_values();

            state._fsp--;

            stream_range_values.add(r1.getTree());
            dbg.location(125,3);
            RANGE_SEPARATOR38=(Token)match(input,RANGE_SEPARATOR,FOLLOW_RANGE_SEPARATOR_in_byte_range634);  
            stream_RANGE_SEPARATOR.add(RANGE_SEPARATOR38);

            dbg.location(126,5);
            pushFollow(FOLLOW_range_values_in_byte_range641);
            r2=range_values();

            state._fsp--;

            stream_range_values.add(r2.getTree());


            // AST REWRITE
            // elements: r1, r2
            // token labels: 
            // rule labels: retval, r1, r2
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);
            RewriteRuleSubtreeStream stream_r1=new RewriteRuleSubtreeStream(adaptor,"token r1",r1!=null?r1.tree:null);
            RewriteRuleSubtreeStream stream_r2=new RewriteRuleSubtreeStream(adaptor,"token r2",r2!=null?r2.tree:null);

            root_0 = (Object)adaptor.nil();
            // 126:21: -> ^( RANGE $r1 $r2)
            {
                dbg.location(126,24);
                // /home/matt/dev/search/byteseek/antlr/regularExpression.g:126:24: ^( RANGE $r1 $r2)
                {
                Object root_1 = (Object)adaptor.nil();
                dbg.location(126,26);
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_1);

                dbg.location(126,32);
                adaptor.addChild(root_1, stream_r1.nextTree());
                dbg.location(126,36);
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
        }
        dbg.location(127, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "byte_range");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "byte_range"

    public static class range_values_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "range_values"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:130:1: range_values : ( BYTE | CASE_SENSITIVE_STRING );
    public final regularExpressionParser.range_values_return range_values() throws RecognitionException {
        regularExpressionParser.range_values_return retval = new regularExpressionParser.range_values_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set39=null;

        Object set39_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "range_values");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(130, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:131:2: ( BYTE | CASE_SENSITIVE_STRING )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(131,2);
            set39=(Token)input.LT(1);
            if ( input.LA(1)==BYTE||input.LA(1)==CASE_SENSITIVE_STRING ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set39));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                dbg.recognitionException(mse);
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
        }
        dbg.location(133, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "range_values");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "range_values"

    public static class all_bitmask_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "all_bitmask"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:136:1: all_bitmask : AMPERSAND BYTE -> ^( ALL_BITMASK BYTE ) ;
    public final regularExpressionParser.all_bitmask_return all_bitmask() throws RecognitionException {
        regularExpressionParser.all_bitmask_return retval = new regularExpressionParser.all_bitmask_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AMPERSAND40=null;
        Token BYTE41=null;

        Object AMPERSAND40_tree=null;
        Object BYTE41_tree=null;
        RewriteRuleTokenStream stream_AMPERSAND=new RewriteRuleTokenStream(adaptor,"token AMPERSAND");
        RewriteRuleTokenStream stream_BYTE=new RewriteRuleTokenStream(adaptor,"token BYTE");

        try { dbg.enterRule(getGrammarFileName(), "all_bitmask");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(136, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:137:2: ( AMPERSAND BYTE -> ^( ALL_BITMASK BYTE ) )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:137:4: AMPERSAND BYTE
            {
            dbg.location(137,4);
            AMPERSAND40=(Token)match(input,AMPERSAND,FOLLOW_AMPERSAND_in_all_bitmask684);  
            stream_AMPERSAND.add(AMPERSAND40);

            dbg.location(137,14);
            BYTE41=(Token)match(input,BYTE,FOLLOW_BYTE_in_all_bitmask686);  
            stream_BYTE.add(BYTE41);



            // AST REWRITE
            // elements: BYTE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 137:20: -> ^( ALL_BITMASK BYTE )
            {
                dbg.location(137,23);
                // /home/matt/dev/search/byteseek/antlr/regularExpression.g:137:23: ^( ALL_BITMASK BYTE )
                {
                Object root_1 = (Object)adaptor.nil();
                dbg.location(137,25);
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ALL_BITMASK, "ALL_BITMASK"), root_1);

                dbg.location(137,37);
                adaptor.addChild(root_1, stream_BYTE.nextNode());

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
        }
        dbg.location(138, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "all_bitmask");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "all_bitmask"

    public static class any_bitmask_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "any_bitmask"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:140:1: any_bitmask : TILDE BYTE -> ^( ANY_BITMASK BYTE ) ;
    public final regularExpressionParser.any_bitmask_return any_bitmask() throws RecognitionException {
        regularExpressionParser.any_bitmask_return retval = new regularExpressionParser.any_bitmask_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token TILDE42=null;
        Token BYTE43=null;

        Object TILDE42_tree=null;
        Object BYTE43_tree=null;
        RewriteRuleTokenStream stream_BYTE=new RewriteRuleTokenStream(adaptor,"token BYTE");
        RewriteRuleTokenStream stream_TILDE=new RewriteRuleTokenStream(adaptor,"token TILDE");

        try { dbg.enterRule(getGrammarFileName(), "any_bitmask");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(140, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:141:2: ( TILDE BYTE -> ^( ANY_BITMASK BYTE ) )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:141:4: TILDE BYTE
            {
            dbg.location(141,4);
            TILDE42=(Token)match(input,TILDE,FOLLOW_TILDE_in_any_bitmask706);  
            stream_TILDE.add(TILDE42);

            dbg.location(141,10);
            BYTE43=(Token)match(input,BYTE,FOLLOW_BYTE_in_any_bitmask708);  
            stream_BYTE.add(BYTE43);



            // AST REWRITE
            // elements: BYTE
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 141:16: -> ^( ANY_BITMASK BYTE )
            {
                dbg.location(141,19);
                // /home/matt/dev/search/byteseek/antlr/regularExpression.g:141:19: ^( ANY_BITMASK BYTE )
                {
                Object root_1 = (Object)adaptor.nil();
                dbg.location(141,21);
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(ANY_BITMASK, "ANY_BITMASK"), root_1);

                dbg.location(141,33);
                adaptor.addChild(root_1, stream_BYTE.nextNode());

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
        }
        dbg.location(142, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "any_bitmask");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "any_bitmask"

    public static class mnemonic_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mnemonic"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:144:1: mnemonic : (m= SET_ASCII -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) ) | m= SET_PRINT -> ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) ) | m= SET_GRAPH -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) ) | m= SET_WORD -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] ) | m= SET_ALPHANUM -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) | m= SET_ALPHA -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) | m= SET_UPPER -> ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) | m= SET_LOWER -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ) | m= SET_PUNCT -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) ) | m= SET_HEXDIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) ) | m= SET_DIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) | m= SET_WHITESPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) | m= SET_BLANK -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] ) | m= SET_SPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) | m= SET_TAB -> BYTE[$m,\"09\"] | m= SET_NEWLINE -> BYTE[$m,\"0a\"] | m= SET_RETURN -> BYTE[$m,\"0d\"] | m= SET_CONTROL -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] ) );
    public final regularExpressionParser.mnemonic_return mnemonic() throws RecognitionException {
        regularExpressionParser.mnemonic_return retval = new regularExpressionParser.mnemonic_return();
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
        RewriteRuleTokenStream stream_SET_HEXDIGIT=new RewriteRuleTokenStream(adaptor,"token SET_HEXDIGIT");
        RewriteRuleTokenStream stream_SET_BLANK=new RewriteRuleTokenStream(adaptor,"token SET_BLANK");
        RewriteRuleTokenStream stream_SET_PUNCT=new RewriteRuleTokenStream(adaptor,"token SET_PUNCT");
        RewriteRuleTokenStream stream_SET_CONTROL=new RewriteRuleTokenStream(adaptor,"token SET_CONTROL");
        RewriteRuleTokenStream stream_SET_DIGIT=new RewriteRuleTokenStream(adaptor,"token SET_DIGIT");

        try { dbg.enterRule(getGrammarFileName(), "mnemonic");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(144, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:145:2: (m= SET_ASCII -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) ) | m= SET_PRINT -> ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) ) | m= SET_GRAPH -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) ) | m= SET_WORD -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] ) | m= SET_ALPHANUM -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) | m= SET_ALPHA -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) | m= SET_UPPER -> ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) | m= SET_LOWER -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ) | m= SET_PUNCT -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) ) | m= SET_HEXDIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) ) | m= SET_DIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) | m= SET_WHITESPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) | m= SET_BLANK -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] ) | m= SET_SPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) | m= SET_TAB -> BYTE[$m,\"09\"] | m= SET_NEWLINE -> BYTE[$m,\"0a\"] | m= SET_RETURN -> BYTE[$m,\"0d\"] | m= SET_CONTROL -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] ) )
            int alt11=18;
            try { dbg.enterDecision(11);

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

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(11);}

            switch (alt11) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:145:4: m= SET_ASCII
                    {
                    dbg.location(145,5);
                    m=(Token)match(input,SET_ASCII,FOLLOW_SET_ASCII_in_mnemonic731);  
                    stream_SET_ASCII.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 145:17: -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) )
                    {
                        dbg.location(145,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:145:20: ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(145,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(145,26);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:145:26: ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(145,28);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(145,34);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "00"));
                        dbg.location(145,48);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "7f"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:146:4: m= SET_PRINT
                    {
                    dbg.location(146,5);
                    m=(Token)match(input,SET_PRINT,FOLLOW_SET_PRINT_in_mnemonic756);  
                    stream_SET_PRINT.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 146:17: -> ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) )
                    {
                        dbg.location(146,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:146:20: ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(146,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(146,26);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:146:26: ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(146,28);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(146,34);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "' '"));
                        dbg.location(146,49);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'~'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:147:4: m= SET_GRAPH
                    {
                    dbg.location(147,5);
                    m=(Token)match(input,SET_GRAPH,FOLLOW_SET_GRAPH_in_mnemonic781);  
                    stream_SET_GRAPH.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 147:17: -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) )
                    {
                        dbg.location(147,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:147:20: ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(147,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(147,26);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:147:26: ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(147,28);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(147,34);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'!'"));
                        dbg.location(147,49);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'~'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:148:4: m= SET_WORD
                    {
                    dbg.location(148,5);
                    m=(Token)match(input,SET_WORD,FOLLOW_SET_WORD_in_mnemonic806);  
                    stream_SET_WORD.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 148:16: -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] )
                    {
                        dbg.location(148,19);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:148:19: ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(148,21);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(148,25);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:148:25: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(148,27);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(148,33);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'0'"));
                        dbg.location(148,48);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(148,64);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:148:64: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(148,66);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(148,72);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
                        dbg.location(148,87);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(148,103);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:148:103: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(148,105);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(148,111);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
                        dbg.location(148,126);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(148,142);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "'_'"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:149:4: m= SET_ALPHANUM
                    {
                    dbg.location(149,5);
                    m=(Token)match(input,SET_ALPHANUM,FOLLOW_SET_ALPHANUM_in_mnemonic853);  
                    stream_SET_ALPHANUM.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 149:20: -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                    {
                        dbg.location(149,23);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:149:23: ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(149,25);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(149,29);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:149:29: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(149,31);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(149,37);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
                        dbg.location(149,52);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(149,68);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:149:68: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(149,70);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(149,76);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
                        dbg.location(149,91);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(149,107);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:149:107: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(149,109);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(149,115);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'0'"));
                        dbg.location(149,130);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 6 :
                    dbg.enterAlt(6);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:150:4: m= SET_ALPHA
                    {
                    dbg.location(150,5);
                    m=(Token)match(input,SET_ALPHA,FOLLOW_SET_ALPHA_in_mnemonic897);  
                    stream_SET_ALPHA.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 150:17: -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                    {
                        dbg.location(150,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:150:20: ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(150,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(150,26);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:150:26: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(150,28);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(150,34);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
                        dbg.location(150,49);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(150,65);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:150:65: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(150,67);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(150,73);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
                        dbg.location(150,88);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 7 :
                    dbg.enterAlt(7);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:151:4: m= SET_UPPER
                    {
                    dbg.location(151,5);
                    m=(Token)match(input,SET_UPPER,FOLLOW_SET_UPPER_in_mnemonic931);  
                    stream_SET_UPPER.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 151:17: -> ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                    {
                        dbg.location(151,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:151:20: ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(151,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(151,26);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:151:26: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(151,28);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(151,34);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
                        dbg.location(151,49);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 8 :
                    dbg.enterAlt(8);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:152:4: m= SET_LOWER
                    {
                    dbg.location(152,5);
                    m=(Token)match(input,SET_LOWER,FOLLOW_SET_LOWER_in_mnemonic955);  
                    stream_SET_LOWER.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 152:17: -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) )
                    {
                        dbg.location(152,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:152:20: ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(152,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(152,26);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:152:26: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(152,28);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(152,34);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
                        dbg.location(152,49);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 9 :
                    dbg.enterAlt(9);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:153:4: m= SET_PUNCT
                    {
                    dbg.location(153,5);
                    m=(Token)match(input,SET_PUNCT,FOLLOW_SET_PUNCT_in_mnemonic979);  
                    stream_SET_PUNCT.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 153:17: -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) )
                    {
                        dbg.location(153,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:153:20: ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(153,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(153,26);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:153:26: ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(153,28);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(153,34);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'!'"));
                        dbg.location(153,49);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'/'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(153,65);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:153:65: ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(153,67);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(153,73);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "':'"));
                        dbg.location(153,88);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'@'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(153,104);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:153:104: ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(153,106);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(153,112);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'['"));
                        dbg.location(153,127);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'`'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(153,143);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:153:143: ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(153,145);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(153,151);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'{'"));
                        dbg.location(153,166);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'~'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 10 :
                    dbg.enterAlt(10);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:154:4: m= SET_HEXDIGIT
                    {
                    dbg.location(154,5);
                    m=(Token)match(input,SET_HEXDIGIT,FOLLOW_SET_HEXDIGIT_in_mnemonic1033);  
                    stream_SET_HEXDIGIT.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 154:20: -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) )
                    {
                        dbg.location(154,23);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:154:23: ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(154,25);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(154,29);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:154:29: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(154,31);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(154,37);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'0'"));
                        dbg.location(154,52);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(154,68);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:154:68: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(154,70);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(154,76);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
                        dbg.location(154,91);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'f'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(154,107);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:154:107: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(154,109);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(154,115);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
                        dbg.location(154,130);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'F'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 11 :
                    dbg.enterAlt(11);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:155:4: m= SET_DIGIT
                    {
                    dbg.location(155,5);
                    m=(Token)match(input,SET_DIGIT,FOLLOW_SET_DIGIT_in_mnemonic1077);  
                    stream_SET_DIGIT.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 155:17: -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                    {
                        dbg.location(155,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:155:20: ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(155,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(155,26);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:155:26: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(155,28);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(155,34);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'0'"));
                        dbg.location(155,49);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 12 :
                    dbg.enterAlt(12);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:156:4: m= SET_WHITESPACE
                    {
                    dbg.location(156,5);
                    m=(Token)match(input,SET_WHITESPACE,FOLLOW_SET_WHITESPACE_in_mnemonic1101);  
                    stream_SET_WHITESPACE.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 156:21: -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                    {
                        dbg.location(156,24);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:156:24: ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(156,26);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(156,30);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "09"));
                        dbg.location(156,44);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0a"));
                        dbg.location(156,58);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0d"));
                        dbg.location(156,72);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "' '"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 13 :
                    dbg.enterAlt(13);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:157:4: m= SET_BLANK
                    {
                    dbg.location(157,5);
                    m=(Token)match(input,SET_BLANK,FOLLOW_SET_BLANK_in_mnemonic1126);  
                    stream_SET_BLANK.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 157:17: -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] )
                    {
                        dbg.location(157,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:157:20: ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(157,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(157,26);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "09"));
                        dbg.location(157,40);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "' '"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 14 :
                    dbg.enterAlt(14);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:158:4: m= SET_SPACE
                    {
                    dbg.location(158,5);
                    m=(Token)match(input,SET_SPACE,FOLLOW_SET_SPACE_in_mnemonic1146);  
                    stream_SET_SPACE.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 158:17: -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                    {
                        dbg.location(158,20);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:158:20: ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(158,22);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(158,26);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "09"));
                        dbg.location(158,40);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0a"));
                        dbg.location(158,54);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0b"));
                        dbg.location(158,68);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0c"));
                        dbg.location(158,82);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0d"));
                        dbg.location(158,96);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "' '"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 15 :
                    dbg.enterAlt(15);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:159:4: m= SET_TAB
                    {
                    dbg.location(159,5);
                    m=(Token)match(input,SET_TAB,FOLLOW_SET_TAB_in_mnemonic1178);  
                    stream_SET_TAB.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 159:15: -> BYTE[$m,\"09\"]
                    {
                        dbg.location(159,18);
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, m, "09"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 16 :
                    dbg.enterAlt(16);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:160:4: m= SET_NEWLINE
                    {
                    dbg.location(160,5);
                    m=(Token)match(input,SET_NEWLINE,FOLLOW_SET_NEWLINE_in_mnemonic1191);  
                    stream_SET_NEWLINE.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 160:19: -> BYTE[$m,\"0a\"]
                    {
                        dbg.location(160,22);
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, m, "0a"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 17 :
                    dbg.enterAlt(17);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:161:4: m= SET_RETURN
                    {
                    dbg.location(161,5);
                    m=(Token)match(input,SET_RETURN,FOLLOW_SET_RETURN_in_mnemonic1204);  
                    stream_SET_RETURN.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 161:18: -> BYTE[$m,\"0d\"]
                    {
                        dbg.location(161,21);
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, m, "0d"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 18 :
                    dbg.enterAlt(18);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:162:4: m= SET_CONTROL
                    {
                    dbg.location(162,5);
                    m=(Token)match(input,SET_CONTROL,FOLLOW_SET_CONTROL_in_mnemonic1217);  
                    stream_SET_CONTROL.add(m);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 162:19: -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] )
                    {
                        dbg.location(162,22);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:162:22: ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(162,24);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(162,28);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:162:28: ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(162,30);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(162,36);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "00"));
                        dbg.location(162,50);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "1f"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(162,65);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "7f"));

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
        }
        dbg.location(163, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "mnemonic");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "mnemonic"

    public static class byte_shorthand_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "byte_shorthand"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:166:1: byte_shorthand : (sh= TAB_SHORTHAND -> BYTE[$sh,\"09\"] | sh= NEWLINE_SHORTHAND -> BYTE[$sh,\"0a\"] | sh= VERTICAL_TAB_SHORTHAND -> BYTE[$sh,\"0b\"] | sh= FORM_FEED_SHORTHAND -> BYTE[$sh,\"0c\"] | sh= RETURN_SHORTHAND -> BYTE[$sh,\"0d\"] | sh= ESCAPE_SHORTHAND -> BYTE[$sh,\"1b\"] );
    public final regularExpressionParser.byte_shorthand_return byte_shorthand() throws RecognitionException {
        regularExpressionParser.byte_shorthand_return retval = new regularExpressionParser.byte_shorthand_return();
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

        try { dbg.enterRule(getGrammarFileName(), "byte_shorthand");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(166, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:167:2: (sh= TAB_SHORTHAND -> BYTE[$sh,\"09\"] | sh= NEWLINE_SHORTHAND -> BYTE[$sh,\"0a\"] | sh= VERTICAL_TAB_SHORTHAND -> BYTE[$sh,\"0b\"] | sh= FORM_FEED_SHORTHAND -> BYTE[$sh,\"0c\"] | sh= RETURN_SHORTHAND -> BYTE[$sh,\"0d\"] | sh= ESCAPE_SHORTHAND -> BYTE[$sh,\"1b\"] )
            int alt12=6;
            try { dbg.enterDecision(12);

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

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(12);}

            switch (alt12) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:167:4: sh= TAB_SHORTHAND
                    {
                    dbg.location(167,6);
                    sh=(Token)match(input,TAB_SHORTHAND,FOLLOW_TAB_SHORTHAND_in_byte_shorthand1252);  
                    stream_TAB_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 167:22: -> BYTE[$sh,\"09\"]
                    {
                        dbg.location(167,25);
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "09"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:168:4: sh= NEWLINE_SHORTHAND
                    {
                    dbg.location(168,6);
                    sh=(Token)match(input,NEWLINE_SHORTHAND,FOLLOW_NEWLINE_SHORTHAND_in_byte_shorthand1265);  
                    stream_NEWLINE_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 168:26: -> BYTE[$sh,\"0a\"]
                    {
                        dbg.location(168,29);
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "0a"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:169:4: sh= VERTICAL_TAB_SHORTHAND
                    {
                    dbg.location(169,6);
                    sh=(Token)match(input,VERTICAL_TAB_SHORTHAND,FOLLOW_VERTICAL_TAB_SHORTHAND_in_byte_shorthand1278);  
                    stream_VERTICAL_TAB_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 169:30: -> BYTE[$sh,\"0b\"]
                    {
                        dbg.location(169,33);
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "0b"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:170:4: sh= FORM_FEED_SHORTHAND
                    {
                    dbg.location(170,6);
                    sh=(Token)match(input,FORM_FEED_SHORTHAND,FOLLOW_FORM_FEED_SHORTHAND_in_byte_shorthand1290);  
                    stream_FORM_FEED_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 170:28: -> BYTE[$sh,\"0c\"]
                    {
                        dbg.location(170,31);
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "0c"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:171:4: sh= RETURN_SHORTHAND
                    {
                    dbg.location(171,6);
                    sh=(Token)match(input,RETURN_SHORTHAND,FOLLOW_RETURN_SHORTHAND_in_byte_shorthand1303);  
                    stream_RETURN_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 171:25: -> BYTE[$sh,\"0d\"]
                    {
                        dbg.location(171,28);
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "0d"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 6 :
                    dbg.enterAlt(6);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:172:4: sh= ESCAPE_SHORTHAND
                    {
                    dbg.location(172,6);
                    sh=(Token)match(input,ESCAPE_SHORTHAND,FOLLOW_ESCAPE_SHORTHAND_in_byte_shorthand1316);  
                    stream_ESCAPE_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 172:25: -> BYTE[$sh,\"1b\"]
                    {
                        dbg.location(172,28);
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "1b"));

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
        }
        dbg.location(173, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "byte_shorthand");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "byte_shorthand"

    public static class set_shorthand_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "set_shorthand"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:176:1: set_shorthand : (sh= DIGIT_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) | sh= NOT_DIGIT_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) | sh= WORD_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) | sh= NOT_WORD_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) | sh= WHITE_SPACE_SHORTHAND -> ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) | sh= NOT_WHITE_SPACE_SHORTHAND -> ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) );
    public final regularExpressionParser.set_shorthand_return set_shorthand() throws RecognitionException {
        regularExpressionParser.set_shorthand_return retval = new regularExpressionParser.set_shorthand_return();
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

        try { dbg.enterRule(getGrammarFileName(), "set_shorthand");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(176, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:177:2: (sh= DIGIT_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) | sh= NOT_DIGIT_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) | sh= WORD_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) | sh= NOT_WORD_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) | sh= WHITE_SPACE_SHORTHAND -> ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) | sh= NOT_WHITE_SPACE_SHORTHAND -> ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) )
            int alt13=6;
            try { dbg.enterDecision(13);

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

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(13);}

            switch (alt13) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:177:4: sh= DIGIT_SHORTHAND
                    {
                    dbg.location(177,6);
                    sh=(Token)match(input,DIGIT_SHORTHAND,FOLLOW_DIGIT_SHORTHAND_in_set_shorthand1338);  
                    stream_DIGIT_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 177:24: -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                    {
                        dbg.location(177,27);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:177:27: ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(177,29);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(177,33);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:177:33: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(177,35);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(177,41);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'0'"));
                        dbg.location(177,57);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:178:4: sh= NOT_DIGIT_SHORTHAND
                    {
                    dbg.location(178,6);
                    sh=(Token)match(input,NOT_DIGIT_SHORTHAND,FOLLOW_NOT_DIGIT_SHORTHAND_in_set_shorthand1362);  
                    stream_NOT_DIGIT_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 178:28: -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                    {
                        dbg.location(178,31);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:178:31: ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(178,33);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INVERTED_SET, "INVERTED_SET"), root_1);

                        dbg.location(178,46);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:178:46: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(178,48);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(178,54);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'0'"));
                        dbg.location(178,70);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:179:4: sh= WORD_SHORTHAND
                    {
                    dbg.location(179,6);
                    sh=(Token)match(input,WORD_SHORTHAND,FOLLOW_WORD_SHORTHAND_in_set_shorthand1386);  
                    stream_WORD_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 179:23: -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                    {
                        dbg.location(179,26);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:179:26: ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(179,28);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(179,32);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:179:32: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(179,34);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(179,40);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'0'"));
                        dbg.location(179,56);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(179,73);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:179:73: ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(179,75);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(179,81);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'a'"));
                        dbg.location(179,97);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(179,114);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:179:114: ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(179,116);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(179,122);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'A'"));
                        dbg.location(179,138);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(179,155);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "'_'"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:180:4: sh= NOT_WORD_SHORTHAND
                    {
                    dbg.location(180,6);
                    sh=(Token)match(input,NOT_WORD_SHORTHAND,FOLLOW_NOT_WORD_SHORTHAND_in_set_shorthand1433);  
                    stream_NOT_WORD_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 180:27: -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                    {
                        dbg.location(180,30);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:180:30: ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(180,32);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INVERTED_SET, "INVERTED_SET"), root_1);

                        dbg.location(180,45);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:180:45: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(180,47);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(180,53);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'0'"));
                        dbg.location(180,69);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(180,86);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:180:86: ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(180,88);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(180,94);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'a'"));
                        dbg.location(180,110);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(180,127);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:180:127: ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        dbg.location(180,129);
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        dbg.location(180,135);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'A'"));
                        dbg.location(180,151);
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        dbg.location(180,168);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "'_'"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 5 :
                    dbg.enterAlt(5);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:181:4: sh= WHITE_SPACE_SHORTHAND
                    {
                    dbg.location(181,6);
                    sh=(Token)match(input,WHITE_SPACE_SHORTHAND,FOLLOW_WHITE_SPACE_SHORTHAND_in_set_shorthand1480);  
                    stream_WHITE_SPACE_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 181:29: -> ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                    {
                        dbg.location(181,32);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:181:32: ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(181,34);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        dbg.location(181,38);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "09"));
                        dbg.location(181,53);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "0a"));
                        dbg.location(181,68);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "0d"));
                        dbg.location(181,83);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "' '"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 6 :
                    dbg.enterAlt(6);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:182:4: sh= NOT_WHITE_SPACE_SHORTHAND
                    {
                    dbg.location(182,6);
                    sh=(Token)match(input,NOT_WHITE_SPACE_SHORTHAND,FOLLOW_NOT_WHITE_SPACE_SHORTHAND_in_set_shorthand1505);  
                    stream_NOT_WHITE_SPACE_SHORTHAND.add(sh);



                    // AST REWRITE
                    // elements: 
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 182:33: -> ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                    {
                        dbg.location(182,36);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:182:36: ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(182,38);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INVERTED_SET, "INVERTED_SET"), root_1);

                        dbg.location(182,51);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "09"));
                        dbg.location(182,66);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "0a"));
                        dbg.location(182,81);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "0d"));
                        dbg.location(182,96);
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "' '"));

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
        }
        dbg.location(183, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "set_shorthand");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "set_shorthand"

    public static class case_insensitive_string_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "case_insensitive_string"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:185:1: case_insensitive_string : CASE_INSENSITIVE_STRING ;
    public final regularExpressionParser.case_insensitive_string_return case_insensitive_string() throws RecognitionException {
        regularExpressionParser.case_insensitive_string_return retval = new regularExpressionParser.case_insensitive_string_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CASE_INSENSITIVE_STRING44=null;

        Object CASE_INSENSITIVE_STRING44_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "case_insensitive_string");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(185, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:186:2: ( CASE_INSENSITIVE_STRING )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:186:4: CASE_INSENSITIVE_STRING
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(186,4);
            CASE_INSENSITIVE_STRING44=(Token)match(input,CASE_INSENSITIVE_STRING,FOLLOW_CASE_INSENSITIVE_STRING_in_case_insensitive_string1535); 
            CASE_INSENSITIVE_STRING44_tree = (Object)adaptor.create(CASE_INSENSITIVE_STRING44);
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
        }
        dbg.location(187, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "case_insensitive_string");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "case_insensitive_string"

    public static class case_sensitive_string_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "case_sensitive_string"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:190:1: case_sensitive_string : CASE_SENSITIVE_STRING ;
    public final regularExpressionParser.case_sensitive_string_return case_sensitive_string() throws RecognitionException {
        regularExpressionParser.case_sensitive_string_return retval = new regularExpressionParser.case_sensitive_string_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CASE_SENSITIVE_STRING45=null;

        Object CASE_SENSITIVE_STRING45_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "case_sensitive_string");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(190, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:191:2: ( CASE_SENSITIVE_STRING )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:191:4: CASE_SENSITIVE_STRING
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(191,4);
            CASE_SENSITIVE_STRING45=(Token)match(input,CASE_SENSITIVE_STRING,FOLLOW_CASE_SENSITIVE_STRING_in_case_sensitive_string1549); 
            CASE_SENSITIVE_STRING45_tree = (Object)adaptor.create(CASE_SENSITIVE_STRING45);
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
        }
        dbg.location(192, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "case_sensitive_string");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "case_sensitive_string"

    public static class group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "group"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:195:1: group : OPEN regex CLOSE -> regex ;
    public final regularExpressionParser.group_return group() throws RecognitionException {
        regularExpressionParser.group_return retval = new regularExpressionParser.group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OPEN46=null;
        Token CLOSE48=null;
        regularExpressionParser.regex_return regex47 = null;


        Object OPEN46_tree=null;
        Object CLOSE48_tree=null;
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleTokenStream stream_CLOSE=new RewriteRuleTokenStream(adaptor,"token CLOSE");
        RewriteRuleSubtreeStream stream_regex=new RewriteRuleSubtreeStream(adaptor,"rule regex");
        try { dbg.enterRule(getGrammarFileName(), "group");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(195, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:195:7: ( OPEN regex CLOSE -> regex )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:195:9: OPEN regex CLOSE
            {
            dbg.location(195,9);
            OPEN46=(Token)match(input,OPEN,FOLLOW_OPEN_in_group1562);  
            stream_OPEN.add(OPEN46);

            dbg.location(195,14);
            pushFollow(FOLLOW_regex_in_group1564);
            regex47=regex();

            state._fsp--;

            stream_regex.add(regex47.getTree());
            dbg.location(195,20);
            CLOSE48=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_group1566);  
            stream_CLOSE.add(CLOSE48);



            // AST REWRITE
            // elements: regex
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

            root_0 = (Object)adaptor.nil();
            // 195:26: -> regex
            {
                dbg.location(195,29);
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
        }
        dbg.location(196, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "group");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "group"

    public static class quantifier_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "quantifier"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:199:1: quantifier : ( optional | zero_to_many | one_to_many | repeat ) ;
    public final regularExpressionParser.quantifier_return quantifier() throws RecognitionException {
        regularExpressionParser.quantifier_return retval = new regularExpressionParser.quantifier_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        regularExpressionParser.optional_return optional49 = null;

        regularExpressionParser.zero_to_many_return zero_to_many50 = null;

        regularExpressionParser.one_to_many_return one_to_many51 = null;

        regularExpressionParser.repeat_return repeat52 = null;



        try { dbg.enterRule(getGrammarFileName(), "quantifier");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(199, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:200:2: ( ( optional | zero_to_many | one_to_many | repeat ) )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:201:2: ( optional | zero_to_many | one_to_many | repeat )
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(201,2);
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:201:2: ( optional | zero_to_many | one_to_many | repeat )
            int alt14=4;
            try { dbg.enterSubRule(14);
            try { dbg.enterDecision(14);

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

                dbg.recognitionException(nvae);
                throw nvae;
            }

            } finally {dbg.exitDecision(14);}

            switch (alt14) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:201:4: optional
                    {
                    dbg.location(201,4);
                    pushFollow(FOLLOW_optional_in_quantifier1587);
                    optional49=optional();

                    state._fsp--;

                    adaptor.addChild(root_0, optional49.getTree());

                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:202:5: zero_to_many
                    {
                    dbg.location(202,5);
                    pushFollow(FOLLOW_zero_to_many_in_quantifier1596);
                    zero_to_many50=zero_to_many();

                    state._fsp--;

                    adaptor.addChild(root_0, zero_to_many50.getTree());

                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:203:5: one_to_many
                    {
                    dbg.location(203,5);
                    pushFollow(FOLLOW_one_to_many_in_quantifier1606);
                    one_to_many51=one_to_many();

                    state._fsp--;

                    adaptor.addChild(root_0, one_to_many51.getTree());

                    }
                    break;
                case 4 :
                    dbg.enterAlt(4);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:204:5: repeat
                    {
                    dbg.location(204,5);
                    pushFollow(FOLLOW_repeat_in_quantifier1612);
                    repeat52=repeat();

                    state._fsp--;

                    adaptor.addChild(root_0, repeat52.getTree());

                    }
                    break;

            }
            } finally {dbg.exitSubRule(14);}


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
        }
        dbg.location(206, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "quantifier");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "quantifier"

    public static class repeat_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "repeat"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:209:1: repeat : OPEN_CURLY n1= NUMBER ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) ) CLOSE_CURLY ;
    public final regularExpressionParser.repeat_return repeat() throws RecognitionException {
        regularExpressionParser.repeat_return retval = new regularExpressionParser.repeat_return();
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

        try { dbg.enterRule(getGrammarFileName(), "repeat");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(209, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:209:8: ( OPEN_CURLY n1= NUMBER ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) ) CLOSE_CURLY )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:209:10: OPEN_CURLY n1= NUMBER ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) ) CLOSE_CURLY
            {
            dbg.location(209,10);
            OPEN_CURLY53=(Token)match(input,OPEN_CURLY,FOLLOW_OPEN_CURLY_in_repeat1628);  
            stream_OPEN_CURLY.add(OPEN_CURLY53);

            dbg.location(209,23);
            n1=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_repeat1632);  
            stream_NUMBER.add(n1);

            dbg.location(210,2);
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:210:2: ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) )
            int alt15=3;
            try { dbg.enterSubRule(15);
            try { dbg.enterDecision(15);

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

                    dbg.recognitionException(nvae);
                    throw nvae;
                }
            }
            else if ( (LA15_0==CLOSE_CURLY) ) {
                alt15=3;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                dbg.recognitionException(nvae);
                throw nvae;
            }
            } finally {dbg.exitDecision(15);}

            switch (alt15) {
                case 1 :
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:211:3: ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) )
                    {
                    dbg.location(211,3);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:211:3: ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:211:5: REPEAT_SEPARATOR n2= NUMBER
                    {
                    dbg.location(211,5);
                    REPEAT_SEPARATOR54=(Token)match(input,REPEAT_SEPARATOR,FOLLOW_REPEAT_SEPARATOR_in_repeat1643);  
                    stream_REPEAT_SEPARATOR.add(REPEAT_SEPARATOR54);

                    dbg.location(211,24);
                    n2=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_repeat1647);  
                    stream_NUMBER.add(n2);



                    // AST REWRITE
                    // elements: n2, n1
                    // token labels: n1, n2
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_n1=new RewriteRuleTokenStream(adaptor,"token n1",n1);
                    RewriteRuleTokenStream stream_n2=new RewriteRuleTokenStream(adaptor,"token n2",n2);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 211:33: -> ^( REPEAT $n1 $n2)
                    {
                        dbg.location(211,36);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:211:36: ^( REPEAT $n1 $n2)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(211,39);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(REPEAT, "REPEAT"), root_1);

                        dbg.location(211,46);
                        adaptor.addChild(root_1, stream_n1.nextNode());
                        dbg.location(211,50);
                        adaptor.addChild(root_1, stream_n2.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;
                case 2 :
                    dbg.enterAlt(2);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:213:3: ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) )
                    {
                    dbg.location(213,3);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:213:3: ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:213:5: REPEAT_SEPARATOR MANY
                    {
                    dbg.location(213,5);
                    REPEAT_SEPARATOR55=(Token)match(input,REPEAT_SEPARATOR,FOLLOW_REPEAT_SEPARATOR_in_repeat1673);  
                    stream_REPEAT_SEPARATOR.add(REPEAT_SEPARATOR55);

                    dbg.location(213,22);
                    MANY56=(Token)match(input,MANY,FOLLOW_MANY_in_repeat1675);  
                    stream_MANY.add(MANY56);



                    // AST REWRITE
                    // elements: MANY, n1
                    // token labels: n1
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_n1=new RewriteRuleTokenStream(adaptor,"token n1",n1);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 213:28: -> ^( REPEAT $n1 MANY )
                    {
                        dbg.location(213,31);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:213:31: ^( REPEAT $n1 MANY )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(213,34);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(REPEAT, "REPEAT"), root_1);

                        dbg.location(213,41);
                        adaptor.addChild(root_1, stream_n1.nextNode());
                        dbg.location(213,45);
                        adaptor.addChild(root_1, stream_MANY.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;
                case 3 :
                    dbg.enterAlt(3);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:215:3: ( -> ^( REPEAT $n1 $n1) )
                    {
                    dbg.location(215,3);
                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:215:3: ( -> ^( REPEAT $n1 $n1) )
                    dbg.enterAlt(1);

                    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:215:8: 
                    {

                    // AST REWRITE
                    // elements: n1, n1
                    // token labels: n1
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_n1=new RewriteRuleTokenStream(adaptor,"token n1",n1);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"token retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 215:8: -> ^( REPEAT $n1 $n1)
                    {
                        dbg.location(215,11);
                        // /home/matt/dev/search/byteseek/antlr/regularExpression.g:215:11: ^( REPEAT $n1 $n1)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        dbg.location(215,14);
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(REPEAT, "REPEAT"), root_1);

                        dbg.location(215,21);
                        adaptor.addChild(root_1, stream_n1.nextNode());
                        dbg.location(215,25);
                        adaptor.addChild(root_1, stream_n1.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;

            }
            } finally {dbg.exitSubRule(15);}

            dbg.location(217,3);
            CLOSE_CURLY57=(Token)match(input,CLOSE_CURLY,FOLLOW_CLOSE_CURLY_in_repeat1724);  
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
        }
        dbg.location(218, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "repeat");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "repeat"

    public static class optional_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "optional"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:221:1: optional : QUESTION_MARK ;
    public final regularExpressionParser.optional_return optional() throws RecognitionException {
        regularExpressionParser.optional_return retval = new regularExpressionParser.optional_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token QUESTION_MARK58=null;

        Object QUESTION_MARK58_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "optional");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(221, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:221:9: ( QUESTION_MARK )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:221:11: QUESTION_MARK
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(221,11);
            QUESTION_MARK58=(Token)match(input,QUESTION_MARK,FOLLOW_QUESTION_MARK_in_optional1735); 
            QUESTION_MARK58_tree = (Object)adaptor.create(QUESTION_MARK58);
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
        }
        dbg.location(222, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "optional");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "optional"

    public static class zero_to_many_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "zero_to_many"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:225:1: zero_to_many : MANY ;
    public final regularExpressionParser.zero_to_many_return zero_to_many() throws RecognitionException {
        regularExpressionParser.zero_to_many_return retval = new regularExpressionParser.zero_to_many_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MANY59=null;

        Object MANY59_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "zero_to_many");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(225, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:226:2: ( MANY )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:226:4: MANY
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(226,4);
            MANY59=(Token)match(input,MANY,FOLLOW_MANY_in_zero_to_many1747); 
            MANY59_tree = (Object)adaptor.create(MANY59);
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
        }
        dbg.location(227, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "zero_to_many");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "zero_to_many"

    public static class one_to_many_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "one_to_many"
    // /home/matt/dev/search/byteseek/antlr/regularExpression.g:230:1: one_to_many : PLUS ;
    public final regularExpressionParser.one_to_many_return one_to_many() throws RecognitionException {
        regularExpressionParser.one_to_many_return retval = new regularExpressionParser.one_to_many_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS60=null;

        Object PLUS60_tree=null;

        try { dbg.enterRule(getGrammarFileName(), "one_to_many");
        if ( getRuleLevel()==0 ) {dbg.commence();}
        incRuleLevel();
        dbg.location(230, 1);

        try {
            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:231:2: ( PLUS )
            dbg.enterAlt(1);

            // /home/matt/dev/search/byteseek/antlr/regularExpression.g:231:4: PLUS
            {
            root_0 = (Object)adaptor.nil();

            dbg.location(231,4);
            PLUS60=(Token)match(input,PLUS,FOLLOW_PLUS_in_one_to_many1760); 
            PLUS60_tree = (Object)adaptor.create(PLUS60);
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
        }
        dbg.location(232, 2);

        }
        finally {
            dbg.exitRule(getGrammarFileName(), "one_to_many");
            decRuleLevel();
            if ( getRuleLevel()==0 ) {dbg.terminate();}
        }

        return retval;
    }
    // $ANTLR end "one_to_many"

    // Delegated rules


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA3 dfa3 = new DFA3(this);
    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA6_eotS =
        "\27\uffff";
    static final String DFA6_eofS =
        "\27\uffff";
    static final String DFA6_minS =
        "\1\16\24\0\2\uffff";
    static final String DFA6_maxS =
        "\1\66\24\0\2\uffff";
    static final String DFA6_acceptS =
        "\25\uffff\1\1\1\2";
    static final String DFA6_specialS =
        "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1"+
        "\16\1\17\1\20\1\21\1\22\1\23\1\24\2\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\1\2\1\3\3\uffff\1\22\1\20\1\21\22\uffff\1\4\1\5\1\6\1\7"+
            "\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\23\1\24",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            ""
    };

    static final short[] DFA6_eot = DFA.unpackEncodedString(DFA6_eotS);
    static final short[] DFA6_eof = DFA.unpackEncodedString(DFA6_eofS);
    static final char[] DFA6_min = DFA.unpackEncodedStringToUnsignedChars(DFA6_minS);
    static final char[] DFA6_max = DFA.unpackEncodedStringToUnsignedChars(DFA6_maxS);
    static final short[] DFA6_accept = DFA.unpackEncodedString(DFA6_acceptS);
    static final short[] DFA6_special = DFA.unpackEncodedString(DFA6_specialS);
    static final short[][] DFA6_transition;

    static {
        int numStates = DFA6_transitionS.length;
        DFA6_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA6_transition[i] = DFA.unpackEncodedString(DFA6_transitionS[i]);
        }
    }

    class DFA6 extends DFA {

        public DFA6(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 6;
            this.eot = DFA6_eot;
            this.eof = DFA6_eof;
            this.min = DFA6_min;
            this.max = DFA6_max;
            this.accept = DFA6_accept;
            this.special = DFA6_special;
            this.transition = DFA6_transition;
        }
        public String getDescription() {
            return "45:1: sequence : ({...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? ) | {...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) ) );";
        }
        public void error(NoViableAltException nvae) {
            dbg.recognitionException(nvae);
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA6_0 = input.LA(1);

                         
                        int index6_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA6_0==BYTE) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 1;}

                        else if ( (LA6_0==FULL_STOP) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 2;}

                        else if ( (LA6_0==OPEN_SQUARE) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 3;}

                        else if ( (LA6_0==TAB_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 4;}

                        else if ( (LA6_0==NEWLINE_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 5;}

                        else if ( (LA6_0==VERTICAL_TAB_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 6;}

                        else if ( (LA6_0==FORM_FEED_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 7;}

                        else if ( (LA6_0==RETURN_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 8;}

                        else if ( (LA6_0==ESCAPE_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 9;}

                        else if ( (LA6_0==DIGIT_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 10;}

                        else if ( (LA6_0==NOT_DIGIT_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 11;}

                        else if ( (LA6_0==WORD_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 12;}

                        else if ( (LA6_0==NOT_WORD_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 13;}

                        else if ( (LA6_0==WHITE_SPACE_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 14;}

                        else if ( (LA6_0==NOT_WHITE_SPACE_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 15;}

                        else if ( (LA6_0==AMPERSAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 16;}

                        else if ( (LA6_0==TILDE) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 17;}

                        else if ( (LA6_0==CASE_SENSITIVE_STRING) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 18;}

                        else if ( (LA6_0==CASE_INSENSITIVE_STRING) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 19;}

                        else if ( (LA6_0==OPEN) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 20;}

                         
                        input.seek(index6_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA6_1 = input.LA(1);

                         
                        int index6_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA6_2 = input.LA(1);

                         
                        int index6_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA6_3 = input.LA(1);

                         
                        int index6_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA6_4 = input.LA(1);

                         
                        int index6_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_4);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA6_5 = input.LA(1);

                         
                        int index6_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_5);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA6_6 = input.LA(1);

                         
                        int index6_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_6);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA6_7 = input.LA(1);

                         
                        int index6_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_7);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA6_8 = input.LA(1);

                         
                        int index6_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_8);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA6_9 = input.LA(1);

                         
                        int index6_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_9);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA6_10 = input.LA(1);

                         
                        int index6_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_10);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA6_11 = input.LA(1);

                         
                        int index6_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_11);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA6_12 = input.LA(1);

                         
                        int index6_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_12);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA6_13 = input.LA(1);

                         
                        int index6_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_13);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA6_14 = input.LA(1);

                         
                        int index6_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_14);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA6_15 = input.LA(1);

                         
                        int index6_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_15);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA6_16 = input.LA(1);

                         
                        int index6_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_16);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA6_17 = input.LA(1);

                         
                        int index6_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_17);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA6_18 = input.LA(1);

                         
                        int index6_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_18);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA6_19 = input.LA(1);

                         
                        int index6_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_19);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA6_20 = input.LA(1);

                         
                        int index6_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (evalPredicate(sequencesAsTree,"sequencesAsTree")) ) {s = 21;}

                        else if ( (evalPredicate(!sequencesAsTree,"!sequencesAsTree")) ) {s = 22;}

                         
                        input.seek(index6_20);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 6, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA3_eotS =
        "\26\uffff";
    static final String DFA3_eofS =
        "\1\25\25\uffff";
    static final String DFA3_minS =
        "\1\15\25\uffff";
    static final String DFA3_maxS =
        "\1\67\25\uffff";
    static final String DFA3_acceptS =
        "\1\uffff\24\1\1\2";
    static final String DFA3_specialS =
        "\1\0\25\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\25\1\1\1\2\1\3\3\uffff\1\22\1\20\1\21\22\uffff\1\4\1\5\1"+
            "\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\23\1\24\1\25",
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
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "49:3: ( sequence -> ^( SEQUENCE quantified_atom sequence ) )?";
        }
        public void error(NoViableAltException nvae) {
            dbg.recognitionException(nvae);
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA3_0 = input.LA(1);

                         
                        int index3_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA3_0==BYTE) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 1;}

                        else if ( (LA3_0==FULL_STOP) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 2;}

                        else if ( (LA3_0==OPEN_SQUARE) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 3;}

                        else if ( (LA3_0==TAB_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 4;}

                        else if ( (LA3_0==NEWLINE_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 5;}

                        else if ( (LA3_0==VERTICAL_TAB_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 6;}

                        else if ( (LA3_0==FORM_FEED_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 7;}

                        else if ( (LA3_0==RETURN_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 8;}

                        else if ( (LA3_0==ESCAPE_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 9;}

                        else if ( (LA3_0==DIGIT_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 10;}

                        else if ( (LA3_0==NOT_DIGIT_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 11;}

                        else if ( (LA3_0==WORD_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 12;}

                        else if ( (LA3_0==NOT_WORD_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 13;}

                        else if ( (LA3_0==WHITE_SPACE_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 14;}

                        else if ( (LA3_0==NOT_WHITE_SPACE_SHORTHAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 15;}

                        else if ( (LA3_0==AMPERSAND) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 16;}

                        else if ( (LA3_0==TILDE) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 17;}

                        else if ( (LA3_0==CASE_SENSITIVE_STRING) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 18;}

                        else if ( (LA3_0==CASE_INSENSITIVE_STRING) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 19;}

                        else if ( (LA3_0==OPEN) && ((evalPredicate(!sequencesAsTree,"!sequencesAsTree")||evalPredicate(sequencesAsTree,"sequencesAsTree")))) {s = 20;}

                        else if ( (LA3_0==EOF||LA3_0==ALT||LA3_0==CLOSE) ) {s = 21;}

                         
                        input.seek(index3_0);
                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 3, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA10_eotS =
        "\16\uffff";
    static final String DFA10_eofS =
        "\16\uffff";
    static final String DFA10_minS =
        "\1\16\1\uffff\1\16\3\uffff\1\16\7\uffff";
    static final String DFA10_maxS =
        "\1\65\1\uffff\1\65\3\uffff\1\65\7\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\13\1\uffff\1\2\1\3\1\4\1\uffff\1\6\1\10\1\11\1\12\1\7"+
        "\1\1\1\5";
    static final String DFA10_specialS =
        "\16\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\2\1\uffff\1\12\1\uffff\1\1\1\uffff\1\6\1\10\1\11\22\5\6\3"+
            "\6\4\1\7",
            "",
            "\1\14\1\uffff\1\14\1\uffff\1\14\1\13\42\14",
            "",
            "",
            "",
            "\1\15\1\uffff\1\15\1\uffff\1\15\1\13\42\15",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "()+ loopback of 109:2: ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | all_bitmask | any_bitmask | byte_set )+";
        }
        public void error(NoViableAltException nvae) {
            dbg.recognitionException(nvae);
        }
    }
 

    public static final BitSet FOLLOW_regex_in_start134 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_start136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_regex155 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ALT_in_regex169 = new BitSet(new long[]{0x007FFE000071C000L});
    public static final BitSet FOLLOW_sequence_in_regex171 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_quantified_atom_in_sequence237 = new BitSet(new long[]{0x007FFE000071C002L});
    public static final BitSet FOLLOW_sequence_in_sequence249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_quantified_atom_in_sequence283 = new BitSet(new long[]{0x007FFE000071C002L});
    public static final BitSet FOLLOW_quantified_atom_in_sequence294 = new BitSet(new long[]{0x007FFE000071C002L});
    public static final BitSet FOLLOW_atom_in_quantified_atom354 = new BitSet(new long[]{0x6900000000000002L});
    public static final BitSet FOLLOW_quantifier_in_quantified_atom359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hexbyte_in_atom401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_any_byte_in_atom406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_set_in_atom411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_shorthand_in_atom416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_shorthand_in_atom421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_all_bitmask_in_atom426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_any_bitmask_in_atom431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_sensitive_string_in_atom436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_insensitive_string_in_atom441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_group_in_atom446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BYTE_in_hexbyte461 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FULL_STOP_in_any_byte471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_SQUARE_in_byte_set489 = new BitSet(new long[]{0x003FFFFFFFF34000L});
    public static final BitSet FOLLOW_CARET_in_byte_set500 = new BitSet(new long[]{0x003FFFFFFFF34000L});
    public static final BitSet FOLLOW_set_specification_in_byte_set502 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_set_specification_in_byte_set527 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_CLOSE_SQUARE_in_byte_set549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hexbyte_in_set_specification565 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_byte_shorthand_in_set_specification570 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_set_shorthand_in_set_specification575 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_mnemonic_in_set_specification580 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_case_sensitive_string_in_set_specification586 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_case_insensitive_string_in_set_specification591 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_byte_range_in_set_specification596 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_all_bitmask_in_set_specification601 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_any_bitmask_in_set_specification606 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_byte_set_in_set_specification611 = new BitSet(new long[]{0x003FFFFFFFF34002L});
    public static final BitSet FOLLOW_range_values_in_byte_range630 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_RANGE_SEPARATOR_in_byte_range634 = new BitSet(new long[]{0x0000000000104000L});
    public static final BitSet FOLLOW_range_values_in_byte_range641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_range_values0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AMPERSAND_in_all_bitmask684 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_BYTE_in_all_bitmask686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_any_bitmask706 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_BYTE_in_any_bitmask708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_ASCII_in_mnemonic731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_PRINT_in_mnemonic756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_GRAPH_in_mnemonic781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_WORD_in_mnemonic806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_ALPHANUM_in_mnemonic853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_ALPHA_in_mnemonic897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_UPPER_in_mnemonic931 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_LOWER_in_mnemonic955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_PUNCT_in_mnemonic979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_HEXDIGIT_in_mnemonic1033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_DIGIT_in_mnemonic1077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_WHITESPACE_in_mnemonic1101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_BLANK_in_mnemonic1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_SPACE_in_mnemonic1146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_TAB_in_mnemonic1178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_NEWLINE_in_mnemonic1191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_RETURN_in_mnemonic1204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_CONTROL_in_mnemonic1217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TAB_SHORTHAND_in_byte_shorthand1252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEWLINE_SHORTHAND_in_byte_shorthand1265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VERTICAL_TAB_SHORTHAND_in_byte_shorthand1278 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORM_FEED_SHORTHAND_in_byte_shorthand1290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_SHORTHAND_in_byte_shorthand1303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_SHORTHAND_in_byte_shorthand1316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIGIT_SHORTHAND_in_set_shorthand1338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_DIGIT_SHORTHAND_in_set_shorthand1362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WORD_SHORTHAND_in_set_shorthand1386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_WORD_SHORTHAND_in_set_shorthand1433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHITE_SPACE_SHORTHAND_in_set_shorthand1480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_WHITE_SPACE_SHORTHAND_in_set_shorthand1505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_INSENSITIVE_STRING_in_case_insensitive_string1535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_SENSITIVE_STRING_in_case_sensitive_string1549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_group1562 = new BitSet(new long[]{0x007FFE000071E000L});
    public static final BitSet FOLLOW_regex_in_group1564 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_CLOSE_in_group1566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optional_in_quantifier1587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_zero_to_many_in_quantifier1596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_one_to_many_in_quantifier1606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_repeat_in_quantifier1612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_CURLY_in_repeat1628 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_NUMBER_in_repeat1632 = new BitSet(new long[]{0x1400000000000000L});
    public static final BitSet FOLLOW_REPEAT_SEPARATOR_in_repeat1643 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_NUMBER_in_repeat1647 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_REPEAT_SEPARATOR_in_repeat1673 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_MANY_in_repeat1675 = new BitSet(new long[]{0x1000000000000000L});
    public static final BitSet FOLLOW_CLOSE_CURLY_in_repeat1724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_MARK_in_optional1735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MANY_in_zero_to_many1747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_one_to_many1760 = new BitSet(new long[]{0x0000000000000002L});

}