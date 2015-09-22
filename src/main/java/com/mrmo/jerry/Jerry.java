package com.mrmo.jerry;

import android.content.Context;

/**
 * Created by Jerry on 2015/9/20.
 *
 */
public class Jerry {

    public static JerryWithBuilder with(Context context){
        return new JerryWithBuilder(context);
    }


    public static JerryDispatcher load(String url){

        JerryBuilder jerryBuilder = new JerryBuilder();
        jerryBuilder.isOpenFlag = false;
        jerryBuilder.url = url;
        return  new JerryDispatcher(jerryBuilder);
    }



}
