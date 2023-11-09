package jerry.backup.media.helper;

import jerry.backup.media.data.FailedJob;
import jerry.backup.media.data.Media;
import jerry.backup.media.enums.FailedReasonEnum;
import jerry.backup.media.enums.MediaTypeEnum;
import jerry.backup.media.service.IFailedJobService;
import jerry.backup.media.service.IMediaService;
import jerry.xtool.utils.FileUtils;
import jerry.xtool.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
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
            ArrayList<String> excludeFileNames,
            MediaTypeEnum mediaType
    ) throws IOException {

        ArrayList<String> allMedia = findMedias(sourcePath, excludeFileNames, mediaType);

        for (String mediaFilePath: allMedia){
            copy(mediaFilePath, targetSource, mediaType);
        }

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

    private void copy(String sourceFilePath, String targetDirPath, MediaTypeEnum mediaType) throws IOException {
        File sourceFile = new File(sourceFilePath);
        int[] date = null;
        try {
            date = mediaInfoHelper.getCreateDate(sourceFilePath);
        }catch (Exception exception){
            log.error(exception.getMessage());
            saveFailed(sourceFile, mediaType, FailedReasonEnum.PARSE_FILENAME_FAILED);
            return;
        }

        if(date == null || date[0] == 0){
            log.error("文件日期解析失败, file:{}", sourceFilePath);
            saveFailed(sourceFile, mediaType, FailedReasonEnum.PARSE_FILENAME_FAILED);
            return;
        }

        boolean hasProcessed = mediaService.hasProcessed(sourceFilePath, sourceFile.getName());
        if(hasProcessed){
            return;
        }

        targetDirPath = StringUtils.rtrim(targetDirPath.trim(), File.separator) + File.separator + date[0] + File.separator;

        if(date[1] != 0){
            targetDirPath = targetDirPath + File.separator + date[1];
        }

        if(date[2] != 0){
            targetDirPath = targetDirPath + File.separator + date[2];
        }

        String targetFilePath = targetDirPath + File.separator + sourceFile.getName();

        Path path = Paths.get(targetDirPath);
        Files.createDirectories(path);

        File targetFile = new File(targetFilePath);
        if(targetFile.exists()){
            boolean isSame;
            try {
                isSame = FileUtils.isSaveFile(sourceFile, targetFile);
            } catch (NoSuchAlgorithmException | IOException e) {
                log.error("check save file failed, source:{}, target:{}, exception:{}", sourceFilePath, targetFilePath, e.getMessage());
                return;
            }

            // 目标文件已存在，但是却没有成功记录，可能是从其他的 source 目录已 copy 过来的，所以这里只保存本条数据成功记录即可
            if(isSame){
                saveSuccess(sourceFile, targetFilePath, mediaType);
                log.info("duplicate target file, source:{}, target:{}", sourceFilePath, targetFilePath);
            }else{
                // 目标文件跟源文件虽然同名，但不是同一个文件，此时把目标文件改一下名字存入
                targetFilePath = targetDirPath + File.separator + getDuplicateFileName(sourceFile.getName());
            }
        }

        doCopy(sourceFile, targetFilePath, mediaType);

    }

    private void doCopy(File sourceFile, String targetFilePath, MediaTypeEnum mediaType){

        try {
            FileUtils.copyFile(sourceFile, new File(targetFilePath));
        }catch (IOException exception){
            log.error("copy file failed: exception:{}, source:{}, dest:{}",
                    exception.getMessage(),
                    sourceFile.getAbsolutePath(),
                    targetFilePath
            );
            saveFailed(sourceFile, mediaType, FailedReasonEnum.COPY_FAILED);
            return;
        }

        saveSuccess(sourceFile, targetFilePath, mediaType);

    }

    private void saveSuccess(File sourceFile, String targetFilePath, MediaTypeEnum mediaType){
        Media media = new Media();
        media.setType(mediaType);
        media.setFilename(sourceFile.getName());
        media.setSourceDirPath(sourceFile.getAbsolutePath());
        media.setSourceDirMd5(StringUtils.toMD5(sourceFile.getAbsolutePath()));
        media.setTargetFilePath(targetFilePath);
        media.setResult(1);
        mediaService.save(media);
    }

    private void saveFailed(File sourceFile, MediaTypeEnum mediaType, FailedReasonEnum failedReason){

        FailedJob failedJob = new FailedJob();
        failedJob.setType(mediaType);
        failedJob.setReason(failedReason);
        failedJob.setFilename(sourceFile.getName());
        failedJob.setSourceDirPath(sourceFile.getAbsolutePath());

        try {
            failedJobService.save(failedJob);
        }catch (DataIntegrityViolationException e){
            log.warn("failed record duplicate, file:{}", sourceFile.getAbsolutePath());
        }

    }

    private String getDuplicateFileName(String filename){
        String ext = FileUtils.getFileExt(filename);
        return StringUtils.rtrim(filename, "." + ext) + "_" + StringUtils.randomString(4) + "." + ext;
    }

}
