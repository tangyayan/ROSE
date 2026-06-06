package exceptions;

public class SemanticException extends OberonException {
    public SemanticException() {
        this("Semantic Exception");
    }

    public SemanticException(String message) {
        super(message);
    }

    public SemanticException(int yyline, int yycolumn) {
        super("Semantic Exception at line " + yyline + ", column " + yycolumn);
    }
}