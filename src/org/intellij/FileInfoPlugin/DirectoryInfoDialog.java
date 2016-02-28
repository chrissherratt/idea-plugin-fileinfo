package org.intellij.FileInfoPlugin;

import com.intellij.openapi.project.Project;
import org.intellij.FileInfoPlugin.model.DirInfoProperties;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class DirectoryInfoDialog extends AbstractResourceInfoDialog {

    public DirectoryInfoDialog(Project project, DirInfoProperties model) {
        super(project, model);
    }

    @NotNull
    protected JComponent createCenterPanel() {

        JLabel vcsStatusValueLabel = new JLabel(model.getVcsStatusText());

        JComponent centerPanel = super.createCenterPanel();

        JSeparator separator;
        int y = 0;

        c = new SimpleGridBagConstraints(0, 0);
        centerPanel.add(iconLabel, c);

        c = new SimpleGridBagConstraints(1, 0, PAD_LEFT_INSETS);
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

        if (model.isVcsEnabled()) {
            y++;
            addLabel("VCS Status:", centerPanel, 0, y, PAD_DEFAULT);

            c = new SimpleGridBagConstraints(1, y, PAD_LEFT_INSETS);
            centerPanel.add(vcsStatusValueLabel, c);
        }

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


        return centerPanel;
    }


}
