package jerry.backup.media.repository;

import jerry.backup.media.data.Media;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends PagingAndSortingRepository<Media, Long> {

}
