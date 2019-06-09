package org.techtown.soolzipinjejuu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import net.daum.android.map.MapView;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.techtown.soolzipinjejuu.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener {

    private ListView noticeListView;
    private NoticeListAdapter adapter;
    private List<Notice> noticeList;
    private static final String DAUM_API_KEY = "8c925eb3eedcdc8163d8c99950dc681d";
    private Button gpsButton;
    private TextView gpsTextView;
    private LocationManager gpsLocationManager;
    private LocationListener gpsLocationListener;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //시작

        MapView mapView = new MapView(this);
        mapView.setDaumMapApiKey(DAUM_API_KEY);
        ViewGroup viewGroup = findViewById(R.id.fl_maps);
        viewGroup.addView(mapView);

        mapView.setMapViewEventListener((MapView.MapViewEventListener) this);

        //gps 시작

        gpsButton = (Button) findViewById(R.id.buttons);
        gpsTextView = (TextView) findViewById(R.id.textViews);

        gpsLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsLocationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                gpsTextView.append("\n " +location.getLatitude() + " "+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
            return;
        }else{
            configureButton();
        }


        //gps 끝

        noticeListView = (ListView) findViewById(R.id.noticeListView);
        noticeList = new ArrayList<Notice>();
        adapter = new NoticeListAdapter(getApplicationContext(), noticeList);
        noticeListView.setAdapter(adapter);

//        final Button SoolZipButton = (Button) findViewById(R.id.SoolZipButtonn);
        final LinearLayout notice = (LinearLayout) findViewById(R.id.notice);

//        SoolZipButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                notice.setVisibility(View.GONE);
//                SoolZipButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment, new SoolZipFragment());
//                fragmentTransaction.commit();
//            }
//        });

        new BackgroundTask().execute();
    }

    // gps함수 시작
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();

                    return;
        }
    }

    private void configureButton() {
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpsLocationManager.requestLocationUpdates("gps", 5000, 0, gpsLocationListener);
            }
        });

    }

    //gps함수 끝

    @Override
    public void onMapViewInitialized(MapView mapView) {

        // Move
        MapPoint myPoint = MapPoint.mapPointWithGeoCoord(33.499556, 126.531263);
        mapView.setMapCenterPoint(myPoint, true);

        // Make marker
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(myPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        // add marker
        mapView.addPOIItem(marker);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    class BackgroundTask extends AsyncTask<Void, Void, String>{

        String target;

        @Override
        protected void onPreExecute() {
            target = "http://lmh5920.cafe24.com/NoticeList.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) !=null){
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result) {
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String noticeContent, noticeName, noticeDate;
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    noticeContent = object.getString("noticeContent");
                    noticeName = object.getString("noticeName");
                    noticeDate = object.getString("noticeDate");
                    Notice notice = new Notice(noticeContent, noticeName, noticeDate);
                    noticeList.add(notice);
                    count++;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private long lastTimeBackPressed;

    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500){
            finish();
            return;
        }
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 눌러 종료합니다.", Toast.LENGTH_SHORT).show();
        lastTimeBackPressed = System.currentTimeMillis();
    }
}