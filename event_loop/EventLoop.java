//package event_loop;
//
//import utils.ResponseStructure;
//import java.nio.ByteBuffer;
//import java.util.ArrayDeque;
//import java.util.Comparator;
//import java.util.Deque;
//import java.util.PriorityQueue;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class EventLoop {
//    private final PriorityQueue<Event> events;
//    public final Deque<Event> processedEvents;
//    private final ExecutorService threadPool;
//
//    public EventLoop() {
//        events = new PriorityQueue<>(Comparator.comparingInt(Event::getPriority));
//        processedEvents = new ArrayDeque<>();
//        threadPool = Executors.newCachedThreadPool(); // Using a cached thread pool for handling asynchronous requests
//    }
//
//    public void addEvent(Event event) {
//        synchronized (events) {
//            events.offer(event);
//            events.notify(); // Notify any waiting threads that an event has been added
//        }
//    }
//
//    public void start() {
//        // Start the main processing loop in a separate thread
//        threadPool.execute(() -> {
//            while (true) {
//                Event event;
//                synchronized (events) {
//                    // Wait until there is an event to process
//                    while (events.isEmpty()) {
//                        try {
//                            events.wait(); // Wait for a notification that an event is available
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    // Get the next event to process
//                    event = events.poll();
//                }
//
//                processEvent(event);
//            }
//        });
//    }
//
//    private void processEvent(Event event) {
//        ResponseStructure res;
//        boolean async = event.getAsynchronous();
//
//        if (async) {
//            res = processAsynchronously(event.getRequestBytes());
//        } else {
//            res = processSynchronously(event.getRequestBytes());
//        }
//
//        ByteBuffer replyBuffer = createResponseBuffer(res);
//        event.setResponseBytes(replyBuffer);
//        synchronized (processedEvents) {
//            processedEvents.offer(event); // Add processed event to the deque
//            processedEvents.notify(); // Notify any waiting threads that a response is available
//        }
//    }
//
//    private ResponseStructure processAsynchronously(String[] parsedString) {
//        AtomicReference<ResponseStructure> res = new AtomicReference<>(new ResponseStructure());
//        threadPool.execute(() -> {
//            res.set(RequestHandlers.requestHandlerFunction(parsedString));
//        });
//        // Return a default response for now, as the actual response will be set asynchronously
//        return res.get();
//    }
//
//    private ResponseStructure processSynchronously(String[] parsedString) {
//        return RequestHandlers.requestHandlerFunction(parsedString);
//    }
//
//    private ByteBuffer createResponseBuffer(ResponseStructure response) {
//        ByteBuffer replyBuffer = ByteBuffer.allocate(4); // Allocate space for the buffer
//        if (response.getStatusCode() == 200) {
//            byte[] replyBytes = response.getData().getBytes();
//            replyBuffer = ByteBuffer.allocate(4 + replyBytes.length);
//            replyBuffer.putInt(replyBytes.length);
//            replyBuffer.put(replyBytes);
//        } else if (response.getStatusCode() == 401 || response.getStatusCode() == 500) {
//            byte[] replyBytes = response.getMessage().getBytes();
//            replyBuffer = ByteBuffer.allocate(4 + replyBytes.length);
//            replyBuffer.putInt(replyBytes.length);
//            replyBuffer.put(replyBytes);
//        }
//        return replyBuffer;
//    }
//}


