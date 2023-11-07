package jerry.backup.media.helper;

import jerry.backup.media.data.FailedJob;
import jerry.backup.media.enums.FailedReasonEnum;
import jerry.backup.media.enums.MediaTypeEnum;
import jerry.backup.media.service.IFailedJobService;
import jerry.backup.media.service.IMediaService;
import jerry.xtool.utils.FileUtils;
import jerry.xtool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Component
@Slf4j
public class SyncHelper {

    private final IMediaService mediaService;
    private final IFailedJobService failedJobService;
    private final MediaInfoHelper mediaInfoHelper;

    public SyncHelper(
            IMediaService mediaService,
            IFailedJobService failedJobService,
            MediaInfoHelper mediaInfoHelper
    ) {
        this.mediaService = mediaService;
        this.failedJobService = failedJobService;
        this.mediaInfoHelper = mediaInfoHelper;
    }

    public void syncMedia(
            String sourcePath,
            String targetSource,
            ArrayList<String> excludeFolderNames,
            ArrayList<String> excludeFileNames,
            MediaTypeEnum mediaType
    ){

        ArrayList<String> allMedia = findMedias(sourcePath, excludeFileNames, mediaType);

    }

    private ArrayList<String> findMedias(String sourcePath,
                                         ArrayList<String> excludeFileNames,
                                         MediaTypeEnum mediaType
    ){
        File folder = new File(sourcePath);
        if(!folder.exists() || !folder.isDirectory()){
            throw new RuntimeException("文件夹不存在或者不是文件夹, path:" + sourcePath);
        }

        File[] files = folder.listFiles();
        ArrayList<String> medias = new ArrayList<>();
        if(null == files){
            return medias;
        }

        for (File file: files){
            if(file.isFile()){

                String filename = file.getName();
                boolean checkType;

                if(MediaTypeEnum.IMAGE == mediaType){
                    checkType = FileUtils.isImage(filename);
                }else if (MediaTypeEnum.VIDEO == mediaType){
                    checkType = FileUtils.isVideo(filename);
                }else {
                    checkType = FileUtils.isImage(filename) ||  FileUtils.isVideo(filename);
                }

                if(checkType && !isExcludeFile(filename, excludeFileNames)){
                    medias.add(file.getAbsolutePath());
                }else {
                    log.warn("found not media file or exclude: {}", file.getAbsolutePath());
                }
            }
        }

        return medias;
    }


    public ArrayList<String> findAllFolders(String sourcePath, ArrayList<String> excludeFolders){

        ArrayList<String> allFolders = new ArrayList<>();

        loadFolders(sourcePath, excludeFolders, allFolders);

        return allFolders;
    }

    private void loadFolders(String sourcePath, ArrayList<String> excludeFolders, ArrayList<String> folders){
        File folder = new File(sourcePath);

        if(!folder.exists() || !folder.isDirectory()){
            throw new RuntimeException("文件夹不存在或者不是文件夹, path:" + sourcePath);
        }

        File[] files = folder.listFiles();

        if(null == files){
            return ;
        }

        for (File file: files){
            if(file.isDirectory() && !isExcludeFolder(file.getName(), excludeFolders)){
                folders.add(file.getAbsolutePath());
                loadFolders(file.getAbsolutePath(), excludeFolders, folders);
            }
        }
    }

    private boolean isExcludeFolder(String folderName, ArrayList<String> excludeFolders){
        if(null == excludeFolders){
            return false;
        }

        for (String exclude: excludeFolders){
            if(folderName.toLowerCase().contains(exclude.toLowerCase())){
                return true;
            }
        }

        return false;
    }

    private boolean isExcludeFile(String filename, ArrayList<String> excludeFileNames){

        if(null == excludeFileNames || excludeFileNames.isEmpty()){
            return false;
        }
        for (String exclude: excludeFileNames){
            if(filename.toLowerCase().contains(exclude.toLowerCase())){
                return true;
            }
        }

        return false;

    }

    private void copy(String sourcePath, String targetPath, MediaTypeEnum mediaType) throws IOException {
        File sourceFile = new File(sourcePath);
        int[] date = null;
        try {
            date = mediaInfoHelper.getCreateDate(sourcePath);
        }catch (Exception exception){
            log.error(exception.getMessage());
            saveFailedJob(sourceFile, mediaType, FailedReasonEnum.PARSE_FILENAME_FAILED);
            return;
        }

        if(date == null || date[0] == 0){
            log.error("文件日期解析失败, file:{}", sourcePath);
            saveFailedJob(sourceFile, mediaType, FailedReasonEnum.PARSE_FILENAME_FAILED);
            return;
        }

        targetPath = StringUtils.rtrim(targetPath.trim(), File.separator) + File.separator + date[0] + File.separator;

        if(date[1] != 0){
            targetPath = targetPath + File.separator + date[1];
        }

        if(date[2] != 0){
            targetPath = targetPath + File.separator + date[2];
        }

        String targetFilePath = targetPath + File.separator + sourceFile.getName();

        Path path = Paths.get(targetPath);
        Files.createDirectories(path);

        File targetFile = new File(targetFilePath);
        if(targetFile.exists()){
            
        }




    }

    private void saveFailedJob(File sourceFile, MediaTypeEnum mediaType, FailedReasonEnum failedReason){

        FailedJob failedJob = new FailedJob();
        failedJob.setType(mediaType);
        failedJob.setReason(failedReason);
        failedJob.setFilename(sourceFile.getName());
        failedJob.setSourceDirPath(sourceFile.getAbsolutePath());

        failedJobService.save(failedJob);

    }

}
