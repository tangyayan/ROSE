package exceptions;

public class TypeMismatchedException extends SemanticException {
    public TypeMismatchedException() {
        this("Type Mismatched");
    }

    public TypeMismatchedException(String message) {
        super(message);
    }

    public TypeMismatchedException(int yyline, int yycolumn) {
        super("Type Mismatched at line " + yyline + ", column " + yycolumn);
    }
}