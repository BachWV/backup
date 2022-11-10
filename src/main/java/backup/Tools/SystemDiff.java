package backup.Tools;

public class SystemDiff {
    public static boolean isWindows(){
        String ss= System.getProperty("os.name");
        System.out.println(ss);
        return ss.toLowerCase().contains("windows");
    }
    public static boolean isLinux(){
        String ss= System.getProperty("os.name");
        System.out.println(ss);
        return ss.toLowerCase().contains("linux");
    }
}
