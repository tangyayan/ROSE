package exceptions;

public class LexicalException extends OberonException {
    public LexicalException() {
        this("Lexical Exception");
    }

    public LexicalException(String message) {
        super(message);
    }
}