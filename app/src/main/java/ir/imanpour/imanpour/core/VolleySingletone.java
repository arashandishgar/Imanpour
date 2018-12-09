package ir.imanpour.imanpour.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LruCache;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.component.ActivityMain;

import static ir.imanpour.imanpour.core.G.sqLiteDatabase;

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
  public static void sendNotification() {
    Notification notification = null;
    String CHANNEL_ID = "imanpour_channel_01";// The id of the channel.
    CharSequence name = "imanpour";// The user-visible name of the channel.
    Cursor cursorNotify = G.sqLiteDatabase.rawQuery("select * from Rss  where notify=0", null);
    final int countNotify = cursorNotify.getCount();
    cursorNotify.close();
    Cursor cursor1 = G.sqLiteDatabase.rawQuery("select * from Rss  where unread=0", null);
    final int countUnread = cursor1.getCount();
    if (countUnread >= 0) {
      final ArrayList<RssParser.Item> items = DataBaseHelper.convertCoursorToArray(cursor1);
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        G.notificationManager.createNotificationChannel(mChannel);
      }
      final RemoteViews remoteView = new RemoteViews(G.context.getPackageName(), R.layout.notification);
      remoteView.setTextViewText(R.id.txt_description, " شما " + items.size() + " پیام" + " خوانده نشده دارید  ");
      remoteView.setImageViewResource(R.id.img_notification, R.drawable.notification_layout);
      remoteView.setTextViewText(R.id.txt_counter, "" + items.size());
      Intent intent=new Intent(G.context, ActivityMain.class);
      intent.putExtra(G.CAN_CANCEL,true);

      PendingIntent pendingIntent = PendingIntent.getActivity(G.context, 100,intent, PendingIntent.FLAG_UPDATE_CURRENT);
      NotificationCompat.Builder builder = new NotificationCompat.Builder(G.context, CHANNEL_ID)
        .setSmallIcon(R.drawable.notification_small_icon)
        .setChannelId(CHANNEL_ID)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setContentIntent(pendingIntent)
        .setCustomContentView(remoteView);
      notification = builder.build();
    }
    if (countNotify == 0) {
     G.notificationManager.cancel(G.NOTIFY_ID);
      return;
    } else if (countNotify > 0) {
      G.notificationManager.cancel(G.NOTIFY_ID);
      G.notificationManager.notify(G.NOTIFY_ID, notification);
      sqLiteDatabase.execSQL("UPDATE Rss SET  notify=1 where  notify=0");
    }
    cursor1.close();
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
