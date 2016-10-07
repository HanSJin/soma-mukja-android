package com.hansjin.mukja_android.Splash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hansjin.mukja_android.Map.MapActivity;
import com.hansjin.mukja_android.Model.Food;
import com.hansjin.mukja_android.R;
import com.hansjin.mukja_android.Utils.Connections.CSConnection;
import com.hansjin.mukja_android.Utils.Connections.ServiceGenerator;
import com.hansjin.mukja_android.Utils.Constants.Constants;
import com.hansjin.mukja_android.Utils.Loadings.LoadingUtil;
import com.hansjin.mukja_android.pio.ResultActivity_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@EActivity(R.layout.activity_activity)
public class SplashActivity extends AppCompatActivity {
    SplashActivity activity;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    SplashAdapter adapter;

    public static double lat = 0.0;
    public static double lon = 0.0;

    @ViewById
    SwipeRefreshLayout pullToRefresh;

    @ViewById
    Toolbar cs_toolbar;

    @ViewById
    public LinearLayout indicator;

    //currrent location start
    private LocationManager locationManager = null; // 위치 정보 프로바이더
    private LocationListener locationListener = null; //위치 정보가 업데이트시 동작

    private static final String TAG = "debug";
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    //currrent location end

    @AfterViews
    void afterBindingView() {
        this.activity = this;

        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("음식리스트");

        if (recyclerView == null) {
            recyclerView = (RecyclerView) findViewById(R.id.exercise_recycler_view);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
        }

        if (adapter == null) {
            adapter = new SplashAdapter(new SplashAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Food item = adapter.getItem(position) ;

                    Intent intent = new Intent(SplashActivity.this, ResultActivity_.class);
                    Log.i("test","보냄 : "+item.get_id());
                    intent.putExtra("food_id",item.get_id());
                    intent.putExtra("food_name",item.getName());
                    startActivity(intent);
                }
            }, this);
        }
        recyclerView.setAdapter(adapter);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                connectTestCall();
            }
        });

        connectTestCall();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //GPS_PROVIDER: GPS를 통해 위치를 알려줌
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //NETWORK_PROVIDER: WI-FI 네트워크나 통신사의 기지국 정보를 통해 위치를 알려줌
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(isGPSEnabled && isNetworkEnabled){
            Toast.makeText(getApplicationContext(), "내 위치정보를 가져오는 중입니다.", Toast.LENGTH_SHORT).show();
            locationListener = new MyLocationListener();

            //선택된 프로바이더를 사용해 위치정보를 업데이트
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);

        }else{
            alertbox("gps 상태!!", "당신의 gps 상태 : off");
        }
    }
    protected void alertbox(String title, String mymessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("your device's gps is disable")
                .setCancelable(false)
                .setTitle("**gps status**")
                .setPositiveButton("gps on", new DialogInterface.OnClickListener() {

                    //  폰 위치 설정 페이지로 넘어감
                    public void onClick(DialogInterface dialog, int id) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //현재 위치 정보를 받기위해 선택한 프로바이더에 위치 업데이터 요청! requestLocationUpdates()메소드를 사용함.
    private class MyLocationListener implements LocationListener {

        @Override
        //LocationListener을 이용해서 위치정보가 업데이트 되었을때 동작 구현
        public void onLocationChanged(Location loc) {
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            //좌표 정보 얻어 토스트메세지 출력
            Toast.makeText(getBaseContext(), "Location changed : Lat" + lat +
                    "Lng: " + lon, Toast.LENGTH_SHORT).show();

            // 도시명 구하기
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try{
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if(addresses.size()>0)
                    System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }catch(IOException e){
                e.printStackTrace();
            }
            String s = lon + "n" + lat + "nn당신의 현재 도시명 : " + cityName;
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generatedchmethod stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

    }
    void refresh() {
        adapter.clear();
        adapter.notifyDataSetChanged();
        pullToRefresh.setRefreshing(false);
    }

    @UiThread
    void uiThread(List<Food> response) {
        for (Food food : response) {
            adapter.addData(food);
        }
        adapter.notifyDataSetChanged();
    }

    void connectTestCall() {
        LoadingUtil.startLoading(indicator);
        CSConnection conn = ServiceGenerator.createService(CSConnection.class);
        conn.getAllFood()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Food>>() {
                    @Override
                    public final void onCompleted() {
                        LoadingUtil.stopLoading(indicator);
                    }
                    @Override
                    public final void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public final void onNext(List<Food> response) {
                        if (response != null) {
                            uiThread(response);
                        } else {
                            Toast.makeText(getApplicationContext(), Constants.ERROR_MSG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
