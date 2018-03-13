package com.zjm.zviewlibrary.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * <p>
 * Created by zjm on 2018/3/13.
 */
public class ImgDonwloadUtils {

    private static String sImgUrl;
    private static String sFileName;
    private static OnDownloadListener sListener;
    private static Bitmap mBitmap;
    private final static String TAG = "ImgDonwloadUtils";
    private static Context sContext;

    public static void donwloadImg(Context contexts, String imgUrl, String fileName, OnDownloadListener listener) {
        sContext = contexts;
        sImgUrl = imgUrl;
        sFileName = fileName;
        sListener = listener;
        new Thread(saveFileRunnable).start();
    }

    private static Runnable saveFileRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mBitmap = BitmapFactory.decodeStream(getImageStream(sImgUrl));
                File file = saveFile(mBitmap, sFileName);

                Message tempMsg = messageHandler.obtainMessage();
                tempMsg.obj = file.getAbsolutePath();
                messageHandler.sendMessage(tempMsg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    @SuppressLint("HandlerLeak")
    private static Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "下载完成");
            String path = (String) msg.obj;
            sListener.onSucceed(path);
        }
    };


    /**
     * Get image from newwork
     *
     * @param path The path of image
     * @return InputStream
     * @throws Exception
     */
    public static InputStream getImageStream(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }


    /**
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public static File saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = sContext.getFilesDir();
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
//        fileName = UUID.randomUUID().toString()+".jpg";
        fileName = fileName + ".jpg";
        File myCaptureFile = new File(sContext.getFilesDir() + "/" + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;
    }

    public interface OnDownloadListener {
        void onSucceed(String filePath);
    }
}
