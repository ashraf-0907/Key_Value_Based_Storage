package event_loop;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;


public class ResponseSender {
    public static void sendResponse(Event event) {
        try {
            OutputStream os = event.getSocket().getOutputStream();
            ByteBuffer responseBytes = event.getResponseBytes();
            byte[] bytesToSend = responseBytes.array();
            os.write(ByteBuffer.allocate(4).putInt(bytesToSend.length).array());
            os.write(bytesToSend);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
