package com.itsoha.mobilesafe.Engine;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 备份短信的引擎
 * Created by Administrator on 2017/3/31.
 */

public class SmsBackup {
    private static int intdex = 0;
    public static void backup(Context context, String path, callBack callBack){
        FileOutputStream fos = null;
        try {
            //定义文件的路径
            File file = new File(path);
            Log.i("路径", "backup: "+path);
            //检查文件有没有，没有就创建一个
            if (file.exists()){
                file.mkdirs();
            }
            //获取内容解析者，对短信的数据进行查询
            Cursor query = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);

            //获得所有的条目赋值给进度条的最大值
            /*if (query.getCount() == 0){
                return;
            }*/

            if (callBack != null){
                callBack.setMax(query.getCount());
            }
//            dialog.setMax(query.getCount());
            //文件的输出流
            fos = new FileOutputStream(file);

            //以XML文件的形式存储数据
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos,"utf-8");

            serializer.startDocument("utf-8",true);

            serializer.startTag(null,"smss");

            //遍历数据进行储存
            while (query.moveToNext()){
                serializer.startTag(null,"sms");

                serializer.startTag(null,"address");
                serializer.text(query.getString(0));
                serializer.endTag(null,"address");

                serializer.startTag(null,"date");
                serializer.text(query.getString(1));
                serializer.endTag(null,"date");

                serializer.startTag(null,"type");
                serializer.text(query.getString(2));
                serializer.endTag(null,"type");

                serializer.startTag(null,"body");
                //短信内容中可能有非法字符 需要抓住这些异常使程序正常往下执行
                try {
                    serializer.text(query.getString(3));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                serializer.endTag(null,"body");

                serializer.endTag(null,"sms");
                //每增加一条数据进度条就增加一
                if (callBack != null){
                    callBack.setProgress(intdex++);
                }
//                dialog.setProgress(intdex++);
            }

            serializer.endTag(null,"smss");
            serializer.endDocument();
            query.close();

            intdex = 0;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 定义一个回调接口，实现不同进度条的更新
     */
    public interface callBack{
        public void setMax(int max);
        public void setProgress(int current);
    }
}
