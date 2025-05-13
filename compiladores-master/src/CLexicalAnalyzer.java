package src;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CLexicalAnalyzer {
    public static void main(String[] args) throws IOException {
        String path = new File("").getAbsolutePath() + "\\src\\program.c";
        ErrorList listaErros = new ErrorList();
        LexicalAnalyzer lexer = new LexicalAnalyzer(new FileReader(path), listaErros);
        // CToken token;

        // while ((token = lexer.yylex()) != null) {
        //     System.out.println("<" + token.name + ", " + token.value + "> (" + token.line + " - " + token.column + ")");
        // }
        Parser parser = new Parser(lexer, listaErros);
        parser.parse();
    }
}
