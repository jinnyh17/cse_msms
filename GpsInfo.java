/* ���� ������Ʈ
 * ���ù���
 * 
 * ȯ���� ��ġ�� �˾ƿ��� Ŭ����.
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
  
    boolean isGPSEnabled = false;   // ���� GPS �������
    boolean isNetworkEnabled = false;    // ��Ʈ��ũ ������� 
    boolean isGetLocation = false;  // GPS ���°�
  
    Location location; 
    double lat; // ���� 
    double lon; // �浵
    
    protected LocationManager locationManager; 
  
    // �ּ� GPS ���� ������Ʈ �Ÿ� 10���� 
 //   private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; 
  
    // �ּ� GPS ���� ������Ʈ �ð� �и������̹Ƿ� 1��
 //   private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1;   
    
    public GpsInfo(Context context) { //������ �� ��ġ �˾ƿ���.
        this.mContext = context;
        getLocation();
    }
    
    /*** GPS �� wife ������ �����ִ��� Ȯ��.***/
    public boolean isGetLocation() {
        return this.isGetLocation;
    }
  
    public Location getLocation() {
        try {
            locationManager = (LocationManager)mContext.getSystemService(LOCATION_SERVICE);
           
            // GPS ���� �������� 
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  
            // ���� ��Ʈ��ũ ���� �� �˾ƿ��� 
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
  
            if (!isGPSEnabled && !isNetworkEnabled) {
                // GPS �� ��Ʈ��ũ����� �������� ������ �ҽ� ����
            	System.out.println("gps �� network�� ��� �������� �ʽ��ϴ�.");
            } else {
                this.isGetLocation = true;
                // ��Ʈ��ũ ������ ���� ��ġ�� �������� 
                if (isNetworkEnabled) {
   //��ġ ������Ʈ - ȯ�� ���ο��� ������Ʈ �ϱ� ������ ���⿡�� ���� ����.
     //               locationManager.requestLocationUpdates(
       //                     LocationManager.NETWORK_PROVIDER,
         //                   MIN_TIME_BW_UPDATES,
           //                 MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            // ���� �浵 ���� 
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
                // gps������ ���� ��ġ�� ��������
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
                            	//���� �浵 ����
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
     * GPS ���� 
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GpsInfo.this);
        }       
    }
      
    /**
     * �������� �����ɴϴ�. 
     * */
    public double getLatitude(){
        if(location != null){
            lat = location.getLatitude();
        }
        return lat;
    }
      
    /**
     * �浵���� �����ɴϴ�. 
     * */
    public double getLongitude(){
        if(location != null){
            lon = location.getLongitude();
        }
        return lon;
    }
     
      
    /**
     * GPS ������ �������� �������� 
     * ���������� ���� ����� alert â
     * */
    public void showSettingsAlert(){
        new AlertDialog.Builder(mContext)
		.setTitle("GPS ��� ���� ����")
		.setMessage("GPS ������ ���� �ʾ������� �ֽ��ϴ�. \n ����â���� ���� Ȯ���ϼ���")
		.setPositiveButton("Ȯ��", new DialogInterface.OnClickListener() {
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