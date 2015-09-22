package com.mrmo.jerry;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * Created by 46812 on 2015/9/20.
 * this class will send request  depends on {@link OkHttpClientSettings}
 * <br/> the correct result from server will be {@link String}
 * <br/> if u want to change result you customize your {@link JerryCallBack}(recommended)
 * <br/>or,u can change this class (not recommended)
 */
public class JerryExecutor {

    private String TAG = JerryExecutor.class.getSimpleName();
    OkHttpClientSettings settings ;
    private OkHttpDispatcher okHttpDispatcher;
    private OkHttpClient client;
    private MethodType type;





    public JerryExecutor(OkHttpClientSettings settings){

        this.settings = settings;
        okHttpDispatcher = OkHttpDispatcher.getDispatcher();
        client = okHttpDispatcher.getClient();


        initDispatcher();
    }

    private void initDispatcher(){
        okHttpDispatcher.setConnectionTimeout(settings.connectionTimeout);
        okHttpDispatcher.setReadTimeout(settings.readTimeout);
        type = settings.type;
        request(settings.callBack);
    }

    private void request( JerryCallBack callBack){

        if(callBack != null){
            execute(concernAboutResult(callBack));
        }else {
            execute(notConcernAboutResult());
        }


    }

    private HttpTaskHandler concernAboutResult(final JerryCallBack callBack){

        HttpTaskHandler handler = new HttpTaskHandler() {
            @Override
            public void onBefore() {

                callBack.setContext(settings.context);
                callBack.onBefore();
                if(settings.isOpenProgressDialog){
                    if(settings.context == null){
                        throw  new IllegalArgumentException("before progress dialog ,make sure you context not null");
                    }else {

                        /** i use a progressbar in my project*/
//                        BaseApplication.MyDialog.show(settings.context);

                    }
                    callBack.onBefore();
                }

            }

                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result);
                    callBack.onSuccess(result);
                }

             /*****
             *  NOTE: if you don't download file ,this method will be never called
             */
            @Override
            public void onDownload(long totalSize, long downloadedSize) {
                Log.d(TAG, "File total size is " + totalSize + ", already download size is " + downloadedSize);
                callBack.onDownload(totalSize, downloadedSize);
            }

            @Override
            public void onFailure(int errorCode) {
                Log.d(TAG,"HTTP CODE IS" + errorCode);
                callBack.onFailure(errorCode);
            }


                @Override
                public void onException(Request request, Exception e) {
                    callBack.onException(request, e);
                }

                @Override
                public void onFinish() {
                    if (settings.isOpenProgressDialog) {
                        if (settings.context == null) {
                            throw new IllegalArgumentException("before progress dialog ,make sure you context not null");
                        } else {
                            /** i use a progressbar in my project*/
//                            BaseApplication.MyDialog.dismiss();
                        }
                    }
                    callBack.onFinish();
                }
            };


        return handler;


    }

    private HttpTaskHandler notConcernAboutResult(){


        HttpTaskHandler handler = new HttpTaskHandler() {
            @Override
            public void onBefore() {
                if(settings.isOpenProgressDialog){
                    if(settings.context == null){
                        throw  new IllegalArgumentException("before progress dialog ,make sure you context not null");
                    }else {
                        /** i use a progressbar in my project*/
//                        BaseApplication.MyDialog.show(settings.context);
                    }
                }
            }

            @Override
            public void onSuccess(String result) {
                Log.d(TAG, result);
            }

            @Override
            public void onFailure(int errorCode) {
                Log.d(TAG,"HTTP CODE IS" + errorCode);
            }

            @Override
            public void onException(Request request, Exception e) {
            }

            @Override
            public void onFinish() {
                if(settings.isOpenProgressDialog){
                    if(settings.context == null){
                        throw  new IllegalArgumentException("before progress dialog ,make sure you context not null");
                    }else {
                        /** i use a progressbar in my project*/
//                        BaseApplication.MyDialog.dismiss();
                    }
                }
            }
        };

        return handler;
    }


    private void execute(HttpTaskHandler handler){
        switch (type){

            case POST:
                okHttpDispatcher.asyncRequest(MethodType.POST, settings.url, settings.params, handler, settings.tag);
                break;

            case GET:
                okHttpDispatcher.asyncRequest(MethodType.GET, settings.url, settings.params, handler, settings.tag);
                break;
            case DOWNLOAD:
                okHttpDispatcher.downloadTask(settings.url,settings.destPath,handler,settings.tag);
                break;
            default: break;
        }


    }

    public CallAttach callAttach(){

        return  new CallAttach(okHttpDispatcher.getClient());
    }



}
