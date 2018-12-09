package ir.imanpour.imanpour.component;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;
import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.core.G;
import ir.imanpour.imanpour.core.SharedPreferencesHelper;
import ir.imanpour.imanpour.core.VolleySingletone;

public class DialogFirstLoad extends Dialog implements InternetConnectivityListener {
  public DialogFirstLoad(Context context) {

    super(context,R.style.Dialog);
  }

  private ProgressBar progressBar = findViewById(R.id.progressBar);
  private TextView textView = findViewById(R.id.txt_firstLoad);
  private String wait = "loading";
  private String fial = "Disconnected please check net and try again";


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_first_load);
    setCancelable(false);
    progressBar = findViewById(R.id.progressBar);
    textView = findViewById(R.id.txt_firstLoad);
    sendRequest();
  }


  private void tryAgain(){
    progressBar.setIndeterminate(false);
    textView.setText(fial);
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendRequest();
      }
    });
  }
  private void sendRequest() {
    progressBar.setIndeterminate(true);
    textView.setText(wait);
    VolleySingletone.sendRequestRss(new VolleySingletone.OnReusltRss() {
      @Override
      public void successd() {
        SharedPreferencesHelper.setBoolean(G.sharedPreferences, G.SharedPreferences_FIRSTLOAD_KEY, false);
        WorkManager.getInstance().enqueueUniquePeriodicWork("1", ExistingPeriodicWorkPolicy.KEEP, G.periodicWorkRequest);
        dismiss();
      }

      @Override
      public void failPars() {
        tryAgain();
      }

      @Override
      public void failInternet(VolleyError message) {
        tryAgain();
      }
    });
  }
  public boolean isNetworkOnline() {
    boolean status = false;
    try {
      ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
  @Override
  public void onInternetConnectivityChanged(boolean isConnected) {
    if(isNetworkOnline()){
      sendRequest();
    }
  }
}
