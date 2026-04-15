/**
 * Custom exception for API-related errors
 */
public class ApiException extends Exception {
    private int statusCode;
    private Exception cause;
    
    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public ApiException(String message, int statusCode, Exception cause) {
        super(message);
        this.statusCode = statusCode;
        this.cause = cause;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public Exception getCause() {
        return cause;
    }
}
