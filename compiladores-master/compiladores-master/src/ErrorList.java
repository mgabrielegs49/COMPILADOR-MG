package src;

import java.util.ArrayList;
import java.util.List;

public class ErrorList {
    private List<ParserError> errors = new ArrayList<>();

    public void addError(String message, int line, int column) {
        errors.add(new ParserError(message, line, column));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void printErrors() {
        if (hasErrors()) {
            System.out.println("Erros encontrados:");
            for (ParserError error : errors) {
                System.out.println(error);
            }
        } else {
            System.out.println("Nenhum erro encontrado.");
        }
    }
}
