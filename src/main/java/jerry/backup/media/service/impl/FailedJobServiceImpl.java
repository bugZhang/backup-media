package jerry.backup.media.service.impl;

import jerry.backup.media.data.FailedJob;
import jerry.backup.media.enums.FailedReasonEnum;
import jerry.backup.media.repository.FailedJobRepository;
import jerry.backup.media.service.IFailedJobService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FailedJobServiceImpl implements IFailedJobService {

    private final FailedJobRepository failedJobRepository;

    public FailedJobServiceImpl(FailedJobRepository failedJobRepository) {
        this.failedJobRepository = failedJobRepository;
    }

    @Override
    public void save(FailedJob failedJob) {

        failedJob.setReverseSourceDir(new StringBuilder(failedJob.getSourceDirPath()).reverse().toString());

        failedJobRepository.save(failedJob);
    }

    @Override
    public List<FailedJob> findByReason(FailedReasonEnum reason) {
        return failedJobRepository.findFirst10ByReason(reason);
    }
}
