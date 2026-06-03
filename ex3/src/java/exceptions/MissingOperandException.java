package exceptions;

public class MissingOperandException extends SyntacticException {
    public MissingOperandException() {
        this("Missing Operand");
    }

    public MissingOperandException(String message) {
        super(message);
    }

    public MissingOperandException(int yyline, int yycolumn) {
        super("Missing Operand at line " + yyline + ", column " + yycolumn);
    }
}