//package event_loop;
//
//import utils.ParsedString;
//import utils.ResponseStructure;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.nio.ByteBuffer;
//import java.util.ArrayDeque;
//import java.util.Deque;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicReference;
//
//public class EventLoop {
//    private final ExecutorService threadPool;
//    private final Deque<Event> pendingEvents;
//    private final Deque<Event> processedEvents;
//
//    public EventLoop() {
//        threadPool = Executors.newCachedThreadPool();
//        pendingEvents = new ArrayDeque<>();
//        processedEvents = new ArrayDeque<>();
//    }
//
//    public void start() {
//        threadPool.execute(() -> {
//            while (true) {
//                Event event;
//                synchronized (pendingEvents) {
//                    while (pendingEvents.isEmpty()) {
//                        try {
//                            pendingEvents.wait(); // Wait for a notification that a new event is available
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    event = pendingEvents.poll(); // Retrieve the next event
//                }
//                if (event != null) {
//                    processEvent(event);
//                }
//            }
//        });
//    }
//
//    public void dispatchEvent(Socket socket, int maxSize) {
//        threadPool.execute(() -> {
//            int result = oneRequest(socket, maxSize);
//            if (result != 0) {
//                System.err.println("Failed to process client request.");
//            }
//        });
//    }
//
//    private int oneRequest(Socket socket, int maxSize) {
//        try {
//            // Read header
//            byte[] headerBytes = new byte[4];
//            if (readFull(socket.getInputStream(), headerBytes, 4) < 0) {
//                msg("read() error");
//                return -1;
//            }
//
//            // Read header (4 bytes) to get the length of the incoming message
//            int len = ByteBuffer.wrap(headerBytes).getInt();
//
//            if (len > maxSize) {
//                msg("too long");
//                return -1;
//            }
//
//            // Read body
//            byte[] bodyBytes = new byte[len];
//            if (readFull(socket.getInputStream(), bodyBytes, len) < 0) {
//                msg("read() error");
//                return -1;
//            }
//
//            String clientMessage = new String(bodyBytes);
//
//            String[] parsedRequest = ParsedString.parsedString(clientMessage);
//
//            int priority = getPriority(parsedRequest);
//
//            Event event = new Event(socket.getInetAddress(), socket.getPort(), parsedRequest, socket.getLocalPort(), priority, priority == 1);
//            addEvent(event);
//
//            return 0;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return -1;
//        }
//    }
//
//    private void processEvent(Event event) {
//        ResponseStructure res;
//        boolean async = event.getAsynchronous();
//        if (async) {
//            res = processAsynchronously(event.getRequestBytes());
//        } else {
//            res = processSynchronously(event.getRequestBytes());
//        }
//
//        // Create a response buffer based on the response structure
//        ByteBuffer replyBuffer = createResponseBuffer(res);
//
//        // Set the response buffer to the event
//        event.setResponseBytes(replyBuffer);
//
//        // Send the response back to the client
//        sendResponse(event.getClientAddress(), event.getClientPort(), replyBuffer);
//
//        synchronized (processedEvents) {
//            processedEvents.offer(event);
//            processedEvents.notify(); // Notify any waiting threads that a response is available
//        }
//    }
//
//
//    private ResponseStructure processAsynchronously(String[] parsedString) {
//        AtomicReference<ResponseStructure> res = new AtomicReference<>(new ResponseStructure());
//        threadPool.execute(() -> {
//            res.set(RequestHandlers.requestHandlerFunction(parsedString));
//        });
//        return res.get();
//    }
//
//    private ResponseStructure processSynchronously(String[] parsedString) {
//        return RequestHandlers.requestHandlerFunction(parsedString);
//    }
//
//    private ByteBuffer createResponseBuffer(ResponseStructure response) {
//        ByteBuffer replyBuffer = ByteBuffer.allocate(4);
//        if (response.getStatusCode() == 200) {
//            byte[] replyBytes = response.getData().getBytes();
//            replyBuffer = ByteBuffer.allocate(4 + replyBytes.length);
//            replyBuffer.putInt(replyBytes.length);
//            replyBuffer.put(replyBytes);
//        } else if (response.getStatusCode() == 401 || response.getStatusCode() == 500) {
//            byte[] replyBytes = response.getMessage().getBytes();
//            replyBuffer = ByteBuffer.allocate(4 + replyBytes.length);
//            replyBuffer.putInt(replyBytes.length);
//            replyBuffer.put(replyBytes);
//        }
//        return replyBuffer;
//    }
//
//    private static int readFull(InputStream in, byte[] buf, int n) throws IOException {
//        int bytesRead = 0;
//        while (bytesRead < n) {
//            int rv = in.read(buf, bytesRead, n - bytesRead);
//            if (rv <= 0) {
//                return -1;
//            }
//            bytesRead += rv;
//        }
//        return 0;
//    }
//
//    private void sendResponse(InetAddress clientAddress, int clientPort, ByteBuffer responseBuffer) {
//        try (Socket socket = new Socket(clientAddress, clientPort);
//             OutputStream os = socket.getOutputStream()) {
//            byte[] responseBytes = responseBuffer.array();
//            os.write(responseBytes);
//            os.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private static int getPriority(String[] parsedString) {
//        int priority = -1;
//        if (parsedString[0].equals("set"))
//            priority = 1;
//        else if (parsedString[0].equals("get"))
//            priority = 2;
//        else if (parsedString[0].equals("del"))
//            priority = 3;
//        return priority;
//    }
//
//    public void addEvent(Event event) {
//        synchronized (pendingEvents) {
//            pendingEvents.offer(event);
//            pendingEvents.notify(); // Notify any waiting threads that a new event is available
//        }
//    }
//
//    private static void msg(String message) {
//        System.err.println(message);
//    }
//}



