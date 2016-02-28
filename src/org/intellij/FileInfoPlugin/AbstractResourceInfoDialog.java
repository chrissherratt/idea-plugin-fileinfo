package org.intellij.FileInfoPlugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.LayeredIcon;
import org.intellij.FileInfoPlugin.model.AbstractInfoProperties;
import org.intellij.FileInfoPlugin.model.ButtonModelReadOnly;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

public abstract class AbstractResourceInfoDialog extends DialogWrapper {

    private static final int MINIMUM_WIDTH = 400;
    private static final URL READONLY_ICON_URL = AbstractResourceInfoDialog.class.getResource("/nodes/locked.png");
    private static final URL COPY_ICON_URL = AbstractResourceInfoDialog.class.getResource("/actions/copy.png");

    protected static final String OS_FILE_SEPARATOR = System.getProperty("file.separator");
    protected static final Insets PAD_DEFAULT = new Insets(2, 0, 2, 0);
    protected static final Insets PAD_LEFT_INSETS = new Insets(2, 8, 2, 2);
    protected static final Insets PAD_CHECKBOX_INSETS = new Insets(0, 8, 2, 2);
    protected static final Insets PAD_SEPARATOR_INSETS = new Insets(4, 2, 8, 2);
    protected static final String PROJECT_PATH_NAME = "$PROJECT_DIR$";
    protected static final MemorySizeFormatter MSF = new MemorySizeFormatter();
    protected static final DateFormat DATETIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);


    protected final ImageIcon readOnlyIcon;
    protected boolean dialogClosed = false;

    protected GridBagConstraints c;
    protected JPanel pathPanel;
    protected JLabel iconLabel;
    protected JLabel nameLabel;
    protected JLabel pathLabel;
    protected JLabel pathPrefixLabel;
    protected JLabel moduleNameLabel;
    protected JLabel libraryNameLabel;
    protected JCheckBox testSourceCheckbox;

    protected AbstractInfoProperties model;
    protected String fullPath = null;

    protected JToolBar toolbar;
    protected JButton copyFilenameBtn;
    protected JButton copyPathBtn;

    protected ActionListener copyButtonActionListener;
    protected Project project;


    public AbstractResourceInfoDialog(Project project, AbstractInfoProperties model) {
        super(project, false);
        this.project = project;
        this.model = model;
        if (READONLY_ICON_URL != null) {
            readOnlyIcon = new ImageIcon(READONLY_ICON_URL);
        } else {
            readOnlyIcon = null;
        }
    }


    public void init() {

        setTitle(model.getFileName());
        super.init();

        getOKAction().putValue(DEFAULT_ACTION, null);
        getOKAction().putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
        setOKActionEnabled(false);

        getCancelAction().putValue(DEFAULT_ACTION, Boolean.TRUE);
        getCancelAction().putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
        setCancelButtonText("Close");

    }


    protected ActionListener getCopyButtonActionListener() {
        if (copyButtonActionListener == null) {
            copyButtonActionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doCopyAction(e);
                }
            };
        }
        return copyButtonActionListener;
    }

    protected void setResourcePathLabels(String resourceName, String resourcePath) {
        String location;

        this.fullPath = resourcePath;

        // remove name from path
        if (resourcePath != null && resourcePath.indexOf(resourceName) > 0) {
            resourcePath = resourcePath.substring(0, resourcePath.length() - resourceName.length());
        }

        location = resourcePath;

        pathLabel.setText(location);

    }

    protected String formatTimestamp(long timestamp) {
        if (timestamp < 0) return "";
        return DATETIME_FORMAT.format(new Date(timestamp));
    }


    protected void doCopyAction(ActionEvent event) {
        if (event == null) return;
        String command = event.getActionCommand();
        if ("File".equals(command)) {
            ClipboardManager.copyString(model.getFileName());
        } else if ("Path".equals(command)) {
            ClipboardManager.copyString(model.getFilePath());
        }
    }


    protected JComponent createNorthPanel() {
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        copyFilenameBtn = createCopyButton("File", "Copy File Name to Clipboard", 'F');
        copyPathBtn = createCopyButton("Path", "Copy File Path to Clipboard", 'P');
        toolbar.add(copyFilenameBtn);
        toolbar.add(copyPathBtn);
        return toolbar;
    }


    protected JButton createCopyButton(String name, String description, char mnemonic) {
        JButton copyBtn = new JButton(name, new ImageIcon(COPY_ICON_URL));
        copyBtn.setMnemonic(mnemonic);
        copyBtn.setActionCommand(name);
        copyBtn.setToolTipText(description);
        copyBtn.addActionListener(getCopyButtonActionListener());
        return copyBtn;
    }

    @NotNull
    protected JComponent createCenterPanel() {

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        iconLabel = new JLabel();
        if (model.isReadOnly()) {
            LayeredIcon layeredIcon = new LayeredIcon(2);
            layeredIcon.setIcon(model.getFileTypeIcon(), 0);
            layeredIcon.setIcon(readOnlyIcon, 1, 0, 0);
            iconLabel.setIcon(layeredIcon);
        } else {
            iconLabel.setIcon(model.getFileTypeIcon());
        }
        String fileIdentifierStr;
        if (model.getPsiElement() != null) {
            fileIdentifierStr = model.getFileName() + " (" + displayPsiElement(model.getPsiElement()) + ")";
        } else {
            fileIdentifierStr = model.getFileName();
        }
        nameLabel = new JLabel(fileIdentifierStr);


        pathLabel = new JLabel();
        pathPrefixLabel = new JLabel();
        setResourcePathLabels(model.getFileName(), model.getFilePath());

        moduleNameLabel = new JLabel(model.getModuleName());
        libraryNameLabel = new JLabel();
        if ("module".equals(model.getLibraryLevel())) {
            libraryNameLabel.setText("module library");
        } else {
            libraryNameLabel.setText(model.getLibraryName() + " (" + model.getLibraryLevel() + " library)");
        }

        testSourceCheckbox = new JCheckBox("Test-Source");
        testSourceCheckbox.setMargin(new Insets(0, 0, 0, 0));
        testSourceCheckbox.setModel(new ButtonModelReadOnly());
        testSourceCheckbox.setFocusable(false);
        testSourceCheckbox.setOpaque(false);
        testSourceCheckbox.setSelected(model.isTestSource());

        pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pathPanel.setBackground(centerPanel.getBackground());
        pathPanel.add(pathPrefixLabel);
        pathPanel.add(pathLabel);

        return centerPanel;
    }

    private String displayPsiElement(PsiElement psiElement) {
        if (psiElement == null) {
            return null;
        }
        return PsiUtil.getName(psiElement);
    }

    protected void addLabel(String labelText, JComponent panel, int x, int y, Insets insets) {
        c = new SimpleGridBagConstraints(x, y);
        c.insets = insets;
        JLabel fieldNameLabel = new JLabel(labelText);
        panel.add(fieldNameLabel, c);
    }


    public void show() {
        pack();
        setSize(MINIMUM_WIDTH, getSize().height);
        super.show();
    }


    public void doCancelAction() {
        dialogClosed = true;
        project = null;
        super.doCancelAction();
    }


    protected class SimpleGridBagConstraints extends GridBagConstraints {

        public SimpleGridBagConstraints(int gridX, int gridY) {
            super();
            this.gridx = gridX;
            this.gridy = gridY;
            this.anchor = GridBagConstraints.NORTHWEST;
        }

        public SimpleGridBagConstraints(int gridX, int gridY, Insets insets) {
            this(gridX, gridY);
            this.insets = insets;
        }

    }


}
