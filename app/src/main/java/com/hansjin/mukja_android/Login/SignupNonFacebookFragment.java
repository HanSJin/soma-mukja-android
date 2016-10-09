package com.hansjin.mukja_android.Login;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.hansjin.mukja_android.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SignupNonFacebookFragment extends Fragment {
    SharedPreferences prefs;
    EditText ET_email, ET_nickname, ET_pw, ET_pw2;
    Button BT_signup;
    List<NameValuePair> params;
    ProgressDialog progress;

    String name_temp;
    String mobno_temp;



    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_non_facebook, container, false);

        prefs = getActivity().getSharedPreferences("Chat", 0);

        ET_email = (EditText)view.findViewById(R.id.ET_email);
        ET_nickname = (EditText)view.findViewById(R.id.ET_nickname);
        ET_pw = (EditText)view.findViewById(R.id.ET_pw);
        ET_pw2 = (EditText)view.findViewById(R.id.ET_pw2);

        BT_signup = (Button)view.findViewById(R.id.BT_signup);

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Registering ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);

        BT_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempEmail = ET_email.getText().toString();
                String tempNickname = ET_nickname.getText().toString();
                String tempPw = ET_pw.getText().toString();
                String tempPw2 = ET_pw2.getText().toString();

                if(tempEmail.equals("") || tempNickname.equals("") || tempPw.equals("") || tempPw2.equals("")){
                    Toast toast = Toast.makeText(getActivity(), "Write your information all", Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, 300);
                    toast.show();
                    return;
                }

                progress.show();
                Log.d("Asfd1","Asfd1");
                SharedPreferences.Editor edit = prefs.edit();
                Log.d("Asfd2","Asfd1");
                edit.putString("REG_FROM", tempEmail);
                Log.d("Asfd3", "Asfd1");
                edit.putString("FROM_NAME", tempNickname);
                if(tempPw.equals(tempPw2)) {
                    edit.putString("FROM_PASSWORD", tempPw);
                }else{
                    Toast toast = Toast.makeText(getActivity(), "incorrect 1st PW and 2nd PW", Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, 300);
                    toast.show();
                    return;
                }
                edit.commit();
                new Signup().execute();
            }
        });

        return view;
    }
    private class Signup extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            Log.d("Asfd7","Asfd1");
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", ET_email.getText().toString()));
            try {
                //params.add(new BasicNameValuePair("name", ET_name.getText().toString()));
                params.add(new BasicNameValuePair("nickname", URLEncoder.encode(ET_nickname.getText().toString(), "utf-8")));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            params.add(new BasicNameValuePair("password", ET_pw.getText().toString()));
            params.add((new BasicNameValuePair("reg_id",prefs.getString("REG_ID",""))));
            Log.d("Asfd6", "reg_id : " + prefs.getString("REG_ID",""));;
            /*
            //앱 설치했던 시각 start
            long first_install_moment = 0;
            try {
                PackageManager pm = getActivity().getPackageManager();
                // ApplicationInfo appInfo = pm.getApplicationInfo("app.package.name", 0);
                ApplicationInfo appInfo = pm.getApplicationInfo("learn2crack.chat", 0);
                String appFile = appInfo.sourceDir;
                first_install_moment = new File(appFile).lastModified(); //Epoch Time
                Log.i("asd", "zxc2" + getData(first_install_moment));
            }catch(Exception ex){
                ex.printStackTrace();
            }
            //앱 설치했던 시각  finish
            Log.d("Asfd8","Asfd1");
            //앱 시작 시각 start
            NonFacebookSigninFragment.connect_start_moment = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            //앱 시작 시각 finish
            params.add((new BasicNameValuePair("connect_start_moment",NonFacebookSigninFragment.connect_start_moment)));
            params.add((new BasicNameValuePair("first_install_moment",String.valueOf(getData(first_install_moment)))));
            Log.d("Asfd9", "Asfd1");
            Log.i("makejin", "params : " + params);
            */

            JSONObject jObj = json.getJSONFromUrl("http://52.198.134.66:8080/signup",params);
            return jObj;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            progress.dismiss();
            try {
                String res = json.getString("response");
                if(res.equals("Sucessfully Registered")) {
                    Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();
                    /*
                    Fragment reg = new UserFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, reg);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.addToBackStack(null);
                    ft.commit();
                    */
                }else{
                    Toast.makeText(getActivity(), res, Toast.LENGTH_LONG).show();
                    /*
                    Toast toast = Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT);
                    int offsetX = 0;
                    int offsetY = 0;
                    toast.setGravity(Gravity.CENTER, offsetX, 300);
                    toast.show();
                    */
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    private static String getData(long datetime) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);
        String strDate = formatter.format(calendar.getTime());

        return strDate;
    }

}