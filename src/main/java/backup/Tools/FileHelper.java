package backup.Tools;

import backup.BackupApp;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;


public class FileHelper {
    public static ArrayList<CloudFile> panList=new ArrayList<>();
    public static void saved() throws FileNotFoundException {
        FileOutputStream out=new FileOutputStream(new File("d://helloprint.txt"));
        BufferedWriter writer=null;
        JSONObject js=new JSONObject();
        JSONArray spArray= new JSONArray();
        try {
            js.put("pid","profile_real_001");
            js.put("type","real");

            for(int i = 0; i< BackupApp.fileSavedlist.size(); i++){
                JSONObject tmp=new JSONObject();

                tmp.put("src",BackupApp.fileSavedlist.get(i).src);
                tmp.put("trg",BackupApp.fileSavedlist.get(i).trg);
              spArray.set(i,tmp);


            }

            System.out.println(js.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try{
            writer= new BufferedWriter(new OutputStreamWriter(out));
            writer.write(js.toString());
            if(writer!=null) writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
