package exceptions;

public class IllegalSymbolException extends LexicalException {
    public IllegalSymbolException() {
        this("Illegal Symbol");
    }

    public IllegalSymbolException(String message) {
        super(message);
    }

    public IllegalSymbolException(int yyline, int yycolumn) {
        super("Illegal Symbol at line " + yyline + ", column " + yycolumn);
    }
}