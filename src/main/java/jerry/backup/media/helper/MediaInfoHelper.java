package jerry.backup.media.helper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import jerry.xtool.utils.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class MediaInfoHelper {

    public String getTimeFromMetaData(File file) throws ImageProcessingException, IOException {

        if(!file.exists()){
            throw new RuntimeException("文件不存在, file:" + file.getAbsolutePath());
        }
        if(!FileUtils.isImage(file.getName()) && !FileUtils.isVideo(file.getName())){
            throw new RuntimeException("获取 meta 信息失败，文件类型不支持, file:" + file.getName());
        }

        Metadata metadata = ImageMetadataReader.readMetadata(file);

        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.println(tag.getTagType());
            }
        }


        return null;

    }
}
