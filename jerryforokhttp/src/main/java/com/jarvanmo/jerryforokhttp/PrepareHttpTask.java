package com.jarvanmo.jerryforokhttp;
import java.util.HashMap;
import java.util.IdentityHashMap;

/**
 * Created by 46812 on 2015/9/20.
 *
 */
public class PrepareHttpTask {

    private OkHttpClientSettings settings;

    public PrepareHttpTask(OkHttpClientSettings settings) {
        this.settings = settings;
    }

    public PrepareHttpTask tag(Object tag) {
        settings.tag = tag;
        return this;
    }

    public PrepareHttpTask connectionTimeout(long connectionTimeout) {
        settings.connectionTimeout = connectionTimeout;
        return this;
    }

    public PrepareHttpTask readTimeout(long readTimeout) {

        return this;
//        Jerry.with(new Activity()).load()
    }

    /**
     * 为http请求增加请求头，该方法key对应的值是唯一的
     * ***/
    public PrepareHttpTask header(HashMap<String,String> header){
        settings.header = header;
        return  this;

    }

    /**
     * 为http请求增加请求头，该方法key对应的值可以有多个。
     * ***/
    public PrepareHttpTask addHeader(IdentityHashMap<String,String> addHeader){
        settings.addHeader = addHeader;
        return  this;

    }


    public PrepareHttpTask noProgressBar() {
        settings.isOpenProgressDialog = false;
        return this;
    }


    public JerryExecutor result(JerryCallBack callBack) {
        settings.callBack = callBack;
        return new JerryExecutor(settings);
    }

    /**
     * call this method if you really don't care about the result form server
     **/
    public JerryExecutor result() {
        return result(null);
    }


}
