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
public class BackupApp extends JFrame {
    public  static JPanel panPanel;
    public static JTabbedPane tabbedPane;
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
        RestorePanelGUI.restorePanel = new JPanel();
        BackupPanelGUI.init();

        RestorePanelGUI.init();

        //============================= restore ============================

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



    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(lookAndFeel);
        SwingUtilities.invokeLater(() -> {
            BackupApp app = new BackupApp();
            app.setVisible(true);
        });
    }
}
