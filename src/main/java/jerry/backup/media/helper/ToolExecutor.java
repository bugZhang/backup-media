package jerry.backup.media.helper;

import jerry.backup.media.factory.ToolThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@Slf4j
public class ToolExecutor implements InitializingBean, DisposableBean {

    private final int corePoolSize = 2;
    private final int maximumPoolSize = 4;

    @Getter
    private ThreadPoolExecutor executor;

    @Override
    public void afterPropertiesSet() {
        log.info("{} init: corePoolSize:{}, maximumPoolSize:{} ", getClass().getName(), corePoolSize, maximumPoolSize);

        executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                61L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100),
                new ToolThreadFactory("JerryTool"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

    }

    public void execute(Runnable runnable){
        executor.execute(runnable);
    }

    public <T> Future<T> submit(Callable<T> task){
        return executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(24, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void destroy() {
        shutdown();
    }
}
