package exceptions;

public class MissingOperatorException extends SyntacticException {
    public MissingOperatorException() {
        this("Missing Operator");
    }

    public MissingOperatorException(String message) {
        super(message);
    }

    public MissingOperatorException(int yyline, int yycolumn) {
        super("Missing Operator at line " + yyline + ", column " + yycolumn);
    }
}