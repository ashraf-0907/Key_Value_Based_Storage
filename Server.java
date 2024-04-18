import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.HashMap;
import datastructures.*;
import java.util.Arrays;
class MultithreadingSocket extends Thread {
    private Socket clientSocket;
    private int maxSize;
    private DBMemoryMap dbmem;

    public MultithreadingSocket(Socket clientSocket,int maxSize, DBMemoryMap dbmem) 
    {
        this.clientSocket = clientSocket;
        this.maxSize = maxSize;
        this.dbmem = dbmem;
    }

    public void run()
    {
        while (true)
        {
            // Here the server only serves one client connection at once
            int err = Server.oneRequest(clientSocket, maxSize, dbmem);
            if (err < 0) {
                break;
            }
        }
        try
        {
            clientSocket.close();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
    }
}
// = new HashMap<>();
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
    private static String requestParser (String request,DBMemoryMap dbmem){
        String[] stringParts = request.split("\\s+");
        for(int i=0;i<stringParts.length;i++)stringParts[i] = stringParts[i].replaceAll("\\s", "");
        String response = "";
        if(stringParts[0].equals("set"))
        {
            System.out.println("h0");
            if(stringParts.length == 3)
            {
                response = dbmem.setValue(stringParts[1],stringParts[2]);
                System.out.println("h1");
            }
            else
            {
                response = "400 error in the set request. invalid syntax";
            }
        }
        else if(stringParts[0].equals("get"))
        {
            if(stringParts.length == 2)
            {
                response = dbmem.getValue(stringParts[1]);
            }
            else
            {
                response = "400 error in the get request. invalid syntax";
            }
        }
        else if(stringParts[0].equals("del"))
        {
            if(stringParts.length == 2)
            {
                response = dbmem.delValue(stringParts[1]);
            }
            else
            {
                response = "400 error in the del request. invalid syntax";
            }
        }
        System.out.println("h2");
        return response;
    }

    // Function to write 'n' bytes from a byte array to an OutputStream
    private static int writeAll(OutputStream out, byte[] buf, int n) throws IOException {
        out.write(buf, 0, n);
        return 0;
    }
    // private static String set(String key,String value){
    //     String response = "";
    //     return response;
    // }

    // private static String get(String key){
    //     String response = "";
    //     return response;
    // }

    // private static String del(String key){
    //     String response = "";
    //     return response;
    // }
    // Function to handle one client request
    public static int oneRequest(Socket socket, int maxSize,DBMemoryMap dbmem) {
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

            String trimClientMessage = clientMessage.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s]+", "").trim();
            System.out.println("client says: " + trimClientMessage);
            String response = requestParser(trimClientMessage,dbmem);

            System.out.println("client says: " + clientMessage);

            // Reply using the same protocol
            byte[] replyBytes = response.getBytes();
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
        DBMemoryMap dbmem = new DBMemoryMap();
        Properties config = utils.Property.loadProperties("config.properties");
        int port = Integer.parseInt(config.getProperty("port", "8081"));
        String localhost = config.getProperty("localhost", "127.0.0.1");
        int maxSize = Integer.parseInt(config.getProperty("maxSize", "4096"));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                MultithreadingSocket object = new MultithreadingSocket(clientSocket,maxSize,dbmem);
                object.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

