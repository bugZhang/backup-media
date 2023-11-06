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

    private ArrayList<String> excludeFileName;

}
