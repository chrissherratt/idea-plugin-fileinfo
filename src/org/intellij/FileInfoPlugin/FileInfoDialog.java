package org.intellij.FileInfoPlugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.FileInfoPlugin.model.ButtonModelReadOnly;
import org.intellij.FileInfoPlugin.model.FileInfoProperties;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class FileInfoDialog extends AbstractResourceInfoDialog {

    protected JButton copyClassNameBtn;
    protected JButton copyFQClassNameBtn;
    protected JLabel lineCountValueLabel;


    public FileInfoDialog(Project project, FileInfoProperties model) {
        super(project, model);
    }


    protected JComponent createNorthPanel() {
        JComponent northPanel = super.createNorthPanel();
        if (model.getFileTypeName().startsWith("Java")) {
            copyClassNameBtn = createCopyButton("ClassName", "Copy Class Name to Clipboard", 'N');
            copyFQClassNameBtn = createCopyButton("FQClassName", "Copy Fully Qualified Class Name to Clipboard", 'Q');
            toolbar.add(copyClassNameBtn);
            toolbar.add(copyFQClassNameBtn);
        }
        return northPanel;
    }

    @NotNull
    protected JComponent createCenterPanel() {

        FileInfoProperties fileModel = (FileInfoProperties) model;

        JComponent centerPanel = super.createCenterPanel();

        JSeparator separator;
        int y = 0;

        JLabel libraryRootLabel = new JLabel(fileModel.getLibraryRoot());
        JLabel fileTypeNameValueLabel = new JLabel(fileModel.getFileTypeName());

        JCheckBox readonlyCheckbox = new JCheckBox("Read-only");
        readonlyCheckbox.setMargin(new Insets(0, 0, 0, 0));
        readonlyCheckbox.setModel(new ButtonModelReadOnly());
        readonlyCheckbox.setFocusable(false);
        readonlyCheckbox.setOpaque(false);
        readonlyCheckbox.setSelected(fileModel.isReadOnly());

        JLabel fileSizeValueLabel = new JLabel(MSF.format(fileModel.getFileSize()));
        lineCountValueLabel = new JLabel("Counting...");
        // Count lines of file.
        // Potentially time consuming operation so do in separate thread
        if (!fileModel.isBinary()) {
            countFileLines();
        }
        JLabel lastModifiedValueLabel = new JLabel(formatTimestamp(fileModel.getLastModified()));
        JLabel vcsStatusValueLabel = new JLabel(fileModel.getVcsStatusText());

        c = new SimpleGridBagConstraints(0, y);
        centerPanel.add(iconLabel, c);

        c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
        centerPanel.add(nameLabel, c);

        y++;
        c = new SimpleGridBagConstraints(0, y, PAD_SEPARATOR_INSETS);
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        separator = new JSeparator(JSeparator.HORIZONTAL);
        centerPanel.add(separator, c);

        y++;
        addLabel("Location:", centerPanel, 0, y, PAD_DEFAULT);

        c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        centerPanel.add(pathPanel, c);

        if (fileModel.getLibraryRoot() != null) {
            y++;
            c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
            centerPanel.add(libraryRootLabel, c);
        }

        if (model.getModuleName() != null) {
            y++;
            addLabel("Module:", centerPanel, 0, y, PAD_DEFAULT);

            c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
            centerPanel.add(moduleNameLabel, c);
        }

        if (model.getLibraryLevel() != null) {
            y++;
            addLabel("Library:", centerPanel, 0, y, PAD_DEFAULT);

            c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
            centerPanel.add(libraryNameLabel, c);
        }

        y++;
        addLabel("Type:", centerPanel, 0, y, PAD_DEFAULT);

        c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
        centerPanel.add(fileTypeNameValueLabel, c);


        Map<String, String> additionalProps = fileModel.getAdditionalProperties();
        for (String key : additionalProps.keySet()) {
            String value = additionalProps.get(key);

            y++;
            addLabel(key + ":", centerPanel, 0, y, PAD_DEFAULT);
            c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
            centerPanel.add(new JLabel(value), c);
        }


        if (fileModel.isVcsEnabled()) {
            y++;
            addLabel("VCS Status:", centerPanel, 0, y, PAD_DEFAULT);

            c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
            centerPanel.add(vcsStatusValueLabel, c);
        }

        y++;
        addLabel("Size:", centerPanel, 0, y, PAD_DEFAULT);

        c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
        centerPanel.add(fileSizeValueLabel, c);


        if (!fileModel.isBinary()) {
            y++;
            addLabel("# Lines:", centerPanel, 0, y, PAD_DEFAULT);

            c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
            centerPanel.add(lineCountValueLabel, c);
        }

        y++;
        addLabel("Last Modified:", centerPanel, 0, y, PAD_DEFAULT);

        c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
        centerPanel.add(lastModifiedValueLabel, c);

        y++;
        c = new SimpleGridBagConstraints(0, y, PAD_SEPARATOR_INSETS);
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        separator = new JSeparator(JSeparator.HORIZONTAL);
        centerPanel.add(separator, c);

        y++;
        addLabel("Attributes:", centerPanel, 0, y, PAD_DEFAULT);

        c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
        centerPanel.add(testSourceCheckbox, c);

        y++;
        c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
        centerPanel.add(readonlyCheckbox, c);


        return centerPanel;
    }

    protected void countFileLines() {
        final BufferedReader reader = new BufferedReader(((FileInfoProperties) model).getVFReader());
        Thread lineCounterThread = new Thread() {
            public void run() {
                int count = 0;
                try {
                    while (!dialogClosed && reader.readLine() != null) {
                        count++;
                        if (count % 100 == 0) {
                            updateLineCount("" + count);
                        }
                    }
                } catch (IOException e) {
                    count = -1;
                }
                updateLineCount("" + count);
            }

        };
        lineCounterThread.start();
    }


    protected void doCopyAction(ActionEvent event) {
        if (event == null) return;
        String command = event.getActionCommand();
        if ("ClassName".equals(command)) {
            ClipboardManager.copyString(model.getVF().getNameWithoutExtension());
        } else if ("FQClassName".equals(command)) {
            ClipboardManager.copyString(getFQName(model.getVF()));
        } else {
            super.doCopyAction(event);
        }
    }

    private String getFQName(VirtualFile vf) {
        if (vf == null) return null;
        VirtualFile packageVF = vf.getParent();
        if (packageVF == null) return null;
        ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        String packageName = fileIndex.getPackageNameByDirectory(packageVF);
        if (packageName != null && packageName.length() > 0) {
            return packageName + "." + vf.getNameWithoutExtension();
        } else {
            return vf.getNameWithoutExtension();
        }
    }


    private void updateLineCount(final String count) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                lineCountValueLabel.setText(count);
            }
        });
    }


}
