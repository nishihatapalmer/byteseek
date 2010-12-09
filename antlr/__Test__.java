import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.debug.DebugEventSocketProxy;


public class __Test__ {

    public static void main(String args[]) throws Exception {
        regularExpressionLexer lex = new regularExpressionLexer(new ANTLRFileStream("/home/matt/dev/search/regex/antlr/__Test___input.txt", "UTF8"));
        CommonTokenStream tokens = new CommonTokenStream(lex);

        regularExpressionParser g = new regularExpressionParser(tokens, 49154, null);
        try {
            g.start();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
    }
}