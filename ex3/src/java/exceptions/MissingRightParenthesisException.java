package exceptions;

public class MissingRightParenthesisException extends SyntacticException {
    public MissingRightParenthesisException() {
        this("Missing Right Parenthesis");
    }

    public MissingRightParenthesisException(String message) {
        super(message);
    }

    public MissingRightParenthesisException(int yyline, int yycolumn) {
        super("Missing Right Parenthesis at line " + yyline + ", column " + yycolumn);
    }
}