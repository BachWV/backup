package backup.Tools;

import backup.BackupApp;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileHelper {
    public static ArrayList<CloudFile> panList=new ArrayList<>();
    public static void saved() throws FileNotFoundException {

        FileOutputStream out=new FileOutputStream(new File("./setting.ini"));
        BufferedWriter writer=null;

        String ss=JSONArray.toJSONString(BackupApp.fileSavedlist);
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
        BackupApp.fileSavedlist.clear();
        for(int i=0;i<bbb.size();i++){
            JSONObject JB=  (JSONObject) bbb.get(i);
            String SRC=JB.getString("src");
            String TRG=JB.getString("trg");
            BackupApp.fileSavedlist.add(new SavedFile(SRC,TRG));

        }
        System.out.println(ff);

    }
}
