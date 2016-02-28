package org.intellij.FileInfoPlugin.model;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

import javax.swing.*;
import java.util.Map;

public abstract class AbstractInfoProperties {

    protected VirtualFile vf;
    protected PsiElement psiElement;
    protected String fileName;
    protected String filePath;
    protected boolean testSource;
    protected String moduleName;
    protected String libraryLevel;
    protected String libraryName;
    protected boolean vcsEnabled;
    protected String vcsStatusText;
    protected String fileContext;
    protected boolean readonly;
    protected String fileTypeName;
    protected Icon fileTypeIcon;
    protected Map<String, String> additionalProperties = new ListMap<String, String>();


    public AbstractInfoProperties(VirtualFile vf) {
        this.vf = vf;
    }

    public VirtualFile getVF() {
        return vf;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public String getLibraryLevel() {
        return libraryLevel;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public void setLibraryLevel(String libraryLevel) {
        this.libraryLevel = libraryLevel;
    }

    public boolean isVcsEnabled() {
        return vcsEnabled;
    }

    public String getVcsStatusText() {
        return vcsStatusText;
    }

    public void setVcsEnabled(boolean vcsEnabled) {
        this.vcsEnabled = vcsEnabled;
    }

    public void setVcsStatusText(String vcsStatus) {
        this.vcsStatusText = vcsStatus;
    }

    public void setTestSource(boolean testSource) {
        this.testSource = testSource;
    }

    public boolean isTestSource() {
        return testSource;
    }

    public String getFileContext() {
        return fileContext;
    }

    public void setFileContext(String fileContext) {
        this.fileContext = fileContext;
    }

    public String getFileTypeName() {
        return fileTypeName;
    }

    public Icon getFileTypeIcon() {
        return fileTypeIcon;
    }

    public abstract void setReadOnly(boolean readOnly);

    public abstract boolean isReadOnly();

    public PsiElement getPsiElement() {
        return psiElement;
    }

    public void setPsiElement(PsiElement psiElement) {
        this.psiElement = psiElement;
    }

    public void setAdditionalProperty(String key, String value) {
        additionalProperties.put(key, value);
    }

    public Map<String, String> getAdditionalProperties() {
        return additionalProperties;
    }

}
