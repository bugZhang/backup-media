package jerry.backup.media;

import jerry.backup.media.job.SyncJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BackupMediaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BackupMediaApplication.class, args);

        SyncJob job = context.getBean(SyncJob.class);
        job.start();

        context.close();
    }

}
