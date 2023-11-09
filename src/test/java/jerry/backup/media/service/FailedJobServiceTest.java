package jerry.backup.media.service;

import jerry.backup.media.data.FailedJob;
import jerry.backup.media.enums.FailedReasonEnum;
import jerry.backup.media.enums.MediaTypeEnum;
import jerry.xtool.utils.StringUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles(value = "dev")
public class FailedJobServiceTest {

    @Autowired
    private IFailedJobService failedJobService;

    @RepeatedTest(2)
    public void save(){
        FailedJob failedJob = new FailedJob();

        failedJob.setReason(FailedReasonEnum.REPEAT);
        failedJob.setFilename("test-name-1");
        failedJob.setType(MediaTypeEnum.IMAGE);
        failedJob.setSourceDirPath("test_path_1");
        failedJob.setTargetFilePath("/Users/jerry/Documents/java-workspace/holla/monkey/target");

        try {
            failedJobService.save(failedJob);
        }catch (DataIntegrityViolationException e){
            System.out.println("主键重复:" + e.getMessage());
        }


    }

    @Test
    public void findByReason(){
        List<FailedJob> list = failedJobService.findByReason(FailedReasonEnum.REPEAT);
        System.out.println("===============");
        System.out.println("size:" + list.size());
    }



}
