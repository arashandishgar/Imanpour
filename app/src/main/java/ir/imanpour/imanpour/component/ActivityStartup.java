package ir.imanpour.imanpour.component;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.core.AdapterFragmentSlpash;
import ir.imanpour.imanpour.core.G;
import ir.imanpour.imanpour.core.SharedPreferencesHelper;
import ir.imanpour.imanpour.listenner.OnPageChange;
import ir.imanpour.imanpour.listenner.SmsPermissionListenner;
import ir.imanpour.imanpour.listenner.StoragePermissionListenner;
import ir.imanpour.imanpour.widjet.PageIndicator;
import ir.imanpour.imanpour.widjet.ViewPagerCustom;

public class ActivityStartup extends AppCompatActivity {
  private ViewPagerCustom viewPager = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(SharedPreferencesHelper.getInteger(G.sharedPreferences,G.SharedPreferences_THEME_KEY,R.style.AppThemeLight));
    setContentView(R.layout.activity_startup);
    if (SharedPreferencesHelper.getBoolean(G.sharedPreferences,G.SharedPreferences_FIRST_RUN_KEY,true)) {
      run();
    } else if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(ActivityStartup.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    } else {
      finish();
      startActivity(new Intent(this, ActivityMain.class));
    }
  }

  private void run() {
    G.onPageChange = new OnPageChange() {
      @Override
      public void onPageChange(int pageNun) {
        viewPager.setCurrentItem(pageNun);
      }
    };
    viewPager = (ViewPagerCustom) findViewById(R.id.viewPager);
    viewPager.setCanSwipe(false);
    final PageIndicator pageIndicator = (PageIndicator) findViewById(R.id.indicator);
    pageIndicator.setIndicatorsCount(3);
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int i, float v, int i1) {
        pageIndicator.setCurrentPage(i);
        pageIndicator.setPercent(v);
      }

      @Override
      public void onPageSelected(int i) {

      }

      @Override
      public void onPageScrollStateChanged(int i) {

      }
    });
    G.smsPermissionListenner = new SmsPermissionListenner() {
      @Override
      public void onSmsPermissionRequest() {
        ActivityCompat.requestPermissions(ActivityStartup.this, new String[]{Manifest.permission.SEND_SMS}, 100);
      }
    };
    G.storagePermissionListenner = new StoragePermissionListenner() {
      @Override
      public void onStoragePermissionRequest() {
        ActivityCompat.requestPermissions(ActivityStartup.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
      }
    };
    AdapterFragmentSlpash adapter = new AdapterFragmentSlpash(this, R.id.viewPager);
    adapter.add(FragmentWelcome.class);
    adapter.add(FragmentGettingKnow.class);
    adapter.add(FragmentConfirmation.class);
  }

  private void show(int type) {
    String sms = "برنامه برای گرفتن کد تایید به منظور نهایی سازی ثبت نام به مجوز ارسال پیام نیاز دارد";
    String sotrage = "برنامه برای اجرا نیاز به مجوز دسترسی به حافظه نیاز دارد ";
    String message;
    if (type == 0) {
      message = sotrage;
    } else {
      message = sms;
    }
    new AlertDialog.Builder(this)
      .setTitle("نمی توانیم برنامه را اجرا کنیم")
      .setMessage(message)
      .setPositiveButton("باز کردن تنظیمات", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          Intent intent = new Intent();
          intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
          Uri uri = Uri.fromParts("package", getPackageName(), null);
          intent.setData(uri);
          startActivityForResult(intent, 50);
        }
      })
      .setCancelable(false)
      .create()
      .show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case 100:
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
          show(0);
        } else {
          FragmentGettingKnow.sendSMS(G.gettingKnownCode);
          viewPager.setCurrentItem(2, true);
        }
        break;
      case 101:
        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
          show(1);
        } else {
          startActivity(new Intent(this, ActivityMain.class));
          finish();
        }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
      if(viewPager!=null) {
        if (viewPager.getCurrentItem() == 1) {
          if (G.smsPermissionListenner != null)
            G.smsPermissionListenner.onSmsPermissionRequest();
        }
      }
      if ( viewPager != null) {
        return;
      }
    } else if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
      if (viewPager != null&&viewPager.getCurrentItem()==1) {
        FragmentGettingKnow.sendSMS(G.gettingKnownCode);
        viewPager.setCurrentItem(2, true);
        return;
      }
    }
    //return;
    if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(ActivityStartup.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    } else if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
      startActivity(new Intent(this, ActivityMain.class));
      finish();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onBackPressed() {
    if (viewPager != null && viewPager.getCurrentItem() == 2) {
      viewPager.setCurrentItem(1, true);
      return;
    }
    super.onBackPressed();
  }
}
