import java.io.*;
import org.antlr.runtime.*;
import org.antlr.runtime.debug.DebugEventSocketProxy;


public class __Test__ {

    public static void main(String args[]) throws Exception {
        regularExpressionLexer lex = new regularExpressionLexer(new ANTLRFileStream("/home/matt/dev/search/byteseek/antlr/__Test___input.txt"));
        CommonTokenStream tokens = new CommonTokenStream(lex);

        regularExpressionParser g = new regularExpressionParser(tokens, 49152, null);
        try {
            g.start();
        } catch (RecognitionException e) {
            e.printStackTrace();
        }
    }
}