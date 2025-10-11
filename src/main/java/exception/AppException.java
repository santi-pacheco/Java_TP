package exception;

public class AppException extends RuntimeException {
    
    private final int statusCode;
    private final String errorType;

    public AppException(String message, int statusCode, String errorType) {
        super(message);
        this.statusCode = statusCode;
        this.errorType = errorType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorType() {
        return errorType;
    }
}