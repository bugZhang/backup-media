package jerry.backup.media.repository;

import jerry.backup.media.data.Media;
import jerry.backup.media.enums.SyncStatusEnum;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends PagingAndSortingRepository<Media, Long> {

    Optional<Media> findFirstBySourceDirMd5AndFilename(String sourceDirMd5, String filename);

    boolean existsBySourceDirMd5AndFilenameAndStatus(String sourceDirMd5, String filename, SyncStatusEnum status);

}
