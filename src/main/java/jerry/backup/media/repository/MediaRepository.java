package jerry.backup.media.repository;

import jerry.backup.media.data.Media;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends PagingAndSortingRepository<Media, Long> {

    Optional<Media> findFirstBySourceDirMd5AndFilename(String reverseSourceDir, String filename);

    boolean existsBySourceDirMd5AndFilename(String sourceDirMd5, String filename);

}
