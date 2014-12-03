/* 졸업 프로젝트
 * 무시무시
 * 
 * 환자의 위치를 알아오는 클래스.
 */
package com.project;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
 
public class GpsInfo extends Service implements LocationListener {
  
    private final Context mContext;
  
    boolean isGPSEnabled = false;   // 현재 GPS 사용유무
    boolean isNetworkEnabled = false;    // 네트워크 사용유무 
    boolean isGetLocation = false;  // GPS 상태값
  
    Location location; 
    double lat; // 위도 
    double lon; // 경도
    
    protected LocationManager locationManager; 
  
    // 최소 GPS 정보 업데이트 거리 10미터 
 //   private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; 
  
    // 최소 GPS 정보 업데이트 시간 밀리세컨이므로 1분
 //   private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1;   
    
    public GpsInfo(Context context) { //생성자 및 위치 알아오기.
        this.mContext = context;
        getLocation();
    }
    
    /*** GPS 나 wife 정보가 켜져있는지 확인.***/
    public boolean isGetLocation() {
        return this.isGetLocation;
    }
  
    public Location getLocation() {
        try {
            locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
           
            // GPS 정보 가져오기 
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  
            // 현재 네트워크 상태 값 알아오기 
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
  
            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS 와 네트워크사용이 가능하지 않을때 소스 구현
            	System.out.println("gps 및 network를 사용 가능하지 않습니다.");
            } else {
                this.isGetLocation = true;
                // 네트워크 정보로 부터 위치값 가져오기 
                if (isNetworkEnabled) {
   //위치 업데이트 - 환자 메인에서 업데이트 하기 때문에 여기에선 하지 않음.
     //               locationManager.requestLocationUpdates(
       //                     LocationManager.NETWORK_PROVIDER,
         //                   MIN_TIME_BW_UPDATES,
           //                 MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // 위도 경도 저장 
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
                // gps정보로 부터 위치값 가져오기
                if (isGPSEnabled) {
                    if (location == null) {
//                        locationManager.requestLocationUpdates(
//                                LocationManager.GPS_PROVIDER,
//                                MIN_TIME_BW_UPDATES,
//                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                            	//위도 경도 저장
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }  
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
      
	/**
     * GPS 종료 
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GpsInfo.this);
        }       
    }
      
    /**
     * 위도값을 가져옵니다. 
     * */
    public double getLatitude(){
        if(location != null){
            lat = location.getLatitude();
        }
        return lat;
    }
      
    /**
     * 경도값을 가져옵니다. 
     * */
    public double getLongitude(){
        if(location != null){
            lon = location.getLongitude();
        }
        return lon;
    }
     
      
    /**
     * GPS 정보를 가져오지 못했을때 
     * 설정값으로 갈지 물어보는 alert 창
     * */
    public void showSettingsAlert(){
        new AlertDialog.Builder(mContext)
		.setTitle("GPS 사용 유무 설정")
		.setMessage("GPS 셋팅이 되지 않았을수도 있습니다. \n 설정창으로 가서 확인하세요")
		.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        })
		.show();
    }
  
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
         
    }
 
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
         
    }
 
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
         
    }
 
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
         
    }
}