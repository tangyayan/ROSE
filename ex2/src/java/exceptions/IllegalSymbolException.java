package exceptions;

public class IllegalSymbolException extends LexicalException {
    public IllegalSymbolException() {
        this("Illegal Symbol");
    }

    public IllegalSymbolException(String message) {
        super(message);
    }
}