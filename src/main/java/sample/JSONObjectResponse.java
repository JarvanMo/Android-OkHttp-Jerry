package sample;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrmo.jerry.JerryCallBack;
import com.squareup.okhttp.Request;

/**
 * Created by Administrator on 2015/9/21.
 * 
 */
public abstract class JSONObjectResponse extends JerryCallBack {


    /**
     * parse your data here no matter what kind method you use the success result always is String
     * in my project i use fast json
     * **/
    @Override
    public void onSuccess(String result) {

        JSONObject jsonObject = JSON.parseObject(result);
        String mess = jsonObject.getString("mess");
        String code = jsonObject.getString("code");

        if (code.equals("ok")) {
            onCorrectData(jsonObject.getJSONObject("data"));
        } else {
            final Context context = getContext();
            if (code.equals("99999")) {


                if (context != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(context, SampleActivity.class);
                            context.startActivity(intent);
                        }
                    }, 1000);
                }
            } else {
                if(context != null){
                }
            }

          onError(mess);
        }


    }

    @Override
    public void onFailure(int errorCode) {
    }

    public  void onError(String mess){

    }

    @Override
    public void onException(Request request, Exception e) {
    }

    public abstract void onCorrectData(JSONObject data);

}
