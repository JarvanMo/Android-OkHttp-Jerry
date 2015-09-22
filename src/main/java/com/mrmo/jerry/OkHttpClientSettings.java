package com.mrmo.jerry;

import android.content.Context;

/**
 * Created by 46812 on 2015/9/20.
 *
 */
public class OkHttpClientSettings {
    public Context context;
    public String url;
    public  RequestParams params;

    public String destPath;

    public Object tag;
    public JerryCallBack callBack;
    public long connectionTimeout = 10000;
    public long readTimeout = 10000;
    public MethodType type;
    public boolean isOpenProgressDialog = true;



}
