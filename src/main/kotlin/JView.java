import javax.swing.*;

/* We had to use Java here because Kotlin in not supported by IntelliJ layout builder */
public class JView extends JFrame {
    protected JButton jRefreshVpnConnButton;
    protected JComboBox<String> jVpnConnections;
    protected JTextField jTargetDir;
    protected JButton jTargetDirChangeButton;
    protected JList<String> jFilesList;
    protected JButton jFilesListChangeButton;
    protected JButton jRemoveFileButton;
    protected JButton jRemoveAllFilesButton;
    protected JButton jRunPauseButton;
    protected JCheckBox jOverwriteFilesCheckBox;
    protected JTextField jFilesField;
    protected JTextField jSizeField;
    protected JTextField jSpeed;
    protected JTextField jSpeedAvg;
    protected JTextArea jLog;
    protected JProgressBar jProgressBar;
    protected JButton jStopButton;
    protected JLabel jStatusLabel;
    protected JPanel jMainPanel;

    public JView() {
        super();
    }

    public JView(String title) {
        super(title);
    }
}


