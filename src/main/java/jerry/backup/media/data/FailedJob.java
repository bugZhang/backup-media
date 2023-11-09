package jerry.backup.media.data;

import jerry.backup.media.enums.FailedReasonEnum;
import jerry.backup.media.enums.MediaTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "failed_jobs")
public class FailedJob extends BaseModel<Long>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private MediaTypeEnum type;
    private String sourceDirPath;
    private String sourceDirMd5;
    private String targetFilePath;
    private FailedReasonEnum reason;
}
