package ir.imanpour.imanpour.component;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.core.G;

import static ir.imanpour.imanpour.component.FragmentGettingKnow.sendSMS;

public class FragmentConfirmation extends Fragment {
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragmet_coformation, container, false);
    final EditText edt_code = (EditText) view.findViewById(R.id.edt_Code);
    view.findViewById(R.id.btn_enter).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean isRight = false;
        int code = 0;
        try {
          code = Integer.parseInt(edt_code.getText().toString());
        } catch (Exception e) {
          Toast.makeText(G.context, "کد را اشتباه زدی", Toast.LENGTH_SHORT).show();
        }
        switch (code) {
          case 723: ;case 872: ;case 718: ;case 429: ;case 423: ;case 493: ;case 293: ;case 983: ;case 463: ;case 433: ;case 401: ;case 340: ;case 280: ;case 728: ;case 871: ;case 861: ;
            isRight = true;
        }
        if (isRight) {
          if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            G.storagePermissionListenner.onStoragePermissionRequest();
          } else {
            startActivity(new Intent(getActivity(), ActivityMain.class));
            getActivity().finish();
          }
        } else {
          Toast.makeText(G.context, "کد را اشتباه زدی", Toast.LENGTH_SHORT).show();
        }
      }
    });
    view.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        G.onPageChange.onPageChange(1);
      }
    });
    view.findViewById(R.id.btn_resend).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
          G.smsPermissionListenner.onSmsPermissionRequest();
        } else {
          sendSMS(""+G.gettingKnownCode);
        }
      }
    });
    return view;
  }
}
