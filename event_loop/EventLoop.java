package event_loop;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EventLoop extends Thread {
    private final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();

    public void dispatchEvent(Event event) {
        eventQueue.add(event);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Event event = eventQueue.take();
                RequestHandler.handleRequest(event);
                synchronized (event) {
                    event.setProcessed(true);
                    event.notify();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

