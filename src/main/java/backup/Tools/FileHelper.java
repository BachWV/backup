package backup.Tools;

import backup.GUIApp;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;


public class FileHelper {
    public static ArrayList<CloudFile> panList=new ArrayList<>();
    public static void saved() throws FileNotFoundException {

        FileOutputStream out=new FileOutputStream(new File("./setting.ini"));
        BufferedWriter writer=null;

        String ss=JSONArray.toJSONString(GUIApp.fileSavedlist);
        System.out.println(ss);
        try{
            writer= new BufferedWriter(new OutputStreamWriter(out));
            writer.write(ss);
            if(writer!=null) writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public static void read() throws FileNotFoundException {
        FileInputStream in=new FileInputStream("./setting.ini");
        StringBuilder sb=new StringBuilder();
        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
        try{
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null){
                sb.append(line);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<SavedFile> ff=new ArrayList<>();

        JSONArray bbb = JSONArray.parseArray(sb.toString());
        System.out.println("--------------------");
        System.out.println(bbb);
        GUIApp.fileSavedlist.clear();
        for (Object o : bbb) {
            JSONObject JB = (JSONObject) o;
            String SRC = JB.getString("src");
            String TRG = JB.getString("trg");
            GUIApp.fileSavedlist.add(new SavedFile(SRC, TRG));

        }
        System.out.println(ff);

    }
}
