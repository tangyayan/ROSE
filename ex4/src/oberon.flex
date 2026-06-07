
import exceptions.*;

%%

%public
%class OberonScanner
%yylexthrow LexicalException

%unicode
%ignorecase

%line
%column

// %debug
%type Symbols

%{
    private String debuginfo(String type) {
        return type + ": at line " + (yyline+1) + ", column " + (yycolumn+1) + ": " + yytext() + "\n";
    }

    private Symbols symbol(int type) {
        return new Symbols(type, yyline+1, yycolumn+1);
    }

    private Symbols symbol(int type, Object value) {
        return new Symbols(type, yyline+1, yycolumn+1, value);
    }
%}

Letter=[A-Za-z]
Digit=[0-9]
Identifier={Letter}({Letter}|{Digit})*
ErrorInteger={Digit}+ {Identifier}
DecInteger=[1-9]{Digit}*
OctInteger=0{Digit}*

LineTerminator = \r|\n|\r\n
WhiteSpace={LineTerminator}|[ \t\f]

%state COMMENT
%%

<YYINITIAL> {
    /* Reserved Words */
    "MODULE"    { return symbol(Token.MODULE); }
    "PROCEDURE" { return symbol(Token.PROCEDURE); }
    "BEGIN"     { return symbol(Token.BEGIN); }
    "END"       { return symbol(Token.END); }
    "CONST"     { return symbol(Token.CONST); }
    "TYPE"      { return symbol(Token.TYPE); }
    "VAR"       { return symbol(Token.VAR); }
    "ARRAY"     { return symbol(Token.ARRAY); }
    "OF"        { return symbol(Token.OF); }
    "RECORD"    { return symbol(Token.RECORD); }
    "WHILE"     { return symbol(Token.WHILE); }
    "DO"        { return symbol(Token.DO); }
    "IF"        { return symbol(Token.IF); }
    "THEN"      { return symbol(Token.THEN); }
    "ELSIF"     { return symbol(Token.ELSIF); }
    "ELSE"      { return symbol(Token.ELSE); }

    /* Keywords */
    "INTEGER" { return symbol(Token.IDENTIFIER, "INTEGER"); }
    "BOOLEAN" { return symbol(Token.IDENTIFIER, "BOOLEAN"); }
    "READ"    { return symbol(Token.IDENTIFIER, "READ"); }
    "WRITE"   { return symbol(Token.IDENTIFIER, "WRITE"); }
    "WRITELN" { return symbol(Token.IDENTIFIER, "WRITELN"); }


    /* Whitespace */
    {WhiteSpace}+ { /* skip */ }

    "(*" { yybegin(COMMENT); }

    /* Operators */
    ":="  { return symbol(Token.ASSIGN); }
    "="   { return symbol(Token.EQ); }
    "#"   { return symbol(Token.NEQ); }
    "<="  { return symbol(Token.LEQ); }
    "<"   { return symbol(Token.LT); }
    ">="  { return symbol(Token.GEQ); }
    ">"   { return symbol(Token.GT); }
    "+"   { return symbol(Token.PLUS); }
    "-"   { return symbol(Token.MINUS); }
    "*"   { return symbol(Token.TIMES); }
    "DIV" { return symbol(Token.DIV); }
    "MOD" { return symbol(Token.MOD); }
    "&"   { return symbol(Token.AND); }
    "OR"  { return symbol(Token.OR); }
    "~"   { return symbol(Token.NOT); }

    /* Other Symbols */
    ";"   { return symbol(Token.SEMI); }
    "."   { return symbol(Token.DOT); }
    "("   { return symbol(Token.LPAREN); }
    ")"   { return symbol(Token.RPAREN); }
    ","   { return symbol(Token.COMMA); }
    "["   { return symbol(Token.LBRACK); }
    "]"   { return symbol(Token.RBRACK); }
    ":"   { return symbol(Token.COLON); }

    {Identifier} { 
        if(yylength() > 24) {
            throw new IllegalIdentifierLengthException("at line " + (yyline+1) + ", column " + (yycolumn+1));
        }
        return symbol(Token.IDENTIFIER, yytext().toUpperCase());
    }
    {ErrorInteger} { throw new IllegalIntegerException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }
    {DecInteger} { 
        if(yylength() > 12) {
            throw new IllegalIntegerRangeException("at line " + (yyline+1) + ", column " + (yycolumn+1));
        }
        return symbol(Token.INTEGER, Integer.valueOf(yytext()));
    }
    {OctInteger} { 
        if(yylength() > 12) {
            throw new IllegalIntegerRangeException("at line " + (yyline+1) + ", column " + (yycolumn+1));
        }
        String octStr = yytext();
        for (char c : octStr.toCharArray()) {
            if (c < '0' || c > '7') {
                throw new IllegalOctalException("at line " + (yyline+1) + ", column " + (yycolumn+1));
            }
        }
        return symbol(Token.INTEGER, Integer.valueOf(Integer.parseInt(octStr, 8)));
    }
}

<COMMENT> {
    // "(*" { throw new MismatchedCommentException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }
    "*)" { yybegin(YYINITIAL); }
    <<EOF>> { throw new MismatchedCommentException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }
    [^] { /* skip */ }
}

<<EOF>> { return symbol(Token.EOF); }
[^] { throw new IllegalSymbolException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }