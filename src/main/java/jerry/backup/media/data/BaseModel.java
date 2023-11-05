package jerry.backup.media.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseModel<ID> {

    @Id
    protected ID id;

    protected Instant createdAt;

    protected Instant updatedAt;

}