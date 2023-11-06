package jerry.backup.media.job;

import jerry.backup.media.config.BackupConfiguration;
import jerry.backup.media.helper.SyncHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
public class SyncJob implements InitializingBean {

    private final BackupConfiguration backupConfiguration;

    private final SyncHelper syncHelper;

    public SyncJob(
            BackupConfiguration backupConfiguration,
            SyncHelper syncHelper
    ) {
        this.backupConfiguration = backupConfiguration;
        this.syncHelper = syncHelper;
    }

    public void start(){
        log.info("sync job start ...");
    }

    public void startFailed(){

    }



    @Override
    public void afterPropertiesSet() throws Exception {
        start();
        ArrayList<String> all = syncHelper.findAllFolders(backupConfiguration.getSourcePath(), backupConfiguration.getExcludeFolderName());
        for (String f : all){
            System.out.println(f);
        }

    }
}
