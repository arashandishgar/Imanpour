package ir.imanpour.imanpour.core;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import ir.imanpour.imanpour.component.ServiceWorkerRssHandler;
import ir.imanpour.imanpour.listenner.OnPageChange;
import ir.imanpour.imanpour.listenner.SmsPermissionListenner;
import ir.imanpour.imanpour.listenner.StoragePermissionListenner;


public class G extends Application {
  public static final String WEB_INTENT ="contact";
  public static final int LIKE = 1;
  public static final int UNREAD =2 ;
  public static final String SharedPreferences_THEME_NOACTIONBAR_KEY ="theme no actionBar" ;
  public static final String SharedPreferences_THEME_KEY ="theme" ;
  public static String THEME = "THEME";
  @SuppressLint("StaticFieldLeak")
  public static Context context;
  public static String APP_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imanpour";
  public static android.os.Handler handler;
  public static OnPageChange onPageChange;
  public static SQLiteDatabase sqLiteDatabase;
  public static SmsPermissionListenner smsPermissionListenner;
  public static StoragePermissionListenner storagePermissionListenner;
  public static int gettingKnownCode = 0;
  public static String DATABASE_DIR = APP_DIR + "/rss.sqlite";
  public static SharedPreferences sharedPreferences;
  public static String SharedPreferences_FIRST_RUN_KEY = "first-Run";
  public static AdapterRecyclerView adapter;
  public static String tag = "1";
  public static PeriodicWorkRequest periodicWorkRequest;

  @Override
  public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
    handler = new Handler();
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SharedPreferences.Editor editor = G.sharedPreferences.edit();
    editor.commit();
    VolleySingletone.getInstance(context);
    runDataBas();
    Constraints constraints = new Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED).build();
    periodicWorkRequest = new PeriodicWorkRequest.Builder(ServiceWorkerRssHandler.class, 30, TimeUnit.MINUTES)
      .setConstraints(constraints)
      .addTag(tag)
      .build();
  }

  public static void runDataBas() {
    if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
      new File(G.APP_DIR).mkdir();
      sqLiteDatabase = new DataBaseHelper(G.context, DATABASE_DIR).getWritableDatabase();
      adapter = new AdapterRecyclerView();
    }
  }
}
