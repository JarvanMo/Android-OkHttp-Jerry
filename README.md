# Android-Jerry-OkHttp



Jerry is just a **Utils**  for   **OKHTTP** in Android.It has  **FLUENT APIS** .

From ok-http 3 , square has changed lots of its apis , so I'm still developing the new version of Jerry.
I'll give a sample later.

----------


Usage
-------------

it's simple! I also give a smaple in my lib.
```
      RequestParams params = new RequestParams();
        params.put("key", "value");

        try {
            params.put("fileKey",new File("filepath"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
      Jerry.with(context).load("your url").post(params).result(new JerryCallBack());
```
context is not necessary , so you can also do this
```
     Jerry.load("your url").post(params).result(new JerryCallBack());
```
if you are not concerned about your result
```
    Jerry.load("your url").post(params).result();
```
with configration.The default connection and read timeout are 10s,but you can customize them  for a special call.
```
Jerry.load("your url").post(params).noProgressBar().tag(this).connectionTimeout(10000).readTimeout(1000).result();
```


> **Note:**

> - if you try to cancel a null object you'll get an exception

CallBack
---------
> **Note:**
> -no matter what kind method you use such as pot,download, you will  always get String in **onSuccess(String result)**., so you use GSON or fastjson etc to parse the result.When you wanna download something,you'll get it's path in  **onSuccess(String result)**, when downlading , Jerry will call **onDownload(int totalSize, int curSize)** back,  in the other method ,it's never called.
>

