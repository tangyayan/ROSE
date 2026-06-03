package exceptions;

public class MissingLeftParenthesisException extends SyntacticException {
    public MissingLeftParenthesisException() {
        this("Missing Left Parenthesis");
    }

    public MissingLeftParenthesisException(String message) {
        super(message);
    }

    public MissingLeftParenthesisException(int yyline, int yycolumn) {
        super("Missing Left Parenthesis at line " + yyline + ", column " + yycolumn);
    }
}