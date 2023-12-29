import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;


public class Client {
    private static String localhost;
    private static int port;

    public static void main(String[] args) {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            prop.load(input);

            // Retrieving Values
            localhost = prop.getProperty("localHost");
            System.out.println(prop.getProperty("port"));
            port = Integer.parseInt(prop.getProperty("port", "8080"));

            for (int i = 0; i < 3; i++) {
                try (Socket socket = new Socket(localhost, port)) {
                    sendRequest(socket, "Message" + (i + 1));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    private static void sendRequest(Socket socket, String msg) {
        try (OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {
            byte[] message = msg.getBytes("UTF-8");
            byte[] headerByte = intToByteArray(message.length);
            out.write(headerByte);
            out.write(message);
            out.flush();
            System.out.println(recvResponse(in));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String recvResponse(InputStream in) {
        try {
            byte[] replyHeaderBytes = readFully(in, 4);
            int replyLength = byteArrayToInt(replyHeaderBytes);

            byte[] replyBytes = readFully(in, replyLength);
            String reply = new String(replyBytes, "UTF-8");
            System.out.println("Server says: " + reply);
            return new String(replyHeaderBytes, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
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
