package src;

import java.io.File;

public class GeneratorC {
    public static void main(String[] args) {
        File sourceCode = new File(new File("").getAbsolutePath() + "\\c.lex");
        jflex.Main.generate(sourceCode);
    }
}
