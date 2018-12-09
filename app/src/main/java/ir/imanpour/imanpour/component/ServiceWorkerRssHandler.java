package ir.imanpour.imanpour.component;

import android.support.annotation.NonNull;

import com.android.volley.VolleyError;

import androidx.work.Worker;
import ir.imanpour.imanpour.core.VolleySingletone;

public class ServiceWorkerRssHandler extends Worker {
  @NonNull
  @Override
  public Result doWork() {
    VolleySingletone.sendRequestRss(new VolleySingletone.OnReusltRss() {
      @Override
      public void successd() {
      }

      @Override
      public void failPars() {
      }

      @Override
      public void failInternet(VolleyError message) {
      }
    });
    return Result.SUCCESS;
  }
}
