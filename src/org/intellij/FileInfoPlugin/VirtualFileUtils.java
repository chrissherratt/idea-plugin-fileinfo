package org.intellij.FileInfoPlugin;

import com.intellij.openapi.vfs.VirtualFile;

public class VirtualFileUtils {

    public static boolean isInPath(VirtualFile vf, VirtualFile vfPath) {
        if (vfPath == null) return false;
        if (vf == null) return false;

        String vfFileSystemPath = vf.getPath();
        String vfPathFileSystemPath = vfPath.getPath();
        if (vfFileSystemPath == null) return false;
        if (vfPathFileSystemPath == null) return false;

        if (vfFileSystemPath.equals(vfPathFileSystemPath)) return true;

        // otherwise, check the parent
        return isInPath(vf.getParent(), vfPath);

    }

    public static boolean isInPath(VirtualFile vf, VirtualFile[] vfPathArray) {
        if (vfPathArray == null) return false;
        boolean isInPath = false;
        for (int i = 0; i < vfPathArray.length; i++) {
            VirtualFile excludedRoot = vfPathArray[i];
            if (VirtualFileUtils.isInPath(vf, excludedRoot)) {
                isInPath = true;
                break;
            }
        }
        return isInPath;
    }

}