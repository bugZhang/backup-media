package jerry.backup.media.job;

import jerry.backup.media.config.BackupConfiguration;
import jerry.backup.media.enums.MediaTypeEnum;
import jerry.backup.media.helper.SyncHelper;
import jerry.backup.media.helper.ToolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
public class SyncJob {

    private final BackupConfiguration configuration;

    private final SyncHelper syncHelper;

    private final ToolExecutor executor;

    public SyncJob(
            BackupConfiguration configuration,
            SyncHelper syncHelper,
            ToolExecutor executor
    ) {
        this.configuration = configuration;
        this.syncHelper = syncHelper;
        this.executor = executor;
    }

    public void start(){
        log.info("Starting sync job");
        String sourcePath = configuration.getSourcePath();
        String targetPath = configuration.getTargetPath();

        ArrayList<String> excludeFolders = configuration.getExcludeFolderName();
        ArrayList<String> excludeFilenames = configuration.getExcludeFileName();
        MediaTypeEnum mediaType = MediaTypeEnum.ALL;

        syncHelper.start(sourcePath, targetPath, configuration.getUncategorizedPath(), excludeFolders, excludeFilenames, mediaType);
    }
}
