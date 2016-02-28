package org.intellij.FileInfoPlugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;

public class FileInfoEditorPopupAction extends AnAction {

    public FileInfoEditorPopupAction() {
        super();
    }

    public FileInfoEditorPopupAction(String text) {
        super(text);
    }

    public FileInfoEditorPopupAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    public void actionPerformed(AnActionEvent event) {
        VirtualFile vf = event.getData(DataKeys.VIRTUAL_FILE);
        if (vf == null) {
            return;
        }
        DisplayFileInfo fileInfo = new DisplayFileInfo(vf, event);
        fileInfo.showInfo();
    }

    public void update(AnActionEvent event) {
        VirtualFile vf = event.getData(DataKeys.VIRTUAL_FILE);
        boolean visible = ((vf != null) && vf.isValid());
        event.getPresentation().setVisible(visible);
    }
}
