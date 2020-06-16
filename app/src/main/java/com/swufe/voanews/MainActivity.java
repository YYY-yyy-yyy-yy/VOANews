package com.swufe.voanews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private final  String TAG = "MainActivity";
    Handler handler;
    private List<HashMap<String,String>> listItem; //存放文字、图片信息
    private SimpleAdapter listItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();

        Thread t = new Thread(this);//当前对象已经实现了Runnable接口
        t.start();//启动线程

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 7){
                    listItem = (List<HashMap<String,String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MainActivity.this,listItem,//listIteam数据源
                            R.layout.list_item,//ListTtem的xml布局实现
                            new String[] {"newsTitle","category"},
                            new int[]{R.id.newsTitle,R.id.category}
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);

            }
        };

        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);

    }

    private void initListView(){
            listItem = new ArrayList<HashMap<String, String>>();
            for(int i =0;i<10;i++){
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("newsTitle",""+i);//标题文字
                map.put("category",""+i);//标题描述
                listItem.add(map);
            }
            //生成适配器的Item和动态数组对应元素
            listItemAdapter = new SimpleAdapter(this,listItem,//listIteam数据源
                    R.layout.list_item,//ListTtem的xml布局实现
                    new String[] {"newsTitle","category"},
                    new int[]{R.id.newsTitle,R.id.category}
            );
        }


    @Override
    public void run() {
        List<HashMap<String,String>> showList = new ArrayList<HashMap<String, String>>();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.51voa.com/").get();
            Log.i(TAG,"run:"+doc.title());
            Elements uls = doc.getElementsByTag("ul");
            Element ul6 = uls.get(5);
            Elements lis = ul6.getElementsByTag("li");

            List<NewsItem> newsList = new ArrayList<NewsItem>();
            for(int j=0;j<lis.size();j++){
                String category = "";
                String cate_url = "";
                String newsTitle ="";
                String news_url = "";

                // 获取A标签的href 网址  select 获取到当前A标签 attr href 获取到地址
                Elements hrefs = lis.get(j).select("a[href]");
                //这个可以获取想要的href
                int l=0;
                for(Element href:hrefs){
                    if(l==0){
                        cate_url = href.attr("abs:href");
                        Log.i(TAG,"cate_url["+l+"]"+cate_url);
                        String cate = href.attr("href").toString();
                        category = cate.substring(1,cate.length()-7);
                        Log.i(TAG,"category["+l+"]"+category);
                    }
                    if(l==hrefs.size()-1){
                        news_url = href.attr("abs:href");
                        Log.i(TAG,"news_url["+l+"]"+news_url);
                        newsTitle = href.text();
                        Log.i(TAG,"newsTitle["+l+"]"+newsTitle);
                    }
                    l++;

                }
                newsList.add(new NewsItem(category,cate_url,newsTitle,news_url));

            }
            //把数据写入到数据库

            NewsManager manager = new NewsManager(this);
            manager.deleteAll();
            manager.addAll(newsList);
            Log.i("run","数据已写入数据库中");
            for(NewsItem item:manager.listAll()){
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("newsTitle",item.getNewsTitle());
                map.put("category",item.getCategory());
                showList.add(map);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Message msg  = handler.obtainMessage(7);
        msg.obj = showList;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> map = (HashMap<String, String>)getListView().getItemAtPosition(position);
        String titleStr = map.get("newsTitle");
        NewsManager manager = new NewsManager(this);
        String url = manager.queryNews_urlByNewsTitle(titleStr);
        String category = manager.queryCategoryByNewsTitle(titleStr);
//        Intent news_url = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        startActivity(news_url);

//        TextView title = view.findViewById(R.id.newsTitle);
//        Log.i(TAG, String.valueOf(title));
//        String categoryStr = map.get("category");
//        Log.i(TAG,titleStr+"-->"+categoryStr);

        //打开新的页面传入参数
        Intent news = new Intent(this,NewsActivity.class);
        news.putExtra("news_url",url);
        news.putExtra("news_Title",titleStr);
        news.putExtra("category",category);
        startActivity(news);
//        startActivityForResult(news,2);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        HashMap<String,String> map = (HashMap<String, String>)getListView().getItemAtPosition(position);
        String categoryStr = map.get("category");
        NewsManager manager = new NewsManager(this);
        final String url = manager.queryCate_urlByCategory(categoryStr);
        AlertDialog.Builder dialog = new AlertDialog.Builder (this);
        dialog.setTitle ("温馨提示").setMessage ("你确定要离开该页面前往网页查看【"+categoryStr+"】分类的新闻吗？");
        //点击确定就退出程序
        dialog.setPositiveButton ("确定", new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent cate_url = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(cate_url);
            }
        });
        //如果取消，就什么都不做，关闭对话框
        dialog.setNegativeButton ("取消",null);
        dialog.show ();
        return true;//true说明屏蔽短按事件
    }

}
