package ir.imanpour.imanpour.component;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;
import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.core.G;
import ir.imanpour.imanpour.core.SharedPreferencesHelper;
import ir.imanpour.imanpour.core.VolleySingletone;
import ir.imanpour.imanpour.widjet.DividerDecoration;

import static ir.imanpour.imanpour.core.G.sqLiteDatabase;

public class ActivityMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, InternetConnectivityListener {
  RecyclerView recyclerView;
  private InternetAvailabilityChecker internetReciver;
  ConstraintLayout constraintLayout;
  private boolean onUnreadState = false;
  private boolean onPageHome = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(SharedPreferencesHelper.getInteger(G.sharedPreferences, G.SharedPreferences_THEME_NOACTIONBAR_KEY, R.style.AppThemeLightNoActionBar));
    setContentView(R.layout.activity_main);
    G.runDataBas();
    onPageHome = true;
    constraintLayout = findViewById(R.id.hidabe);
    constraintLayout.setVisibility(View.GONE);
    SharedPreferencesHelper.setBoolean(G.sharedPreferences, G.SharedPreferences_FIRST_RUN_KEY, false);
    Toolbar toolbar = findViewById(R.id.toolbar);
    if (SharedPreferencesHelper.getBoolean(G.sharedPreferences, G.SharedPreferences_FIRSTLOAD_KEY, true)) {
      DialogFirstLoad dialog = new DialogFirstLoad(this);
      dialog.show();
    } else {
      WorkManager.getInstance().enqueueUniquePeriodicWork("1", ExistingPeriodicWorkPolicy.KEEP, G.periodicWorkRequest);
    }
    setSupportActionBar(toolbar);
    internetReciver = InternetAvailabilityChecker.init(getApplicationContext());
    internetReciver.addInternetConnectivityListener(this);
    if (!isNetworkOnline()) {
      getSupportActionBar().setTitle("disconneted");
    }
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/imanpourir"));
        startActivity(telegram);
      }
    });

    final DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    recyclerView = findViewById(R.id.recyclerView);
    recyclerView.setLayoutManager(new LinearLayoutManager(ActivityMain.this));
    DividerDecoration divider = new DividerDecoration(this, Color.parseColor("#00574b"), DividerItemDecoration.VERTICAL);
    recyclerView.addItemDecoration(divider);
    recyclerView.setAdapter(G.adapter);
    Switch switch_mode = (Switch) navigationView.getMenu().findItem(R.id.mode).getActionView();
    switch_mode.setChecked((SharedPreferencesHelper.getInteger(G.sharedPreferences, G.SharedPreferences_THEME_NOACTIONBAR_KEY, R.style.AppThemeLightNoActionBar) == R.style.AppThemeDarkNoActionBar));
    switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        drawer.closeDrawer(GravityCompat.START);
        if (isChecked) {
          //save this state
          SharedPreferencesHelper.setInteger(G.sharedPreferences, G.SharedPreferences_THEME_NOACTIONBAR_KEY, R.style.AppThemeDarkNoActionBar);
          SharedPreferencesHelper.setInteger(G.sharedPreferences, G.SharedPreferences_THEME_KEY, R.style.AppThemeDark);
        } else {
          SharedPreferencesHelper.setInteger(G.sharedPreferences, G.SharedPreferences_THEME_NOACTIONBAR_KEY, R.style.AppThemeLightNoActionBar);
          SharedPreferencesHelper.setInteger(G.sharedPreferences, G.SharedPreferences_THEME_KEY, R.style.AppThemeLight);
        }
        finish();
        startActivity(new Intent(ActivityMain.this, ActivityMain.class));
      }
    });
    final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout2);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        VolleySingletone.sendRequestRss(new VolleySingletone.OnReusltRss() {
          @Override
          public void successd() {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(G.context, "صفحه با موفقیت به روز رسانی شد", Toast.LENGTH_SHORT).show();
            if (!onPageHome && !onUnreadState) {
              Cursor cursor = sqLiteDatabase.rawQuery("select * from Rss where like=1", null);
              boolean b = cursor.getCount() == 0;
              cursor.close();
              if (b) {
                constraintLayout.setVisibility(View.VISIBLE);
                G.adapter.reset();
              } else {
                constraintLayout.setVisibility(View.INVISIBLE);
                G.adapter.setNewlist(G.LIKE);
              }
            } else if (onUnreadState) {
              G.adapter.setNewlist(G.UNREAD);
            } else {
              G.adapter.setNewlist();
            }
          }

          @Override
          public void failPars() {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(G.context, "صفحه به روز رسانی نشد در صورت تمایل باز دوباره به روز کنید", Toast.LENGTH_SHORT).show();

          }

          @Override
          public void failInternet(VolleyError message) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(G.context, "اینترنت قطع است", Toast.LENGTH_SHORT).show();

          }
        });
      }
    });
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (!onPageHome) {
      G.adapter.reset();
      G.adapter.setNewlist();
      onPageHome = true;
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
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
   /* onPageHome = id == R.id.home;
    onUnreadState = id == R.id.unread;*/
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
      case R.id.about_us:
        Intent intent2 = new Intent(this, WebActivity.class);
        intent2.putExtra(G.WEB_INTENT, "https://www.imanpour.ir/blog/about-us/");
        startActivity(intent2);
        break;
      case R.id.photoshop:
        Intent intent1 = new Intent(this, WebActivity.class);
        intent1.putExtra(G.WEB_INTENT, "https://Zarinp.al/212038");
        startActivity(intent1);
        break;
      case R.id.home:
        onPageHome = true;
        onUnreadState = false;
        constraintLayout.setVisibility(View.GONE);
        G.adapter.reset();
        G.adapter.setNewlist();
        break;
      case R.id.like:
        onPageHome = false;
        onUnreadState = false;
        constraintLayout.setVisibility(View.INVISIBLE);
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
        onPageHome = false;
        onUnreadState = true;
        constraintLayout.setVisibility(View.INVISIBLE);
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

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  protected void onResume() {
    super.onResume();
    boolean canCancel = getIntent().getBooleanExtra(G.CAN_CANCEL, false);
    if (canCancel) {
      G.notificationManager.cancel(G.NOTIFY_ID);
      getIntent().putExtra(G.CAN_CANCEL, false);
    }
    if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
      startActivity(new Intent(this, ActivityStartup.class));
      finish();
    }
    if (!onPageHome && !onUnreadState) {
      Cursor cursor = sqLiteDatabase.rawQuery("select * from Rss where like=1", null);
      boolean b = cursor.getCount() == 0;
      cursor.close();
      if (b) {
        constraintLayout.setVisibility(View.VISIBLE);
        G.adapter.reset();
      } else {
        constraintLayout.setVisibility(View.INVISIBLE);
        G.adapter.setNewlist(G.LIKE);
      }
    } else if (onUnreadState) {
      Cursor cursor1 = G.sqLiteDatabase.rawQuery("select * from Rss where unread=0", null);
      if (cursor1.getCount() == 0) {
        constraintLayout.setVisibility(View.VISIBLE);
        G.adapter.reset();
      } else {
        constraintLayout.setVisibility(View.GONE);
        G.adapter.setNewlist(G.UNREAD);
      }
      cursor1.close();
    } else {
      G.adapter.setNewlist();
    }
   /* if (onUnreadState) {
      G.adapter.reset();
      Cursor cursor1 = G.sqLiteDatabase.rawQuery("select * from Rss where unread=0", null);
      if (cursor1.getCount() == 0) {
        constraintLayout.setVisibility(View.VISIBLE);
      } else {
        constraintLayout.setVisibility(View.GONE);
        G.adapter.setNewlist(G.UNREAD);
      }
      cursor1.close();
    }*/
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
