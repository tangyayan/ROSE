package exceptions;

public class ParameterMismatchedException extends SemanticException {
    public ParameterMismatchedException() {
        this("Parameter Mismatched");
    }

    public ParameterMismatchedException(String message) {
        super(message);
    }
}