package event_loop;

import java.net.Socket;
import java.nio.ByteBuffer;

public class Event {
    private Socket socket;
    private String[] parsedRequest;
    private int priority;
    private boolean processed;
    private ByteBuffer responseBytes;

    public Event(Socket socket, String[] parsedRequest, int priority) {
        this.socket = socket;
        this.parsedRequest = parsedRequest;
        this.priority = priority;
        this.processed = false;
    }

    public Socket getSocket() {
        return socket;
    }

    public String[] getParsedRequest() {
        return parsedRequest;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public ByteBuffer getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(ByteBuffer responseBytes) {
        this.responseBytes = responseBytes;
    }
}
