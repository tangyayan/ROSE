package exceptions;

public class MismatchedCommentException extends LexicalException {
    public MismatchedCommentException() {
        this("Mismatched Comment");
    }

    public MismatchedCommentException(String message) {
        super(message);
    }

    public MismatchedCommentException(int yyline, int yycolumn) {
        super("Mismatched Comment at line " + yyline + ", column " + yycolumn);
    }
}