/// please write a function in utils after parsing the string if it s[0].equals is set then priority is 1 other wise it is 2


package event_loop;

import utils.ResponseStructure;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventLoop {
    private final ExecutorService threadPool;
    private final Deque<Event> pendingEvents;
    private final Deque<Event> processedEvents;

    public EventLoop() {
        threadPool = Executors.newCachedThreadPool();
        pendingEvents = new ArrayDeque<>();
        processedEvents = new ArrayDeque<>();
    }

    public void start() {
        threadPool.execute(() -> {
            while (true) {
                Event event;
                synchronized (pendingEvents) {
                    while (pendingEvents.isEmpty()) {
                        try {
                            pendingEvents.wait(); // Wait for a notification that a new event is available
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    event = pendingEvents.poll(); // Retrieve the next event
                }
                if (event != null) {
                    processEvent(event);
                }
            }
        });
    }

    public void dispatchEvent(Socket clientSocket, int maxSize, int priority) {
        threadPool.execute(() -> {
            try {
                // Read data from clientSocket
                byte[] requestBytes = new byte[maxSize];
                int bytesRead = clientSocket.getInputStream().read(requestBytes);

                if (bytesRead > 0) {
                    // Process the event
                    String clientMessage = new String(requestBytes, 0, bytesRead);
                    String[] parsedRequest = clientMessage.split(" "); // use parsedString present at utlis
                    Event event = new Event(clientSocket.getInetAddress(), clientSocket.getPort(), parsedRequest, clientSocket, priority);
                    addEvent(event);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void processEvent(Event event) {
        ResponseStructure res;
        if (event.getPriority() == 1) { // Assuming priority 1 means asynchronous processing
            res = processAsynchronously(event.getParsedRequest());
        } else {
            res = processSynchronously(event.getParsedRequest());
        }

        try {
            // Create a response buffer based on the response structure
            ByteBuffer replyBuffer = createResponseBuffer(res);

            // Send the response back to the client
            sendResponse(event.getClientSocket(), replyBuffer);

            synchronized (processedEvents) {
                processedEvents.offer(event);
                processedEvents.notify(); // Notify any waiting threads that a response is available
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ResponseStructure processAsynchronously(String[] parsedString) {
        return RequestHandlers.requestHandlerFunction(parsedString);
    }

    private ResponseStructure processSynchronously(String[] parsedString) {
        return RequestHandlers.requestHandlerFunction(parsedString);
    }

    private ByteBuffer createResponseBuffer(ResponseStructure response) {
        ByteBuffer replyBuffer = ByteBuffer.allocate(4);
        if (response.getStatusCode() == 200) {
            byte[] replyBytes = response.getData().getBytes();
            replyBuffer = ByteBuffer.allocate(4 + replyBytes.length);
            replyBuffer.putInt(replyBytes.length);
            replyBuffer.put(replyBytes);
            replyBuffer.flip(); // Flip the buffer to prepare for reading
        }
        return replyBuffer;
    }

    private void sendResponse(Socket clientSocket, ByteBuffer replyBuffer) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        byte[] buffer = new byte[replyBuffer.remaining()];
        replyBuffer.get(buffer); // Read the buffer into the byte array
        outputStream.write(buffer);
        outputStream.flush();
        outputStream.close(); // Close the output stream
    }

    public void addEvent(Event event) {
        synchronized (pendingEvents) {
            pendingEvents.offer(event);
            pendingEvents.notify(); // Notify any waiting threads that a new event is available
        }
    }
}
