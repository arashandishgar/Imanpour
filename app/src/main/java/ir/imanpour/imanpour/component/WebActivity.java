package ir.imanpour.imanpour.component;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.core.G;

public class WebActivity extends AppCompatActivity implements InternetConnectivityListener {

  private WebView webview;
  String url;
  private InternetAvailabilityChecker internetReciver;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.web_activity);
    if(!isNetworkOnline()){
      getSupportActionBar().setTitle("disconnect");
    }
    internetReciver = InternetAvailabilityChecker.init(getApplicationContext());
    internetReciver.addInternetConnectivityListener(this);
    Intent intent = getIntent();
    Bundle bundle = intent.getExtras();
    url = bundle.getString(G.WEB_INTENT);

    webview = (WebView) findViewById(R.id.webView);
    webview.setWebViewClient(new WebViewClient());
    final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swp);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        webview.loadUrl(url);
      }
    });
    webview.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int progress) {
        if (progress < 100 ) {
          swipeRefreshLayout.setRefreshing(true);
        }

        if (progress == 100) {
          swipeRefreshLayout.setRefreshing(false);
        }
      }
    });
    webview.getSettings().setJavaScriptEnabled(true);
    webview.getSettings().setDomStorageEnabled(true);
    webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
    webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    webview.loadUrl(url);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    webview.getSettings().setBuiltInZoomControls(true);
    webview.getSettings().setDisplayZoomControls(false);
  }

  @Override
  public void onBackPressed() {
    if(webview.canGoBack()) {
      webview.goBack();
      return;
    }
    super.onBackPressed();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    startActivity(new Intent(this,ActivityMain.class));
    return true;
  }

  @Override
  public void onInternetConnectivityChanged(boolean isConnected) {
    if(!isNetworkOnline()){
      getSupportActionBar().setTitle("disconncted");
    }else {
      getSupportActionBar().setTitle(R.string.app_name);
      webview.loadUrl(url);
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
