package sample;

import android.app.Activity;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.mrmo.jerry.CallAttach;
import com.mrmo.jerry.Jerry;
import com.mrmo.jerry.JerryCallBack;
import com.mrmo.jerry.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by 46812 on 2015/9/22.
 *
 *
 */
public class SampleActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RequestParams params = new RequestParams();
        params.put("key", "value");


        try {
            params.put("fileKey",new File("filepath"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        Jerry.with(this).load("your url").post(params).result(new JSONObjectResponse() {
            @Override
            public void onCorrectData(JSONObject data) {
                //get data you want
            }
        });
        Jerry.with(this).load("your url").post(params).result(new JerryCallBack());

        /**
         * also you can do this
         * **/
       Jerry.load("your url").post(params).result(new JSONObjectResponse() {
           @Override
           public void onCorrectData(JSONObject data) {

           }
       });

        /**
         * without request params
         * **/
        Jerry.load("your url").get().result(new JSONObjectResponse() {
            @Override
            public void onCorrectData(JSONObject data) {

            }
        });


        /***
         * set okhttpclient
         * **/

        Jerry.load("your url").post(params).noProgressBar().tag(this).connectionTimeout(10000).readTimeout(1000).result();

        /***
         * if you  really don't care your result
         * **/
        Jerry.load("your url").post(params).result();

        /***
         * you also can get a  {@link CallAttach}
         * if you  want to cancel a call
         * if you try cancel a null object you'll get an exception
         * **/

        CallAttach attach =  Jerry.load("your url").post(params).result().callAttach();
        attach.cancel(this);
    }
}
