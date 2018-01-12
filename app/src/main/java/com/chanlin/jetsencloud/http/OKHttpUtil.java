package com.chanlin.jetsencloud.http;

import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by ChanLin on 2018/1/9.
 * jetsenCloud
 * TODO:
 */

public class OKHttpUtil {
    private static OkHttpClient mOkHttpClient;
    public static OkHttpClient getInstanceHttpClient(){
        if (mOkHttpClient == null){
            mOkHttpClient = new OkHttpClient();
        }
        return mOkHttpClient;
    }

    /**
     * 下载文件
     * @param fileUrl 文件url
     * @param destFileDir 存储目标目录
     */
    public <T> void downLoadFile(String fileUrl, final String destFileDir, final ReqCallBack<T> callBack) {
       // final String fileName = MD5.encode(fileUrl);
        final File file = new File(destFileDir, fileUrl);
        if (file.exists()) {
            successCallBack((T) file, callBack);
            return;
        }
        final Request request = new Request.Builder().url(fileUrl).build();
        final Call call = getInstanceHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, e.toString());
                failedCallBack("下载失败", callBack);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "current------>" + current);
                    }
                    fos.flush();
                    successCallBack((T) file, callBack);
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                    failedCallBack("下载失败", callBack);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }
}
