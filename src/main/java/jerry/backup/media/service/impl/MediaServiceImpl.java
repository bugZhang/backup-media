package jerry.backup.media.service.impl;

import jerry.backup.media.data.Media;
import jerry.backup.media.repository.MediaRepository;
import jerry.backup.media.service.IMediaService;
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
        media.setReverseSourceDir(new StringBuilder(media.getSourceDirPath()).reverse().toString());
        mediaRepository.save(media);
    }

    @Override
    public Optional<Media> findBySourceDirAndFilename(String sourceDirPath, String filename) {

        String reverseSourceDir = new StringBuilder(sourceDirPath).reverse().toString();

        return mediaRepository.findFirstByReverseSourceDirAndFilename(reverseSourceDir, filename);
    }

    @Override
    public boolean hasProcessed(String sourceDirPath, String filename) {
        String reverseSourceDir = new StringBuilder(sourceDirPath).reverse().toString();
        return mediaRepository.existsByReverseSourceDirAndFilename(reverseSourceDir, filename);
    }
}
