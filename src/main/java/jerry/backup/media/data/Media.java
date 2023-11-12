package jerry.backup.media.data;

import jerry.backup.media.enums.MediaTypeEnum;
import jerry.backup.media.enums.SyncStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "media")
public class Media extends BaseModel<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private MediaTypeEnum type;
    private String sourceDirPath;
    private String sourceDirMd5;
    private String targetFilePath;
    private SyncStatusEnum status;
}
