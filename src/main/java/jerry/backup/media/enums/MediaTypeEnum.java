package jerry.backup.media.enums;

import lombok.Getter;

public enum MediaTypeEnum {
    UNKNOWN(-1),
    ALL(0),
    IMAGE(1),
    VIDEO(2),
    ;
    @Getter
    private final int type;

    MediaTypeEnum(int type) {
        this.type = type;
    }

    public static MediaTypeEnum of(int type){
        for (MediaTypeEnum mt : values()){
            if(mt.getType() == type){
                return mt;
            }
        }
        return UNKNOWN;
    }
}
