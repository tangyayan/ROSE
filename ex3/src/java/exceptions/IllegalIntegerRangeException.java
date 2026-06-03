package exceptions;

public class IllegalIntegerRangeException extends LexicalException {
    public IllegalIntegerRangeException() {
        this("Illegal Integer Range");
    }

    public IllegalIntegerRangeException(String message) {
        super(message);
    }

    public IllegalIntegerRangeException(int yyline, int yycolumn) {
        super("Illegal Integer Range at line " + yyline + ", column " + yycolumn);
    }
}