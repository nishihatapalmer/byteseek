// $ANTLR 3.2 Sep 23, 2009 12:02:23 /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g 2010-12-08 22:19:07

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

public class regularExpressionParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "SEQUENCE", "ALTERNATE", "REPEAT", "SET", "INVERTED_SET", "RANGE", "BITMASK", "ANY", "ALT", "BYTE", "FULL_STOP", "OPEN_SQUARE", "CARET", "CLOSE_SQUARE", "RANGE_SEPARATOR", "CASE_SENSITIVE_STRING", "AMPERSAND", "SET_ASCII", "SET_PRINT", "SET_GRAPH", "SET_WORD", "SET_ALPHANUM", "SET_ALPHA", "SET_UPPER", "SET_LOWER", "SET_PUNCT", "SET_HEXDIGIT", "SET_DIGIT", "SET_WHITESPACE", "SET_BLANK", "SET_SPACE", "SET_TAB", "SET_NEWLINE", "SET_RETURN", "SET_CONTROL", "TAB_SHORTHAND", "NEWLINE_SHORTHAND", "VERTICAL_TAB_SHORTHAND", "FORM_FEED_SHORTHAND", "RETURN_SHORTHAND", "ESCAPE_SHORTHAND", "DIGIT_SHORTHAND", "NOT_DIGIT_SHORTHAND", "WORD_SHORTHAND", "NOT_WORD_SHORTHAND", "WHITE_SPACE_SHORTHAND", "NOT_WHITE_SPACE_SHORTHAND", "CASE_INSENSITIVE_STRING", "OPEN", "CLOSE", "OPEN_CURLY", "NUMBER", "REPEAT_SEPARATOR", "MANY", "CLOSE_CURLY", "QUESTION_MARK", "PLUS", "QUOTE", "BACK_TICK", "ESCAPE", "HEX_DIGIT", "COMMENT", "WS"
    };
    public static final int CLOSE_CURLY=58;
    public static final int SET_NEWLINE=36;
    public static final int SET_LOWER=28;
    public static final int SET_GRAPH=23;
    public static final int SET_ASCII=21;
    public static final int BITMASK=10;
    public static final int TAB_SHORTHAND=39;
    public static final int DIGIT_SHORTHAND=45;
    public static final int CASE_SENSITIVE_STRING=19;
    public static final int SET_PUNCT=29;
    public static final int EOF=-1;
    public static final int SET_DIGIT=31;
    public static final int RANGE_SEPARATOR=18;
    public static final int NEWLINE_SHORTHAND=40;
    public static final int QUOTE=61;
    public static final int ALT=12;
    public static final int SET_ALPHANUM=25;
    public static final int ESCAPE=63;
    public static final int ESCAPE_SHORTHAND=44;
    public static final int SET_WHITESPACE=32;
    public static final int SET_RETURN=37;
    public static final int CARET=16;
    public static final int QUESTION_MARK=59;
    public static final int OPEN_SQUARE=15;
    public static final int BACK_TICK=62;
    public static final int PLUS=60;
    public static final int SET_UPPER=27;
    public static final int SET_TAB=35;
    public static final int RETURN_SHORTHAND=43;
    public static final int SET_BLANK=33;
    public static final int COMMENT=65;
    public static final int REPEAT_SEPARATOR=56;
    public static final int CLOSE_SQUARE=17;
    public static final int FORM_FEED_SHORTHAND=42;
    public static final int BYTE=13;
    public static final int FULL_STOP=14;
    public static final int VERTICAL_TAB_SHORTHAND=41;
    public static final int INVERTED_SET=8;
    public static final int NUMBER=55;
    public static final int AMPERSAND=20;
    public static final int HEX_DIGIT=64;
    public static final int RANGE=9;
    public static final int SET=7;
    public static final int OPEN_CURLY=54;
    public static final int SET_SPACE=34;
    public static final int SET_HEXDIGIT=30;
    public static final int MANY=57;
    public static final int SET_CONTROL=38;
    public static final int ALTERNATE=5;
    public static final int OPEN=52;
    public static final int SEQUENCE=4;
    public static final int SET_WORD=24;
    public static final int WS=66;
    public static final int ANY=11;
    public static final int CLOSE=53;
    public static final int CASE_INSENSITIVE_STRING=51;
    public static final int NOT_WORD_SHORTHAND=48;
    public static final int NOT_WHITE_SPACE_SHORTHAND=50;
    public static final int WORD_SHORTHAND=47;
    public static final int SET_ALPHA=26;
    public static final int REPEAT=6;
    public static final int SET_PRINT=22;
    public static final int WHITE_SPACE_SHORTHAND=49;
    public static final int NOT_DIGIT_SHORTHAND=46;

    // delegates
    // delegators


        public regularExpressionParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public regularExpressionParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return regularExpressionParser.tokenNames; }
    public String getGrammarFileName() { return "/home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g"; }


    	boolean sequencesAsTree = false;


    public static class start_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "start"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:59:1: start : regex EOF ;
    public final regularExpressionParser.start_return start() throws RecognitionException {
        regularExpressionParser.start_return retval = new regularExpressionParser.start_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token EOF2=null;
        regularExpressionParser.regex_return regex1 = null;


        Object EOF2_tree=null;

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:59:7: ( regex EOF )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:59:9: regex EOF
            {
            root_0 = (Object)adaptor.nil();

            pushFollow(FOLLOW_regex_in_start143);
            regex1=regex();

            state._fsp--;

            adaptor.addChild(root_0, regex1.getTree());
            EOF2=(Token)match(input,EOF,FOLLOW_EOF_in_start145); 

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
        return retval;
    }
    // $ANTLR end "start"

    public static class regex_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "regex"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:63:1: regex : sequence ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) ) ;
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
        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:63:7: ( sequence ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) ) )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:64:3: sequence ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) )
            {
            pushFollow(FOLLOW_sequence_in_regex164);
            sequence3=sequence();

            state._fsp--;

            stream_sequence.add(sequence3.getTree());
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:65:3: ( ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) ) | ( -> sequence ) )
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:66:4: ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) )
                    {
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:66:4: ( ( ALT sequence )+ -> ^( ALT ( sequence )+ ) )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:66:6: ( ALT sequence )+
                    {
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:66:6: ( ALT sequence )+
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
                    	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:66:7: ALT sequence
                    	    {
                    	    ALT4=(Token)match(input,ALT,FOLLOW_ALT_in_regex178);  
                    	    stream_ALT.add(ALT4);

                    	    pushFollow(FOLLOW_sequence_in_regex180);
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
                    // elements: sequence, ALT
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 66:22: -> ^( ALT ( sequence )+ )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:66:25: ^( ALT ( sequence )+ )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot(stream_ALT.nextNode(), root_1);

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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:68:4: ( -> sequence )
                    {
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:68:4: ( -> sequence )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:68:9: 
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
                    // 68:9: -> sequence
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
        }
        return retval;
    }
    // $ANTLR end "regex"

    public static class sequence_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sequence"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:75:1: sequence : ({...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? ) | {...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) ) );
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
        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:76:2: ({...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? ) | {...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) ) )
            int alt6=2;
            alt6 = dfa6.predict(input);
            switch (alt6) {
                case 1 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:76:4: {...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? )
                    {
                    if ( !((sequencesAsTree)) ) {
                        throw new FailedPredicateException(input, "sequence", "sequencesAsTree");
                    }
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:77:2: ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:78:3: ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )?
                    {
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:78:3: ( quantified_atom -> quantified_atom )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:78:5: quantified_atom
                    {
                    pushFollow(FOLLOW_quantified_atom_in_sequence246);
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
                    // 78:21: -> quantified_atom
                    {
                        adaptor.addChild(root_0, stream_quantified_atom.nextTree());

                    }

                    retval.tree = root_0;
                    }

                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:79:3: ( sequence -> ^( SEQUENCE quantified_atom sequence ) )?
                    int alt3=2;
                    alt3 = dfa3.predict(input);
                    switch (alt3) {
                        case 1 :
                            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:79:5: sequence
                            {
                            pushFollow(FOLLOW_sequence_in_sequence258);
                            sequence7=sequence();

                            state._fsp--;

                            stream_sequence.add(sequence7.getTree());


                            // AST REWRITE
                            // elements: quantified_atom, sequence
                            // token labels: 
                            // rule labels: retval
                            // token list labels: 
                            // rule list labels: 
                            // wildcard labels: 
                            retval.tree = root_0;
                            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                            root_0 = (Object)adaptor.nil();
                            // 79:16: -> ^( SEQUENCE quantified_atom sequence )
                            {
                                // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:79:19: ^( SEQUENCE quantified_atom sequence )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SEQUENCE, "SEQUENCE"), root_1);

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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:82:3: {...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) )
                    {
                    if ( !((!sequencesAsTree)) ) {
                        throw new FailedPredicateException(input, "sequence", "!sequencesAsTree");
                    }
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:83:2: ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:84:3: quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) )
                    {
                    pushFollow(FOLLOW_quantified_atom_in_sequence292);
                    quantified_atom8=quantified_atom();

                    state._fsp--;

                    stream_quantified_atom.add(quantified_atom8.getTree());
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:85:3: ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) )
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( ((LA5_0>=BYTE && LA5_0<=OPEN_SQUARE)||(LA5_0>=CASE_SENSITIVE_STRING && LA5_0<=AMPERSAND)||(LA5_0>=TAB_SHORTHAND && LA5_0<=OPEN)) ) {
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
                            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:86:4: ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) )
                            {
                            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:86:4: ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) )
                            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:86:6: ( quantified_atom )+
                            {
                            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:86:6: ( quantified_atom )+
                            int cnt4=0;
                            loop4:
                            do {
                                int alt4=2;
                                int LA4_0 = input.LA(1);

                                if ( ((LA4_0>=BYTE && LA4_0<=OPEN_SQUARE)||(LA4_0>=CASE_SENSITIVE_STRING && LA4_0<=AMPERSAND)||(LA4_0>=TAB_SHORTHAND && LA4_0<=OPEN)) ) {
                                    alt4=1;
                                }


                                switch (alt4) {
                            	case 1 :
                            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:86:6: quantified_atom
                            	    {
                            	    pushFollow(FOLLOW_quantified_atom_in_sequence303);
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
                            // 86:23: -> ^( SEQUENCE ( quantified_atom )+ )
                            {
                                // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:86:26: ^( SEQUENCE ( quantified_atom )+ )
                                {
                                Object root_1 = (Object)adaptor.nil();
                                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SEQUENCE, "SEQUENCE"), root_1);

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
                            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:88:4: ( -> ^( quantified_atom ) )
                            {
                            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:88:4: ( -> ^( quantified_atom ) )
                            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:88:8: 
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
                            // 88:8: -> ^( quantified_atom )
                            {
                                // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:88:11: ^( quantified_atom )
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
        }
        return retval;
    }
    // $ANTLR end "sequence"

    public static class quantified_atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "quantified_atom"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:95:1: quantified_atom : e= atom ( quantifier -> ^( quantifier $e) | -> ^( $e) ) ;
    public final regularExpressionParser.quantified_atom_return quantified_atom() throws RecognitionException {
        regularExpressionParser.quantified_atom_return retval = new regularExpressionParser.quantified_atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        regularExpressionParser.atom_return e = null;

        regularExpressionParser.quantifier_return quantifier10 = null;


        RewriteRuleSubtreeStream stream_quantifier=new RewriteRuleSubtreeStream(adaptor,"rule quantifier");
        RewriteRuleSubtreeStream stream_atom=new RewriteRuleSubtreeStream(adaptor,"rule atom");
        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:96:2: (e= atom ( quantifier -> ^( quantifier $e) | -> ^( $e) ) )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:96:4: e= atom ( quantifier -> ^( quantifier $e) | -> ^( $e) )
            {
            pushFollow(FOLLOW_atom_in_quantified_atom363);
            e=atom();

            state._fsp--;

            stream_atom.add(e.getTree());
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:97:2: ( quantifier -> ^( quantifier $e) | -> ^( $e) )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==OPEN_CURLY||LA7_0==MANY||(LA7_0>=QUESTION_MARK && LA7_0<=PLUS)) ) {
                alt7=1;
            }
            else if ( (LA7_0==EOF||(LA7_0>=ALT && LA7_0<=OPEN_SQUARE)||(LA7_0>=CASE_SENSITIVE_STRING && LA7_0<=AMPERSAND)||(LA7_0>=TAB_SHORTHAND && LA7_0<=CLOSE)) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:97:4: quantifier
                    {
                    pushFollow(FOLLOW_quantifier_in_quantified_atom368);
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
                    // 97:17: -> ^( quantifier $e)
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:97:20: ^( quantifier $e)
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:98:7: 
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
                    // 98:7: -> ^( $e)
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:98:10: ^( $e)
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
        }
        return retval;
    }
    // $ANTLR end "quantified_atom"

    public static class atom_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "atom"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:103:1: atom : ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | bitmask | case_sensitive_string | case_insensitive_string | group ) ;
    public final regularExpressionParser.atom_return atom() throws RecognitionException {
        regularExpressionParser.atom_return retval = new regularExpressionParser.atom_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        regularExpressionParser.hexbyte_return hexbyte11 = null;

        regularExpressionParser.any_byte_return any_byte12 = null;

        regularExpressionParser.byte_set_return byte_set13 = null;

        regularExpressionParser.byte_shorthand_return byte_shorthand14 = null;

        regularExpressionParser.set_shorthand_return set_shorthand15 = null;

        regularExpressionParser.bitmask_return bitmask16 = null;

        regularExpressionParser.case_sensitive_string_return case_sensitive_string17 = null;

        regularExpressionParser.case_insensitive_string_return case_insensitive_string18 = null;

        regularExpressionParser.group_return group19 = null;



        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:103:6: ( ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | bitmask | case_sensitive_string | case_insensitive_string | group ) )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:104:2: ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | bitmask | case_sensitive_string | case_insensitive_string | group )
            {
            root_0 = (Object)adaptor.nil();

            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:104:2: ( hexbyte | any_byte | byte_set | byte_shorthand | set_shorthand | bitmask | case_sensitive_string | case_insensitive_string | group )
            int alt8=9;
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
            case CASE_SENSITIVE_STRING:
                {
                alt8=7;
                }
                break;
            case CASE_INSENSITIVE_STRING:
                {
                alt8=8;
                }
                break;
            case OPEN:
                {
                alt8=9;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:104:4: hexbyte
                    {
                    pushFollow(FOLLOW_hexbyte_in_atom410);
                    hexbyte11=hexbyte();

                    state._fsp--;

                    adaptor.addChild(root_0, hexbyte11.getTree());

                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:105:4: any_byte
                    {
                    pushFollow(FOLLOW_any_byte_in_atom415);
                    any_byte12=any_byte();

                    state._fsp--;

                    adaptor.addChild(root_0, any_byte12.getTree());

                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:106:4: byte_set
                    {
                    pushFollow(FOLLOW_byte_set_in_atom420);
                    byte_set13=byte_set();

                    state._fsp--;

                    adaptor.addChild(root_0, byte_set13.getTree());

                    }
                    break;
                case 4 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:107:4: byte_shorthand
                    {
                    pushFollow(FOLLOW_byte_shorthand_in_atom425);
                    byte_shorthand14=byte_shorthand();

                    state._fsp--;

                    adaptor.addChild(root_0, byte_shorthand14.getTree());

                    }
                    break;
                case 5 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:108:4: set_shorthand
                    {
                    pushFollow(FOLLOW_set_shorthand_in_atom430);
                    set_shorthand15=set_shorthand();

                    state._fsp--;

                    adaptor.addChild(root_0, set_shorthand15.getTree());

                    }
                    break;
                case 6 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:109:4: bitmask
                    {
                    pushFollow(FOLLOW_bitmask_in_atom435);
                    bitmask16=bitmask();

                    state._fsp--;

                    adaptor.addChild(root_0, bitmask16.getTree());

                    }
                    break;
                case 7 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:110:4: case_sensitive_string
                    {
                    pushFollow(FOLLOW_case_sensitive_string_in_atom440);
                    case_sensitive_string17=case_sensitive_string();

                    state._fsp--;

                    adaptor.addChild(root_0, case_sensitive_string17.getTree());

                    }
                    break;
                case 8 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:111:4: case_insensitive_string
                    {
                    pushFollow(FOLLOW_case_insensitive_string_in_atom445);
                    case_insensitive_string18=case_insensitive_string();

                    state._fsp--;

                    adaptor.addChild(root_0, case_insensitive_string18.getTree());

                    }
                    break;
                case 9 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:112:4: group
                    {
                    pushFollow(FOLLOW_group_in_atom450);
                    group19=group();

                    state._fsp--;

                    adaptor.addChild(root_0, group19.getTree());

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
        }
        return retval;
    }
    // $ANTLR end "atom"

    public static class hexbyte_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "hexbyte"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:117:1: hexbyte : BYTE ;
    public final regularExpressionParser.hexbyte_return hexbyte() throws RecognitionException {
        regularExpressionParser.hexbyte_return retval = new regularExpressionParser.hexbyte_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token BYTE20=null;

        Object BYTE20_tree=null;

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:117:9: ( BYTE )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:117:11: BYTE
            {
            root_0 = (Object)adaptor.nil();

            BYTE20=(Token)match(input,BYTE,FOLLOW_BYTE_in_hexbyte465); 
            BYTE20_tree = (Object)adaptor.create(BYTE20);
            adaptor.addChild(root_0, BYTE20_tree);


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
        return retval;
    }
    // $ANTLR end "hexbyte"

    public static class any_byte_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "any_byte"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:121:1: any_byte : FULL_STOP -> ANY ;
    public final regularExpressionParser.any_byte_return any_byte() throws RecognitionException {
        regularExpressionParser.any_byte_return retval = new regularExpressionParser.any_byte_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token FULL_STOP21=null;

        Object FULL_STOP21_tree=null;
        RewriteRuleTokenStream stream_FULL_STOP=new RewriteRuleTokenStream(adaptor,"token FULL_STOP");

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:121:9: ( FULL_STOP -> ANY )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:121:11: FULL_STOP
            {
            FULL_STOP21=(Token)match(input,FULL_STOP,FOLLOW_FULL_STOP_in_any_byte475);  
            stream_FULL_STOP.add(FULL_STOP21);



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
            // 121:22: -> ANY
            {
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
        return retval;
    }
    // $ANTLR end "any_byte"

    public static class byte_set_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "byte_set"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:125:1: byte_set : OPEN_SQUARE ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) ) CLOSE_SQUARE ;
    public final regularExpressionParser.byte_set_return byte_set() throws RecognitionException {
        regularExpressionParser.byte_set_return retval = new regularExpressionParser.byte_set_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OPEN_SQUARE22=null;
        Token CARET23=null;
        Token CLOSE_SQUARE26=null;
        regularExpressionParser.set_specification_return set_specification24 = null;

        regularExpressionParser.set_specification_return set_specification25 = null;


        Object OPEN_SQUARE22_tree=null;
        Object CARET23_tree=null;
        Object CLOSE_SQUARE26_tree=null;
        RewriteRuleTokenStream stream_OPEN_SQUARE=new RewriteRuleTokenStream(adaptor,"token OPEN_SQUARE");
        RewriteRuleTokenStream stream_CLOSE_SQUARE=new RewriteRuleTokenStream(adaptor,"token CLOSE_SQUARE");
        RewriteRuleTokenStream stream_CARET=new RewriteRuleTokenStream(adaptor,"token CARET");
        RewriteRuleSubtreeStream stream_set_specification=new RewriteRuleSubtreeStream(adaptor,"rule set_specification");
        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:126:2: ( OPEN_SQUARE ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) ) CLOSE_SQUARE )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:126:4: OPEN_SQUARE ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) ) CLOSE_SQUARE
            {
            OPEN_SQUARE22=(Token)match(input,OPEN_SQUARE,FOLLOW_OPEN_SQUARE_in_byte_set493);  
            stream_OPEN_SQUARE.add(OPEN_SQUARE22);

            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:127:2: ( ( CARET set_specification -> ^( INVERTED_SET set_specification ) ) | ( set_specification -> ^( SET set_specification ) ) )
            int alt9=2;
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

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:128:3: ( CARET set_specification -> ^( INVERTED_SET set_specification ) )
                    {
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:128:3: ( CARET set_specification -> ^( INVERTED_SET set_specification ) )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:128:5: CARET set_specification
                    {
                    CARET23=(Token)match(input,CARET,FOLLOW_CARET_in_byte_set504);  
                    stream_CARET.add(CARET23);

                    pushFollow(FOLLOW_set_specification_in_byte_set506);
                    set_specification24=set_specification();

                    state._fsp--;

                    stream_set_specification.add(set_specification24.getTree());


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
                    // 128:30: -> ^( INVERTED_SET set_specification )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:128:33: ^( INVERTED_SET set_specification )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INVERTED_SET, "INVERTED_SET"), root_1);

                        adaptor.addChild(root_1, stream_set_specification.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:130:3: ( set_specification -> ^( SET set_specification ) )
                    {
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:130:3: ( set_specification -> ^( SET set_specification ) )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:130:5: set_specification
                    {
                    pushFollow(FOLLOW_set_specification_in_byte_set531);
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
                    // 130:25: -> ^( SET set_specification )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:130:28: ^( SET set_specification )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        adaptor.addChild(root_1, stream_set_specification.nextTree());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;

            }

            CLOSE_SQUARE26=(Token)match(input,CLOSE_SQUARE,FOLLOW_CLOSE_SQUARE_in_byte_set553);  
            stream_CLOSE_SQUARE.add(CLOSE_SQUARE26);


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
        return retval;
    }
    // $ANTLR end "byte_set"

    public static class set_specification_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "set_specification"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:136:1: set_specification : ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | bitmask | byte_set )+ ;
    public final regularExpressionParser.set_specification_return set_specification() throws RecognitionException {
        regularExpressionParser.set_specification_return retval = new regularExpressionParser.set_specification_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        regularExpressionParser.hexbyte_return hexbyte27 = null;

        regularExpressionParser.byte_shorthand_return byte_shorthand28 = null;

        regularExpressionParser.set_shorthand_return set_shorthand29 = null;

        regularExpressionParser.mnemonic_return mnemonic30 = null;

        regularExpressionParser.case_sensitive_string_return case_sensitive_string31 = null;

        regularExpressionParser.case_insensitive_string_return case_insensitive_string32 = null;

        regularExpressionParser.byte_range_return byte_range33 = null;

        regularExpressionParser.bitmask_return bitmask34 = null;

        regularExpressionParser.byte_set_return byte_set35 = null;



        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:137:2: ( ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | bitmask | byte_set )+ )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:138:2: ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | bitmask | byte_set )+
            {
            root_0 = (Object)adaptor.nil();

            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:138:2: ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | bitmask | byte_set )+
            int cnt10=0;
            loop10:
            do {
                int alt10=10;
                alt10 = dfa10.predict(input);
                switch (alt10) {
            	case 1 :
            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:138:4: hexbyte
            	    {
            	    pushFollow(FOLLOW_hexbyte_in_set_specification569);
            	    hexbyte27=hexbyte();

            	    state._fsp--;

            	    adaptor.addChild(root_0, hexbyte27.getTree());

            	    }
            	    break;
            	case 2 :
            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:139:4: byte_shorthand
            	    {
            	    pushFollow(FOLLOW_byte_shorthand_in_set_specification574);
            	    byte_shorthand28=byte_shorthand();

            	    state._fsp--;

            	    adaptor.addChild(root_0, byte_shorthand28.getTree());

            	    }
            	    break;
            	case 3 :
            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:140:4: set_shorthand
            	    {
            	    pushFollow(FOLLOW_set_shorthand_in_set_specification579);
            	    set_shorthand29=set_shorthand();

            	    state._fsp--;

            	    adaptor.addChild(root_0, set_shorthand29.getTree());

            	    }
            	    break;
            	case 4 :
            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:141:4: mnemonic
            	    {
            	    pushFollow(FOLLOW_mnemonic_in_set_specification584);
            	    mnemonic30=mnemonic();

            	    state._fsp--;

            	    adaptor.addChild(root_0, mnemonic30.getTree());

            	    }
            	    break;
            	case 5 :
            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:142:4: case_sensitive_string
            	    {
            	    pushFollow(FOLLOW_case_sensitive_string_in_set_specification590);
            	    case_sensitive_string31=case_sensitive_string();

            	    state._fsp--;

            	    adaptor.addChild(root_0, case_sensitive_string31.getTree());

            	    }
            	    break;
            	case 6 :
            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:143:4: case_insensitive_string
            	    {
            	    pushFollow(FOLLOW_case_insensitive_string_in_set_specification595);
            	    case_insensitive_string32=case_insensitive_string();

            	    state._fsp--;

            	    adaptor.addChild(root_0, case_insensitive_string32.getTree());

            	    }
            	    break;
            	case 7 :
            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:144:4: byte_range
            	    {
            	    pushFollow(FOLLOW_byte_range_in_set_specification600);
            	    byte_range33=byte_range();

            	    state._fsp--;

            	    adaptor.addChild(root_0, byte_range33.getTree());

            	    }
            	    break;
            	case 8 :
            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:145:4: bitmask
            	    {
            	    pushFollow(FOLLOW_bitmask_in_set_specification605);
            	    bitmask34=bitmask();

            	    state._fsp--;

            	    adaptor.addChild(root_0, bitmask34.getTree());

            	    }
            	    break;
            	case 9 :
            	    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:146:4: byte_set
            	    {
            	    pushFollow(FOLLOW_byte_set_in_set_specification610);
            	    byte_set35=byte_set();

            	    state._fsp--;

            	    adaptor.addChild(root_0, byte_set35.getTree());

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
        }
        return retval;
    }
    // $ANTLR end "set_specification"

    public static class byte_range_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "byte_range"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:151:1: byte_range : r1= range_values RANGE_SEPARATOR r2= range_values -> ^( RANGE $r1 $r2) ;
    public final regularExpressionParser.byte_range_return byte_range() throws RecognitionException {
        regularExpressionParser.byte_range_return retval = new regularExpressionParser.byte_range_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token RANGE_SEPARATOR36=null;
        regularExpressionParser.range_values_return r1 = null;

        regularExpressionParser.range_values_return r2 = null;


        Object RANGE_SEPARATOR36_tree=null;
        RewriteRuleTokenStream stream_RANGE_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token RANGE_SEPARATOR");
        RewriteRuleSubtreeStream stream_range_values=new RewriteRuleSubtreeStream(adaptor,"rule range_values");
        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:152:2: (r1= range_values RANGE_SEPARATOR r2= range_values -> ^( RANGE $r1 $r2) )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:152:4: r1= range_values RANGE_SEPARATOR r2= range_values
            {
            pushFollow(FOLLOW_range_values_in_byte_range629);
            r1=range_values();

            state._fsp--;

            stream_range_values.add(r1.getTree());
            RANGE_SEPARATOR36=(Token)match(input,RANGE_SEPARATOR,FOLLOW_RANGE_SEPARATOR_in_byte_range633);  
            stream_RANGE_SEPARATOR.add(RANGE_SEPARATOR36);

            pushFollow(FOLLOW_range_values_in_byte_range640);
            r2=range_values();

            state._fsp--;

            stream_range_values.add(r2.getTree());


            // AST REWRITE
            // elements: r2, r1
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
            // 154:21: -> ^( RANGE $r1 $r2)
            {
                // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:154:24: ^( RANGE $r1 $r2)
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_1);

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
        }
        return retval;
    }
    // $ANTLR end "byte_range"

    public static class range_values_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "range_values"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:158:1: range_values : ( BYTE | CASE_SENSITIVE_STRING );
    public final regularExpressionParser.range_values_return range_values() throws RecognitionException {
        regularExpressionParser.range_values_return retval = new regularExpressionParser.range_values_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token set37=null;

        Object set37_tree=null;

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:159:2: ( BYTE | CASE_SENSITIVE_STRING )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:
            {
            root_0 = (Object)adaptor.nil();

            set37=(Token)input.LT(1);
            if ( input.LA(1)==BYTE||input.LA(1)==CASE_SENSITIVE_STRING ) {
                input.consume();
                adaptor.addChild(root_0, (Object)adaptor.create(set37));
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
        }
        return retval;
    }
    // $ANTLR end "range_values"

    public static class bitmask_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "bitmask"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:164:1: bitmask : AMPERSAND BYTE -> ^( BITMASK BYTE ) ;
    public final regularExpressionParser.bitmask_return bitmask() throws RecognitionException {
        regularExpressionParser.bitmask_return retval = new regularExpressionParser.bitmask_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token AMPERSAND38=null;
        Token BYTE39=null;

        Object AMPERSAND38_tree=null;
        Object BYTE39_tree=null;
        RewriteRuleTokenStream stream_AMPERSAND=new RewriteRuleTokenStream(adaptor,"token AMPERSAND");
        RewriteRuleTokenStream stream_BYTE=new RewriteRuleTokenStream(adaptor,"token BYTE");

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:164:9: ( AMPERSAND BYTE -> ^( BITMASK BYTE ) )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:164:11: AMPERSAND BYTE
            {
            AMPERSAND38=(Token)match(input,AMPERSAND,FOLLOW_AMPERSAND_in_bitmask682);  
            stream_AMPERSAND.add(AMPERSAND38);

            BYTE39=(Token)match(input,BYTE,FOLLOW_BYTE_in_bitmask684);  
            stream_BYTE.add(BYTE39);



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
            // 164:27: -> ^( BITMASK BYTE )
            {
                // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:164:30: ^( BITMASK BYTE )
                {
                Object root_1 = (Object)adaptor.nil();
                root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(BITMASK, "BITMASK"), root_1);

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
        return retval;
    }
    // $ANTLR end "bitmask"

    public static class mnemonic_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "mnemonic"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:168:1: mnemonic : (m= SET_ASCII -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) ) | m= SET_PRINT -> ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) ) | m= SET_GRAPH -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) ) | m= SET_WORD -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] ) | m= SET_ALPHANUM -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) | m= SET_ALPHA -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) | m= SET_UPPER -> ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) | m= SET_LOWER -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ) | m= SET_PUNCT -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) ) | m= SET_HEXDIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) ) | m= SET_DIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) | m= SET_WHITESPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) | m= SET_BLANK -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] ) | m= SET_SPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) | m= SET_TAB -> BYTE[$m,\"09\"] | m= SET_NEWLINE -> BYTE[$m,\"0a\"] | m= SET_RETURN -> BYTE[$m,\"0d\"] | m= SET_CONTROL -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] ) );
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

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:169:2: (m= SET_ASCII -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) ) | m= SET_PRINT -> ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) ) | m= SET_GRAPH -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) ) | m= SET_WORD -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] ) | m= SET_ALPHANUM -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) | m= SET_ALPHA -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) | m= SET_UPPER -> ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ) | m= SET_LOWER -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ) | m= SET_PUNCT -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) ) | m= SET_HEXDIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) ) | m= SET_DIGIT -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ) | m= SET_WHITESPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) | m= SET_BLANK -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] ) | m= SET_SPACE -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] ) | m= SET_TAB -> BYTE[$m,\"09\"] | m= SET_NEWLINE -> BYTE[$m,\"0a\"] | m= SET_RETURN -> BYTE[$m,\"0d\"] | m= SET_CONTROL -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] ) )
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:169:4: m= SET_ASCII
                    {
                    m=(Token)match(input,SET_ASCII,FOLLOW_SET_ASCII_in_mnemonic708);  
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
                    // 169:17: -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:169:20: ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:169:26: ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"7f\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "00"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:170:4: m= SET_PRINT
                    {
                    m=(Token)match(input,SET_PRINT,FOLLOW_SET_PRINT_in_mnemonic733);  
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
                    // 170:17: -> ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:170:20: ^( SET ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:170:26: ^( RANGE BYTE[$m,\"' '\"] BYTE[$m,\"'~'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "' '"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:171:4: m= SET_GRAPH
                    {
                    m=(Token)match(input,SET_GRAPH,FOLLOW_SET_GRAPH_in_mnemonic758);  
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
                    // 171:17: -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:171:20: ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:171:26: ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'~'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'!'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:172:4: m= SET_WORD
                    {
                    m=(Token)match(input,SET_WORD,FOLLOW_SET_WORD_in_mnemonic783);  
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
                    // 172:16: -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:172:19: ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) BYTE[$m,\"'_'\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:172:25: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'0'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:172:64: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:172:103: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "'_'"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:173:4: m= SET_ALPHANUM
                    {
                    m=(Token)match(input,SET_ALPHANUM,FOLLOW_SET_ALPHANUM_in_mnemonic830);  
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
                    // 173:20: -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:173:23: ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:173:29: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:173:68: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:173:107: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'0'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:174:4: m= SET_ALPHA
                    {
                    m=(Token)match(input,SET_ALPHA,FOLLOW_SET_ALPHA_in_mnemonic874);  
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
                    // 174:17: -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:174:20: ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:174:26: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:174:65: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:175:4: m= SET_UPPER
                    {
                    m=(Token)match(input,SET_UPPER,FOLLOW_SET_UPPER_in_mnemonic908);  
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
                    // 175:17: -> ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:175:20: ^( SET ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:175:26: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:176:4: m= SET_LOWER
                    {
                    m=(Token)match(input,SET_LOWER,FOLLOW_SET_LOWER_in_mnemonic932);  
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
                    // 176:17: -> ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:176:20: ^( SET ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:176:26: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:177:4: m= SET_PUNCT
                    {
                    m=(Token)match(input,SET_PUNCT,FOLLOW_SET_PUNCT_in_mnemonic956);  
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
                    // 177:17: -> ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:177:20: ^( SET ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] ) ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] ) ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] ) ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:177:26: ^( RANGE BYTE[$m,\"'!'\"] BYTE[$m,\"'/'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'!'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'/'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:177:65: ^( RANGE BYTE[$m,\"':'\"] BYTE[$m,\"'@'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "':'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'@'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:177:104: ^( RANGE BYTE[$m,\"'['\"] BYTE[$m,\"'`'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'['"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'`'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:177:143: ^( RANGE BYTE[$m,\"'{'\"] BYTE[$m,\"'~'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'{'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:178:4: m= SET_HEXDIGIT
                    {
                    m=(Token)match(input,SET_HEXDIGIT,FOLLOW_SET_HEXDIGIT_in_mnemonic1010);  
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
                    // 178:20: -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:178:23: ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] ) ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:178:29: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'0'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:178:68: ^( RANGE BYTE[$m,\"'a'\"] BYTE[$m,\"'f'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'a'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'f'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:178:107: ^( RANGE BYTE[$m,\"'A'\"] BYTE[$m,\"'F'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'A'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:179:4: m= SET_DIGIT
                    {
                    m=(Token)match(input,SET_DIGIT,FOLLOW_SET_DIGIT_in_mnemonic1054);  
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
                    // 179:17: -> ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:179:20: ^( SET ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:179:26: ^( RANGE BYTE[$m,\"'0'\"] BYTE[$m,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "'0'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:180:4: m= SET_WHITESPACE
                    {
                    m=(Token)match(input,SET_WHITESPACE,FOLLOW_SET_WHITESPACE_in_mnemonic1078);  
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
                    // 180:21: -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:180:24: ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "09"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0a"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0d"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "' '"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 13 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:181:4: m= SET_BLANK
                    {
                    m=(Token)match(input,SET_BLANK,FOLLOW_SET_BLANK_in_mnemonic1103);  
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
                    // 181:17: -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:181:20: ^( SET BYTE[$m,\"09\"] BYTE[$m,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "09"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "' '"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 14 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:182:4: m= SET_SPACE
                    {
                    m=(Token)match(input,SET_SPACE,FOLLOW_SET_SPACE_in_mnemonic1123);  
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
                    // 182:17: -> ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:182:20: ^( SET BYTE[$m,\"09\"] BYTE[$m,\"0a\"] BYTE[$m,\"0b\"] BYTE[$m,\"0c\"] BYTE[$m,\"0d\"] BYTE[$m,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "09"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0a"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0b"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0c"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "0d"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, m, "' '"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 15 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:183:4: m= SET_TAB
                    {
                    m=(Token)match(input,SET_TAB,FOLLOW_SET_TAB_in_mnemonic1155);  
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
                    // 183:15: -> BYTE[$m,\"09\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, m, "09"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 16 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:184:4: m= SET_NEWLINE
                    {
                    m=(Token)match(input,SET_NEWLINE,FOLLOW_SET_NEWLINE_in_mnemonic1168);  
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
                    // 184:19: -> BYTE[$m,\"0a\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, m, "0a"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 17 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:185:4: m= SET_RETURN
                    {
                    m=(Token)match(input,SET_RETURN,FOLLOW_SET_RETURN_in_mnemonic1181);  
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
                    // 185:18: -> BYTE[$m,\"0d\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, m, "0d"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 18 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:186:4: m= SET_CONTROL
                    {
                    m=(Token)match(input,SET_CONTROL,FOLLOW_SET_CONTROL_in_mnemonic1194);  
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
                    // 186:19: -> ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:186:22: ^( SET ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] ) BYTE[$m,\"7f\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:186:28: ^( RANGE BYTE[$m,\"00\"] BYTE[$m,\"1f\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "00"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, m, "1f"));

                        adaptor.addChild(root_1, root_2);
                        }
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
        return retval;
    }
    // $ANTLR end "mnemonic"

    public static class byte_shorthand_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "byte_shorthand"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:190:1: byte_shorthand : (sh= TAB_SHORTHAND -> BYTE[$sh,\"09\"] | sh= NEWLINE_SHORTHAND -> BYTE[$sh,\"0a\"] | sh= VERTICAL_TAB_SHORTHAND -> BYTE[$sh,\"0b\"] | sh= FORM_FEED_SHORTHAND -> BYTE[$sh,\"0c\"] | sh= RETURN_SHORTHAND -> BYTE[$sh,\"0d\"] | sh= ESCAPE_SHORTHAND -> BYTE[$sh,\"1b\"] );
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

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:191:2: (sh= TAB_SHORTHAND -> BYTE[$sh,\"09\"] | sh= NEWLINE_SHORTHAND -> BYTE[$sh,\"0a\"] | sh= VERTICAL_TAB_SHORTHAND -> BYTE[$sh,\"0b\"] | sh= FORM_FEED_SHORTHAND -> BYTE[$sh,\"0c\"] | sh= RETURN_SHORTHAND -> BYTE[$sh,\"0d\"] | sh= ESCAPE_SHORTHAND -> BYTE[$sh,\"1b\"] )
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:191:4: sh= TAB_SHORTHAND
                    {
                    sh=(Token)match(input,TAB_SHORTHAND,FOLLOW_TAB_SHORTHAND_in_byte_shorthand1229);  
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
                    // 191:22: -> BYTE[$sh,\"09\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "09"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:192:4: sh= NEWLINE_SHORTHAND
                    {
                    sh=(Token)match(input,NEWLINE_SHORTHAND,FOLLOW_NEWLINE_SHORTHAND_in_byte_shorthand1242);  
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
                    // 192:26: -> BYTE[$sh,\"0a\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "0a"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:193:4: sh= VERTICAL_TAB_SHORTHAND
                    {
                    sh=(Token)match(input,VERTICAL_TAB_SHORTHAND,FOLLOW_VERTICAL_TAB_SHORTHAND_in_byte_shorthand1255);  
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
                    // 193:30: -> BYTE[$sh,\"0b\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "0b"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:194:4: sh= FORM_FEED_SHORTHAND
                    {
                    sh=(Token)match(input,FORM_FEED_SHORTHAND,FOLLOW_FORM_FEED_SHORTHAND_in_byte_shorthand1267);  
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
                    // 194:28: -> BYTE[$sh,\"0c\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "0c"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:195:4: sh= RETURN_SHORTHAND
                    {
                    sh=(Token)match(input,RETURN_SHORTHAND,FOLLOW_RETURN_SHORTHAND_in_byte_shorthand1280);  
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
                    // 195:25: -> BYTE[$sh,\"0d\"]
                    {
                        adaptor.addChild(root_0, (Object)adaptor.create(BYTE, sh, "0d"));

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 6 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:196:4: sh= ESCAPE_SHORTHAND
                    {
                    sh=(Token)match(input,ESCAPE_SHORTHAND,FOLLOW_ESCAPE_SHORTHAND_in_byte_shorthand1293);  
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
                    // 196:25: -> BYTE[$sh,\"1b\"]
                    {
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
        return retval;
    }
    // $ANTLR end "byte_shorthand"

    public static class set_shorthand_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "set_shorthand"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:200:1: set_shorthand : (sh= DIGIT_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) | sh= NOT_DIGIT_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) | sh= WORD_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) | sh= NOT_WORD_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) | sh= WHITE_SPACE_SHORTHAND -> ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) | sh= NOT_WHITE_SPACE_SHORTHAND -> ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) );
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

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:201:2: (sh= DIGIT_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) | sh= NOT_DIGIT_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ) | sh= WORD_SHORTHAND -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) | sh= NOT_WORD_SHORTHAND -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] ) | sh= WHITE_SPACE_SHORTHAND -> ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) | sh= NOT_WHITE_SPACE_SHORTHAND -> ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] ) )
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:201:4: sh= DIGIT_SHORTHAND
                    {
                    sh=(Token)match(input,DIGIT_SHORTHAND,FOLLOW_DIGIT_SHORTHAND_in_set_shorthand1315);  
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
                    // 201:24: -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:201:27: ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:201:33: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'0'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:202:4: sh= NOT_DIGIT_SHORTHAND
                    {
                    sh=(Token)match(input,NOT_DIGIT_SHORTHAND,FOLLOW_NOT_DIGIT_SHORTHAND_in_set_shorthand1339);  
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
                    // 202:28: -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:202:31: ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INVERTED_SET, "INVERTED_SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:202:46: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'0'"));
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:203:4: sh= WORD_SHORTHAND
                    {
                    sh=(Token)match(input,WORD_SHORTHAND,FOLLOW_WORD_SHORTHAND_in_set_shorthand1363);  
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
                    // 203:23: -> ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:203:26: ^( SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:203:32: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'0'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:203:73: ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'a'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:203:114: ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'A'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "'_'"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 4 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:204:4: sh= NOT_WORD_SHORTHAND
                    {
                    sh=(Token)match(input,NOT_WORD_SHORTHAND,FOLLOW_NOT_WORD_SHORTHAND_in_set_shorthand1410);  
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
                    // 204:27: -> ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:204:30: ^( INVERTED_SET ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] ) ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] ) ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] ) BYTE[$sh,\"'_'\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INVERTED_SET, "INVERTED_SET"), root_1);

                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:204:45: ^( RANGE BYTE[$sh,\"'0'\"] BYTE[$sh,\"'9'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'0'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'9'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:204:86: ^( RANGE BYTE[$sh,\"'a'\"] BYTE[$sh,\"'z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'a'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:204:127: ^( RANGE BYTE[$sh,\"'A'\"] BYTE[$sh,\"'Z'\"] )
                        {
                        Object root_2 = (Object)adaptor.nil();
                        root_2 = (Object)adaptor.becomeRoot((Object)adaptor.create(RANGE, "RANGE"), root_2);

                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'A'"));
                        adaptor.addChild(root_2, (Object)adaptor.create(BYTE, sh, "'Z'"));

                        adaptor.addChild(root_1, root_2);
                        }
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "'_'"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 5 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:205:4: sh= WHITE_SPACE_SHORTHAND
                    {
                    sh=(Token)match(input,WHITE_SPACE_SHORTHAND,FOLLOW_WHITE_SPACE_SHORTHAND_in_set_shorthand1457);  
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
                    // 205:29: -> ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:205:32: ^( SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(SET, "SET"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "09"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "0a"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "0d"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "' '"));

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }
                    break;
                case 6 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:206:4: sh= NOT_WHITE_SPACE_SHORTHAND
                    {
                    sh=(Token)match(input,NOT_WHITE_SPACE_SHORTHAND,FOLLOW_NOT_WHITE_SPACE_SHORTHAND_in_set_shorthand1482);  
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
                    // 206:33: -> ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:206:36: ^( INVERTED_SET BYTE[$sh,\"09\"] BYTE[$sh,\"0a\"] BYTE[$sh,\"0d\"] BYTE[$sh,\"' '\"] )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(INVERTED_SET, "INVERTED_SET"), root_1);

                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "09"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "0a"));
                        adaptor.addChild(root_1, (Object)adaptor.create(BYTE, sh, "0d"));
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
        return retval;
    }
    // $ANTLR end "set_shorthand"

    public static class case_insensitive_string_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "case_insensitive_string"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:209:1: case_insensitive_string : CASE_INSENSITIVE_STRING ;
    public final regularExpressionParser.case_insensitive_string_return case_insensitive_string() throws RecognitionException {
        regularExpressionParser.case_insensitive_string_return retval = new regularExpressionParser.case_insensitive_string_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CASE_INSENSITIVE_STRING40=null;

        Object CASE_INSENSITIVE_STRING40_tree=null;

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:210:2: ( CASE_INSENSITIVE_STRING )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:210:4: CASE_INSENSITIVE_STRING
            {
            root_0 = (Object)adaptor.nil();

            CASE_INSENSITIVE_STRING40=(Token)match(input,CASE_INSENSITIVE_STRING,FOLLOW_CASE_INSENSITIVE_STRING_in_case_insensitive_string1512); 
            CASE_INSENSITIVE_STRING40_tree = (Object)adaptor.create(CASE_INSENSITIVE_STRING40);
            adaptor.addChild(root_0, CASE_INSENSITIVE_STRING40_tree);


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
        return retval;
    }
    // $ANTLR end "case_insensitive_string"

    public static class case_sensitive_string_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "case_sensitive_string"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:214:1: case_sensitive_string : CASE_SENSITIVE_STRING ;
    public final regularExpressionParser.case_sensitive_string_return case_sensitive_string() throws RecognitionException {
        regularExpressionParser.case_sensitive_string_return retval = new regularExpressionParser.case_sensitive_string_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token CASE_SENSITIVE_STRING41=null;

        Object CASE_SENSITIVE_STRING41_tree=null;

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:215:2: ( CASE_SENSITIVE_STRING )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:215:4: CASE_SENSITIVE_STRING
            {
            root_0 = (Object)adaptor.nil();

            CASE_SENSITIVE_STRING41=(Token)match(input,CASE_SENSITIVE_STRING,FOLLOW_CASE_SENSITIVE_STRING_in_case_sensitive_string1526); 
            CASE_SENSITIVE_STRING41_tree = (Object)adaptor.create(CASE_SENSITIVE_STRING41);
            adaptor.addChild(root_0, CASE_SENSITIVE_STRING41_tree);


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
        return retval;
    }
    // $ANTLR end "case_sensitive_string"

    public static class group_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "group"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:219:1: group : OPEN regex CLOSE -> regex ;
    public final regularExpressionParser.group_return group() throws RecognitionException {
        regularExpressionParser.group_return retval = new regularExpressionParser.group_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token OPEN42=null;
        Token CLOSE44=null;
        regularExpressionParser.regex_return regex43 = null;


        Object OPEN42_tree=null;
        Object CLOSE44_tree=null;
        RewriteRuleTokenStream stream_OPEN=new RewriteRuleTokenStream(adaptor,"token OPEN");
        RewriteRuleTokenStream stream_CLOSE=new RewriteRuleTokenStream(adaptor,"token CLOSE");
        RewriteRuleSubtreeStream stream_regex=new RewriteRuleSubtreeStream(adaptor,"rule regex");
        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:219:7: ( OPEN regex CLOSE -> regex )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:219:9: OPEN regex CLOSE
            {
            OPEN42=(Token)match(input,OPEN,FOLLOW_OPEN_in_group1539);  
            stream_OPEN.add(OPEN42);

            pushFollow(FOLLOW_regex_in_group1541);
            regex43=regex();

            state._fsp--;

            stream_regex.add(regex43.getTree());
            CLOSE44=(Token)match(input,CLOSE,FOLLOW_CLOSE_in_group1543);  
            stream_CLOSE.add(CLOSE44);



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
            // 219:26: -> regex
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
        }
        return retval;
    }
    // $ANTLR end "group"

    public static class quantifier_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "quantifier"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:223:1: quantifier : ( optional | zero_to_many | one_to_many | repeat ) ;
    public final regularExpressionParser.quantifier_return quantifier() throws RecognitionException {
        regularExpressionParser.quantifier_return retval = new regularExpressionParser.quantifier_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        regularExpressionParser.optional_return optional45 = null;

        regularExpressionParser.zero_to_many_return zero_to_many46 = null;

        regularExpressionParser.one_to_many_return one_to_many47 = null;

        regularExpressionParser.repeat_return repeat48 = null;



        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:224:2: ( ( optional | zero_to_many | one_to_many | repeat ) )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:225:2: ( optional | zero_to_many | one_to_many | repeat )
            {
            root_0 = (Object)adaptor.nil();

            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:225:2: ( optional | zero_to_many | one_to_many | repeat )
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:225:4: optional
                    {
                    pushFollow(FOLLOW_optional_in_quantifier1564);
                    optional45=optional();

                    state._fsp--;

                    adaptor.addChild(root_0, optional45.getTree());

                    }
                    break;
                case 2 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:226:5: zero_to_many
                    {
                    pushFollow(FOLLOW_zero_to_many_in_quantifier1573);
                    zero_to_many46=zero_to_many();

                    state._fsp--;

                    adaptor.addChild(root_0, zero_to_many46.getTree());

                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:227:5: one_to_many
                    {
                    pushFollow(FOLLOW_one_to_many_in_quantifier1583);
                    one_to_many47=one_to_many();

                    state._fsp--;

                    adaptor.addChild(root_0, one_to_many47.getTree());

                    }
                    break;
                case 4 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:228:5: repeat
                    {
                    pushFollow(FOLLOW_repeat_in_quantifier1589);
                    repeat48=repeat();

                    state._fsp--;

                    adaptor.addChild(root_0, repeat48.getTree());

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
        }
        return retval;
    }
    // $ANTLR end "quantifier"

    public static class repeat_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "repeat"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:233:1: repeat : OPEN_CURLY n1= NUMBER ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) ) CLOSE_CURLY ;
    public final regularExpressionParser.repeat_return repeat() throws RecognitionException {
        regularExpressionParser.repeat_return retval = new regularExpressionParser.repeat_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token n1=null;
        Token n2=null;
        Token OPEN_CURLY49=null;
        Token REPEAT_SEPARATOR50=null;
        Token REPEAT_SEPARATOR51=null;
        Token MANY52=null;
        Token CLOSE_CURLY53=null;

        Object n1_tree=null;
        Object n2_tree=null;
        Object OPEN_CURLY49_tree=null;
        Object REPEAT_SEPARATOR50_tree=null;
        Object REPEAT_SEPARATOR51_tree=null;
        Object MANY52_tree=null;
        Object CLOSE_CURLY53_tree=null;
        RewriteRuleTokenStream stream_OPEN_CURLY=new RewriteRuleTokenStream(adaptor,"token OPEN_CURLY");
        RewriteRuleTokenStream stream_REPEAT_SEPARATOR=new RewriteRuleTokenStream(adaptor,"token REPEAT_SEPARATOR");
        RewriteRuleTokenStream stream_CLOSE_CURLY=new RewriteRuleTokenStream(adaptor,"token CLOSE_CURLY");
        RewriteRuleTokenStream stream_MANY=new RewriteRuleTokenStream(adaptor,"token MANY");
        RewriteRuleTokenStream stream_NUMBER=new RewriteRuleTokenStream(adaptor,"token NUMBER");

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:233:8: ( OPEN_CURLY n1= NUMBER ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) ) CLOSE_CURLY )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:233:10: OPEN_CURLY n1= NUMBER ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) ) CLOSE_CURLY
            {
            OPEN_CURLY49=(Token)match(input,OPEN_CURLY,FOLLOW_OPEN_CURLY_in_repeat1605);  
            stream_OPEN_CURLY.add(OPEN_CURLY49);

            n1=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_repeat1609);  
            stream_NUMBER.add(n1);

            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:234:2: ( ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) ) | ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) ) | ( -> ^( REPEAT $n1 $n1) ) )
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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:235:3: ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) )
                    {
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:235:3: ( REPEAT_SEPARATOR n2= NUMBER -> ^( REPEAT $n1 $n2) )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:235:5: REPEAT_SEPARATOR n2= NUMBER
                    {
                    REPEAT_SEPARATOR50=(Token)match(input,REPEAT_SEPARATOR,FOLLOW_REPEAT_SEPARATOR_in_repeat1620);  
                    stream_REPEAT_SEPARATOR.add(REPEAT_SEPARATOR50);

                    n2=(Token)match(input,NUMBER,FOLLOW_NUMBER_in_repeat1624);  
                    stream_NUMBER.add(n2);



                    // AST REWRITE
                    // elements: n1, n2
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
                    // 235:33: -> ^( REPEAT $n1 $n2)
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:235:36: ^( REPEAT $n1 $n2)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(REPEAT, "REPEAT"), root_1);

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
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:237:3: ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) )
                    {
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:237:3: ( REPEAT_SEPARATOR MANY -> ^( REPEAT $n1 MANY ) )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:237:5: REPEAT_SEPARATOR MANY
                    {
                    REPEAT_SEPARATOR51=(Token)match(input,REPEAT_SEPARATOR,FOLLOW_REPEAT_SEPARATOR_in_repeat1650);  
                    stream_REPEAT_SEPARATOR.add(REPEAT_SEPARATOR51);

                    MANY52=(Token)match(input,MANY,FOLLOW_MANY_in_repeat1652);  
                    stream_MANY.add(MANY52);



                    // AST REWRITE
                    // elements: n1, MANY
                    // token labels: n1
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleTokenStream stream_n1=new RewriteRuleTokenStream(adaptor,"token n1",n1);
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (Object)adaptor.nil();
                    // 237:28: -> ^( REPEAT $n1 MANY )
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:237:31: ^( REPEAT $n1 MANY )
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(REPEAT, "REPEAT"), root_1);

                        adaptor.addChild(root_1, stream_n1.nextNode());
                        adaptor.addChild(root_1, stream_MANY.nextNode());

                        adaptor.addChild(root_0, root_1);
                        }

                    }

                    retval.tree = root_0;
                    }


                    }
                    break;
                case 3 :
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:239:3: ( -> ^( REPEAT $n1 $n1) )
                    {
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:239:3: ( -> ^( REPEAT $n1 $n1) )
                    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:239:8: 
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
                    // 239:8: -> ^( REPEAT $n1 $n1)
                    {
                        // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:239:11: ^( REPEAT $n1 $n1)
                        {
                        Object root_1 = (Object)adaptor.nil();
                        root_1 = (Object)adaptor.becomeRoot((Object)adaptor.create(REPEAT, "REPEAT"), root_1);

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

            CLOSE_CURLY53=(Token)match(input,CLOSE_CURLY,FOLLOW_CLOSE_CURLY_in_repeat1701);  
            stream_CLOSE_CURLY.add(CLOSE_CURLY53);


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
        return retval;
    }
    // $ANTLR end "repeat"

    public static class optional_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "optional"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:245:1: optional : QUESTION_MARK ;
    public final regularExpressionParser.optional_return optional() throws RecognitionException {
        regularExpressionParser.optional_return retval = new regularExpressionParser.optional_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token QUESTION_MARK54=null;

        Object QUESTION_MARK54_tree=null;

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:245:9: ( QUESTION_MARK )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:245:11: QUESTION_MARK
            {
            root_0 = (Object)adaptor.nil();

            QUESTION_MARK54=(Token)match(input,QUESTION_MARK,FOLLOW_QUESTION_MARK_in_optional1712); 
            QUESTION_MARK54_tree = (Object)adaptor.create(QUESTION_MARK54);
            adaptor.addChild(root_0, QUESTION_MARK54_tree);


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
        return retval;
    }
    // $ANTLR end "optional"

    public static class zero_to_many_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "zero_to_many"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:249:1: zero_to_many : MANY ;
    public final regularExpressionParser.zero_to_many_return zero_to_many() throws RecognitionException {
        regularExpressionParser.zero_to_many_return retval = new regularExpressionParser.zero_to_many_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token MANY55=null;

        Object MANY55_tree=null;

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:250:2: ( MANY )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:250:4: MANY
            {
            root_0 = (Object)adaptor.nil();

            MANY55=(Token)match(input,MANY,FOLLOW_MANY_in_zero_to_many1724); 
            MANY55_tree = (Object)adaptor.create(MANY55);
            adaptor.addChild(root_0, MANY55_tree);


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
        return retval;
    }
    // $ANTLR end "zero_to_many"

    public static class one_to_many_return extends ParserRuleReturnScope {
        Object tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "one_to_many"
    // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:254:1: one_to_many : PLUS ;
    public final regularExpressionParser.one_to_many_return one_to_many() throws RecognitionException {
        regularExpressionParser.one_to_many_return retval = new regularExpressionParser.one_to_many_return();
        retval.start = input.LT(1);

        Object root_0 = null;

        Token PLUS56=null;

        Object PLUS56_tree=null;

        try {
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:255:2: ( PLUS )
            // /home/matt/dev/search/regex/src/net/domesdaybook/expression/parser/regularExpression.g:255:4: PLUS
            {
            root_0 = (Object)adaptor.nil();

            PLUS56=(Token)match(input,PLUS,FOLLOW_PLUS_in_one_to_many1737); 
            PLUS56_tree = (Object)adaptor.create(PLUS56);
            adaptor.addChild(root_0, PLUS56_tree);


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
        return retval;
    }
    // $ANTLR end "one_to_many"

    // Delegated rules


    protected DFA6 dfa6 = new DFA6(this);
    protected DFA3 dfa3 = new DFA3(this);
    protected DFA10 dfa10 = new DFA10(this);
    static final String DFA6_eotS =
        "\26\uffff";
    static final String DFA6_eofS =
        "\26\uffff";
    static final String DFA6_minS =
        "\1\15\23\0\2\uffff";
    static final String DFA6_maxS =
        "\1\64\23\0\2\uffff";
    static final String DFA6_acceptS =
        "\24\uffff\1\1\1\2";
    static final String DFA6_specialS =
        "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1"+
        "\16\1\17\1\20\1\21\1\22\1\23\2\uffff}>";
    static final String[] DFA6_transitionS = {
            "\1\1\1\2\1\3\3\uffff\1\21\1\20\22\uffff\1\4\1\5\1\6\1\7\1\10"+
            "\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\22\1\23",
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
            return "75:1: sequence : ({...}? => ( ( quantified_atom -> quantified_atom ) ( sequence -> ^( SEQUENCE quantified_atom sequence ) )? ) | {...}? => ( quantified_atom ( ( ( quantified_atom )+ -> ^( SEQUENCE ( quantified_atom )+ ) ) | ( -> ^( quantified_atom ) ) ) ) );";
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
                        if ( (LA6_0==BYTE) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 1;}

                        else if ( (LA6_0==FULL_STOP) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 2;}

                        else if ( (LA6_0==OPEN_SQUARE) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 3;}

                        else if ( (LA6_0==TAB_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 4;}

                        else if ( (LA6_0==NEWLINE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 5;}

                        else if ( (LA6_0==VERTICAL_TAB_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 6;}

                        else if ( (LA6_0==FORM_FEED_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 7;}

                        else if ( (LA6_0==RETURN_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 8;}

                        else if ( (LA6_0==ESCAPE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 9;}

                        else if ( (LA6_0==DIGIT_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 10;}

                        else if ( (LA6_0==NOT_DIGIT_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 11;}

                        else if ( (LA6_0==WORD_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 12;}

                        else if ( (LA6_0==NOT_WORD_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 13;}

                        else if ( (LA6_0==WHITE_SPACE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 14;}

                        else if ( (LA6_0==NOT_WHITE_SPACE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 15;}

                        else if ( (LA6_0==AMPERSAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 16;}

                        else if ( (LA6_0==CASE_SENSITIVE_STRING) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 17;}

                        else if ( (LA6_0==CASE_INSENSITIVE_STRING) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 18;}

                        else if ( (LA6_0==OPEN) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 19;}

                         
                        input.seek(index6_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA6_1 = input.LA(1);

                         
                        int index6_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA6_2 = input.LA(1);

                         
                        int index6_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA6_3 = input.LA(1);

                         
                        int index6_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA6_4 = input.LA(1);

                         
                        int index6_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_4);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA6_5 = input.LA(1);

                         
                        int index6_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_5);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA6_6 = input.LA(1);

                         
                        int index6_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_6);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA6_7 = input.LA(1);

                         
                        int index6_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_7);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA6_8 = input.LA(1);

                         
                        int index6_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_8);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA6_9 = input.LA(1);

                         
                        int index6_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_9);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA6_10 = input.LA(1);

                         
                        int index6_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_10);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA6_11 = input.LA(1);

                         
                        int index6_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_11);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA6_12 = input.LA(1);

                         
                        int index6_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_12);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA6_13 = input.LA(1);

                         
                        int index6_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_13);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA6_14 = input.LA(1);

                         
                        int index6_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_14);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA6_15 = input.LA(1);

                         
                        int index6_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_15);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA6_16 = input.LA(1);

                         
                        int index6_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_16);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA6_17 = input.LA(1);

                         
                        int index6_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_17);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA6_18 = input.LA(1);

                         
                        int index6_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_18);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA6_19 = input.LA(1);

                         
                        int index6_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((sequencesAsTree)) ) {s = 20;}

                        else if ( ((!sequencesAsTree)) ) {s = 21;}

                         
                        input.seek(index6_19);
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
        "\25\uffff";
    static final String DFA3_eofS =
        "\1\24\24\uffff";
    static final String DFA3_minS =
        "\1\14\24\uffff";
    static final String DFA3_maxS =
        "\1\65\24\uffff";
    static final String DFA3_acceptS =
        "\1\uffff\23\1\1\2";
    static final String DFA3_specialS =
        "\1\0\24\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\24\1\1\1\2\1\3\3\uffff\1\21\1\20\22\uffff\1\4\1\5\1\6\1\7"+
            "\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\22\1\23\1\24",
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
            return "79:3: ( sequence -> ^( SEQUENCE quantified_atom sequence ) )?";
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
                        if ( (LA3_0==BYTE) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 1;}

                        else if ( (LA3_0==FULL_STOP) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 2;}

                        else if ( (LA3_0==OPEN_SQUARE) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 3;}

                        else if ( (LA3_0==TAB_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 4;}

                        else if ( (LA3_0==NEWLINE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 5;}

                        else if ( (LA3_0==VERTICAL_TAB_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 6;}

                        else if ( (LA3_0==FORM_FEED_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 7;}

                        else if ( (LA3_0==RETURN_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 8;}

                        else if ( (LA3_0==ESCAPE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 9;}

                        else if ( (LA3_0==DIGIT_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 10;}

                        else if ( (LA3_0==NOT_DIGIT_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 11;}

                        else if ( (LA3_0==WORD_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 12;}

                        else if ( (LA3_0==NOT_WORD_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 13;}

                        else if ( (LA3_0==WHITE_SPACE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 14;}

                        else if ( (LA3_0==NOT_WHITE_SPACE_SHORTHAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 15;}

                        else if ( (LA3_0==AMPERSAND) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 16;}

                        else if ( (LA3_0==CASE_SENSITIVE_STRING) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 17;}

                        else if ( (LA3_0==CASE_INSENSITIVE_STRING) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 18;}

                        else if ( (LA3_0==OPEN) && (((!sequencesAsTree)||(sequencesAsTree)))) {s = 19;}

                        else if ( (LA3_0==EOF||LA3_0==ALT||LA3_0==CLOSE) ) {s = 20;}

                         
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
        "\15\uffff";
    static final String DFA10_eofS =
        "\15\uffff";
    static final String DFA10_minS =
        "\1\15\1\uffff\1\15\3\uffff\1\15\6\uffff";
    static final String DFA10_maxS =
        "\1\63\1\uffff\1\63\3\uffff\1\63\6\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\12\1\uffff\1\2\1\3\1\4\1\uffff\1\6\1\10\1\11\1\1\1\7"+
        "\1\5";
    static final String DFA10_specialS =
        "\15\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\2\1\uffff\1\11\1\uffff\1\1\1\uffff\1\6\1\10\22\5\6\3\6\4"+
            "\1\7",
            "",
            "\1\12\1\uffff\1\12\1\uffff\1\12\1\13\41\12",
            "",
            "",
            "",
            "\1\14\1\uffff\1\14\1\uffff\1\14\1\13\41\14",
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
            return "()+ loopback of 138:2: ( hexbyte | byte_shorthand | set_shorthand | mnemonic | case_sensitive_string | case_insensitive_string | byte_range | bitmask | byte_set )+";
        }
    }
 

    public static final BitSet FOLLOW_regex_in_start143 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_start145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_regex164 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_ALT_in_regex178 = new BitSet(new long[]{0x001FFF800018E000L});
    public static final BitSet FOLLOW_sequence_in_regex180 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_quantified_atom_in_sequence246 = new BitSet(new long[]{0x001FFF800018E002L});
    public static final BitSet FOLLOW_sequence_in_sequence258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_quantified_atom_in_sequence292 = new BitSet(new long[]{0x001FFF800018E002L});
    public static final BitSet FOLLOW_quantified_atom_in_sequence303 = new BitSet(new long[]{0x001FFF800018E002L});
    public static final BitSet FOLLOW_atom_in_quantified_atom363 = new BitSet(new long[]{0x1A40000000000002L});
    public static final BitSet FOLLOW_quantifier_in_quantified_atom368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hexbyte_in_atom410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_any_byte_in_atom415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_set_in_atom420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_byte_shorthand_in_atom425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_shorthand_in_atom430 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bitmask_in_atom435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_sensitive_string_in_atom440 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_case_insensitive_string_in_atom445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_group_in_atom450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BYTE_in_hexbyte465 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FULL_STOP_in_any_byte475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_SQUARE_in_byte_set493 = new BitSet(new long[]{0x000FFFFFFFF9A000L});
    public static final BitSet FOLLOW_CARET_in_byte_set504 = new BitSet(new long[]{0x000FFFFFFFF9A000L});
    public static final BitSet FOLLOW_set_specification_in_byte_set506 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_set_specification_in_byte_set531 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_CLOSE_SQUARE_in_byte_set553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hexbyte_in_set_specification569 = new BitSet(new long[]{0x000FFFFFFFF8A002L});
    public static final BitSet FOLLOW_byte_shorthand_in_set_specification574 = new BitSet(new long[]{0x000FFFFFFFF8A002L});
    public static final BitSet FOLLOW_set_shorthand_in_set_specification579 = new BitSet(new long[]{0x000FFFFFFFF8A002L});
    public static final BitSet FOLLOW_mnemonic_in_set_specification584 = new BitSet(new long[]{0x000FFFFFFFF8A002L});
    public static final BitSet FOLLOW_case_sensitive_string_in_set_specification590 = new BitSet(new long[]{0x000FFFFFFFF8A002L});
    public static final BitSet FOLLOW_case_insensitive_string_in_set_specification595 = new BitSet(new long[]{0x000FFFFFFFF8A002L});
    public static final BitSet FOLLOW_byte_range_in_set_specification600 = new BitSet(new long[]{0x000FFFFFFFF8A002L});
    public static final BitSet FOLLOW_bitmask_in_set_specification605 = new BitSet(new long[]{0x000FFFFFFFF8A002L});
    public static final BitSet FOLLOW_byte_set_in_set_specification610 = new BitSet(new long[]{0x000FFFFFFFF8A002L});
    public static final BitSet FOLLOW_range_values_in_byte_range629 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_RANGE_SEPARATOR_in_byte_range633 = new BitSet(new long[]{0x0000000000082000L});
    public static final BitSet FOLLOW_range_values_in_byte_range640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_range_values0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AMPERSAND_in_bitmask682 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_BYTE_in_bitmask684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_ASCII_in_mnemonic708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_PRINT_in_mnemonic733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_GRAPH_in_mnemonic758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_WORD_in_mnemonic783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_ALPHANUM_in_mnemonic830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_ALPHA_in_mnemonic874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_UPPER_in_mnemonic908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_LOWER_in_mnemonic932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_PUNCT_in_mnemonic956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_HEXDIGIT_in_mnemonic1010 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_DIGIT_in_mnemonic1054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_WHITESPACE_in_mnemonic1078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_BLANK_in_mnemonic1103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_SPACE_in_mnemonic1123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_TAB_in_mnemonic1155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_NEWLINE_in_mnemonic1168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_RETURN_in_mnemonic1181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SET_CONTROL_in_mnemonic1194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TAB_SHORTHAND_in_byte_shorthand1229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEWLINE_SHORTHAND_in_byte_shorthand1242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VERTICAL_TAB_SHORTHAND_in_byte_shorthand1255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORM_FEED_SHORTHAND_in_byte_shorthand1267 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RETURN_SHORTHAND_in_byte_shorthand1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ESCAPE_SHORTHAND_in_byte_shorthand1293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIGIT_SHORTHAND_in_set_shorthand1315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_DIGIT_SHORTHAND_in_set_shorthand1339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WORD_SHORTHAND_in_set_shorthand1363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_WORD_SHORTHAND_in_set_shorthand1410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHITE_SPACE_SHORTHAND_in_set_shorthand1457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_WHITE_SPACE_SHORTHAND_in_set_shorthand1482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_INSENSITIVE_STRING_in_case_insensitive_string1512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CASE_SENSITIVE_STRING_in_case_sensitive_string1526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_in_group1539 = new BitSet(new long[]{0x001FFF800018E000L});
    public static final BitSet FOLLOW_regex_in_group1541 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_CLOSE_in_group1543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_optional_in_quantifier1564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_zero_to_many_in_quantifier1573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_one_to_many_in_quantifier1583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_repeat_in_quantifier1589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OPEN_CURLY_in_repeat1605 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_NUMBER_in_repeat1609 = new BitSet(new long[]{0x0500000000000000L});
    public static final BitSet FOLLOW_REPEAT_SEPARATOR_in_repeat1620 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_NUMBER_in_repeat1624 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_REPEAT_SEPARATOR_in_repeat1650 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_MANY_in_repeat1652 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_CLOSE_CURLY_in_repeat1701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUESTION_MARK_in_optional1712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MANY_in_zero_to_many1724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_one_to_many1737 = new BitSet(new long[]{0x0000000000000002L});

}