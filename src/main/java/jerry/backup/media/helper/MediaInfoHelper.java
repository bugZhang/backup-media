package jerry.backup.media.helper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.mp4.Mp4Directory;
import jerry.xtool.utils.DateTimeUtils;
import jerry.xtool.utils.FileUtils;
import jerry.xtool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MediaInfoHelper {

    private final String[] dataPattern = {
            "(\\d{4})-(\\d{2})-(\\d{2})_*",
            "(\\d{4})_(\\d{2})_(\\d{2})_*",
            "(\\d{4})(\\d{2})(\\d{2})_*",
    };

    private final String[] cleanPrefix = {
            "IMG_", "IMG", "MEIPAI_", "PANO_", "SNAPSHOT_", "wx_camera_", "Screenshot_", "TG-", "retouch_", "lv_", "mmexport",
            "vid_", "VID_"
    };

    private final String[] cleanContain = {
    };

    @Nullable
    public int[] getCreateDate(String filePath) throws ImageProcessingException, IOException {
        File file = new File(filePath);

        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        int[] date = parseDateFromTimestamp(file.getName());

        if (date[0] == 0) {
            date = parseDateFromDateString(file.getName());
        }

        if (date[0] == 0) {
            date = parseDateFromMetaData(file);
        }

        if (date[0] == 0) {
            date = null;
        }

        return date;
    }

    public int[] parseDateFromDateString(String filename) {

        int[] date = new int[3];
        Pattern r;
        Matcher m;

        // 获取 年月日 格式的
        filename = trimFileName(filename);
        LocalDateTime current = LocalDateTime.now();
        for (String pattern : dataPattern) {
            r = Pattern.compile(pattern);
            m = r.matcher(filename);

            while (m.find()){
                if (StringUtils.isNotEmpty(m.group(1))) {
                    int year = Integer.parseInt(m.group(1));
                    if (year > current.getYear() || year < 2000) {
                        continue;
                    }
                    date[0] = Integer.parseInt(m.group(1));
                }
                if (StringUtils.isNotEmpty(m.group(2))) {
                    date[1] = Integer.parseInt(m.group(2));
                }
                if (StringUtils.isNotEmpty(m.group(3))) {
                    date[2] = Integer.parseInt(m.group(3));
                }
                return date;
            }
        }

        return date;
    }

    private int[] parseDateFromTimestamp(String filename) {
        int[] date = new int[3];
        Pattern r;
        Matcher m;

        // 自定义特殊规则
        if(filename.contains("_edit_")){
            return date;
        }

        filename = trimFileName(filename);

        // 获取时间戳格式
        String tsPattern = "\\d{10}";
        r = Pattern.compile(tsPattern);
        m = r.matcher(filename);

        while (m.find()){

            int ts;
            try {
                ts = Integer.parseInt(m.group(0));
            }catch (NumberFormatException exception){
                continue;
            }

            Instant instant = Instant.ofEpochSecond(ts);
            // after 2010-01-01
            if (instant.isBefore(Instant.now()) && instant.isAfter(Instant.ofEpochSecond(1262278861))) {
                LocalDateTime localDateTime = DateTimeUtils.instantToLocalDateTime(instant, "Asia/Shanghai");
                date[0] = localDateTime.getYear();
                date[1] = localDateTime.getMonthValue();
                date[2] = localDateTime.getDayOfMonth();
                return date;
            }
        }
        return date;
    }


    private String trimFileName(String filename) {

        for (String prefix : cleanPrefix) {
            if (filename.startsWith(prefix)) {
                filename = filename.replaceFirst(prefix, "");
            }
            if (filename.startsWith(prefix.toLowerCase())) {
                filename = filename.replaceFirst(prefix.toLowerCase(), "");
            }
            if (filename.startsWith(prefix.toUpperCase())) {
                filename = filename.replaceFirst(prefix.toUpperCase(), "");
            }
        }

        for (String str: cleanContain){
            filename = filename.replaceFirst(str, "");
        }

        return filename;
    }

    public static void main(String[] args) throws ImageProcessingException, IOException {

//        String filename = "IMG_1289116551000.jpg";


        MediaInfoHelper mediaInfoHelper = new MediaInfoHelper();

        List<String> filenames = new ArrayList<>();
        filenames.add("Z:\\verysyncbackup\\jerry-xiaomi\\DCIM\\Screenshots\\Screenshot_2021-01-28-22-41-03-689_com.tencent.mm.jpg");
//        filenames.add("20190826_0083.JPG");
//        filenames.add("IMG_20230927.JPG");
//        filenames.add("IMG_2022-11-25.JPG");
//        filenames.add("IMG_2021_02_25_1231.JPG");
//        filenames.add("IMG_123123124213423.jpg");
//        filenames.add("/Users/jerry/Desktop/WechatIMG164.jpg");

        for (String filename : filenames) {
            int[] date = mediaInfoHelper.getCreateDate(filename);

            if (date == null) {
                System.out.println("解析失败");
                continue;
            }

            System.out.println(date.length);
            System.out.println(date[0] + " " + date[1] + " " + date[2]);
            System.out.println("===================");
        }


    }

    public int[] parseDateFromMetaData(File file) throws ImageProcessingException, IOException {

        if (!file.exists()) {
            throw new RuntimeException("文件不存在, file:" + file.getAbsolutePath());
        }
        boolean isImage = FileUtils.isImage(file.getName());
        boolean isVideo = FileUtils.isVideo(file.getName());
        if (!isImage && !isVideo) {
            throw new RuntimeException("获取 meta 信息失败，文件类型不支持, file:" + file.getName());
        }

        int[] date = new int[3];

        Metadata metadata = ImageMetadataReader.readMetadata(file);


//        for (Directory directory : metadata.getDirectories()) {
//            for (Tag tag : directory.getTags()) {
//                System.out.println(tag);
//            }
//        }

        Date createDate = null;
        if (isVideo) {
            Mp4Directory mp4Directory = metadata.getFirstDirectoryOfType(Mp4Directory.class);
            if (null != mp4Directory) {
                createDate = mp4Directory.getDate(Mp4Directory.TAG_CREATION_TIME);
            }
        }

        if (isImage) {
            ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (null != directory) {
                createDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            }
        }

        if (createDate == null) {
            return date;
        }

        LocalDateTime localDateTime = DateTimeUtils.instantToLocalDateTime(createDate.toInstant(), "Asia/Shanghai");
        date[0] = localDateTime.getYear();
        date[1] = localDateTime.getMonthValue();
        date[2] = localDateTime.getDayOfMonth();

        return date;

    }
}
