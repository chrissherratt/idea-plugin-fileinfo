package org.intellij.FileInfoPlugin.model;

import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class FileInfoProperties extends AbstractInfoProperties {

    public static final String FILETYPE = "FileType";

    protected boolean binary;
    protected long fileSize;
    protected long lastModified;
    protected String libraryRoot;

    public FileInfoProperties(VirtualFile vf) {
        super(vf);
    }

    public void setFileTypeIcon(Icon fileTypeIcon) {
        this.fileTypeIcon = fileTypeIcon;
    }

    public boolean isReadOnly() {
        return readonly;
    }

    public void setReadOnly(boolean readonly) {
        this.readonly = readonly;
    }

    public void setFileTypeName(String fileTypeName) {
        this.fileTypeName = fileTypeName;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setLastModified(long timeStamp) {
        this.lastModified = timeStamp;
    }

    public void setLibraryRoot(String libraryRoot) {
        this.libraryRoot = libraryRoot;
    }

    public String getLibraryRoot() {
        return libraryRoot;
    }

    public Reader getVFReader() {
        Reader reader;
        try {
            reader = new InputStreamReader(vf.getInputStream());
        } catch (IOException e) {
            reader = null;
        }
        return reader;
    }

}
