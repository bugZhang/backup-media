package jerry.backup.media.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ToolThreadFactory implements ThreadFactory {

    private final String threadName;

    private final AtomicInteger nextId = new AtomicInteger(1);

    public ToolThreadFactory(String threadName) {
        this.threadName = threadName + "-Worker-";
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = this.threadName + nextId.getAndIncrement();
        Thread thread = new Thread(null, r, threadName, 0);
        thread.setDaemon(true);
        return thread;
    }
}
