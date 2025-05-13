package src;

import java.io.IOException;

public class Parser {
    private LexicalAnalyzer lexer;
    private CToken lookahead;
    private ErrorList errorList;
    private final SymbolTable symbolTable = new SymbolTable();

    public Parser(LexicalAnalyzer lexer, ErrorList errorList) throws IOException {
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
    
    while (lookahead != null && !lookahead.name.equals("chavedireita")) {
        if (lookahead.name.equals("int")) {
            declaracaoVariavel();
        } else {
            comando();
        }
        
        // Adicione esta verificação para evitar loops infinitos
        if (lookahead == null) {
            error("Fim inesperado do arquivo");
            break;
        }
    }
    
    consume("chavedireita");
}

    private void declaracaoVariavel() throws IOException {
        consume("int");
        declararVariavel();
        while (lookahead.name.equals("virgula")) {
            consume("virgula");
            declararVariavel();
        }
        consume("pontoevirgula");
    }

    private void declararVariavel() throws IOException {
        String varName = lookahead.value;
        if (symbolTable.exists(varName)) {
            error("Variável já declarada: " + varName);
        }
        consume("ID");

        if (lookahead.name.equals("abreColchete")) {
            novoArray(varName);
        } else if (lookahead.name.equals("IGUAL")) {
            consume("IGUAL");
            expr();
            symbolTable.declare(varName, "int");
        } else {
            symbolTable.declare(varName, "int");
        }
    }

    private void novoArray(String arrayName) throws IOException {
        consume("abreColchete");
        
        // Tamanho do array (opcional)
        int size = -1;
        if (lookahead.name.equals("inteiro")) {
            size = Integer.parseInt(lookahead.value);
            consume("inteiro");
        }
        
        consume("fechaColchete");
        
        // Inicialização
        if (lookahead.name.equals("IGUAL")) {
            consume("IGUAL");
            inicializacaoArray(arrayName, size);
        }
        
        symbolTable.declare(arrayName, "int[]", true, size);
    }

    private void inicializacaoArray(String arrayName, int declaredSize) throws IOException {
        consume("abreChave");
        int count = 0;
        
        if (lookahead.name.equals("inteiro")) {
            count++;
            consume("inteiro");
            while (lookahead.name.equals("virgula")) {
                consume("virgula");
                consume("inteiro");
                count++;
            }
        }
        
        consume("fechaChave");
        
        if (declaredSize > 0 && count != declaredSize) {
            error("Tamanho do array '" + arrayName + "' inválido. Esperado: " + declaredSize + ", Recebido: " + count);
        }
    }

private void comando() throws IOException {
    switch (lookahead.name) {
        case "printf": 
            print(); 
            break;
        case "scanf": 
            scan(); 
            break;
        case "ID": 
            expressao(); 
            consume("pontoevirgula"); 
            break;
        default: 
            error("Comando inválido: " + lookahead.name);
            // Adicione consumo de token para recuperação de erro
            lookahead = lexer.yylex();
            break;
    }
}

    private void print() throws IOException {
        consume("printf");
        consume("parenteseesquerdo");
        
        if (lookahead.name.equals("STRING")) {
            consume("STRING");
        } else {
            expr();
        }
        
        while (lookahead.name.equals("virgula")) {
            consume("virgula");
            expr();
        }
        
        consume("parentesedireito");
        consume("pontoevirgula");
    }

    private void scan() throws IOException {
        consume("scanf");
        consume("parenteseesquerdo");
        consume("STRING");
        consume("virgula");
        
        if (lookahead.name.equals("ecomercial")) {
            consume("ecomercial");
        }
        consume("ID");
        
        consume("parentesedireito");
        consume("pontoevirgula");
    }

    private void expressao() throws IOException {
        String varName = lookahead.value;
        if (!symbolTable.exists(varName)) {
            error("Variável não declarada: " + varName);
        }
        consume("ID");
        
        if (lookahead.name.equals("abreColchete")) {
            consume("abreColchete");
            expr();
            consume("fechaColchete");
        }
        
        consume("IGUAL");
        expr();
    }

    private void expr() throws IOException {
        termo();
        while (lookahead.name.equals("SOMA") || lookahead.name.equals("SUBTRACAO")) {
            consume(lookahead.name);
            termo();
        }
    }

    private void termo() throws IOException {
        fator();
        while (lookahead.name.equals("MULTIPLICACAO") || lookahead.name.equals("DIVISAO")) {
            consume(lookahead.name);
            fator();
        }
    }

    private void fator() throws IOException {
        switch (lookahead.name) {
            case "ID":
                String varName = lookahead.value;
                consume("ID");
                
                if (lookahead.name.equals("abreColchete")) {
                    consume("abreColchete");
                    expr();
                    consume("fechaColchete");
                }
                break;
                
            case "inteiro":
                consume("inteiro");
                break;
                
            case "parenteseesquerdo":
                consume("parenteseesquerdo");
                expr();
                consume("parentesedireito");
                break;
                
            default:
                error("Esperado: ID, inteiro ou '(', encontrado: " + lookahead.name);
        }
    }

    public void printSymbols() {
        symbolTable.printTable();
    }
}