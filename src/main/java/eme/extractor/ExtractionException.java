package eme.extractor;

/**
 * Exception class for the extraction process.
 * @author Timur Saglam
 */
public class ExtractionException extends RuntimeException {
    private static final long serialVersionUID = 5467201218625089945L;

    /**
     * Creates a new {@link ExtractionException}.
     * @param message is the exception message.
     */
    public ExtractionException(String message) {
        super(message);
    }

    /**
     * Creates a new {@link ExtractionException}.
     * @param message is the exception message.
     * @param cause is the cause of the exception.
     */
    public ExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
