package jerry.backup.media.data;

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
    private Integer type;
    private String sourcePath;
    private String targetPath;
    private Integer result;


}
