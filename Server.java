import java.io.IOException;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Server {
    private static final int MAX_MESSAGE_SIZE = 4096;

    public static void main(String[] args) {
        Properties prop = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream("config.properties");
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int port = Integer.parseInt(prop.getProperty("port", "8080"));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running at port number " + port);
            while (true) {
                Socket clientSocket;
                try {
                    clientSocket = serverSocket.accept();
                    System.out.println("client is connected: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            byte[] headerBytes = readFully(inputStream, 4);
            if (headerBytes == null) {
                System.out.println("EOF");
                return;
            }

            int requestLength = byteArrayToInt(headerBytes);

            if (requestLength > MAX_MESSAGE_SIZE) {
                System.out.println("Request too long");
                return;
            }

            byte[] requestBytes = readFully(inputStream, requestLength);
            if (requestBytes == null) {
                System.out.println("EOF");
                return;
            }

            String request = new String(requestBytes, "UTF-8");
            System.out.println("Client says: " + request);

            String reply = "world";
            byte[] replyBytes = reply.getBytes("UTF-8");
            byte[] replyHeaderBytes = intToByteArray(replyBytes.length);

            outputStream.write(replyHeaderBytes);
            outputStream.write(replyBytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close(); // Close the socket after handling the client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static byte[] readFully(InputStream inputStream, int length) throws IOException {
        byte[] buffer = new byte[length];
        int bytesRead = 0;
        while (bytesRead < length) {
            int read = inputStream.read(buffer, bytesRead, length - bytesRead);
            if (read == -1) {
                return null;  // EOF
            }
            bytesRead += read;
        }
        return buffer;
    }

    private static byte[] intToByteArray(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value >>> 24);
        bytes[1] = (byte) (value >>> 16);
        bytes[2] = (byte) (value >>> 8);
        bytes[3] = (byte) value;
        return bytes;
    }

    private static int byteArrayToInt(byte[] bytes) {
        return (bytes[0] << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }
}

