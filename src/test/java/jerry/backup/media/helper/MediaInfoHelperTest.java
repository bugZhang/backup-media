package jerry.backup.media.helper;

import com.drew.imaging.ImageProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;

@SpringBootTest
@ActiveProfiles(value = "dev")
public class MediaInfoHelperTest {

    @Autowired
    private MediaInfoHelper mediaInfoHelper;

    @Test
    public void getTimeFromMetaData() throws ImageProcessingException, IOException {
        File file = new File("/Users/jerry/Desktop/WechatIMG4.jpeg");
        mediaInfoHelper.getTimeFromMetaData(file);
        System.out.println("==================");

    }

}
