package com.swufe.voanews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements Runnable{
    private final  String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread t = new Thread(this);//当前对象已经实现了Runnable接口
        t.start();//启动线程

    }

    @Override
    public void run() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.51voa.com/").get();
            Log.i(TAG,"run:"+doc.title());
            Elements uls = doc.getElementsByTag("ul");
            Element ul6 = uls.get(5);
            Elements lis = ul6.getElementsByTag("li");
            for(Element li : lis){
                String url1 = li.select("a").attr("abs:href");
            }
            int i = 0;
            for(int j=0;j<lis.size();j++){
                // 获取A标签的href 网址  select 获取到当前A标签 attr href 获取到地址
                Elements hrefs = lis.get(j).select("a[href]");
                int l=0;
                //这个可以获取所有的href
                for(Element href:hrefs){
                    if(l==0){
                        Log.i(TAG,"url["+l+"]"+href.attr("abs:href"));
                    }
                    if(l==hrefs.size()-1){
                        Log.i(TAG,"url["+l+"]"+href.attr("abs:href"));
                    }
                    l++;
                }
                Elements href = lis.get(j).select("a[href]");
                Element url2 = lis.get(j).getElementById("a");
                String text = lis.get(j).text();
                Log.i(TAG,"text"+text);
//                Log.i(TAG,"url1"+s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final  int bufferSize= 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"utf-8");
        for(;;){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz <0 ){
                break;
            }
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
