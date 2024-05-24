import event_loop.Event;
import event_loop.EventLoop;
import event_loop.ResponseSender;
import utils.ParsedString;
import utils.Property;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Properties;

public class Server {
    private static EventLoop eventLoop;

    public static void main(String[] args) {
        Properties config = Property.loadProperties("config.properties");
        String host = config.getProperty("host", "0.0.0.0");
        int port = Integer.parseInt(config.getProperty("port", "8081"));
        int maxSize = Integer.parseInt(config.getProperty("maxSize", "4096"));

        System.out.println("Starting server...");

        eventLoop = new EventLoop();
        eventLoop.start();

        System.out.println("Server started.");

        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName(host))) {
            System.out.println("Server is running on " + host + ":" + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from client: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
                handleClient(clientSocket, maxSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket, int maxSize) {
        new Thread(() -> {
            try {
                while (true) {
                    byte[] headerBytes = new byte[4];
                    if (readFull(socket.getInputStream(), headerBytes, 4) < 0) {
                        break;
                    }

                    int len = ByteBuffer.wrap(headerBytes).getInt();
                    if (len > maxSize) {
                        System.err.println("Message too long");
                        break;
                    }

                    byte[] bodyBytes = new byte[len];
                    if (readFull(socket.getInputStream(), bodyBytes, len) < 0) {
                        break;
                    }

                    String clientMessage = new String(bodyBytes);
                    String[] parsedRequest = ParsedString.parsedString(clientMessage);
                    int priority = getPriority(parsedRequest);

                    Event event = new Event(socket, parsedRequest, priority);
                    eventLoop.dispatchEvent(event);

                    synchronized (event) {
                        while (!event.isProcessed()) {
                            event.wait();
                        }
                    }

                    ResponseSender.sendResponse(event);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static int readFull(InputStream in, byte[] buf, int n) throws IOException {
        int bytesRead = 0;
        while (bytesRead < n) {
            int rv = in.read(buf, bytesRead, n - bytesRead);
            if (rv <= 0) {
                return -1;
            }
            bytesRead += rv;
        }
        return 0;
    }

    private static int getPriority(String[] parsedRequest) {
        switch (parsedRequest[0]) {
            case "set":
                return 1;
            case "get":
                return 2;
            case "del":
                return 3;
            default:
                return -1;
        }
    }
}
