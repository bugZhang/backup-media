package jerry.backup.media.enums;

import lombok.Getter;

public enum FailedReasonEnum {
    UNKNOWN(0),
    REPEAT(1),
    ;

    @Getter
    private final int reason;

    FailedReasonEnum(int reason) {
        this.reason = reason;
    }

    public static FailedReasonEnum of(int reason){
        for (FailedReasonEnum r : values()){
            if(r.getReason() == reason){
                return r;
            }
        }
        return UNKNOWN;
    }
}
