package com.jarvanmo.jerryforokhttp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mo on 2015/9/20.
 * update by mo on 2015/10/26.
 */
public class OkHttpDispatcher {

    private final String TAG = this.getClass().getSimpleName();
    private final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
    private final MediaType FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");
    private static OkHttpDispatcher dispatcher = new OkHttpDispatcher();

    private OkHttpClient client;


    private Call call;

    private Handler mDelivery;
    private long connectionTimeout = 10000;
    private long configConnectionTimeout;
    private long readTimeout = 10000;
    private long configReadTimeout = 10000;


    private OkHttpDispatcher() {
        client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        configConnectionTimeout = connectionTimeout;
        configReadTimeout = readTimeout;

//        client.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
//        client.setConnectTimeout(readTimeout, TimeUnit.MILLISECONDS);
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkHttpDispatcher getDispatcher() {

        if (dispatcher == null) {
            dispatcher = new OkHttpDispatcher();
        }

        return dispatcher;
    }

    public OkHttpClient getClient() {

        return client;
    }

    public Call getCall(){
        return call;
    }

    @Deprecated
    public void setConnectionTimeout(long connectionTimeout) {

//        getClient().setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);


    }

    @Deprecated
    public void setReadTimeout(long readTimeout) {
//        getClient().setConnectTimeout(readTimeout, TimeUnit.MILLISECONDS);
    }


    public void asyncRequest(OkHttpClientSettings settings, final HttpTaskHandler handler) {

        Request request = null;

        switch (settings.type) {

            case POST:

                Log.d(TAG, "post method url = " + settings.url);
                if (settings.params != null) {
                    request = postRequest(settings);
                } else if (settings.requestBody != null) {
                    request = postRequestWithRequestBody(settings);
                }

                break;
            case GET:
                request = getRequest(settings);
                break;
            case PUT:
                request = putRequest(settings);
                break;
            case DELETE:
                request = deleteRequest(settings);
                break;
            default:
                break;
        }


        if (request == null) {
            throw new IllegalArgumentException("request must be not null");
        }

        handler.onBefore();
        call = client.newCall(request);
//        OkHttpClient cloneClient = getClient();
//        cloneClient.setConnectTimeout(configConnectionTimeout, TimeUnit.MILLISECONDS);
//        cloneClient.setReadTimeout(configReadTimeout, TimeUnit.MILLISECONDS);
//        cloneClient.setWriteTimeout(configReadTimeout, TimeUnit.MILLISECONDS);
        taskStart(call, handler);

    }

    /****
     * start a new http request with callback {@link HttpTaskHandler}
     ***/
    private void taskStart(final Call call, final HttpTaskHandler handler) {

        call.enqueue(new Callback() {
            @Override
            public void onFailure(final  Call call, final IOException e) {

              Log.w(TAG,(e == null ||e.getMessage()== null) ? "can't get exception message":e.getMessage());

                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.onException(call, e);
                        handler.onFinish();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                if (response.isSuccessful()) {

                    final String result = response.body().string();
                    final Headers headers = response.headers();
//                    String responseType = headers.get("Content-Type");
                    Log.d(TAG, "response " + result);

                    //如果响应头是json则成功，否则onFailure

//                    if(responseType.equals("application/json")){

//                    if (responseType.startsWith("application/json")) {

                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.onSuccess(result, headers);

                        }
                    });
//                    }


                } else {
                    final int code = response.code();
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.onFailure(code);
                        }
                    });

                }

                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.onFinish();
                    }
                });
                response.body().close();

            }
        });


    }

    /***
     * 拼写get方法
     **/
    private String formatGetUrl(String url, RequestParams params) {

        String equalsMark = "=";
        String andMark = "&";
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (params != null) {

            final ConcurrentHashMap<String, String> urlParams = params.urlParams;
            if (urlParams != null && urlParams.size() > 0) {
                sb.append("?");

                for (Map.Entry<String, String> entry : urlParams.entrySet()) {
//                    Map.Entry entry = (Map.Entry) entry1;
                    sb.append(entry.getKey());
                    sb.append(equalsMark);
                    sb.append(entry.getValue());
                    sb.append(andMark);
                }

                int lastIndex = sb.lastIndexOf(andMark);
                sb.deleteCharAt(lastIndex);

            }
        }

        return sb.toString();

    }

    /**
     * get a request object that use Method("post")
     **/
    private Request postRequest(OkHttpClientSettings settings) {
        return createRequestBuilder(settings)
                .url(settings.url)
                .tag(settings.tag)
                .post(createRequestBody(settings.params))
                .build();
    }

    private Request postRequestWithRequestBody(OkHttpClientSettings settings) {
        return createRequestBuilder(settings).url(settings.url).tag(settings.tag).post(settings.requestBody).build();
    }

    private Request putRequest(OkHttpClientSettings settings) {

        return createRequestBuilder(settings).url(settings.url).tag(settings.tag).put(settings.requestBody).build();
    }

    private Request deleteRequest(OkHttpClientSettings settings) {

        return createRequestBuilder(settings).url(settings.url).tag(settings.tag).delete().build();
    }


    /**
     * get a request object that use Method("get")
     **/
    private Request getRequest(OkHttpClientSettings settings) {

        String desUrl = formatGetUrl(settings.url, settings.params);
//        return new Request.Builder()
//                .url(url)
//                .tag(tag)
//                .post(createRequestBody(requestParams))
//                .build();
        Log.d(TAG, "get method url = " + desUrl);
        return createRequestBuilder(settings).url(desUrl).tag(settings.tag).get().build();
    }


    /***
     * 生成builder
     **/
    private Request.Builder createRequestBuilder(OkHttpClientSettings settings) {

        Request.Builder builder = new Request.Builder();

        Set<Map.Entry<String, String>> allSet;
        Iterator<Map.Entry<String, String>> iterator;
        if (settings.header != null) {
            allSet = settings.header.entrySet();

            iterator = allSet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                builder.header(entry.getKey(), entry.getValue());
            }
        }

        if (settings.addHeader != null) {
            allSet = settings.addHeader.entrySet();
            iterator = allSet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                builder.addHeader(entry.getKey(), entry.getValue());

            }
        }

        return builder;

    }

    /***
     * handle request params
     *
     * @param requestParams the params you want to send
     ****/
    private RequestBody createRequestBody(RequestParams requestParams) {




//
//        MultipartBuilder builder = new MultipartBuilder()
//                .type(MultipartBuilder.FORM);
         MultipartBody.Builder builder = new MultipartBody.Builder();
         builder.setType(MultipartBody.FORM);

        if (requestParams != null) {
            Iterator it;
            final ConcurrentHashMap<String, String> urlParams = requestParams.urlParams;
            final ConcurrentHashMap<String, RequestParams.FileWrapper> fileParams = requestParams.fileParams;

            if (urlParams != null) {
                it = urlParams.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();

                    if (entry.getKey().equals(Jerry.REQUEST_BODY_JSON)) {
//
                        RequestBody requestBody = RequestBody.create(JSON, (String) entry.getValue());
                        builder.addPart(requestBody);

                    } else {
//                        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
//                                RequestBody.create(null, (String) entry.getValue()));
                        builder.addFormDataPart((String)entry.getKey(),(String) entry.getValue());
                    }


                }
            }

            if (fileParams != null) {
                it = fileParams.entrySet().iterator();
                RequestBody fileBody;
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
     */
    public void downloadTask(final OkHttpClientSettings settings, final HttpTaskHandler handler) {
//        final Request request = new Request.Builder()
//                .url(settings.url)
//                .tag(tag)
//                .get()
//                .build();

        final  Request request = createRequestBuilder(settings).url(settings.url).tag(settings.tag).get().build();
        handler.onBefore();
        final Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call1, final  IOException e) {

                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.onException(call, e);
                    }
                });



            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {


                InputStream is = null;
                byte[] buf = new byte[10 * 1024];
                int len;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();

                    File dir = new File(settings.destPath);
                    if (!dir.exists()) {

                        if(!dir.mkdirs()){
                            Log.e(TAG,"do download task --> make dirs wrong");
                            return;
                        }
                    }
                    final File file;
                    if (settings.fileName != null) {
                        file = new File(dir, settings.fileName);
                    } else {
                        file = new File(dir, getFileName(settings.url));
                    }
                    final long totalSize = response.body().contentLength();
                    long downloadedSize = 0l;
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);

                        downloadedSize += len;
                        final long tempDownloadedSize = downloadedSize;
                        mDelivery.post(new Runnable() {
                            @Override
                            public void run() {
                                handler.onDownload(totalSize, tempDownloadedSize);
                            }
                        });
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.onSuccess(file.getAbsolutePath(), response.headers());
                        }
                    });
//                    sendSuccessResultCallback(file.getAbsolutePath(), callback);
                } catch (final IOException e) {
//                    sendFailedStringCallback(response.request(), e, callback);

                    mDelivery.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.onException(call, e);
                        }
                    });
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    handler.onFinish();
                }

            }

        });

    }



    /****
     * parse file name by its path
     */
    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }


}

enum MethodType {
    POST, GET, PUT, DELETE, DOWNLOAD;

    @Override
    public String toString() {
        return super.toString();
    }
}
