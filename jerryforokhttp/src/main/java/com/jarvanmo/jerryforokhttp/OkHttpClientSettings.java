package com.jarvanmo.jerryforokhttp;

import android.content.Context;

import java.util.HashMap;
import java.util.IdentityHashMap;

import okhttp3.RequestBody;

/**
 * Created by 46812 on 2015/9/20.
 *
 */
public class OkHttpClientSettings {
    public Context context;
    public String url;
    public RequestParams params;

    public String destPath;
    public String fileName;


    public Object tag;
    public JerryCallBack callBack;
    public long connectionTimeout = 10000;
    public long readTimeout = 10000;
    public long writeTimeout = 1000;
    public MethodType type;
    public boolean isOpenProgressDialog = true;
    public RequestBody requestBody;
    public HashMap<String,String> header;
    public IdentityHashMap<String,String> addHeader;
}
