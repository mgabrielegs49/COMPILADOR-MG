package src;

public class ParserError {
    private String message;
    private int line;
    private int column;

    public ParserError(String message, int line, int column) {
        this.message = message;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Erro [linha " + line + ", coluna " + column + "]: " + message;
    }
}
