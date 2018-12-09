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
          case R.id.rdt_basij:
            onFinsh(820);
            ;break;
          case R.id.rdt_mahdaviyat:
            onFinsh(193);
            ;break;
          case R.id.rdt_studierendenvertretung:
            onFinsh(281);
            ;break;
          case R.id.rdt_statistik:
            onFinsh(123);
            ;break;
          case R.id.rdt_electric:
            onFinsh(423);
            ;break;
          case R.id.rdt_mechanic:
            onFinsh(248);
            ;break;
          case R.id.rdt_health:
            onFinsh(712);
            ;break;
          case R.id.rdt_math:
            onFinsh(401);
            break;
          case R.id.rdt_physic:
            onFinsh(382);
            break;
          case R.id.rdt_computer:
            onFinsh(125);
            break;
          case R.id.rdt_sinceComputer:
            onFinsh(802);
            break;
          case R.id.rdt_enviroment:
            onFinsh(221);
            break;
          case R.id.rdt_theather:
            onFinsh(100);
            ;break;
          case R.id.rdt_rain:
            onFinsh(124);
            ;break;
          case R.id.rdt_music:
            onFinsh(870);
            ;break;
          case R.id.rdt_officer:
            onFinsh(667);
            ;break;
          case R.id.rdt_other:
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
    smsManager.sendTextMessage("500054107756", null, ""+msg, null, null);
  }

}
