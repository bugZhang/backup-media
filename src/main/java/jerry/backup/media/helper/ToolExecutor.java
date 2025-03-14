package jerry.backup.media.helper;

import jerry.backup.media.factory.ToolThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
@Slf4j
public class ToolExecutor implements InitializingBean {

    private final int corePoolSize = 2;
    private final int maximumPoolSize = 4;

    @Getter
    private ThreadPoolExecutor executor;

    @Override
    public void afterPropertiesSet() {
        log.info("{} init: corePoolSize:{}, maximumPoolSize:{} ", getClass().getName(), corePoolSize, maximumPoolSize);

        executor = new ThreadPoolExecutor(
                corePoolSize,   // 线程池的核心线程数量
                maximumPoolSize,    // 线程池的最大线程数
                61L,   // 非核心线程的最大存活时间
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100), // 当新任务来的时候会先判断当前运行的线程数量是否达到核心线程数，如果达到的话，新任务就会被存放在队列中。
                new ToolThreadFactory("JerryTool"),
                new ThreadPoolExecutor.CallerRunsPolicy()   //拒绝策略，当提交的任务过多而不能及时处理时，我们可以定制策略来处理任务
        );

    }

    public void execute(Runnable runnable){
        executor.execute(runnable);
    }

    public <T> Future<T> submit(Callable<T> task){
        return executor.submit(task);
    }
}
