package jerry.backup.media.service;

import jerry.backup.media.config.BackupConfiguration;
import jerry.backup.media.data.Media;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value = "dev")
public class MediaServiceTest {

    @Autowired
    private BackupConfiguration configuration;

    @Autowired
    private IMediaService mediaService;


    @Test
    public void save(){

        System.out.println("----------------" + configuration.getSourcePath());

        Media media = new Media();
        media.setType(1);
        media.setFilename("aaaa");
        media.setSourcePath("source/path");
        media.setTargetPath("target/pah");
        media.setResult(2);
        mediaService.save(media);
    }

}
