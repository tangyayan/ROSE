package exceptions;

public class LexicalException extends OberonException {
    public LexicalException() {
        this("Lexical Exception");
    }

    public LexicalException(String message) {
        super(message);
    }

    public LexicalException(int yyline, int yycolumn) {
        super("Lexical Exception at line " + yyline + ", column " + yycolumn);
    }
}