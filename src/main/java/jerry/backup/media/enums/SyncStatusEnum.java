package jerry.backup.media.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SyncStatusEnum {

    UNKNOWN(0),
    SUCCESS(1),
    FAILED_PARSE_FILENAME(2),
    FAILED_REPEATED(3),
    FAILED_COPY_FAILED(4),
    ;

    @Getter
    private final int status;

    public static SyncStatusEnum of(int st){
        for (SyncStatusEnum statusEnum: values()){
            if(st == statusEnum.getStatus()){
                return statusEnum;
            }
        }
        return UNKNOWN;
    }

}
