package jerry.backup.media.repository;

import jerry.backup.media.data.FailedJob;
import jerry.backup.media.enums.FailedReasonEnum;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface FailedJobRepository extends PagingAndSortingRepository<FailedJob, Long> {

    List<FailedJob> findFirst10ByReason(FailedReasonEnum reason);

}
