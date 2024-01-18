import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Properties;

public class Server {

    private static void msg(String message) {
        System.err.println(message);
    }

    // Function to read 'n' bytes from an InputStream into a byte array
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

    // Function to write 'n' bytes from a byte array to an OutputStream
    private static int writeAll(OutputStream out, byte[] buf, int n) throws IOException {
        out.write(buf, 0, n);
        return 0;
    }

    // Function to handle one client request
    private static int oneRequest(Socket socket, int maxSize) {
        try {
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

            // Do something
            String clientMessage = new String(bodyBytes);
            System.out.println("client says: " + clientMessage);

            // Reply using the same protocol
            String reply = "world";
            byte[] replyBytes = reply.getBytes();
            ByteBuffer replyBuffer = ByteBuffer.allocate(4 + replyBytes.length);
            replyBuffer.putInt(replyBytes.length);
            replyBuffer.put(replyBytes);
            writeAll(socket.getOutputStream(), replyBuffer.array(), replyBuffer.array().length);

            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void main(String[] args) {
        Properties config = utils.Property.loadProperties("config.properties");
        int port = Integer.parseInt(config.getProperty("port", "8080"));
        String localhost = config.getProperty("localhost", "127.0.0.1");
        int maxSize = Integer.parseInt(config.getProperty("maxSize", "4096"));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                while (true) {
                    // Here the server only serves one client connection at once
                    int err = oneRequest(clientSocket, maxSize);
                    if (err < 0) {
                        break;
                    }
                }

                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

