import event_loop.Event;
import event_loop.EventLoop;
import utils.ParsedString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Properties;

public class Server {
    private static EventLoop eventLoop;

    public static void main(String[] args) {
        Properties config = utils.Property.loadProperties("config.properties");
        int port = Integer.parseInt(config.getProperty("port", "8081"));
        int maxSize = Integer.parseInt(config.getProperty("maxSize", "4096"));

        eventLoop = new EventLoop(); // Create an instance of EventLoop

        // Start the EventLoop
        eventLoop.start();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Add clientSocket to EventLoop for processing
                oneRequest(clientSocket, maxSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to handle one client request
    public static int oneRequest(Socket socket, int maxSize) {
        try {
            while (true) {
                // Read header
                byte[] headerBytes = new byte[4];
                if (readFull(socket.getInputStream(), headerBytes, 4) < 0) {
                    msg("read() error");
                    return -1;
                }

                // Read header (4 bytes) to get the length of the incoming message
                int len = ByteBuffer.wrap(headerBytes).getInt();

                if (len > maxSize) {
                    msg("too long");
                    return -1;
                }

                // Read body
                byte[] bodyBytes = new byte[len];
                if (readFull(socket.getInputStream(), bodyBytes, len) < 0) {
                    msg("read() error");
                    return -1;
                }

                String clientMessage = new String(bodyBytes);

                String[] parsedRequest = ParsedString.parsedString(clientMessage);

                int priority = getPriority(parsedRequest);

                Event event = new Event(socket.getInetAddress(), socket.getPort(), parsedRequest, socket.getLocalPort(), priority, priority == 1 ? false : true);
                eventLoop.addEvent(event);

                boolean processed = false;

                // Wait for the event to be processed
                synchronized (eventLoop.processedEvents) {
                    while (!processed && eventLoop.processedEvents.isEmpty()) {
                        try {
                            eventLoop.processedEvents.wait(); // Wait for the EventLoop to process the event
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!eventLoop.processedEvents.isEmpty()) {
                        processed = true;
                    }
                }

                // Process the response after the event is processed
                if (processed) {
                    checkAndSendResponse(socket);
                } else {
                    System.err.println("Failed to process event.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Function to send response if deque is not empty
    private static void checkAndSendResponse(Socket socket) {
        if (!eventLoop.processedEvents.isEmpty()) {
            Event event = eventLoop.processedEvents.poll();
            sendResponse(socket, event.getResponseBytes());
        }
    }

    // Function to send response to the client
    private static void sendResponse(Socket socket, ByteBuffer responseBytes) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            byte[] bytesToSend = responseBytes.array();
            outputStream.write(bytesToSend);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Utility function to read data from InputStream
    private static int readFull(InputStream in, byte[] buf, int n) throws IOException {
        int bytesRead = 0;
        while (bytesRead < n) {
            int rv = in.read(buf, bytesRead, n - bytesRead);
            if (rv <= 0) {
                return -1;  // error, or unexpected EOF
            }
            bytesRead += rv;
        }
        return 0;
    }

    // Function to get priority of the request
    private static int getPriority(String[] parsedString){
        int priority = -1;
        if(parsedString[0].equals("set"))
            priority = 1;
        else if(parsedString[0].equals("get"))
            priority = 2;
        else if(parsedString[0].equals("del"))
            priority= 3;
        return priority;
    }

    // Utility function to print messages
    private static void msg(String message) {
        System.err.println(message);
    }
}
