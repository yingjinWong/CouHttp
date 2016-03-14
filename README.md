##CouHttp是将android app里一些常用的http请求的封装       
####底层运用java的API HttpURLConnection实现，现在只封装了GET和POST方法。除此之外，还进行了一些小小的扩展。   
     
* #####首先在application里初始化     
    
        @Override
        public void onCreate() {
            super.onCreate();
            CouHttp couHttp =CouHttp.getInstance();
            couHttp.init(getApplicationContext());
            couHttp.setDebug(true);//打印log
            couHttp.setDiskCache();
        }
    
* #####String的get和post请求     
    1.StringRequest(String url, Listener<String> listener);   
    2.StringRequest(String url, int method, Map<String, String> params, Listener<String> listener)    
    3.StringRequest(String url, int method, Map<String, String> headers, Map<String, String> params, Listener<String> listener)     
    
    1.get请求     
    2.如果method为Request.METHOD_GET,则将params已表单形式，在url后面并接？然后连接起来     
    3.增加请求头headers     
     
* #####扩展JSONObject和JSONArray请求     
    JSONArrayRequest(String url, JSONArray array, Listener<String> l)     
    JSONArrayRequest(String url, JSONArray array, Map<String, String> headers, Listener<String> l)    
    JSONObjectRequest(String url, JSONObject object, Listener<String> l)    
    JSONObjectRequest(String url, JSONObject object, Map<String, String> headers, Listener<String> l)    
    
    请求方式均为post，JSONObject和JSONArray以body方式写入请求    
    
* #####拉取图片    
    1.ImageRequest(String uri, Listener<Bitmap> listener)    
    2.ImageRequest(String uri, Listener<Bitmap> listener, int width, int height)    
    3.ImageRequest(String uri, Listener<Bitmap> listener, int width, int height, ImageRequest.BitmapProcessor bitmapProcessor)    
    
    get请求拉取图片，设置width和height后，将会压缩到期望大小    
    bitmapProcessor为一个处理bitmap接口，调用process(Bitmap bitmap)方法进行处理，返回处理之后的bitmap    
    所有bitmap均会cache在内存中，如果设置couHttp.setDiskCache()，会在本地保存     
    ######uri:      
        http://localhost/aa.jpg    
        file:///ab.png
        content://ac.webp
        assets://icon.jpg     
        
* #####下载文件    
    1.FileRequest(String url, File dir, String name, Listener<File> listener)     
    2.FileRequest(String url, File saved, Listener listener)    
       
* #####上传    
    1.BitmapUpload(String url, Listener<String> l, BitmapUpload.BitmapContainer... containers    
    2.BitmapUpload(String url, Map<String, String> params, Listener<String> l, BitmapUpload.BitmapContainer... containers)    
    3.BitmapUpload(String url, Map<String, String> params, Listener<String> l, Map<String, String> headers, BitmapUpload.BitmapContainer... containers）    
    4.FileUpload(String url, Listener<String> l, FileUpload.FileContainer... containers)    
    5.FileUpload(String url, Map<String, String> params, Listener<String> l, FileUpload.FileContainer... containers)    
    6.FileUpload(String url, Map<String, String> headers, Map<String, String> params, Listener<String> l, FileUpload.FileContainer... containers)    
    扩展直接bitmap上传，支持多文件    
    
* #####所有请求支持都可以在RequestFactory创建   
      
* 上传和下载均支持获取进度registProgressListener(ProgressListener progressListener)


