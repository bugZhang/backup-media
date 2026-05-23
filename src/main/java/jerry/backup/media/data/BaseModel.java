package jerry.backup.media.data;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseModel<ID> {

    protected ID id;

    protected Instant createdAt;

    protected Instant updatedAt;

}