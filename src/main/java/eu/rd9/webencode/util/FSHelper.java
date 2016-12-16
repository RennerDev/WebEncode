package eu.rd9.webencode.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by renne on 16.12.2016.
 */
public class FSHelper {

    private static Map<String, FolderNode> folderNodeMap = new HashMap<>();

    public static void loadFolderTree() {
        folderNodeMap.clear();

        new Thread(() -> {
            folderNodeMap.clear();

            Map<String, FolderNode> folderNodeMap = new HashMap<>();
            FSHelper fsHelper = new FSHelper();
            fsHelper.collecDirs(File.listRoots(), folderNodeMap);
            FSHelper.folderNodeMap.putAll(folderNodeMap);

        }).start();

    }

    private void collecDirs(File[] files, Map<String, FolderNode> folderNodeMap) {
        for (File f : files) {
            FolderNode folderNode = new FolderNode();
            folderNode.path = f;
            String[] subPaths = folderNode.path.list((current, name) -> new File(current, name).isDirectory());
            if (subPaths == null || subPaths.length == 0) {
                folderNode.hasChildren = false;
                folderNodeMap.put(f.getPath(), folderNode);
                continue;
            }

            folderNode.hasChildren = true;

            List<File> subPathes = new ArrayList<>();
            for (String subPath : subPaths) {
                File f2 = new File(f + "/" + subPath);
                if (!f2.isDirectory() || !f2.canRead())
                    continue;
                subPathes.add(f);
            }

            collecDirs(subPathes.toArray(new File[subPathes.size()]), folderNodeMap);

        }
    }

}
