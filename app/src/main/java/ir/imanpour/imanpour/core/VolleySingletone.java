package ir.imanpour.imanpour.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class VolleySingletone {
  @SuppressLint("StaticFieldLeak")
  private static VolleySingletone mInstance;
  private RequestQueue mRequestQueue;
  private ImageLoader mImageLoader;
  @SuppressLint("StaticFieldLeak")
  private static Context mCtx;

  private VolleySingletone(Context context) {
    mCtx = context;
    mRequestQueue = getRequestQueue();

    mImageLoader = new ImageLoader(mRequestQueue,
      new ImageLoader.ImageCache() {
        private final LruCache<String, Bitmap>
          cache = new LruCache<String, Bitmap>(20);

        @Override
        public Bitmap getBitmap(String url) {
          return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
          cache.put(url, bitmap);
        }
      });
  }

  public static synchronized VolleySingletone getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new VolleySingletone(context);
    }
    return mInstance;
  }
  public interface  OnReusltRss{
    void successd();
    void failPars();
    void failInternet(VolleyError message);
  }
  public static void sendRequestRss(final OnReusltRss onResult) {
    if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

      StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.imanpour.ir/blog/feed/", new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
           RssParser rssParser=new RssParser(response, new RssParser.OnResult() {
            @Override
            public void onSuccessful() {
              G.adapter.setNewlist();
              if(onResult!=null){
                onResult.successd();
              }
            }
            @Override
            public void onFail() {
              if(onResult!=null){
                onResult.failPars();
              }

            }
          });
           rssParser.execute();
        }
      }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(onResult!=null) {
            onResult.failInternet(error);
          }
        }
      });
      VolleySingletone.getInstance(G.context).addToRequestQueue(stringRequest);
    }
  }

  public RequestQueue getRequestQueue() {
    if (mRequestQueue == null) {
      // getApplicationContext() is key, it keeps you from leaking the
      // Activity or BroadcastReceiver if someone passes one in.
      mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
    }
    return mRequestQueue;
  }

  public <T> void addToRequestQueue(Request<T> req) {
    getRequestQueue().add(req);
  }

  public ImageLoader getImageLoader() {
    return mImageLoader;
  }
}
