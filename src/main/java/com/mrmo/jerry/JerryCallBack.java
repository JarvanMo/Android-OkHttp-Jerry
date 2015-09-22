package com.mrmo.jerry;


import android.content.Context;

/**
 * Created by 46812 on 2015/9/20.
 *
 */
public  class JerryCallBack extends  HttpTaskHandler{

    /***
     * @return
     * NOTE : may be return nul
     * ***/
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

}
