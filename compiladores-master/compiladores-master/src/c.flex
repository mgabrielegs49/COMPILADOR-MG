package src;

import java_cup.runtime.*;

%%

%{
    private ErrorList listaErros;

    public LexicalAnalyzer(java.io.FileReader in, ErrorList listaErros){
        this(in);
        this.listaErros = listaErros;
    }

    public ErrorList getListaErros() {
        return listaErros;
    }

    private CToken createToken(String name, String value) {
        return new CToken(name, value, yyline, yycolumn);
    }
%}

%public
%class LexicalAnalyzer
%type CToken
%line
%column

// Expressões regulares
inteiro = 0|[1-9][0-9]*

// Palavras-chave
program = "program"
void = "void"
main = "main"
int = "int"
printf = "printf"
scanf = "scanf"

// Operadores
SOMA = "+"
SUBTRACAO = "-"
MULTIPLICACAO = "*"
DIVISAO = "/"
IGUAL = "="

// Outros símbolos
porcentagem = "%"
ecomercial = "&"
pontoevirgula = ";"
virgula = ","
abrePar = "("
fechaPar = ")"
abreChave = "{"
fechaChave = "}"
doisPontos = ":"
STRING = \"([^\"\\]|\\.)*\"

// Identificador
ID = [a-zA-Z_][a-zA-Z0-9_]*

// Espaços em branco
brancos = [ \t\n\r]+

%%

// Palavras-chave — colocar antes de ID
{program}       { return createToken("program", yytext()); }
{void}          { return createToken("void", yytext()); }
{main}          { return createToken("main", yytext()); }
{int}           { return createToken("int", yytext()); }
{printf}        { return createToken("printf", yytext()); }
{scanf}         { return createToken("scanf", yytext()); }

// Tokens
{STRING}        { return createToken("STRING", yytext()); }
{inteiro}       { return createToken("inteiro", yytext()); }
{SOMA}          { return createToken("SOMA", yytext()); }
{SUBTRACAO}     { return createToken("SUBTRACAO", yytext()); }
{MULTIPLICACAO} { return createToken("MULTIPLICACAO", yytext()); }
{DIVISAO}       { return createToken("DIVISAO", yytext()); }
{IGUAL}         { return createToken("IGUAL", yytext()); }

{porcentagem}   { return createToken("porcentagem", yytext()); }
{ecomercial}    { return createToken("ecomercial", yytext()); }
{pontoevirgula} { return createToken("pontoevirgula", yytext()); }
{virgula}       { return createToken("virgula", yytext()); }
{abrePar}       { return createToken("parenteseesquerdo", yytext()); }
{fechaPar}      { return createToken("parentesedireito", yytext()); }
{abreChave}     { return createToken("chaveesquerda", yytext()); }
{fechaChave}    { return createToken("chavedireita", yytext()); }
{doisPontos}    { return createToken("doispontos", yytext()); }

{ID}            { return createToken("ID", yytext()); }
{brancos}       { /* ignora */ }

. { listaErros.addError("Caractere inválido: '" + yytext() + "'", yyline, yycolumn); }
