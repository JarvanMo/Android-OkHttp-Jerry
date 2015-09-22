package com.mrmo.jerry;

/**
 * Created by Jerry on 2015/9/20.
 * this class will tell {@link PrepareHttpTask} the {@link com.squareup.okhttp.OkHttpClient}
 * how to work , such as which method it will use etc
 */
public class JerryDispatcher {

    private JerryBuilder builder;

    private OkHttpClientSettings settings;

    public JerryDispatcher(JerryBuilder builder){
        this.builder = builder;
    }

    public PrepareHttpTask post(RequestParams params){
        settings = new OkHttpClientSettings();
        settings.context =  builder.context;
        settings.url = builder.url;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        settings.params = params;
        settings.type = MethodType.GET;
        return  new PrepareHttpTask(settings);
    }


    public PrepareHttpTask get(RequestParams params){
        settings = new OkHttpClientSettings();
        settings.context =  builder.context;
        settings.url = builder.url;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        settings.params = params;
        settings.type = MethodType.GET;
        return  new PrepareHttpTask(settings);
    }

    public PrepareHttpTask get(){
        settings = new OkHttpClientSettings();
        settings.context =  builder.context;
        settings.url = builder.url;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        settings.params = new RequestParams();
        settings.type = MethodType.GET;
        return  new PrepareHttpTask(settings);
    }


    public PrepareHttpTask download(String destPath){
        settings = new OkHttpClientSettings();
        settings.context =  builder.context;
        settings.url = builder.url;
        settings.type = MethodType.DOWNLOAD;
        settings.destPath = destPath;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        return  new PrepareHttpTask(settings);
    }

}
