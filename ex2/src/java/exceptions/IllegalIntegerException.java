package exceptions;

public class IllegalIntegerException extends LexicalException {
    public IllegalIntegerException() {
        this("Illegal Integer");
    }

    public IllegalIntegerException(String message) {
        super(message);
    }
}