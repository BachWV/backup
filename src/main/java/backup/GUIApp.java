package backup;

import javax.swing.*;
import java.awt.*;

import backup.GUI.BackupPanelGUI;
import backup.GUI.PanGUI;
import backup.GUI.RestorePanelGUI;
import backup.Tools.*;
import com.formdev.flatlaf.FlatLightLaf;

import java.util.ArrayList;
import javax.swing.JTree;

/**
 * 备份应用 GUI 入口
 */
public class GUIApp extends JFrame {
    public  static JPanel panPanel;
    public static JTabbedPane tabbedPane;
    JFileChooser fc = new JFileChooser();

    public static Backuper backuper = new Backuper();
    public static ArrayList<SavedFile> fileSavedlist =new ArrayList<>();
    public GUIApp(){
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
            WatchService.registerAllFileDir();

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
        panPanel=new JPanel();
        PanGUI.init();
        tabbedPane.add("网盘", panPanel);

        add(tabbedPane);
        //add(backupPanel);
        pack();
        setLocationRelativeTo(null);

    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(lookAndFeel);
        SwingUtilities.invokeLater(() -> {
            GUIApp app = new GUIApp();
            app.setVisible(true);
        });
    }
}
