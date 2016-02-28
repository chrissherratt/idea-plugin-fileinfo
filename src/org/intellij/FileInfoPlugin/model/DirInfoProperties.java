package org.intellij.FileInfoPlugin.model;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;

public class DirInfoProperties extends AbstractInfoProperties {

    private static final Icon DIRECTORY_ICON = UIManager.getIcon("FileView.directoryIcon");
    private static final String DIRECTORY_NAME = "Directory";

    public DirInfoProperties(VirtualFile vf) {
        super(vf);
    }

    public Icon getFileTypeIcon() {
        return DIRECTORY_ICON;
    }

    public String getFileTypeName() {
        return DIRECTORY_NAME;
    }

    public void setReadOnly(boolean readOnly) {
    }

    public boolean isReadOnly() {
        return false;
    }

}
