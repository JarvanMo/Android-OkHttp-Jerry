package com.mrmo.jerry;

import android.content.Context;

/**
 * Created by Administrator on 2015/9/21.
 *
 */
public class JerryWithBuilder {

    private   Context context;
    public JerryWithBuilder(Context context){

        this.context = context;
    }

    public JerryDispatcher load(String url){
        JerryBuilder builder = new JerryBuilder();
        builder.context = context;
        builder.isOpenFlag = true;
        builder.url = url;
        return new JerryDispatcher(builder);
    }
}
