package exceptions;

public class IllegalIdentifierLengthException extends LexicalException {
    public IllegalIdentifierLengthException() {
        this("Illegal Identifier Length");
    }

    public IllegalIdentifierLengthException(String message) {
        super(message);
    }
}