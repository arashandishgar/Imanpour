package ir.imanpour.imanpour.component;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.VolleyError;

import androidx.work.Worker;
import ir.imanpour.imanpour.core.VolleySingletone;

public class ServiceWorkerRssHandler extends Worker {
  @NonNull
  @Override
  public Result doWork() {
        Log.i("Test","send");
    VolleySingletone.sendRequestRss(new VolleySingletone.OnReusltRss() {
      @Override
      public void successd() {
        Log.i("Test","ok");
      }

      @Override
      public void failPars() {
        Log.i("Test","pars");

      }

      @Override
      public void failInternet(VolleyError message) {

        Log.i("Test","Intenet :"+message);

      }
    });
    return Result.SUCCESS;
  }
}
