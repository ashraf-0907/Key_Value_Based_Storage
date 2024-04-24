package event_loop;

import utils.ResponseStructure;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class EventLoop {
    private final PriorityQueue<Event> events;
    public final Deque<Event> processedEvents;
    private final ExecutorService threadPool;

    public EventLoop() {
        events = new PriorityQueue<>(Comparator.comparingInt(Event::getPriority));
        processedEvents = new ArrayDeque<>();
        threadPool = Executors.newCachedThreadPool(); // Using a cached thread pool for handling asynchronous requests
    }

    public void addEvent(Event event) {
        synchronized (events) {
            events.offer(event);
            events.notify(); // Notify any waiting threads that an event has been added
        }
    }

    public void start() {
        // Start the main processing loop in a separate thread
        threadPool.execute(() -> {
            while (true) {
                Event event;
                synchronized (events) {
                    // Wait until there is an event to process
                    while (events.isEmpty()) {
                        try {
                            events.wait(); // Wait for a notification that an event is available
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // Get the next event to process
                    event = events.poll();
                }

                processEvent(event);
            }
        });
    }

    private void processEvent(Event event) {
        ResponseStructure res;
        boolean async = event.getAsynchronous();

        if (async) {
            res = processAsynchronously(event.getRequestBytes());
        } else {
            res = processSynchronously(event.getRequestBytes());
        }

        ByteBuffer replyBuffer = createResponseBuffer(res);
        event.setResponseBytes(replyBuffer);
        synchronized (processedEvents) {
            processedEvents.offer(event); // Add processed event to the deque
            processedEvents.notify(); // Notify any waiting threads that a response is available
        }
    }

    private ResponseStructure processAsynchronously(String[] parsedString) {
        AtomicReference<ResponseStructure> res = new AtomicReference<>(new ResponseStructure());
        threadPool.execute(() -> {
            res.set(RequestHandlers.requestHandlerFunction(parsedString));
        });
        // Return a default response for now, as the actual response will be set asynchronously
        return res.get();
    }

    private ResponseStructure processSynchronously(String[] parsedString) {
        return RequestHandlers.requestHandlerFunction(parsedString);
    }

    private ByteBuffer createResponseBuffer(ResponseStructure response) {
        ByteBuffer replyBuffer = ByteBuffer.allocate(4); // Allocate space for the buffer
        if (response.getStatusCode() == 200) {
            byte[] replyBytes = response.getData().getBytes();
            replyBuffer = ByteBuffer.allocate(4 + replyBytes.length);
            replyBuffer.putInt(replyBytes.length);
            replyBuffer.put(replyBytes);
        } else if (response.getStatusCode() == 401 || response.getStatusCode() == 500) {
            byte[] replyBytes = response.getMessage().getBytes();
            replyBuffer = ByteBuffer.allocate(4 + replyBytes.length);
            replyBuffer.putInt(replyBytes.length);
            replyBuffer.put(replyBytes);
        }
        return replyBuffer;
    }
}
