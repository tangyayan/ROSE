package exceptions;

public class MissingOperatorException extends SyntacticException {
    public MissingOperatorException() {
        this("Missing Operator");
    }

    public MissingOperatorException(String message) {
        super(message);
    }
}