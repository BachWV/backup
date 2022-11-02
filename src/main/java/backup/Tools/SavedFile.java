package backup.Tools;

public class SavedFile{
    public String getSrc() {
        return src;
    }

    public String getTrg() {
        return trg;
    }

    public String src;
    public String trg;

    public SavedFile(String text, String text1) {
        src=text;
        trg=text1;
    }

}
