package jerry.backup.media.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SyncJob implements InitializingBean {

    public void start(){
        log.info("sync job start ...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
