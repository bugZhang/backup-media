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
//        File file = new File("Z:\\照片\\日常\\朵朵\\2023\\05\\IMG_0095.JPG");
//        File file = new File("Z:\\verysyncbackup\\jerry-iphone\\photos\\IMG_20231104_5205.HEIC");
//        File file = new File("Z:\\verysyncbackup\\jerry-xiaomi\\DCIM\\Camera\\踢毽(0).mp4");
//        File file = new File("Z:\\verysyncbackup\\mary_huawei_dcim\\DJI Album\\DJI_20220605_173011_30_video.mp4");
//        File file = new File("Z:\\照片\\旅行\\2016_03_19_大运河\\IMG_8207.JPG");
//        File file = new File("Z:\\verysyncbackup\\mary_huawei_dcim\\Camera\\IMG_4541.JPG");
        File file = new File("F:\\DCIM\\PICT0046.jpg");

        System.out.println("---------");
        System.out.println(file.isDirectory());
        System.out.println(file.isHidden());
        int[] createTime = mediaInfoHelper.parseDateFromMetaData(file);
        if (null != createTime){
            System.out.println(createTime[0] + " " + createTime[1] + " " + createTime[2]);
        }

        System.out.println("==================");

    }

    @Test
    public void parseDateFromAttributeData() throws IOException {
//        File file = new File("Z:\\照片\\日常\\朵朵\\2023\\05\\IMG_0095.JPG");
//        File file = new File("Z:\\verysyncbackup\\jerry-iphone\\photos\\IMG_20231104_5205.HEIC");
//        File file = new File("Z:\\verysyncbackup\\jerry-xiaomi\\DCIM\\Camera\\踢毽(0).mp4");
//        File file = new File("Z:\\verysyncbackup\\mary_huawei_dcim\\DJI Album\\DJI_20220605_173011_30_video.mp4");
//        File file = new File("Z:\\照片\\旅行\\2016_03_19_大运河\\IMG_8207.JPG");
//        File file = new File("C:\\Users\\plamk\\Pictures\\wallpaper\\100 Beautiful Ultra HD 4K Wallpapers Pack-108 (1).jpg");
        File file = new File("Z:\\照片\\duoduo相机\\PAS_ATR_PICT0005.jpg");

        System.out.println("---------");
        System.out.println(file.isDirectory());
        System.out.println(file.isHidden());
        int[] createTime = mediaInfoHelper.parseDateFromAttributeData(file);
        if (null != createTime){
            System.out.println(createTime[0] + " " + createTime[1] + " " + createTime[2]);
        }

        System.out.println("==================");

    }

}
