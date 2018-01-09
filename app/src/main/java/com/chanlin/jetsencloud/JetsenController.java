package com.chanlin.jetsencloud;

import android.content.Context;
import android.os.Handler;

import com.chanlin.jetsencloud.http.BookTask;
import com.chanlin.jetsencloud.http.OKHttpUtil;
import com.chanlin.jetsencloud.util.Constant;
import com.chanlin.jetsencloud.util.LogUtil;
import com.chanlin.jetsencloud.util.SystemShare;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by ChanLin on 2018/1/9.
 * jetsenCloud
 * TODO:
 */

public class JetsenController {
    private Context mContext;
    private Handler mMainThreadHandler;
    private String mRemoteAddress;


    public JetsenController(Context context,Handler handler, String urlHost){
        this.mContext = context;
        this.mMainThreadHandler = handler;
        this.mRemoteAddress = urlHost;
    }
    public JetsenController(Context context, Handler handler){
        this(context,handler, Constant.Host);
    }

    public void courceList(String bookId){
        if (null != bookId){
            OkHttpClient mOkHttpClient = OKHttpUtil.getInstanceHttpClient();
            String token =SystemShare.getSettingString(mContext,Constant.k12token);
            String code = SystemShare.getSettingString(mContext,Constant.k12code);
            //创建一个Request
            final Request request = new Request.Builder()
                    .url(Constant.Host+"?course="+bookId)
                    .addHeader(Constant.k12appKey,Constant.k12appValue)
                    .addHeader(Constant.k12avKey,Constant.k12avValue)
                    .addHeader(Constant.k12url,Constant.code_book_list)
                    .addHeader(Constant.k12code, code)
                    .addHeader(Constant.k12token, token)
                    .build();
            //new call
            Call call = mOkHttpClient.newCall(request);
            //请求加入调度
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(Request request, IOException e)
                {
                }

                @Override
                public void onResponse(final Response response) throws IOException
                {
                    String htmlStr =  response.body().string();
                    LogUtil.showError("header ",request.headers().toString());
                    LogUtil.showError("courceList",htmlStr);
                }
            });



        }
    }

}
