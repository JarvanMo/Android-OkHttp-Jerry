package com.mrmo.jerry;

/**
 * Created by 46812 on 2015/9/20.
 *
 */
public class PrepareHttpTask {

    private OkHttpClientSettings settings;
    private  JerryExecutor executor;

    public PrepareHttpTask(OkHttpClientSettings settings){
        this.settings =  settings;
    }

    public PrepareHttpTask tag(Object tag){
       settings.tag  = tag;
        return this;
    }

    public PrepareHttpTask connectionTimeout( long connectionTimeout){
        settings.connectionTimeout = connectionTimeout;
        return this;
    }

    public PrepareHttpTask readTimeout( long readTimeout){
        return this;
//        Jerry.with(new Activity()).load()
    }


    public PrepareHttpTask noProgressBar(){
        settings.isOpenProgressDialog = false;
        return this;
    }


    public JerryExecutor result(JerryCallBack callBack){
        settings.callBack = callBack;
        executor = new JerryExecutor(settings);
        return  executor;
    }

    /**
     * call this method if you really don't care about the result form server
     * **/
    public JerryExecutor result( ){
      return result(null);
    }




}
