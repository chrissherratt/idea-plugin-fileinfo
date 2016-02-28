package org.intellij.FileInfoPlugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

import javax.swing.*;

public class FileInfoElementEditorPopupAction extends AnAction {

    public FileInfoElementEditorPopupAction() {
        super();
    }

    public FileInfoElementEditorPopupAction(String text) {
        super(text);
    }

    public FileInfoElementEditorPopupAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    public void actionPerformed(AnActionEvent event) {
        VirtualFile vf = null;
        PsiElement navElement = null;
        PsiElement element = event.getData(DataKeys.PSI_ELEMENT);
        if (element instanceof PsiVariable) { //includes parameters
            PsiVariable variable = (PsiVariable) element;
            PsiType type = variable.getType();
            if (type instanceof PsiClassType) {
                PsiClassType classType = (PsiClassType) type;
                PsiClass psiClass = classType.resolve();
                if (psiClass != null) {
                    element = psiClass;
                }
            }
        }

        if (element != null) {
            navElement = element.getNavigationElement();
            if (navElement != null) {
                PsiFile psiFile = navElement.getContainingFile();
                if (psiFile != null) {
                    vf = psiFile.getVirtualFile();
                }
            }
        }
        if (vf == null) {
            vf = event.getData(DataKeys.VIRTUAL_FILE);
        }
        if (vf == null) {
            return;
        }
        DisplayFileInfo fileInfo = new DisplayFileInfo(vf, navElement, event);
        fileInfo.showInfo();
    }

    public void update(AnActionEvent event) {
        PsiElement element = event.getData(DataKeys.PSI_ELEMENT);
        boolean visible = ((element != null) && element.isValid());
        event.getPresentation().setVisible(visible);
    }

}