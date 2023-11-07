package jerry.backup.media.helper;

import com.drew.imaging.ImageProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

@SpringBootTest
@ActiveProfiles(value = "dev")
public class MediaInfoHelperTest {

    @Autowired
    private MediaInfoHelper mediaInfoHelper;


    @Test
    public void getTimeFromMetaData() throws ImageProcessingException, IOException {
//        File file = new File("Z:\\照片\\日常\\朵朵\\2023\\05\\IMG_0095.JPG");
        File file = new File("Z:\\verysyncbackup\\jerry-iphone\\photos\\IMG_20231104_5205.HEIC");
        int[] createTime = mediaInfoHelper.getCreateTimeFromMetaData(file);
        if (null != createTime){
            System.out.println(createTime[0]);
        }

        System.out.println("==================");

    }

}
