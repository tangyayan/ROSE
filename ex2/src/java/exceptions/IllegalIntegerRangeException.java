package exceptions;

public class IllegalIntegerRangeException extends LexicalException {
    public IllegalIntegerRangeException() {
        this("Illegal Integer Range");
    }

    public IllegalIntegerRangeException(String message) {
        super(message);
    }
}