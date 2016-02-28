package org.intellij.FileInfoPlugin;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import geo.marcoschmidt.ImageInfo;
import org.intellij.FileInfoPlugin.model.AbstractInfoProperties;
import org.intellij.FileInfoPlugin.model.DirInfoProperties;
import org.intellij.FileInfoPlugin.model.FileInfoProperties;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DisplayFileInfo {

//    private static final Logger LOG = Logger.getInstance("#org.intellij.FileInfoPlugin");

    private static final String OS_FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String JAVA_OUTSIDE_SOURCE_ICON = "/fileTypes/javaOutsideSource.png";

    VirtualFile vf;
    PsiElement psiElement;
    AnActionEvent event;
    FileType fileType;

    String fileContext;
    String libraryLevel;
    String libraryName;
    String libraryRoot;
    String moduleName;
    boolean readOnly;
    boolean testSource;


    public DisplayFileInfo(VirtualFile vf, AnActionEvent event) {
        this(vf, null, event);
    }

    public DisplayFileInfo(VirtualFile vf, PsiElement psiElement, AnActionEvent event) {
        this.vf = vf;
        this.event = event;
        this.psiElement = psiElement;
    }

    public void showInfo() {

        if (vf == null) return; // no current file
        if (!vf.isValid()) return;

        getGenericProperties(vf);

        Project project = event.getData(DataKeys.PROJECT);

        if (vf.isDirectory()) {
            if (isJarFile(vf)) {
                showFileInfo(true, project);
            } else {
                showDirectoryInfo();
            }
        } else {
            showFileInfo(false, project);
        }


    }

    private void getGenericProperties(VirtualFile vf) {

        Project project = event.getData(DataKeys.PROJECT);
        Module module = event.getData(DataKeys.MODULE);

        FileTypeManager typeManager = FileTypeManager.getInstance();
        fileType = typeManager.getFileTypeByFile(vf);

        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        ProjectFileIndex fileIndex = rootManager.getFileIndex();

        if (fileIndex.isIgnored(vf)) {
            fileContext = "Excluded";
        } else if (fileIndex.isInLibraryClasses(vf)) {
            fileContext = "Library class";
        } else if (fileIndex.isInLibrarySource(vf)) {
            fileContext = "Library source";
        } else if (fileIndex.isInSource(vf)) {
            fileContext = "Source";
        } else {
            fileContext = "Unknown";
        }

        // for library files, determine the library details
        if (fileIndex.isInLibraryClasses(vf) || fileIndex.isInLibrarySource(vf)) {
            List<OrderEntry> oe = fileIndex.getOrderEntriesForFile(vf);
            for (OrderEntry orderEntry : oe) {
                if (orderEntry instanceof LibraryOrderEntry) {
                    LibraryOrderEntry libOrderEntry = (LibraryOrderEntry) orderEntry;
                    libraryLevel = libOrderEntry.getLibraryLevel();
                    libraryName = libOrderEntry.getLibraryName();

                    // for library source files - what is the path to the corresponding library class?
                    if (fileIndex.isInLibrarySource(vf) && project != null) {
                        PsiFile psiFile = PsiManager.getInstance(project).findFile(vf);
                        if (psiFile instanceof PsiJavaFile) {
                            PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
                            PsiClass[] clazzes = psiJavaFile.getClasses();
                            if (clazzes.length > 0) {
                                PsiElement psiElement = clazzes[0].getOriginalElement();
                                if (psiElement != null) {
                                    PsiFile containingFile = psiElement.getContainingFile();
                                    if (containingFile != null) {
                                        VirtualFile virtualFile = containingFile.getVirtualFile();
                                        if (virtualFile != null) {
                                            VirtualFile parentVirtualFile = virtualFile.getParent();
                                            if (parentVirtualFile != null) {
                                                libraryRoot = parentVirtualFile.getPresentableUrl();
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        moduleName = (module != null) ? module.getName() : null;

        readOnly = !vf.isWritable();
        testSource = fileIndex.isInTestSourceContent(vf);

    }

    private void showFileInfo(boolean isJarfile, Project project) {

        FileType JAVA_FILE_TYPE = FileTypeManager.getInstance().getFileTypeByExtension("java");
        FileType IMAGE_FILE_TYPE = FileTypeManager.getInstance().getFileTypeByExtension("gif");

        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
        ProjectFileIndex fileIndex = rootManager.getFileIndex();

        String fileTypeName = fileType.getDescription();
        Icon fileTypeIcon = fileType.getIcon();
        if (fileTypeName.endsWith("files")) {
            fileTypeName = fileTypeName.substring(0, fileTypeName.length() - 1);
        }

        if (JAVA_FILE_TYPE.equals(fileType) && !fileIndex.isInSourceContent(vf) && !fileIndex.isInLibrarySource(vf)) {
            fileTypeName = fileTypeName + " (outside source path)";
            fileTypeIcon = new ImageIcon(Project.class.getResource(JAVA_OUTSIDE_SOURCE_ICON));
        }

        FileInfoProperties model = new FileInfoProperties(vf);
        model.setPsiElement(psiElement);
        model.setFileName(vf.getName());
        model.setFileTypeName(fileTypeName);
        model.setFileTypeIcon(fileTypeIcon);
        model.setModuleName(moduleName);
        model.setLibraryName(libraryName);
        model.setLibraryLevel(libraryLevel);
        model.setFileContext(fileContext);
        model.setReadOnly(readOnly);
        model.setTestSource(testSource);

        String filePath;
        boolean binary;
        long fileSize;

        if (isJarfile) {
            filePath = vf.getPresentableUrl();
            binary = true;
            // get the size from the physical file
            File jarFile = new File(filePath);
            fileSize = jarFile.length();
        } else {
            filePath = vf.getPath();
            binary = fileType.isBinary();
            fileSize = vf.getLength();

            //additional properties
            if (binary) {
                if (vf.getFileType().equals(IMAGE_FILE_TYPE))
                    model.setAdditionalProperty("Image Details", getImageDetails(vf));
            } else {
                String charSetName = (vf.getCharset() != null) ? vf.getCharset().displayName() : "";
                model.setAdditionalProperty("Character Set", charSetName);
            }
        }
        model.setFilePath(checkPathSeparators(filePath));
        model.setLibraryRoot(libraryRoot);
        model.setBinary(binary);
        model.setFileSize(fileSize);
        model.setLastModified(vf.getTimeStamp());

        //Version control details
        setVcsStatus(model, project);

        FileInfoDialog dialog = new FileInfoDialog(project, model);
        dialog.init();
        dialog.show();

    }

    private void setVcsStatus(AbstractInfoProperties model, Project project) {
        AbstractVcs[] abstractVcses = ProjectLevelVcsManager.getInstance(project).getAllActiveVcss();

        boolean vcsEnabled;
        String vcsStatusText;
        if (abstractVcses != null && abstractVcses.length > 0) {
            vcsEnabled = true;
            FileStatus status = FileStatusManager.getInstance(project).getStatus(vf);
            vcsStatusText = status.getText();

        } else {
            vcsEnabled = false;
            vcsStatusText = null;
        }
        model.setVcsEnabled(vcsEnabled);
        model.setVcsStatusText(vcsStatusText);
    }

    private void showDirectoryInfo() {

        Project project = event.getData(DataKeys.PROJECT);

        String dirName;
        String dirPath;

        dirName = vf.getName();
        dirPath = vf.getPath();

        // make sure all paths are separated by the o/s specific separator
        dirPath = checkPathSeparators(dirPath);

        DirInfoProperties model = new DirInfoProperties(vf);
        model.setFileName(dirName);
        model.setFilePath(dirPath);
        model.setModuleName(moduleName);
        model.setLibraryName(libraryName);
        model.setLibraryLevel(libraryLevel);
        model.setFileContext(fileContext);
        model.setTestSource(testSource);

        setVcsStatus(model, project);

        DirectoryInfoDialog dialog = new DirectoryInfoDialog(project, model);
        dialog.init();
        dialog.show();
    }

    private boolean isJarFile(VirtualFile vf) {
        String vfPath = vf.getPath();
        return (vfPath.endsWith("!/"));
    }


    private String checkPathSeparators(String path) {
        if (path == null) return null;
        if ("\\".equals(OS_FILE_SEPARATOR)) {
            // windows
            path = path.replace('/', '\\');
        } else {
            // unix
            path = path.replace('\\', '/');
        }
        return path;
    }

    protected String getImageDetails(VirtualFile vf) {
        String imageDetails = "Could not determine image properties";
        InputStream imgInputStream = null;
        try {
            imgInputStream = vf.getInputStream();
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setInput(imgInputStream);

            if (imageInfo.check()) {
                String imgFormat = imageInfo.getFormatName();
                int imgWidth = imageInfo.getWidth();
                int imgHeight = imageInfo.getHeight();
                int imgBpp = imageInfo.getBitsPerPixel();
                imageDetails = "Format: " + imgFormat + ", Size: " + imgWidth + "x" + imgHeight + ", Bits Per Pixel: " + imgBpp;
            }
        }
        catch (Exception e) {
            System.err.println("Error loading image");
            e.printStackTrace(System.err);
            //LOG.info("Error loading image", e);
        }
        finally {
            try {
                if (imgInputStream != null) imgInputStream.close();
            } catch (IOException e) {
                //ignore
            }
        }
        return imageDetails;
    }

}
