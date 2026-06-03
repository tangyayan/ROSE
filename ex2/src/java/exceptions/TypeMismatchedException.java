package exceptions;

public class TypeMismatchedException extends SemanticException {
    public TypeMismatchedException() {
        this("Type Mismatched");
    }

    public TypeMismatchedException(String message) {
        super(message);
    }
}