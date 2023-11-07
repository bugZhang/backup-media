package jerry.backup.media.helper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import jerry.xtool.utils.DateTimeUtils;
import jerry.xtool.utils.FileUtils;
import jerry.xtool.utils.StringUtils;
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

    @Nullable
    public int[] getCreateDate(String filePath){
        File file = new File(filePath);

        if(!file.exists() || !file.isDirectory()){
            return null;
        }

        int[] date = parseDateWithTimestamp(file.getName());

        if(date[0] == 0){
            date = parseDateWithDateString(file.getName());
        }

        if(date[0] == 0){
            date = null;
        }

        return date;
    }

    public int[] parseDateWithDateString(String filename){

        int[] date = new int[3];
        Pattern r;
        Matcher m;

        // 获取 年月日 格式的
        filename = trimFileName(filename);
        for (String pattern : dataPattern){
            r = Pattern.compile(pattern);
            m = r.matcher(filename);
            if(m.find()){
                if(StringUtils.isNotEmpty(m.group(1))){
                    date[0] = Integer.parseInt(m.group(1));
                }
                if(StringUtils.isNotEmpty(m.group(2))){
                    date[1] = Integer.parseInt(m.group(2));
                }
                if(StringUtils.isNotEmpty(m.group(3))){
                    date[2] = Integer.parseInt(m.group(3));
                }
                return date;
            }
        }

        return date;
    }

    private int[] parseDateWithTimestamp(String filename){
        int[] date = new int[3];
        Pattern r;
        Matcher m;

        // 获取时间戳格式
        String tsPattern = "\\d{10}";
        r = Pattern.compile(tsPattern);
        m = r.matcher(filename);

        if(m.find()){
            int ts = Integer.parseInt(m.group(0));
            Instant instant = Instant.ofEpochSecond(ts);
            if(instant.isBefore(Instant.ofEpochSecond(3813724551L)) && instant.isAfter(Instant.ofEpochSecond(973583751))){
                LocalDateTime localDateTime = DateTimeUtils.instantToLocalDateTime(instant, "Asia/Shanghai");
                date[0] = localDateTime.getYear();
                date[1] = localDateTime.getMonthValue();
                date[2] = localDateTime.getDayOfMonth();
                return date;
            }
        }
        return date;
    }


    private String trimFileName(String filename){

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

    public static void main(String[] args) {

//        String filename = "IMG_1289116551000.jpg";


        MediaInfoHelper mediaInfoHelper = new MediaInfoHelper();

        List<String> filenames = new ArrayList<>();
        filenames.add("IMG_20200725_0083.MOV");
        filenames.add("20190826_0083.JPG");
        filenames.add("IMG_20230927.JPG");
        filenames.add("IMG_2022-11-25.JPG");
        filenames.add("IMG_2021_02_25_1231.JPG");
        filenames.add("IMG_123123124213423.jpg");
        filenames.add("/Users/jerry/Desktop/WechatIMG164.jpg");

        for (String filename: filenames){
            int[] date = mediaInfoHelper.getCreateDate(filename);

            if(date == null){
                System.out.println("解析失败");
                continue;
            }

            System.out.println(date.length);
            System.out.println(date[0] + " " + date[1] + " " + date[2]);
            System.out.println("===================");
        }


    }
    public int[] getCreateTimeFromMetaData(File file) throws ImageProcessingException, IOException {

        if(!file.exists()){
            throw new RuntimeException("文件不存在, file:" + file.getAbsolutePath());
        }
        if(!FileUtils.isImage(file.getName()) && !FileUtils.isVideo(file.getName())){
            throw new RuntimeException("获取 meta 信息失败，文件类型不支持, file:" + file.getName());
        }

        int[] date = new int[3];

        Metadata metadata = ImageMetadataReader.readMetadata(file);


        /**
         * Date/Time Original     36867  2023:05:07 15:23:55
         * Date/Time Original     36867  2023:11:04 15:24:16
         * Creation Time     20481  星期六 六月 24 22:06:09 +08:00 2023
         */

        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        Date createDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

        if(createDate == null){
            // todo 读取 Creation Time
        }

        if(createDate == null){
            return date;
        }

        LocalDateTime localDateTime = DateTimeUtils.instantToLocalDateTime(createDate.toInstant(), "Asia/Shanghai");
        date[0] = localDateTime.getYear();
        date[1] = localDateTime.getMonthValue();
        date[2] = localDateTime.getDayOfMonth();

        return date;

    }
}
