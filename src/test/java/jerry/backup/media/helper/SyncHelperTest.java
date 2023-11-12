package jerry.backup.media.helper;

import jerry.backup.media.config.BackupConfiguration;
import jerry.backup.media.enums.MediaTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
@ActiveProfiles(value = "dev")
public class SyncHelperTest {

    @Autowired
    private SyncHelper syncHelper;

    @Autowired
    private BackupConfiguration configuration;

    @Test
    public void startMedia() throws IOException {
        String sourcePath = configuration.getSourcePath();
        String targetPath = configuration.getTargetPath();

        ArrayList<String> excludeFolders = configuration.getExcludeFolderName();
        ArrayList<String> excludeFilenames = configuration.getExcludeFileName();
        MediaTypeEnum mediaType = MediaTypeEnum.ALL;

        syncHelper.start(sourcePath, targetPath, configuration.getUncategorizedPath(), excludeFolders, excludeFilenames, mediaType);

    }

    @Test
    public void getUncategorizedFilePath(){
        String path = "Z:\\verysyncbackup\\[备份]马丽的 iPhone\\相册\\2015年07月\\IMG_2886.JPG";
        File file = new File(path);
//        String uncategorizedFilePath = syncHelper.getUncategorizedFilePath(file, configuration.getUncategorizedPath());
//        System.out.println(uncategorizedFilePath);

    }
}
