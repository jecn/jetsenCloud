package com.chanlin.jetsencloud.http;

import com.squareup.okhttp.OkHttpClient;

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

}
