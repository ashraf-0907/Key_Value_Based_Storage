package event_loop;

import DataStructure.DBMemoryMap;
import utils.ResponseStructure;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RequestHandler {
    public static void handleRequest(Event event) {
        String[] request = event.getParsedRequest();
        String responseMessage;
        int statusCode;

        switch (request[0]) {
            case "set":
                responseMessage = DBMemoryMap.setValue(request[1], request[2]);
                statusCode = 200;
                break;
            case "get":
                responseMessage = DBMemoryMap.getValue(request[1]);
                statusCode = 200;
                break;
            case "del":
                responseMessage = DBMemoryMap.delValue(request[1]);
                statusCode = 200;
                break;
            default:
                responseMessage = "Invalid command";
                statusCode = 400;
        }

        ResponseStructure response = new ResponseStructure(statusCode, responseMessage, "");
        ByteBuffer responseBuffer = ByteBuffer.wrap(response.toString().getBytes(StandardCharsets.UTF_8));
        event.setResponseBytes(responseBuffer);
    }
}

