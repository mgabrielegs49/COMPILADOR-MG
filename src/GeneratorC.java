package src;

public class GeneratorC {
    public static void main(String[] args) {
        String[] jflexArgs = {"-d", "src", "c.flex"};
        jflex.Main.main(jflexArgs);
    }
}