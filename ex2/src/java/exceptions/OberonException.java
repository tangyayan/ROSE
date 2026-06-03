package exceptions;

public class OberonException extends Exception {
    public OberonException() {
        this("Oberon Exception");
    }

    public OberonException(String message) {
        super(message);
    }
}