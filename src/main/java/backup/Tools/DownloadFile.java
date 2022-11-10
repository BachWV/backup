package backup.Tools;

import com.alibaba.fastjson.JSONException;
import okhttp3.Call;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DownloadFile {
    public static void main(String url,String tagretDir,String filename) {
        //String url="https://cn-beijing-data.aliyundrive.net/5fb583f91e3b39a7d7a9495c9646b03662400114%2F5fb583f994427eda018d4cd99eaaaf359c805e54?di=bj29&dr=504864870&f=635f7d8942ee83237c0a48599307df6daccc3f7a&security-token=CAIS%2BgF1q6Ft5B2yfSjIr5f%2FEYLelO1s8o%2B%2BRVCJh2EROr5N2Z%2F%2B1Dz2IHFPeHJrBeAYt%2FoxmW1X5vwSlq5rR4QAXlDfNXujY1f8qVHPWZHInuDox55m4cTXNAr%2BIhr%2F29CoEIedZdjBe%2FCrRknZnytou9XTfimjWFrXWv%2Fgy%2BQQDLItUxK%2FcCBNCfpPOwJms7V6D3bKMuu3OROY6Qi5TmgQ41Uh1jgjtPzkkpfFtkGF1GeXkLFF%2B97DRbG%2FdNRpMZtFVNO44fd7bKKp0lQLukMWr%2Fwq3PIdp2ma447NWQlLnzyCMvvJ9OVDFyN0aKEnH7J%2Bq%2FzxhTPrMnpkSlacGoABRjTXEby619blJSkO0SvBuRLCCoFxvjKs2G4SVoXX4U8O90aLsMW2h7%2Bsj48X8nnm3Zlkj2LGvsxy%2B%2B1Ui9tHv8qole%2FPAPLdO7JwN4da71tpmfynx%2BUMZXECHRAM4A1Xen2JZa2hZfNqouq6Mry7Stoco1W0AKYfjDxWFQzQ5JU%3D&u=fe41fb4999f3492abd07f436e0ec49bd&x-oss-access-key-id=STS.NTJZ8jy2MEMUGv8caD61a6ZU6&x-oss-expires=1667238961&x-oss-signature=hHB6m3uhLUzVlTvsci2X46NMrYOW5jOvqUX84FFF%2FLQ%3D&x-oss-signature-version=OSS2";
        OkHttpUtils.get(url,new OkHttpCallback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                byte []bytes=response.body().bytes();

                //切割出图片名称==》PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png


                Path filePath = Paths.get(tagretDir+"\\" +filename);

                    //不存在文件 => 创建
                    Files.write(filePath, bytes, StandardOpenOption.CREATE);
                  //super.onFinish("1","1");
               // System.out.println(sss);
               // super.onResponse(call, response);
            }
        });

    }
}
