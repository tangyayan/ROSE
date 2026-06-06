package exceptions;

public class IllegalIntegerException extends LexicalException {
    public IllegalIntegerException() {
        this("Illegal Integer");
    }

    public IllegalIntegerException(String message) {
        super(message);
    }

    public IllegalIntegerException(int yyline, int yycolumn) {
        super("Illegal Integer at line " + yyline + ", column " + yycolumn);
    }
}