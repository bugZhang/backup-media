package jerry.backup.media.helper;

import jerry.backup.media.enums.MediaTypeEnum;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;

@Component
public class SyncHelper {

    public void syncMedia(
            String sourcePath,
            String targetSource,
            ArrayList<String> excludeFolderNames,
            ArrayList<String> excludeFileNames,
            MediaTypeEnum mediaType
    ){



    }

    public ArrayList<String> findAllFolders(String sourcePath, ArrayList<String> excludeFolders){

        ArrayList<String> allFolders = new ArrayList<>();

        loadFolders(sourcePath, excludeFolders, allFolders);

        return allFolders;
    }

    private void loadFolders(String sourcePath, ArrayList<String> excludeFolders, ArrayList<String> folders){
        File folder = new File(sourcePath);

        if(!folder.exists() || !folder.isDirectory()){
            throw new RuntimeException("文件夹不存在或者不是文件夹, path:" + sourcePath);
        }

        File[] files = folder.listFiles();

        if(null == files){
            return ;
        }

        for (File file: files){
            if(file.isDirectory() && !isExcludeFolder(file.getName(), excludeFolders)){
                folders.add(file.getAbsolutePath());
                loadFolders(file.getAbsolutePath(), excludeFolders, folders);
            }
        }
    }

    private boolean isExcludeFolder(String folderName, ArrayList<String> excludeFolders){
        if(null == excludeFolders){
            return false;
        }

        for (String exclude: excludeFolders){
            if(folderName.toLowerCase().contains(exclude.toLowerCase())){
                return true;
            }
        }

        return false;
    }

}
