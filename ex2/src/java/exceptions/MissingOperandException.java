package exceptions;

public class MissingOperandException extends SyntacticException {
    public MissingOperandException() {
        this("Missing Operand");
    }

    public MissingOperandException(String message) {
        super(message);
    }
}