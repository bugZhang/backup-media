package jerry.backup.media.service;

import jerry.backup.media.data.FailedJob;
import jerry.backup.media.enums.FailedReasonEnum;

import java.util.List;

public interface IFailedJobService {
    void save(FailedJob failedJob);
    List<FailedJob> findByReason(FailedReasonEnum reason);
}
