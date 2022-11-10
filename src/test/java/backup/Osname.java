package backup;

import org.junit.Test;

public class Osname {
    @Test
    public boolean isWindows(){
        String ss= System.getProperty("os.name");
        return ss.toLowerCase().contains("windows");
    }
    @Test
    public boolean isLinux(){
        String ss= System.getProperty("os.name");
        System.out.println(ss);
        return ss.toLowerCase().contains("linux");
    }
}
