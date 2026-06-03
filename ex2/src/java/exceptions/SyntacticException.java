package exceptions;

public class SyntacticException extends OberonException {
    public SyntacticException() {
        this("Syntactic Exception");
    }

    public SyntacticException(String message) {
        super(message);
    }
}