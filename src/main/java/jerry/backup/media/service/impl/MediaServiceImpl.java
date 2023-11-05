package jerry.backup.media.service.impl;

import jerry.backup.media.data.Media;
import jerry.backup.media.repository.MediaRepository;
import jerry.backup.media.service.IMediaService;
import org.springframework.stereotype.Service;

@Service
public class MediaServiceImpl implements IMediaService {

    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public void save(Media media) {

        mediaRepository.save(media);

    }
}
