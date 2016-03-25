package com.jarvanmo.jerryforokhttp;


import android.util.Log;

import co.tton.mlibrary.base.BaseApplication;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;

/**
 * Created by 46812 on 2015/9/20.
 * this class will send request  depends on {@link OkHttpClientSettings}
 * <br/> the correct result from server will be {@link String}
 * <br/> if u want to change result you customize your {@link JerryCallBack}(recommended)
 * <br/>or,u can change this class (not recommended)
 */
public class JerryExecutor {

    private String TAG = JerryExecutor.class.getSimpleName();
    OkHttpClientSettings settings;
    private OkHttpDispatcher okHttpDispatcher;
    private OkHttpClient client;
    private MethodType type;


    public JerryExecutor(OkHttpClientSettings settings) {

        this.settings = settings;
        okHttpDispatcher = OkHttpDispatcher.getDispatcher();
        client = okHttpDispatcher.getClient();
        initDispatcher();
    }

    private void initDispatcher() {
        type = settings.type;
        request(settings.callBack);
    }

    private void request(JerryCallBack callBack) {

        if (callBack != null) {
            execute(concernAboutResult(callBack));
        } else {
            execute(notConcernAboutResult());
        }


    }

    private HttpTaskHandler concernAboutResult(final JerryCallBack callBack) {


        return new HttpTaskHandler() {
            @Override
            public void onBefore() {

                callBack.setContext(settings.context);
                callBack.onBefore();
                if (settings.isOpenProgressDialog) {
                    if (settings.context == null) {
                        throw new IllegalArgumentException("before progress dialog ,make sure you context not null");
                    } else {
                        BaseApplication.MyDialog.show(settings.context);
                    }
                }

            }

            @Override
            public void onSuccess(String result, Headers headers) {
                Log.d(TAG, result);
                callBack.onSuccess(result, headers);
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
                Log.d(TAG, "HTTP CODE IS " + errorCode);
                callBack.onFailure(errorCode);
            }


            @Override
            public void onException(Call call, Exception e) {
                callBack.onException(call, e);
            }

            @Override
            public void onFinish() {
                if (settings.isOpenProgressDialog) {
                    if (settings.context == null) {
                        throw new IllegalArgumentException("before progress dialog ,make sure you context not null");
                    } else {
                        BaseApplication.MyDialog.dismiss();
                    }
                }

                callBack.onFinish();
            }
        };


    }

    private HttpTaskHandler notConcernAboutResult() {


        return new HttpTaskHandler() {
            @Override
            public void onBefore() {
                if (settings.isOpenProgressDialog) {
                    if (settings.context == null) {
                        throw new IllegalArgumentException("before progress dialog ,make sure you context not null");
                    } else {
                        BaseApplication.MyDialog.show(settings.context);
                    }
                }
            }

            @Override
            public void onSuccess(String result, Headers headers) {
                Log.d(TAG, result);
            }


            @Override
            public void onFailure(int errorCode) {
                Log.d(TAG, "HTTP CODE IS" + errorCode);
            }

            @Override
            public void onException(Call call, Exception e) {
            }

            @Override
            public void onFinish() {
                if (settings.isOpenProgressDialog) {
                    if (settings.context == null) {
                        throw new IllegalArgumentException("before progress dialog ,make sure you context not null");
                    }
                }
            }
        };
    }

//    public boolean isActivityRunning(Context context) {
//
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> infos = activityManager.getRunningTasks(1);
//        if (infos != null && infos.size() > 0) {
//
//            ComponentName name = infos.get(0).topActivity;
//
//            if (context.getClass().getName().equals(name.getClassName())) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    private void execute(HttpTaskHandler handler) {
        switch (type) {

            case POST:
//                okHttpDispatcher.asyncRequest(settings, handler);
//                break;
            case GET:
//                okHttpDispatcher.asyncRequest(settings, handler);
//                break;
            case PUT:
//                okHttpDispatcher.asyncRequest(settings, handler);
//                break;
            case DELETE:
                okHttpDispatcher.asyncRequest(settings, handler);
                break;
            case DOWNLOAD:
                okHttpDispatcher.downloadTask(settings, handler);
                break;
            default:
                break;
        }


    }

    @Deprecated
    public CallAttach callAttach() {

        return new CallAttach(okHttpDispatcher.getCall());
    }


}
