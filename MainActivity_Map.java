package org.techtown.termproject1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity_Map extends AppCompatActivity {
    public static final String TAG = "ShowTime";
    String Location;
    TimeZone tz;
    EditText loctiontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        TextView timetv = findViewById(R.id.TimetextView);
        EditText location = findViewById(R.id.location);
        EditText showtext = findViewById(R.id.ShowTimeText);
        loctiontext = findViewById(R.id.ShowLocText);

        Button checkbtn = findViewById(R.id.Checkbutton);
        Button locationbtn = findViewById(R.id.locationbutton);
        Button Timebtn = findViewById(R.id.Timebutton);
        Button Backbtn = findViewById(R.id.Backbutton);

        //위험 권한을 자동으로 부여하기 위한 코드
        AndPermission.with(this)
                .runtime()
                .permission(Permission.ACCESS_FINE_LOCATION,
                            Permission.ACCESS_COARSE_LOCATION)
                .onGranted(new Action<List<String>>(){
                    @Override
                    public void onAction(List<String> permissions) {
                        showToast("허용된 권한 개수: " + permissions.size());
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        showToast("거부된 권한 개수: "+ permissions.size());
                    }
                })
                .start();

        //지역 선택하기
        Location = location.getText().toString();

        //선택한 지역의 시간 받아오기
        tz = TimeZone.getTimeZone(Location);

        //확인하기 버튼을 눌렀을 때 showtext에 시간 뿌리기
        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                df.setTimeZone(tz);

                timetv.setText( Location +"의 현재 시각은? ");
                showtext.setText(df.format(date));
            }
        });

        //나의 위치 버튼 클릭
        locationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLocationService();
            }
        });

        //나의 시간 버튼 클릭
        Timebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date now = new Date();
                DateFormat tf = new SimpleDateFormat("HH:mm:ss a");
                tf.setTimeZone(tz);

                timetv.setText( " 나의 시간 ");
                showtext.setText(tf.format(now));
            }
        });

        //돌아가기 버튼 클릭
        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void startLocationService(){
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //위치 관리자 객체 참조 및 이전에 확인했던 위치 정보 뿌리기
        try {
            android.location.Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null){
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String message = "최근 위치 -> Latitude : " + latitude + "\t " +
                        "Longitude: " + longitude;
                loctiontext.setText(message);

            }
            GPSListener gpsListener = new GPSListener();
            long minTime = 10000;
            float minDistance = 0;

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDistance,gpsListener);
            Toast.makeText(getApplicationContext(),"내 위치 확인 요청", Toast.LENGTH_SHORT).show();
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

class GPSListener implements LocationListener{
    public void onLocationChanged(Location location){
        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        String message = "내 위치 -> Latitude: " + latitude + "\t" +
                "Longitude : " + longitude;
        loctiontext.setText(message);
    }
    public void onProviderDisabled(String provider){}
    public void onProviderEnabled(String provider){}
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}