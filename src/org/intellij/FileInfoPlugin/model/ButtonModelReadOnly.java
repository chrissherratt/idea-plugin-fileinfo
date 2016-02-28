package org.intellij.FileInfoPlugin.model;

import javax.swing.*;

public class ButtonModelReadOnly extends DefaultButtonModel {

    public boolean isArmed() {
        return false;
    }

    public boolean isPressed() {
        return false;
    }

    public boolean isRollover() {
        return false;
    }

}
