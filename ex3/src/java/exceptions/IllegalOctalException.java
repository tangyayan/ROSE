package exceptions;

public class IllegalOctalException extends LexicalException {
    public IllegalOctalException() {
        this("Illegal Octal");
    }

    public IllegalOctalException(String message) {
        super(message);
    }

    public IllegalOctalException(int yyline, int yycolumn) {
        super("Illegal Octal at line " + yyline + ", column " + yycolumn);
    }
}