package exceptions;

public class MismatchedCommentException extends LexicalException {
    public MismatchedCommentException() {
        this("Mismatched Comment");
    }

    public MismatchedCommentException(String message) {
        super(message);
    }
}