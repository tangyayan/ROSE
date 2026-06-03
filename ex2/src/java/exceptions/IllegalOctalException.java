package exceptions;

public class IllegalOctalException extends LexicalException {
    public IllegalOctalException() {
        this("Illegal Octal");
    }

    public IllegalOctalException(String message) {
        super(message);
    }
}