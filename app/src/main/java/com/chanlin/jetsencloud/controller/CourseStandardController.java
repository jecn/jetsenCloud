package com.chanlin.jetsencloud.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.chanlin.jetsencloud.http.HttpCallBack;
import com.chanlin.jetsencloud.http.MessageConfig;
import com.chanlin.jetsencloud.http.OKHttpUtil;
import com.chanlin.jetsencloud.util.Constant;
import com.chanlin.jetsencloud.util.SystemShare;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * Created by ChanLin on 2018/1/10.
 * jetsenCloud
 * TODO:
 */

public class CourseStandardController {
    private Context mContext;
    private Handler mMainHandler;
    private String mRemoteAddress;
    public CourseStandardController(Context context,Handler handler, String urlHost){
        this.mContext = context;
        this.mMainHandler = handler;
        this.mRemoteAddress = urlHost;
    }
    public CourseStandardController(Context context, Handler handler){
        this(context,handler, Constant.Host);
    }

    /**
     * 本地数据库获取数据
     */

    /**
     * 网络获取 资源数据
     * @param bookId
     */
    public void getCourseStandardList(int bookId){
            OkHttpClient mOkHttpClient = OKHttpUtil.getInstanceHttpClient();
            String token = SystemShare.getSettingString(mContext,Constant.k12token);
            String code = SystemShare.getSettingString(mContext,Constant.k12code);
            //创建一个Request
            Headers gd = Headers.of();

            final Request request = new Request.Builder()
                    .url(Constant.Host+"?book_id="+bookId)
                    .addHeader(Constant.k12appKey,Constant.k12appValue)
                    .addHeader(Constant.k12avKey,Constant.k12avValue)
                    .addHeader(Constant.k12url,Constant.code_course_standard_tree)
                    .addHeader(Constant.k12code, code)
                    .addHeader(Constant.k12token, token)
                    .build();
            //new call
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new HttpCallBack() {
                @Override
                public void onSuccess(String result_json) {
                    if (null != mMainHandler){
                        Message success = mMainHandler.obtainMessage(MessageConfig.course_standard_http_success_MESSAGE);
                        success.obj = result_json;
                        success.sendToTarget();
                    }
                }
                @Override
                public void onFalse(String result_json) {
                    if (null != mMainHandler){
                        Message success = mMainHandler.obtainMessage(MessageConfig.course_standard_http_false_MESSAGE);
                        success.obj = result_json;
                        success.sendToTarget();
                    }
                }
                @Override
                public void onException() {
                    if (null != mMainHandler){
                        Message success = mMainHandler.obtainMessage(MessageConfig.course_standard_http_exception_MESSAGE);
                        success.sendToTarget();
                    }
                }
            });

        }
}
