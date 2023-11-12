package jerry.backup.media.service;

import jerry.backup.media.config.BackupConfiguration;
import jerry.backup.media.data.Media;
import jerry.backup.media.enums.MediaTypeEnum;
import jerry.backup.media.enums.SyncStatusEnum;
import jerry.xtool.utils.StringUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles(value = "dev")
public class MediaServiceTest {

    @Autowired
    private BackupConfiguration configuration;

    @Autowired
    private IMediaService mediaService;


    @RepeatedTest(5)
    public void save(){

        System.out.println("----------------" + configuration.getSourcePath());

        Media media = new Media();
        media.setType(MediaTypeEnum.UNKNOWN);
        media.setFilename(StringUtils.randomString(8));
        media.setSourceDirPath("/Users/jerry/Documents/java-workspace/holla/monkey/monkey-user-service/");
        media.setTargetFilePath("/Users/jerry/Documents/java-workspace/holla/monkey/target");
        media.setStatus(SyncStatusEnum.FAILED_REPEATED);
        mediaService.save(media);
    }

    @Test
    public void findBySourceDirAndFilename(){
        String sourceDirPath = "/Users/jerry/Documents/java-workspace/holla/monkey/monkey-user-service/";
        String filename = "9Mw2HAp6";
        Optional<Media> mediaOpt = mediaService.findBySourceDirAndFilename(sourceDirPath, filename);
        System.out.println("==============");
        System.out.println(mediaOpt.get().getSourceDirPath());
        System.out.println(mediaOpt.get().getFilename());
    }

    @Test
    public void existsWithStatus(){
        String sourceDirPath = "/Users/jerry/Documents/java-workspace/holla/monkey/monkey-user-service/";
        String filename = "9Mw2HAp6";
        boolean hasProcessed = mediaService.existsWithStatus(sourceDirPath, filename, SyncStatusEnum.SUCCESS);
        System.out.println("-----------------");
        System.out.println(hasProcessed);
    }

}
