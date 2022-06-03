package panda.rainmaker.http;

public class HttpResult {

    private final boolean success;
    private final String message;

    public HttpResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public boolean isFailed() {
        return !success;
    }

    public String getMessage() {
        return message;
    }
}
