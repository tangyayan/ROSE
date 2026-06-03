
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
%type String

%{
    private String debuginfo(String type) {
        return type + ": at line " + (yyline+1) + ", column " + (yycolumn+1) + ": " + yytext() + "\n";
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
    "MODULE"    { return "RESERVED WORD\n"; }
    "PROCEDURE" { return "RESERVED WORD\n"; }
    "BEGIN"     { return "RESERVED WORD\n"; }
    "END"       { return "RESERVED WORD\n"; }
    "CONST"     { return "RESERVED WORD\n"; }
    "TYPE"      { return "RESERVED WORD\n"; }
    "VAR"       { return "RESERVED WORD\n"; }
    "ARRAY"     { return "RESERVED WORD\n"; }
    "OF"        { return "RESERVED WORD\n"; }
    "RECORD"    { return "RESERVED WORD\n"; }
    "WHILE"     { return "RESERVED WORD\n"; }
    "DO"        { return "RESERVED WORD\n"; }
    "IF"        { return "RESERVED WORD\n"; }
    "THEN"      { return "RESERVED WORD\n"; }
    "ELSIF"     { return "RESERVED WORD\n"; }
    "ELSE"      { return "RESERVED WORD\n"; }

    /* Keywords */
    "INTEGER" { return "KEYWORD\n"; }
    "BOOLEAN" { return "KEYWORD\n"; }
    "READ" { return "KEYWORD\n"; }
    "WRITE" { return "KEYWORD\n"; }
    "WRITELN" { return "KEYWORD\n"; }

    /* Whitespace */
    {WhiteSpace}+ { /* skip */ }

    "(*" { yybegin(COMMENT); }

    /* Operators */
    ":=" { return ":=\n"; }
    "=" { return "=\n"; }
    "#" { return "#\n"; }
    "<" { return "<\n"; }
    "<=" { return "<=\n"; }
    ">" { return ">\n"; }
    ">=" { return ">=\n"; }
    "+" { return "+\n"; }
    "-" { return "-\n"; }
    "*" { return "*\n"; }
    "DIV" { return "DIV\n"; }
    "MOD" { return "MOD\n"; }
    "&" { return "&\n"; }
    "OR" { return "OR\n"; }
    "~" { return "~\n"; }

    /* Other Symbols */
    ";" { return "SEMICOLON\n"; }
    "." { return "DOT\n"; }
    "(" { return "LPAREN\n"; }
    ")" { return "RPAREN\n"; }
    "," { return "COMMA\n"; }
    "[" { return "LBRACK\n"; }
    "]" { return "RBRACK\n"; }
    ":" { return "COLON\n"; }

    {Identifier} { 
        if(yylength() > 24) {
            throw new IllegalIdentifierLengthException();
        }
        return debuginfo("Identifier"); 
    }
    {ErrorInteger} { throw new IllegalIntegerException(); }
    {DecInteger} { 
        if(yylength() > 12) {
            throw new IllegalIntegerRangeException();
        }
        return debuginfo("Decimal Integer" + Integer.parseInt(yytext()));
    }
    {OctInteger} { 
        if(yylength() > 12) {
            throw new IllegalIntegerRangeException();
        }
        String octStr = yytext();
        for (char c : octStr.toCharArray()) {
            if (c < '0' || c > '7') {
                throw new IllegalOctalException();
            }
        }
        return debuginfo("Octal Integer" + Integer.parseInt(octStr, 8));
    }
}

<COMMENT> {
    // "(*" { throw new MismatchedCommentException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }
    "*)" { yybegin(YYINITIAL); }
    <<EOF>> { throw new MismatchedCommentException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }
    [^] { /* skip */ }
}

<<EOF>> { return "EOF\n"; }
[^] { throw new IllegalSymbolException("at line " + (yyline+1) + ", column " + (yycolumn+1)); }