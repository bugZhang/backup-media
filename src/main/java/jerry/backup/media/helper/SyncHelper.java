package jerry.backup.media.helper;

import jerry.backup.media.data.Media;
import jerry.backup.media.enums.MediaTypeEnum;
import jerry.backup.media.enums.SyncStatusEnum;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class SyncHelper {

    public volatile static AtomicInteger syncFolderLock = new AtomicInteger(2);

    private final Object syncLock = new Object();

    private final IMediaService mediaService;
    private final MediaInfoHelper mediaInfoHelper;
    private final ToolExecutor executor;

    public SyncHelper(
            IMediaService mediaService,
            MediaInfoHelper mediaInfoHelper,
            ToolExecutor executor
    ) {
        this.mediaService = mediaService;
        this.mediaInfoHelper = mediaInfoHelper;
        this.executor = executor;
    }

    public void start(
            String sourcePath,
            String targetSource,
            String uncategorizedPath,   // 解析失败时存放的文件夹
            ArrayList<String> excludeFolders,
            ArrayList<String> excludeFileNames,
            MediaTypeEnum mediaType
    ) {

        // todo 三个路径做检测 是否有空值

        Instant startAt = Instant.now();
        log.info("start task:{}, at:{}", sourcePath, startAt.toString());

        ArrayList<String> allFolders = findAllFolders(sourcePath, excludeFolders);
        allFolders.add(sourcePath);

        while (!allFolders.isEmpty()){

            if(syncFolderLock.intValue() == 0){
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            int size = allFolders.size();
            String tPath = allFolders.get(size - 1);
            allFolders.remove(size - 1);
            executor.execute(() -> this.syncDir(tPath, targetSource, uncategorizedPath, excludeFileNames, mediaType));
            syncFolderLock.getAndDecrement();

        }


    }

    private void syncDir(
            String sourcePath,
            String targetSource,
            String uncategorizedPath,
            ArrayList<String> excludeFileNames,
            MediaTypeEnum mediaType
    ) {

        Instant startAt = Instant.now();
        log.info("start sync directory, path:{}, at:{}", sourcePath, startAt.toString());

        ArrayList<String> allMedia = findMedias(sourcePath, excludeFileNames, mediaType);

        for (String mediaFilePath: allMedia){
            try {
                sync(mediaFilePath, targetSource, uncategorizedPath);
            } catch (IOException e) {
                log.error("sync file failed, exception:{}, mediaFilePath:{}, targetSource:{}, uncategorizedPath:{}",
                        e.getMessage(), mediaFilePath, targetSource, uncategorizedPath);
            }
        }

        int lock = syncFolderLock.getAndIncrement();
        String threadName = Thread.currentThread().getName();
        log.info("end sync directory, use:{} seconds, lock:{}, thread:{}, path:{}", Instant.now().getEpochSecond() - startAt.getEpochSecond(), lock, threadName, sourcePath);
    }

    private void sync(String sourceFilePath, String targetDirPath, String uncategorizedPath) throws IOException {
        File sourceFile = new File(sourceFilePath);
        int[] date = null;
        try {
            date = mediaInfoHelper.getCreateDate(sourceFilePath);
        }catch (Exception exception){
            log.error(exception.getMessage());
        }
        String targetFilePath;
        SyncStatusEnum status = SyncStatusEnum.SUCCESS;

        if(date == null || date[0] == 0){
            log.error("文件日期解析失败, file:{}", sourceFilePath);
            targetDirPath = getUncategorizedParentPath(sourceFile, uncategorizedPath);
            targetFilePath = targetDirPath + File.separator + sourceFile.getName();
            status = SyncStatusEnum.FAILED_PARSE_FILENAME;
        }else {
            targetDirPath = StringUtils.rtrim(targetDirPath.trim(), File.separator) + File.separator + date[0] + File.separator;

            if(date[1] != 0){
                String month = date[1] < 10 ? "0" + date[1] : String.valueOf(date[1]);
                targetDirPath = targetDirPath + File.separator + month;
            }

            // 不需要精确到日期
//        if(date[2] != 0){
//            targetDirPath = targetDirPath + File.separator + date[2];
//        }

            targetFilePath = targetDirPath + File.separator + sourceFile.getName();
        }


        Path path = Paths.get(targetDirPath);
        Files.createDirectories(path);

        File targetFile = new File(targetFilePath);

        Media media = new Media();
        media.setSourceDirPath(sourceFilePath);
        media.setFilename(sourceFile.getName());
        media.setTargetFilePath(targetFilePath);
        media.setStatus(status);

        if (targetFile.exists()){

            // 防止相同名字的不同文件，所以这里先检查一下数据库是否有记录
            // 如果没有记录了再去对比文件 hash
            Optional<Media> mediaOpt = mediaService.findBySourceDirAndFilename(sourceFilePath, sourceFile.getName());
            if(mediaOpt.isPresent()){
                media = mediaOpt.get();
                if(mediaOpt.get().getStatus() == SyncStatusEnum.SUCCESS || mediaOpt.get().getStatus() == SyncStatusEnum.FAILED_PARSE_FILENAME){
                    return;
                }
            }else {
                boolean isSame;
                try {
                    isSame = FileUtils.isSaveFile(sourceFile, targetFile);
                } catch (NoSuchAlgorithmException | IOException e) {
                    log.error("check save file failed, source:{}, target:{}, exception:{}", sourceFilePath, targetFilePath, e.getMessage());
                    return;
                }
                if(isSame){
                    // 目标文件已存在，但是却没有成功记录，可能是从其他的 source 目录已 copy 过来的，所以这里只保存本条数据成功记录即可
                    save(media);
                    log.info("duplicate target file, source:{}, target:{}", sourceFilePath, targetFilePath);
                    return;
                }else {
                    // 目标文件跟源文件虽然同名，但不是同一个文件，此时把目标文件改一下名字存入
                    targetFilePath = targetDirPath + File.separator + getDuplicateFileName(sourceFile);
                    targetFile = new File(targetFilePath);
                }
            }
        }


        try {
            FileUtils.copyFile(sourceFile, new File(targetFilePath));
        }catch (IOException exception){
            log.error("copy file failed: exception:{}, source:{}, dest:{}",
                    exception.getMessage(),
                    sourceFile.getAbsolutePath(),
                    targetFilePath
            );
            status = SyncStatusEnum.FAILED_COPY_FAILED;
        }

        media.setTargetFilePath(targetFilePath);
        media.setStatus(status);

        save(media);
    }

    private void save(Media media){

        String sourceFilePath = media.getSourceDirPath();
        if(StringUtils.isEmpty(sourceFilePath)){
            return;
        }

        media.setType(getMediaTypeByFilePath(sourceFilePath));
        media.setSourceDirMd5(StringUtils.toMD5(sourceFilePath));

        try {
            mediaService.save(media);
        }catch (DataIntegrityViolationException e){
            log.warn("failed record duplicate, status:{}, file:{}", media.getStatus(), sourceFilePath);
        }
    }

    private void saveFailedRecord(String sourceFilePath, String filename, String targetPath, SyncStatusEnum status){

        Media media = new Media();
        media.setFilename(filename);
        media.setSourceDirPath(sourceFilePath);
        media.setTargetFilePath(targetPath);
        media.setStatus(status);

        save(media);
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

                if(isExcludeFile(filename, excludeFileNames)){
                    continue;
                }

                if(checkType){
                    medias.add(file.getAbsolutePath());
                }else {
                    log.warn("found not media file: {}", file.getAbsolutePath());
                }
            }
        }

        return medias;
    }


    private ArrayList<String> findAllFolders(String sourcePath, ArrayList<String> excludeFolders){

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
            if(file.isDirectory() && !file.isHidden() && !isExcludeFolder(file.getName(), excludeFolders)){
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
            if(folderName.contains(exclude)){
                return true;
            }
//            if(folderName.toLowerCase().contains(exclude.toLowerCase())){
//                return true;
//            }
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

    private MediaTypeEnum getMediaTypeByFilePath(String filePath){
        MediaTypeEnum mediaType = MediaTypeEnum.UNKNOWN;

        if(FileUtils.isImage(filePath)){
            mediaType = MediaTypeEnum.IMAGE;
        } else if (FileUtils.isVideo(filePath)) {
            mediaType = MediaTypeEnum.VIDEO;
        }

        return mediaType;
    }

    private String getDuplicateFileName(File sourceFile){
        String filename = sourceFile.getName();
        String ext = FileUtils.getFileExt(filename);
        return StringUtils.rtrim(filename, "." + ext) + "_" + StringUtils.randomString(4) + "." + ext;
    }

    private String getUncategorizedParentPath(File sourceFile, String uncategorizedPath){

        File father = sourceFile.getParentFile();
        File grandFather = null;
        File grandGrandFather = null;
        if(null != father){
            grandFather = father.getParentFile();
            if(null != grandFather){
                grandGrandFather = grandFather.getParentFile();
            }
        }

        uncategorizedPath = StringUtils.rtrim(uncategorizedPath, File.separator);

        if(null != grandGrandFather){
            uncategorizedPath = uncategorizedPath + File.separator + grandGrandFather.getName();
        }

        if(null != grandFather){
            uncategorizedPath = uncategorizedPath + File.separator + grandFather.getName();
        }

        if(null != father){
            uncategorizedPath = uncategorizedPath + File.separator + father.getName();
        }

        return uncategorizedPath;
    }

}
