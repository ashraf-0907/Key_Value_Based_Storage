package event_loop;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Event {
    private InetAddress clientAddress;
    private int clientPort;
    private String[] parsedRequest;
    private ByteBuffer responseBytes;
    private int port;
    private int priority;
    private boolean asynchronous;

    public Event(InetAddress clientAddress, int clientPort, String[] request, int port, int priority, boolean asynchronous) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.parsedRequest = request;
        this.priority = priority;
        this.port = port;
        this.asynchronous = asynchronous;
    }

    // Getters and setters
    public InetAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public String[] getRequestBytes() {
        return parsedRequest;
    }

    public void setRequestBytes(String[] request) {
        this.parsedRequest = request;
    }

    public ByteBuffer getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(ByteBuffer responseBytes) {
        this.responseBytes = responseBytes;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPriority(int priority){
        this.priority = priority;
    }

    public int getPriority(){
        return this.priority;
    }

    public boolean getAsynchronous(){
        return this.asynchronous;
    }
}