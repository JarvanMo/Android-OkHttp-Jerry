package com.mrmo.jerry;

import com.squareup.okhttp.OkHttpClient;


/**
 * Created by Administrator on 2015/9/21.
 *
 */
public class CallAttach {

    private OkHttpClient client;

    public CallAttach (OkHttpClient client) {
        this.client = client;
    }



    /***
     * @param tag
     * if you try cancel a null object you'll get an exception
     * **/
    public void cancel(Object tag){

        if(tag == null ){
            throw  new IllegalArgumentException("can't cancel a call with null tag");
        }else {
            client.cancel(tag);

        }
    }


}
