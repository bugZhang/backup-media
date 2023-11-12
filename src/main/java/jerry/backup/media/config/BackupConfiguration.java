package jerry.backup.media.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@ConfigurationProperties(prefix = "backup.media")
@Configuration
@Getter
@Setter
public class BackupConfiguration {

    private String sourcePath;
    private String targetPath;

    private ArrayList<String> excludeFolderName;

    private ArrayList<String> excludeFileName;  // 排除的文件名，包含就算

    private String uncategorizedPath;   // 未解析到日期的文件夹

}
