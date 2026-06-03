package exceptions;

public class IllegalIdentifierLengthException extends LexicalException {
    public IllegalIdentifierLengthException() {
        this("Illegal Identifier Length");
    }

    public IllegalIdentifierLengthException(String message) {
        super(message);
    }

    public IllegalIdentifierLengthException(int yyline, int yycolumn) {
        super("Illegal Identifier Length at line " + yyline + ", column " + yycolumn);
    }
}