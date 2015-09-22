package com.mrmo.jerry;

import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mo on 2015/9/20.
 *
 */
public class OkHttpDispatcher {

    private static OkHttpDispatcher dispatcher = new OkHttpDispatcher();

    private OkHttpClient client;

    private Object tag;

    private Call call;

    private Handler mDelivery;
    private long connectionTimeout = 10000;
    private long configConnectionTimeout;
    private long readTimeout = 10000;
    private long configReadTimeout = 10000;


    private OkHttpDispatcher() {
        client = new OkHttpClient();
        configConnectionTimeout = connectionTimeout;
        configReadTimeout = readTimeout;
        client.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
        client.setConnectTimeout(readTimeout, TimeUnit.MILLISECONDS);
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkHttpDispatcher getDispatcher() {

        if (dispatcher == null) {
            dispatcher = new OkHttpDispatcher();
        }

        return dispatcher;
    }

    public OkHttpClient getClient() {

        return client.clone();
    }

   public void setConnectionTimeout(long connectionTimeout){

       getClient().setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);


   }
    public void setReadTimeout(long readTimeout){
        getClient().setConnectTimeout(readTimeout, TimeUnit.MILLISECONDS);
    }


    /**
     * @param type only two kind types
     * @see MethodType
     */

    public void asyncRequest(MethodType type, String url, final RequestParams requestParams, final HttpTaskHandler handler, Object tag) {

        Request request = null;

        switch (type) {

            case POST:
                request = postRequest(url, requestParams,tag);
                break;
            case GET:
                request = getRequest(url, requestParams, tag);
                break;
            default:
                break;
        }


        if (request == null) {
            throw new IllegalArgumentException("request must be not null");
        }

        handler.onBefore();
        call = client.newCall(request);
        OkHttpClient cloneClient= getClient();
        cloneClient.setConnectTimeout(configConnectionTimeout, TimeUnit.MILLISECONDS);
        cloneClient.setReadTimeout(configReadTimeout, TimeUnit.MILLISECONDS);
        taskStart(call,handler);

    }

    /****
     * start a new http request with callback {@link HttpTaskHandler}
     * ***/
    private void taskStart(Call call,final HttpTaskHandler handler) {

        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.onException(request, e);
                        handler.onFinish();
                    }
                });

            }

            @Override
            public void onResponse(final Response response) throws IOException {

                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            try {
                                handler.onSuccess(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.onFailure(response.code());
                        }
                        handler.onFinish();
                    }
                });

            }


        });
    }

    /**
     * get a request object that use Method("post")
     *
     * @param url
     * @param requestParams your params
     **/
    private Request postRequest(String url, RequestParams requestParams,Object tag) {
        return new Request.Builder()
                .url(url)
                .tag(tag)
                .post(createRequestBody(requestParams))
                .build();
    }



    /**
     * get a request object that use Method("get")
     *
     * @param url
     * @param requestParams your params
     **/
    private Request getRequest(String url, RequestParams requestParams,Object tag) {

        return new Request.Builder()
                .url(url)
                .tag(tag)
                .post(createRequestBody(requestParams))
                .build();
    }



    /***
     * handle request params
     *
     * @param requestParams the params you want to send
     ****/
    private RequestBody createRequestBody(RequestParams requestParams) {


        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        if (requestParams != null) {
            Iterator it;
            final ConcurrentHashMap<String, String> urlParams = requestParams.urlParams;
            final ConcurrentHashMap<String, RequestParams.FileWrapper> fileParams = requestParams.fileParams;

            if (urlParams != null) {
                it = urlParams.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, (String) entry.getValue()));
                }
            }

            if (fileParams != null) {
                it = fileParams.entrySet().iterator();
                RequestBody fileBody = null;
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    RequestParams.FileWrapper fileWrapper = (RequestParams.FileWrapper) entry.getValue();
                    fileBody = RequestBody.create(MediaType.parse(fileWrapper.contentType), fileWrapper.file);
                    builder.addPart(Headers.of("Content-Disposition",
                                    "form-data; name=\"" + entry.getKey() + "\"; filename=\"" + fileWrapper.file.getName() + "\""),
                            fileBody);

                }
            }
        }

        return builder.build();
    }



    /**
     * a async task to download something
     *
     * @param url
     * @param destFileDir local dir to save file
     * @param handler
     */
    public void downloadTask(final String url, final String destFileDir, final HttpTaskHandler handler, Object tag) {
        final Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .build();
        handler.onBefore();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {

                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.onException(request,e);
                    }
                });
            }

            @Override
            public void onResponse(Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();

                    File dir = new File(destFileDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    final File file = new File(dir, getFileName(url));
                    final  long totalSize = response.body().contentLength();
                    long downloadedSize =  0l;
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);

                        downloadedSize += len;
                        final long tempDownloadedSize = downloadedSize;
                        mDelivery.post(new Runnable() {
                            @Override
                            public void run() {
                                handler.onDownload(totalSize,tempDownloadedSize);
                            }
                        });
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.onSuccess(file.getAbsolutePath());
                        }
                    });
//                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (final IOException e) {
//                    sendFailedStringCallback(response.request(), e, callback);

                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.onException(request,e);
                        }
                    });
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                    }

                    handler.onFinish();
                }

            }
        });
    }


    public void downloadTask(final String url, final String destFileDir,  HttpTaskHandler handler) {
        downloadTask(url, destFileDir, handler, null);
    }

    /****
     * parse file name by its path
     * @param path
     *
     * @return
     */
    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }


}

enum MethodType {
    POST, GET ,DOWNLOAD
}
