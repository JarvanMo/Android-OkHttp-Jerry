package com.jarvanmo.jerryforokhttp;

import android.content.Context;

import okhttp3.MediaType;


/**
 * Created by Jerry on 2015/9/20.
 *
 */
public class Jerry {

    public static final String CONTENT_TYPE = "Content-Type";

    public static final MediaType JSON = MediaType.parse("application/json");
    public static final String REQUEST_BODY_JSON = "application/json";
    public static final MediaType REQUEST_BODY_FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");
    public static JerryWithBuilder with(Context context) {
        return new JerryWithBuilder(context);
    }


    public static JerryDispatcher load(String url) {

        JerryBuilder jerryBuilder = new JerryBuilder();
        jerryBuilder.isOpenFlag = false;
        jerryBuilder.url = url;
        return new JerryDispatcher(jerryBuilder);
    }


}
