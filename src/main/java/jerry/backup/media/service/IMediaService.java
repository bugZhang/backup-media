package jerry.backup.media.service;

import jerry.backup.media.data.Media;
import jerry.backup.media.enums.SyncStatusEnum;

import java.util.Optional;

public interface IMediaService {
    void save(Media media);
    Optional<Media> findBySourceDirAndFilename(String sourceDirPath, String filename);

    boolean existsWithStatus(String sourceDirPath, String filename, SyncStatusEnum status);
}
