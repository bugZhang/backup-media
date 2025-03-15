package jerry.backup.media;

import jerry.backup.media.job.SyncJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BackupMediaApplication {

    private static ApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(BackupMediaApplication.class, args);

        SyncJob job = context.getBean(SyncJob.class);

        job.start();
    }

}
