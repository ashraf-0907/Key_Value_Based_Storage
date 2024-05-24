package utils;

public class ResponseStructure {
    private final int statusCode;
    private final String message;
    private final String error;

    public ResponseStructure(int statusCode, String message, String error) {
        this.statusCode = statusCode;
        this.message = message;
        this.error = error;
    }

    @Override
    public String toString() {
        return "StatusCode: " + statusCode + "\nMessage: " + message + "\nError: " + error;
    }
}
