package exceptions;

public class OberonException extends Exception {
    public OberonException() {
        this("Oberon Exception");
    }

    public OberonException(String message) {
        super(message);
    }

    public OberonException(int yyline, int yycolumn) {
        super("Oberon Exception at line " + yyline + ", column " + yycolumn);
    }
}