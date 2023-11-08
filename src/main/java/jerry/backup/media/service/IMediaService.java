package jerry.backup.media.service;

import jerry.backup.media.data.Media;

import java.util.Optional;

public interface IMediaService {
    void save(Media media);
    Optional<Media> findBySourceDirAndFilename(String sourceDirPath, String filename);

    boolean hasProcessed(String sourceDirPath, String filename);
}
