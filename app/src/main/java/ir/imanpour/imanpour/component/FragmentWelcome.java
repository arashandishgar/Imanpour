package ir.imanpour.imanpour.component;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.core.G;

public class FragmentWelcome extends Fragment {
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    G.handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if(G.gettingKnownCode==0)
        G.onPageChange.onPageChange(1);
      }
    },5000);
    return inflater.inflate(R.layout.fragment_wellcome,container,false);
  }
}
