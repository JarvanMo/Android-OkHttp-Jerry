package com.mrmo.jerry;

import com.squareup.okhttp.Request;

/**
 * Created by Jerry on 2015/9/20.
 * client callback
 */
public class HttpTaskHandler {

    /**called before  {@link com.squareup.okhttp.OkHttpClient} execute  a request
     * ***/
    public void onBefore(){

    }

    /**
     * called if http code is 200...etc.
     * @param result
     * the result called back will be String
     * <br>if it was called by a normal http request , the result will be response's body</>
     * if it was called by download task , the result will be the path of file
     * **/
    public  void onSuccess(String result){

    }

    /***
     *called when you download file
     * **/
    public void onDownload(long totalSize, long downloadedSize){

    }
    /**
     * called if http occurs error
     * @param errorCode
     * it's http code but not 200
     * ***/
    public void onFailure(int errorCode){


    }


    /***
     * called when {@link com.squareup.okhttp.OkHttpClient} occurs occurs exceptions
     * such as socket timeout
     * **/
    public void onException(Request request,Exception e){

    };


    /**
     * will be called  no matter what happens
     * */
    public void onFinish(){
    }

}
