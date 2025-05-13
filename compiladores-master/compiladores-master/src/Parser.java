package src;

import java.io.IOException;

public class Parser {
    private LexicalAnalyzer lexer;
    private CToken lookahead;
    private ErrorList errorList;
    private final SymbolTable symbolTable = new SymbolTable();

    public Parser(LexicalAnalyzer lexer, ErrorList errorList) 
        throws IOException {
            this.lexer = lexer;
            this.lookahead = lexer.yylex();
            this.errorList = errorList;
    }

    private void consume(String expected) throws IOException {
        if (lookahead == null) throw new RuntimeException("Fim inesperado");
        if (lookahead.name.equals(expected)) {
            lookahead = lexer.yylex();
        } else {
            error("Esperado: " + expected + ", encontrado: " + lookahead.name);
        }
    }

    private void error(String msg) {
        if (lookahead != null) {
            errorList.addError("Erro sintático: " + msg, lookahead.line, lookahead.column);
        } else {
            errorList.addError("Erro sintático: " + msg, -1, -1);
        }
        // throw new RuntimeException("Erro sintático [linha " + lookahead.line + ", coluna " + lookahead.column + "]: " + msg);
    }

    public void parse() throws IOException {
        programa();
        System.out.println("Análise sintática concluída com sucesso.\n");
        
        if (errorList.hasErrors()) {
            errorList.printErrors();
        } else {
            printSymbols();
        }
    }

    // program ID ; void main() ( ) { ... }
    private void programa() throws IOException {
        consume("program");
        consume("ID");
        consume("pontoevirgula");
        funcao();
    }

    private void funcao() throws IOException {
        consume("void");
        consume("main");
        consume("parenteseesquerdo");
        consume("parentesedireito");
        bloco();
    }

    private void bloco() throws IOException {
        consume("chaveesquerda");

        // múltiplas declarações ou comandos
        while (lookahead != null && (
            lookahead.name.equals("int") || // declaração
            lookahead.name.equals("printf") || // print
            lookahead.name.equals("scanf") ||  // scan
            lookahead.name.equals("ID")        // expressão
        )) {
            if (lookahead.name.equals("int")) {
                declaracaoVariavel();
            } else {
                comando();
            }
        }

        consume("chavedireita");
    }

    private void declaracaoVariavel() throws IOException {
        consume("int");
        if (lookahead.name.equals("ID")) {
            if (symbolTable.exists(lookahead.value)){
                error("Variável já declarada: " + lookahead.value);
            }
            symbolTable.declare(lookahead.value, "int");
            consume("ID");
        }

        while (lookahead.name.equals("virgula")) {
            consume("virgula");
            if (lookahead.name.equals("ID")) {
                if (symbolTable.exists(lookahead.value)){
                    error("Variável já declarada: " + lookahead.value);
                }
                symbolTable.declare(lookahead.value, "int");
                consume("ID");
            }
        }
        consume("pontoevirgula");
    }

    private void comando() throws IOException {
        switch (lookahead.name) {
            case "printf": print(); break;
            case "scanf": scan(); break;
            case "ID": expressao(); consume("pontoevirgula"); break;
            default: error("Comando inválido");
        }
    }

    private void print() throws IOException {
        consume("printf");  // Consumir "printf"
        consume("parenteseesquerdo"); // Consumir "("
        
        // Se for uma string, consome o token STRING (que pode conter "%d", "%s", etc.)
        if (lookahead.name.equals("STRING")) {
            consume("STRING");
        } else if (lookahead.name.equals("ID")) {
            consume("ID");
        }

        // Caso o printf tenha mais parâmetros (como vírgula para outro argumento)
        while (lookahead.name.equals("virgula")) {
            consume("virgula");
            if (lookahead.name.equals("STRING")) {
                consume("STRING");
            } else if (lookahead.name.equals("ID")) {
                // consume("ID");
                expr();
            }
        }

        consume("parentesedireito"); // Consumir ")"
        consume("pontoevirgula"); // Consumir ";"
    }

    private void scan() throws IOException {
        consume("scanf");  // Consumir "scanf"
        consume("parenteseesquerdo"); // Consumir "("

        // Se a string for um especificador de formato, consome o token STRING
        if (lookahead.name.equals("STRING")) {
            consume("STRING");  // Consome a string, que pode ter o formato "%d"
        }

        consume("virgula"); // Consumir a vírgula entre os parâmetros

        // Consumir o identificador (variável que recebe o valor)
        if (lookahead.name.equals("ecomercial")) {  // Verifica o "&"
            consume("ecomercial");  // Consumir "&"
        }
        consume("ID");  // Consumir o identificador (como "a", "b", etc.)

        consume("parentesedireito"); // Consumir ")"
        consume("pontoevirgula"); // Consumir ";"
    }

    private void expressao() throws IOException {
        if (!symbolTable.exists(lookahead.value)) {
            error("Variável não declarada: " + lookahead.value);
        }
        consume("ID");
        consume("IGUAL");
        expr();
    }

    private void expr() throws IOException {
        termo();
        while (lookahead.name.equals("SOMA") || lookahead.name.equals("SUBTRACAO")) {
            consume(lookahead.name);  // Consumir "+" ou "-"
            termo();  // Processar o próximo termo da expressão (pode ser outro número ou variável)
        }
    }

    private void termo() throws IOException {
        fator();  // Analisa o fator (pode ser um número, variável ou expressão entre parênteses)
        
        while (lookahead.name.equals("MULTIPLICACAO") || lookahead.name.equals("DIVISAO")) {
            consume(lookahead.name);  // Consome '*' ou '/'
            fator();  // Processa o próximo fator
        }
    }

    private void fator() throws IOException {
        switch (lookahead.name) {
            case "ID":
                consume("ID");  // Consome uma variável (como 'a', 'b', etc.)
                break;
            case "inteiro":
                consume("inteiro");  // Consome um número inteiro
                break;
            case "parenteseesquerdo":
                consume("parenteseesquerdo");  // Consome o '('
                expr();  // Analisa a expressão entre parênteses
                if (lookahead.name.equals("parentesedireito")) {
                    consume("parentesedireito");  // Consome o ')'
                } else {
                    error("Esperado: ')', encontrado: " + lookahead.name);
                }   break;
            // case "SOMA": consume("SOMA"); expr();
            //     break;
            default:
                error("Esperado: ID, inteiro ou '(', encontrado: " + lookahead.name);
                break;
        }
    }

    public void parseDeclaration(String type, String name) {
        try {
            symbolTable.declare(name, type);
            System.out.println("Declarado: " + name + " do tipo " + type);
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }

    public void printSymbols() {
        symbolTable.printTable();
    }
}
