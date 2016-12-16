package eu.rd9.webencode.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by renne on 16.12.2016.
 */
public class FolderNode {

    public File path;
    public boolean hasChildren;

    public Map<String, FolderNode> children = new HashMap<>();
}
