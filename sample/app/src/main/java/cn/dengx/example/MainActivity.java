package cn.dengx.example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.dengx.couhttp.BitmapUpload;
import cn.dengx.couhttp.CouHttp;
import cn.dengx.couhttp.ImageRequest;
import cn.dengx.couhttp.Request;
import cn.dengx.couhttp.RequestFactory;
import cn.dengx.couhttp.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private Button get, post, upload, image, image2;
    private ImageView imageView;
    private L l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        get = (Button) findViewById(R.id.get);
        post = (Button) findViewById(R.id.post);
        upload = (Button) findViewById(R.id.upload);
        image = (Button) findViewById(R.id.image);
        image2 = (Button) findViewById(R.id.image2);
        imageView = (ImageView) findViewById(R.id.imageView);

        l = new L(textView);

        get.setOnClickListener(this);
        post.setOnClickListener(this);
        upload.setOnClickListener(this);
        image.setOnClickListener(this);
        image2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get:
                getHTML();
                break;

            case R.id.image:
                getImageFromAssets();
                break;

            case R.id.post:
                postJSON();
                break;

            case R.id.upload:
                uploadIcon();
                break;

            case R.id.image2:
                getImageFromNet();
                break;
        }
    }

    private void postJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("userId", "18888");
            object.put("name", "cou");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = RequestFactory.jsonObject("http://192.168.31.240:8080/viewspace/user/show", object, l);
        CouHttp.getInstance().addRequest(request);
    }

    private void getHTML() {
        Request request = RequestFactory.string("http://www.baidu.com", l);
        CouHttp.getInstance().addRequest(request);
    }

    private void getImageFromAssets() {
        Request r = RequestFactory.image("assets://xxxh.jpg", new MyProcess(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap result,Request request) {
                imageView.setImageBitmap(result);
            }

            @Override
            public void onFail(Request request, Response.Error error) {
                textView.setText(request.toString() + "\r\n" + error);
            }
        });
        CouHttp.getInstance().addRequest(r);
    }

    private void getImageFromNet() {
        Request request = RequestFactory.image("http://imgsrc.baidu.com/forum/pic/item/dc54564e9258d109b2e4381dd158ccbf6c814d1d.jpg",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap result,Request request) {
                        imageView.setImageBitmap(result);
                    }

                    @Override
                    public void onFail(Request request, Response.Error error) {
                        textView.setText(request.toString() + "\r\n" + error);
                    }
                });
        CouHttp.getInstance().addRequest(request);
    }

    private void uploadIcon() {
        Request request = RequestFactory.upload("http://192.168.31.240:8080/viewspace/user/upload", l,
                new BitmapUpload.BitmapContainer("file", "ic_launcher.png",
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)));
        CouHttp.getInstance().addRequest(request);

    }

    static class L implements Response.Listener<String> {
        private TextView textView;

        public L(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onResponse(String result,Request request) {
            textView.setText(result);
        }

        @Override
        public void onFail(Request request, Response.Error error) {
            textView.setText(request.toString() + "\r\n" + error);
        }
    }

    static class MyProcess implements ImageRequest.BitmapProcessor {

        @Override
        public Bitmap process(Bitmap bitmap) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setShader(new BitmapShader(bitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            canvas.drawCircle(w / 2, h / 2, h / 2, p);
            bitmap.recycle();
            return b;
        }
    }
}
