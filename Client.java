import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;

public class Client {

    private static final int MAX_MSG = 4096;

    private static void writeFull(OutputStream os, byte[] buffer) throws IOException {
        os.write(buffer);
        os.flush();
    }

    private static void readFull(InputStream is, byte[] buffer, int len) throws IOException {
        int bytesRead = 0;
        while (bytesRead < len) {
            int result = is.read(buffer, bytesRead, len - bytesRead);
            if (result == -1) {
                throw new IOException("Unexpected end of stream");
            }
            bytesRead += result;
        }
    }

    private static void query(Socket socket, String text) {
        try {
            int len = text.length();
            if (len > MAX_MSG) {
                throw new IllegalArgumentException("Message too long");
            }

            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            // Sending the length of the message
            byte[] lenBytes = ByteBuffer.allocate(4).putInt(len).array();
            writeFull(os, lenBytes);

            // Sending the message
            byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
            writeFull(os, textBytes);

            // Read 4 bytes header
            byte[] replyLenBytes = new byte[4];
            readFull(is, replyLenBytes, 4);
            int replyLen = ByteBuffer.wrap(replyLenBytes).getInt();

            if (replyLen > MAX_MSG) {
                throw new IllegalArgumentException("Received message too long");
            }

            // Read reply body
            byte[] replyBytes = new byte[replyLen];
            readFull(is, replyBytes, replyLen);

            // Do something with the reply
            String replyText = new String(replyBytes, StandardCharsets.UTF_8);
            System.out.println("Server says: " + replyText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Properties prop = utils.Property.loadProperties("config.properties");
        String localhost = prop.getProperty("localHost", "127.0.0.1");
        int port = Integer.parseInt(prop.getProperty("port", "8080"));

        try (Socket socket = new Socket(localhost, port);
             Scanner sc = new Scanner(System.in)) {

            while (true) {
                String toBeSent = sc.nextLine();
                if (toBeSent.equals("-1")) {
                    break;
                }
                query(socket, toBeSent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
