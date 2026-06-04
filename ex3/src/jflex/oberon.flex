
import exceptions.*;
import java_cup.runtime.*;

%%

%public
%class OberonScanner
%yylexthrow LexicalException

%unicode
%ignorecase

%line
%column

// %debug
%cup
%cupdebug

%{
    private String debuginfo(String type) {
        return type + ": at line " + (yyline+1) + ", column " + (yycolumn+1) + ": " + yytext() + "\n";
    }

    private Symbol symbol(int type) {
        return new JavaSymbol(type, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, Object value) {
        return new JavaSymbol(type, yyline+1, yycolumn+1, value);
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
    "MODULE"    { return symbol(sym.MODULE); }
    "PROCEDURE" { return symbol(sym.PROCEDURE); }
    "BEGIN"     { return symbol(sym.BEGIN); }
    "END"       { return symbol(sym.END); }
    "CONST"     { return symbol(sym.CONST); }
    "TYPE"      { return symbol(sym.TYPE); }
    "VAR"       { return symbol(sym.VAR); }
    "ARRAY"     { return symbol(sym.ARRAY); }
    "OF"        { return symbol(sym.OF); }
    "RECORD"    { return symbol(sym.RECORD); }
    "WHILE"     { return symbol(sym.WHILE); }
    "DO"        { return symbol(sym.DO); }
    "IF"        { return symbol(sym.IF); }
    "THEN"      { return symbol(sym.THEN); }
    "ELSIF"     { return symbol(sym.ELSIF); }
    "ELSE"      { return symbol(sym.ELSE); }

    /* Keywords */
    "INTEGER" { return symbol(sym.IDENTIFIER, "INTEGER"); }
    "BOOLEAN" { return symbol(sym.IDENTIFIER, "BOOLEAN"); }
    "READ"    { return symbol(sym.IDENTIFIER, "READ"); }
    "WRITE"   { return symbol(sym.IDENTIFIER, "WRITE"); }
    "WRITELN" { return symbol(sym.IDENTIFIER, "WRITELN"); }


    /* Whitespace */
    {WhiteSpace}+ { /* skip */ }

    "(*" { yybegin(COMMENT); }

    /* Operators */
    ":="  { return symbol(sym.ASSIGN); }
    "="   { return symbol(sym.EQ); }
    "#"   { return symbol(sym.NEQ); }
    "<="  { return symbol(sym.LEQ); }
    "<"   { return symbol(sym.LT); }
    ">="  { return symbol(sym.GEQ); }
    ">"   { return symbol(sym.GT); }
    "+"   { return symbol(sym.PLUS); }
    "-"   { return symbol(sym.MINUS); }
    "*"   { return symbol(sym.TIMES); }
    "DIV" { return symbol(sym.DIV); }
    "MOD" { return symbol(sym.MOD); }
    "&"   { return symbol(sym.AND); }
    "OR"  { return symbol(sym.OR); }
    "~"   { return symbol(sym.NOT); }

    /* Other Symbols */
    ";"   { return symbol(sym.SEMI); }
    "."   { return symbol(sym.DOT); }
    "("   { return symbol(sym.LPAREN); }
    ")"   { return symbol(sym.RPAREN); }
    ","   { return symbol(sym.COMMA); }
    "["   { return symbol(sym.LBRACK); }
    "]"   { return symbol(sym.RBRACK); }
    ":"   { return symbol(sym.COLON); }

    {Identifier} { 
        if(yylength() > 24) {
            throw new IllegalIdentifierLengthException("at line " + (yyline+1) + ", column " + (yycolumn+1));
        }
        return symbol(sym.IDENTIFIER, yytext().toUpperCase());
    }
    {ErrorInteger} { throw new IllegalIntegerException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }
    {DecInteger} { 
        if(yylength() > 12) {
            throw new IllegalIntegerRangeException("at line " + (yyline+1) + ", column " + (yycolumn+1));
        }
        return symbol(sym.INTEGER, Integer.valueOf(yytext()));
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
        return symbol(sym.INTEGER, Integer.valueOf(Integer.parseInt(octStr, 8)));
    }
}

<COMMENT> {
    // "(*" { throw new MismatchedCommentException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }
    "*)" { yybegin(YYINITIAL); }
    <<EOF>> { throw new MismatchedCommentException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }
    [^] { /* skip */ }
}

<<EOF>> { return symbol(sym.EOF); }
[^] { throw new IllegalSymbolException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }