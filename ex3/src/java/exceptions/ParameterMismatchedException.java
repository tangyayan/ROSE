package exceptions;

public class ParameterMismatchedException extends SemanticException {
    public ParameterMismatchedException() {
        this("Parameter Mismatched");
    }

    public ParameterMismatchedException(String message) {
        super(message);
    }

    public ParameterMismatchedException(int yyline, int yycolumn) {
        super("Parameter Mismatched at line " + yyline + ", column " + yycolumn);
    }
}