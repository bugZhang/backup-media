package jerry.backup.media.helper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import jerry.xtool.utils.FileUtils;
import jerry.xtool.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MediaInfoHelper {

    private final String[] dataPattern = {
            "(\\d{4})(\\d{2})(\\d{2})_*",
            "(\\d{4})-(\\d{2})-(\\d{2})_*",
            "(\\d{4})_(\\d{2})_(\\d{2})_*"
    };

    private final String[] cleanPrefix = {
            "IMG_", "IMG", "MEIPAI_", "PANO_", "SNAPSHOT_", "wx_camera_", "Screenshot_", "TG-",
            "vid_", "VID_"
    };

    public Instant getCreateTimeWithFilename(String filename){

        filename = trimFileName(filename);
        Pattern r;
        Matcher m;
        for (String pattern : dataPattern){
            r = Pattern.compile(pattern);
            m = r.matcher(filename);
            if(m.find()){
                System.out.println( m.group(1));
                System.out.println( m.group(2));
                System.out.println( m.group(3));
                return null;
            }
        }

        System.out.println("---------------");

        return null;
    }

    public String trimFileName(String filename){

        for (String prefix: cleanPrefix){
            if(filename.startsWith(prefix)){
                filename = filename.replaceFirst(prefix, "");
            }
            if(filename.startsWith(prefix.toLowerCase())){
                filename = filename.replaceFirst(prefix.toLowerCase(), "");
            }
            if(filename.startsWith(prefix.toUpperCase())){
                filename = filename.replaceFirst(prefix.toUpperCase(), "");
            }

            return filename;

        }


        return filename;
    }

//    public static void main(String[] args) {
//        MediaInfoHelper mediaInfoHelper = new MediaInfoHelper();
//        mediaInfoHelper.getShootingAt("IMG_20200725_0083.MOV");
//        System.out.println("fn");
//
//    }
    public Instant getCreateTimeFromMetaData(File file) throws ImageProcessingException, IOException {

        if(!file.exists()){
            throw new RuntimeException("文件不存在, file:" + file.getAbsolutePath());
        }
        if(!FileUtils.isImage(file.getName()) && !FileUtils.isVideo(file.getName())){
            throw new RuntimeException("获取 meta 信息失败，文件类型不支持, file:" + file.getName());
        }

        Metadata metadata = ImageMetadataReader.readMetadata(file);


        /**
         * Date/Time Original     36867  2023:05:07 15:23:55
         * Date/Time Original     36867  2023:11:04 15:24:16
         * Creation Time     20481  星期六 六月 24 22:06:09 +08:00 2023
         */
        Instant createTime = null;
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if(tag.getTagType() == 36867 && StringUtils.isNotEmpty(tag.getDescription())){  // Date/Time Original
                    createTime = Instant.parse(tag.getDescription().trim());
                    break;
                }
            }
        }


        return createTime;

    }
}
