package ir.imanpour.imanpour.component;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import ir.imanpour.imanpour.R;
import ir.imanpour.imanpour.core.G;

public class FragmentGettingKnow extends Fragment {
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view= inflater.inflate(R.layout.fragment_gettingknown,container,false);
    RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.radioGroup);
    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
          case R.id.rdt_1:
            onFinsh(1);
            ;break;
          case R.id.rdt_2:
            onFinsh(2);
            ;break;
          case R.id.rdt_3:
            onFinsh(3);
            ;break;
          case R.id.rdt_4:
            onFinsh(4);
            ;break;
          case R.id.rdt_5:
            onFinsh(5);
            ;break;
          case R.id.rdt_6:
            onFinsh(6);
            ;break;
          case R.id.rdt_7:
            onFinsh(7);
            ;break;
          case R.id.rdt_8:
            onFinsh(8);
            ;break;
          case R.id.rdt_9:
            if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
              G.storagePermissionListenner.onStoragePermissionRequest();
            } else {
              startActivity(new Intent(getActivity(), ActivityMain.class));
              getActivity().finish();
            }
            ;break;
        }
      }
    });
    return view;
  }
  private void onFinsh(int value){
    G.gettingKnownCode=value;
    if (ActivityCompat.checkSelfPermission(G.context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
      G.smsPermissionListenner.onSmsPermissionRequest();
    } else {
      sendSMS(""+G.gettingKnownCode);
      G.onPageChange.onPageChange(2);
    }
  }
  public static void sendSMS( Object msg) {
    SmsManager smsManager = SmsManager.getDefault();
    Toast.makeText(G.context,"send sms",Toast.LENGTH_SHORT).show();
    //smsManager.sendTextMessage("50002060757500", null, ""+msg, null, null);
  }

}
