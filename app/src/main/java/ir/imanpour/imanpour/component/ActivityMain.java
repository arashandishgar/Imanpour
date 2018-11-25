package ir.imanpour.imanpour.component;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;
import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.core.G;
import ir.imanpour.imanpour.widjet.DividerDecoration;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InternetConnectivityListener {
  RecyclerView recyclerView;
  AlertDialog alertDialog = null;
  private InternetAvailabilityChecker internetReciver;
  ConstraintLayout constraintLayout;
  private boolean onUnreadState=false;
  private boolean onPageHome=false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    runDb();
    constraintLayout = (ConstraintLayout) findViewById(R.id.hidabe);
    constraintLayout.setVisibility(View.GONE);
    WorkManager.getInstance().enqueueUniquePeriodicWork("1", ExistingPeriodicWorkPolicy.KEEP, G.periodicWorkRequest);
    SharedPreferences.Editor editor = G.sharedPreferences.edit();
    editor.putBoolean(G.firstRun, false);
    editor.commit();
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(R.string.app_name);
    internetReciver = InternetAvailabilityChecker.init(getApplicationContext());
    internetReciver.addInternetConnectivityListener(this);
    if (!isNetworkOnline()) {
      getSupportActionBar().setTitle("disconneted");
    }
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
          call();
        } else {
          ActivityCompat.requestPermissions(ActivityMain.this, new String[]{Manifest.permission.CALL_PHONE}, 100);
        }
      }
    });

    final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    recyclerView = findViewById(R.id.recyclerView);
    recyclerView.setAdapter(G.adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(ActivityMain.this));
   /* Drawable drawable= ContextCompat.getDrawable(ActivityMain.this, R.drawable.divider);
    ((DividerItemDecoration) divider).setDrawable(drawable);*/
    DividerDecoration divider = new DividerDecoration(this, Color.parseColor("#00574b"), DividerItemDecoration.VERTICAL);
    recyclerView.addItemDecoration(divider);
   /* Switch switch_mode = (Switch) navigationView.getMenu().findItem(R.id.switch_mode).getActionView();
    switch_mode.setChecked((G.THEME == R.style.AppThemeDark));
    switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        drawer.closeDrawer(GravityCompat.START);
        if (isChecked) {
          //save this state
          G.THEME = R.style.AppThemeDark;
        } else {
          G.THEME = R.style.AppThemeBase_NoActionBar;
        }
        SharedPreferences.Editor editor = G.sharedPreferences.edit();
        editor.putInt(G.themeP, G.THEME);
        editor.apply();
        finish();
        startActivity(getIntent());
      }
    });*/

  }
  public void runDb(){
    if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
      G.runDataBas();
    }else {
      this.startActivity(new Intent(this,ActivityStartup.class));
      finish();
    }
  }
  private void call() {
    Intent callIntent = new Intent(Intent.ACTION_CALL);
    callIntent.setData(Uri.parse("tel:09211953056"));
    startActivity(callIntent);
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      call();
    } else {

      alertDialog = new AlertDialog.Builder(ActivityMain.this)
        .setCancelable(true)
        .setPositiveButton("رفتن به تنظیمات", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 50);
          }
        })
        .setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            alertDialog.dismiss();
          }
        })
        .setTitle("توجه برنامه برای تماس با نیاز به دسترسی به مجوز تماس دارد بارفتن به تنظیمات آن را فعال کنید")
        .create();
      alertDialog.show();
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
      call();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if(!onPageHome){
      G.adapter.reset();
      G.adapter.setNewlist();
      return;
    }
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    onPageHome= id==R.id.home;
    onUnreadState = id == R.id.unread;
    switch (id) {
      case R.id.instagram:
        String url = "https://www.instagram.com/imanpour.ir/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
        break;
      case R.id.contact_with_us:
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(G.WEB_INTENT, "https://www.imanpour.ir/blog/contact-us/");
        startActivity(intent);
        break;
      case R.id.photoshop:
        Intent intent1 = new Intent(this, WebActivity.class);
        intent1.putExtra(G.WEB_INTENT, "https://Zarinp.al/212038");
        startActivity(intent1);
        break;
      case R.id.home:
        constraintLayout.setVisibility(View.GONE);
        G.adapter.reset();
        G.adapter.setNewlist();
        break;
      case R.id.like:
        Cursor cursor = G.sqLiteDatabase.rawQuery("select * from Rss where like=1", null);
        G.adapter.reset();
        if (cursor.getCount() == 0) {
          constraintLayout.setVisibility(View.VISIBLE);
        } else {
          G.adapter.setNewlist(G.LIKE);
        }
        cursor.close();
        break;
      case R.id.unread:
        G.adapter.reset();
         Cursor cursor1 = G.sqLiteDatabase.rawQuery("select * from Rss where unread=0", null);
        if (cursor1.getCount() == 0) {
          constraintLayout.setVisibility(View.VISIBLE);
        } else {
          G.adapter.setNewlist(G.UNREAD);
        }
        cursor1.close();
        break;
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    runDb();
    if(onUnreadState){
        G.adapter.reset();
        Cursor cursor1 = G.sqLiteDatabase.rawQuery("select * from Rss where unread=0", null);
        if (cursor1.getCount() == 0) {
          constraintLayout.setVisibility(View.VISIBLE);
        } else {
          G.adapter.setNewlist(G.UNREAD);
        }
        cursor1.close();
    }
  }

  @Override
  public void onInternetConnectivityChanged(boolean isConnected) {
    if (!isNetworkOnline()) {
      getSupportActionBar().setTitle("disconncted");
    } else {
      getSupportActionBar().setTitle(R.string.app_name);
    }
  }

  public boolean isNetworkOnline() {
    boolean status = false;
    try {
      ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = cm.getNetworkInfo(0);
      if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
        status = true;
      } else {
        netInfo = cm.getNetworkInfo(1);
        if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
          status = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return status;
  }
}
