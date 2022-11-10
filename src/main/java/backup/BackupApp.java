package backup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import backup.GUI.*;
import backup.Tools.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import javax.swing.JTree;

/**
 * 备份应用 GUI 入口
 */
public class BackupApp extends JFrame implements ActionListener {

    private  JButton restoreSourceButton;
    private  JButton restoreTargetButton;
    private  JButton restoreStartButton;
    private  JLabel restoreSourceLabel;
    private  JLabel restoreTargetLabel;

    private static JPanel restorePanel ;
    public  static JPanel panPanel;
    public static JTabbedPane tabbedPane;


    private static final String RESTORE_SOURCE_PLACEHOLDER = "选择其他已备份文件";
    private static final String RESTORE_TARGET_PLACEHOLDER = "选择还原位置";
    JFileChooser fc = new JFileChooser();

    public static Backuper backuper = new Backuper();
    private final DiffChecker diffChecker = new DiffChecker();
    public static ArrayList<SavedFile> fileSavedlist =new ArrayList<>();
    public BackupApp(){
        super("备份之王");

        //Flat Light
        FlatLightLaf.install();

//Flat Dark
        //FlatDarkLaf.install();

//Flat IntelliJ
      //  FlatIntelliJLaf.install();

//Flat Darcula
 //       FlatDarculaLaf.install();
        initSetting();
        initPanel();

    }

    public static JTree panTree;

    private void initSetting() {
        try{
            FileHelper.read();
            WatchServiceDemo.main();

        }catch (Exception e){
            e.printStackTrace();
        }


    }
    private void initPanel(){
        //窗体大小
        this.setPreferredSize(new Dimension(800, 600));
        //LOGO在左上角
        int width = 45, height = 45;
        // 创建1个图标实例,注意image目录要与src同级
        ImageIcon icon = new ImageIcon("img//logo.png");
        icon.setImage(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        // 1个图片标签,显示图片
        JLabel jlImg = new JLabel(icon);
        jlImg.setSize(width, height);
        jlImg.setBounds(20, 44, width, height);
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(2);



        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        BackupPanelGUI.init();

        //============================= restore ============================

        restorePanel = new JPanel();
        restorePanel.setLayout(new GridBagLayout());

        restoreSourceLabel = fixedSizeLabel(RESTORE_SOURCE_PLACEHOLDER);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 3;
        restorePanel.add(restoreSourceLabel, c);

        restoreSourceButton = new JButton("Choose backup file");
        restoreSourceButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 4;
        c.gridwidth = 1;
        restorePanel.add(restoreSourceButton, c);

        restoreTargetLabel = fixedSizeLabel(RESTORE_TARGET_PLACEHOLDER);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        restorePanel.add(restoreTargetLabel, c);

        restoreTargetButton = new JButton("Choose target dir");
        restoreTargetButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 1;
        c.gridwidth = 1;
        restorePanel.add(restoreTargetButton, c);



        restoreStartButton = new JButton("Start restore");
        restoreStartButton.addActionListener(this);
        c.gridx = 3;
        c.gridy = 2;
        c.gridwidth = 1;
        restorePanel.add(restoreStartButton, c);

        tabbedPane.add("Restore", restorePanel);

        //============================== diff =============================
        JPanel diffPanel = new JPanel();
        diffPanel.setLayout(new GridBagLayout());





//-------------------
        panPanel=new JPanel();
        PanGUI.init();

        tabbedPane.add("网盘", panPanel);
        //----------------------

        add(tabbedPane);
        //add(backupPanel);
        pack();
        setLocationRelativeTo(null);

    }

    private JLabel fixedSizeLabel(String text) {
        JLabel label = new JLabel(text);
        Dimension dimension = new Dimension(320, 32);
        label.setMinimumSize(dimension);
        label.setMaximumSize(dimension);
        label.setPreferredSize(dimension);
        return label;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
       if (e.getSource() == restoreStartButton) { // 点击开始恢复按钮
            if (RESTORE_SOURCE_PLACEHOLDER.equals(restoreSourceLabel.getText())) {
                error("You need to select the backup file first");
                return;
            }
            if (RESTORE_TARGET_PLACEHOLDER.equals(restoreTargetLabel.getText())) {
                error("You need to select the directory to place the restored files first");
                return;
            }

            try {
             //   String password = new String(restorePassword.getPassword()).trim();
              //  backuper.restore(restoreSourceLabel.getText(), restoreTargetLabel.getText(), password);

            } catch (Exception exception) {
                exception.printStackTrace();
                error(exception.getMessage());
            }
        } else{
            if (e.getSource() == restoreTargetButton) {
                // 选择备份源文件、备份目标文件和恢复目标文件时，只允许选择目录
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setCurrentDirectory(new File("C:\\Users\\Charon\\Desktop"));
            } else if (e.getSource() == restoreSourceButton) {
                // 选择恢复源文件时，只允许选择普通文件
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }

            int result = fc.showOpenDialog(this);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }

            // 获取选择的文件路径并设置相应 label 的值
            File file = fc.getSelectedFile();
            String path = file.getAbsolutePath();
            if (e.getSource() == restoreSourceButton) {
                restoreSourceLabel.setText(path);
            } else if (e.getSource() == restoreTargetButton) {
                restoreTargetLabel.setText(path);
            }
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(lookAndFeel);
        SwingUtilities.invokeLater(() -> {
            BackupApp app = new BackupApp();
            app.setVisible(true);
        });
    }
}
