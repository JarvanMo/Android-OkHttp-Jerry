package com.jarvanmo.jerryforokhttp;


import okhttp3.RequestBody;

/**
 * Created by Jerry on 2015/9/20.
 * this class will tell {@link PrepareHttpTask} the {@link okhttp3.OkHttpClient}
 * how to work , such as which method it will use etc
 */
public class JerryDispatcher {

    private JerryBuilder builder;

    private OkHttpClientSettings settings;


    public JerryDispatcher(JerryBuilder builder) {
        this.builder = builder;
    }

    public PrepareHttpTask post(RequestParams params) {
        settings = new OkHttpClientSettings();
        settings.context = builder.context;
        settings.url = builder.url;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        settings.params = params;
        settings.type = MethodType.POST;
        return new PrepareHttpTask(settings);
    }


    public PrepareHttpTask post(RequestBody requestBody){
        settings = new OkHttpClientSettings();
        settings.context = builder.context;
        settings.url = builder.url;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        settings.requestBody = requestBody;
        settings.type = MethodType.POST;
        return new PrepareHttpTask(settings);
    }

    public PrepareHttpTask put(RequestBody requestBody){
        settings = new OkHttpClientSettings();
        settings.context = builder.context;
        settings.url = builder.url;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        settings.requestBody = requestBody;
        settings.type = MethodType.PUT;
        return new PrepareHttpTask(settings);
    }

    public PrepareHttpTask delete(RequestBody requestBody){
        settings = new OkHttpClientSettings();
        settings.context = builder.context;
        settings.url = builder.url;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        settings.requestBody = requestBody;
        settings.type = MethodType.DELETE;
        return new PrepareHttpTask(settings);
    }


    public PrepareHttpTask get(RequestParams params) {
        settings = new OkHttpClientSettings();
        settings.context = builder.context;
        settings.url = builder.url;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        settings.params = params;
        settings.type = MethodType.GET;
        return new PrepareHttpTask(settings);
    }

    public PrepareHttpTask get() {
        settings = new OkHttpClientSettings();
        settings.context = builder.context;
        settings.url = builder.url;
        settings.isOpenProgressDialog = builder.isOpenFlag;
        settings.params = new RequestParams();
        settings.type = MethodType.GET;
        return new PrepareHttpTask(settings);
    }


    public PrepareHttpTask download(String destPath) {
        return download(destPath,null);
    }

    /**
     * @param destPath
     * 文件存放路径
     * @param  fileName
     * 文件名称
     * **/
    public PrepareHttpTask download(String destPath,String fileName) {
        settings = new OkHttpClientSettings();
        settings.context = builder.context;
        settings.url = builder.url;
        settings.type = MethodType.DOWNLOAD;
        settings.destPath = destPath;

        if(fileName != null) {
            settings.fileName = fileName;
        }
        settings.isOpenProgressDialog = builder.isOpenFlag;
        return new PrepareHttpTask(settings);
    }

}
