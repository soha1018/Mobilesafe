package com.itsoha.mobilesafe.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/2/14.
 */
public class StreamUtils {

    public static String StreamToString(InputStream inputStream)  {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int temp = -1;
        byte[] by = new byte[1024];
        try {
            while ((temp = inputStream.read(by))!=-1){
                outputStream.write(by,0,temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream.toString();
    }
}
