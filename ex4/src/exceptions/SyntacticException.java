package exceptions;

public class SyntacticException extends OberonException {
    public SyntacticException() {
        this("Syntactic Exception");
    }

    public SyntacticException(String message) {
        super(message);
    }

    public SyntacticException(int yyline, int yycolumn) {
        super("Syntactic Exception at line " + yyline + ", column " + yycolumn);
    }
}