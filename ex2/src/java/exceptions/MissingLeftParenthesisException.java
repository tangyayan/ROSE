package exceptions;

public class MissingLeftParenthesisException extends SyntacticException {
    public MissingLeftParenthesisException() {
        this("Missing Left Parenthesis");
    }

    public MissingLeftParenthesisException(String message) {
        super(message);
    }
}