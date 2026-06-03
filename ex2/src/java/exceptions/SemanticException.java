package exceptions;

public class SemanticException extends OberonException {
    public SemanticException() {
        this("Semantic Exception");
    }

    public SemanticException(String message) {
        super(message);
    }
}