import utils.Property;

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

    public static void main(String[] args) {
        Properties prop = Property.loadProperties("config.properties");
        String host = prop.getProperty("host", "127.0.0.1");
        int port = Integer.parseInt(prop.getProperty("port", "8081"));

        try (Socket socket = new Socket(host, port);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Connected to server on " + host + ":" + port);

            while (true) {
                String toBeSent = sc.nextLine();
                if (toBeSent.equals("-1")) {
                    break;
                }
                sendRequest(socket, toBeSent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(Socket socket, String text) {
        try {
            int len = text.length();
            if (len > MAX_MSG) {
                throw new IllegalArgumentException("Message too long");
            }

            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();

            ByteBuffer lenBuffer = ByteBuffer.allocate(4);
            lenBuffer.putInt(len);
            os.write(lenBuffer.array());

            os.write(text.getBytes(StandardCharsets.UTF_8));
            os.flush();

            byte[] replyLenBytes = new byte[4];
            if (is.read(replyLenBytes) < 0) {
                throw new IOException("Connection closed while reading reply length");
            }
            int replyLen = ByteBuffer.wrap(replyLenBytes).getInt();

            if (replyLen > MAX_MSG) {
                throw new IllegalArgumentException("Received message too long");
            }

            byte[] replyBytes = new byte[replyLen];
            int bytesRead = 0;
            while (bytesRead < replyLen) {
                int read = is.read(replyBytes, bytesRead, replyLen - bytesRead);
                if (read == -1) {
                    throw new IOException("Connection closed while reading reply body");
                }
                bytesRead += read;
            }

            String replyText = new String(replyBytes, StandardCharsets.UTF_8);
            System.out.println("Server says: " + replyText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



