package com.hansjin.mukja_android.Login;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hansjin.mukja_android.R;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class LoginMainActivity extends Activity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    String SENDER_ID = "665375685332";


    static final String TAG = "L2C";

    GoogleCloudMessaging gcm;
    SharedPreferences prefs;

    List<NameValuePair> params;

    Context context;
    String regid;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        context = getApplicationContext();
        prefs = getSharedPreferences("TodayFood", 0);

        /*
        if(checkPlayServices()){
            new Register().execute();

        }
        */

        getKeyHash(getApplicationContext());
        if(!prefs.getString("REG_FROM","").isEmpty()){ // email
            //해당 id로 로그인 이미 되있는 상태이면
            //전환할 화면 선택.
            //Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
            /*
            Fragment fragment = new NonFacebookSigninFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
            */
        }else if(!prefs.getString("REG_ID", "").isEmpty()){ // reg_id
            //해당 id로 로그인 된 적은 없지만(혹은 로그아웃한 뒤), GCM Reg_id 최초 등록은 이미 한 상태라면
            //전환할 화면 선택.
            //Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_LONG).show();

            //Toast.makeText(getApplicationContext(),"reg2",Toast.LENGTH_SHORT).show();
            Fragment fragment = new LoginChoiceFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();

        }
        else if(checkPlayServices()){
            //해당 id로 로그인 된 적도 없고, GCM Reg_id 최초 등록도 하지않은 상태(앱 다운받고 실행이 완전 처음인 경우)라면
            //전환할 화면 선택.
            Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_LONG).show();
            new RegisterGCM().execute();
        }else{
            //안드로이드 기기 호환이 지원되지 않는 경우
            Toast.makeText(getApplicationContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
        }
    }

        public static final String getKeyHash(Context context) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                        PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                    Log.d("KeyHash: ", keyHash);
                    return keyHash;
                }

            } catch (PackageManager.NameNotFoundException e) {
               Log.d(TAG + "getKeyHash Error:%s", e.getMessage());
            } catch (NoSuchAlgorithmException e) {Log.d(TAG + "getKeyHash Error:%s", e.getMessage());
            }
            return "";
        }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private class RegisterGCM extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    regid = gcm.register(SENDER_ID);
                    Log.e("RegId",regid);

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_ID", regid);
                    edit.commit();

                }

                return  regid;

            } catch (IOException ex) {
                Log.e("Error", ex.getMessage());
                return "Fails";

            }
        }
        @Override
        protected void onPostExecute(String json) {
            //RegisterGCM 후 전환할 화면 선택
            //Toast.makeText(getApplicationContext(), "4", Toast.LENGTH_LONG).show();
            Fragment fragment = new LoginChoiceFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();

        }
    }



}

