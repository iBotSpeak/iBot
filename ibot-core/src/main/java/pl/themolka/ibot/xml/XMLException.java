package pl.themolka.ibot.xml;

public class XMLException extends Exception {
    public XMLException() {
    }

    public XMLException(String message) {
        super(message);
    }

    public XMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLException(Throwable cause) {
        super(cause);
    }
}
