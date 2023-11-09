package jerry.backup.media.service.impl;

import jerry.backup.media.data.Media;
import jerry.backup.media.repository.MediaRepository;
import jerry.backup.media.service.IMediaService;
import jerry.xtool.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MediaServiceImpl implements IMediaService {

    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public void save(Media media) {
        media.setSourceDirMd5(StringUtils.toMD5(media.getSourceDirPath()));
        mediaRepository.save(media);
    }

    @Override
    public Optional<Media> findBySourceDirAndFilename(String sourceDirPath, String filename) {

        return mediaRepository.findFirstBySourceDirMd5AndFilename(StringUtils.toMD5(sourceDirPath), filename);
    }

    @Override
    public boolean hasProcessed(String sourceDirPath, String filename) {
        return mediaRepository.existsBySourceDirMd5AndFilename(StringUtils.toMD5(sourceDirPath), filename);
    }
}
