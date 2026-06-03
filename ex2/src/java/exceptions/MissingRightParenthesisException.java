package exceptions;

public class MissingRightParenthesisException extends SyntacticException {
    public MissingRightParenthesisException() {
        this("Missing Right Parenthesis");
    }

    public MissingRightParenthesisException(String message) {
        super(message);
    }
}