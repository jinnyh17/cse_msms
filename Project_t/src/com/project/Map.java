/* 졸업 프로젝트
 * 무시무시
 * 
 * 환자 위치를 지도에 나타내는 클래스. 
 */package com.project;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends FragmentActivity implements OnMapClickListener {

	private GoogleMap mGoogleMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		// BitmapDescriptorFactory 생성하기 위한 소스
		MapsInitializer.initialize(getApplicationContext());

		init();
	}

	/** Map 클릭시 터치 이벤트 */
	public void onMapClick(LatLng point) {

		// 현재 위도와 경도에서 화면 포인트를 알려준다
		Point screenPt = mGoogleMap.getProjection().toScreenLocation(point);

		// 현재 화면에 찍힌 포인트로 부터 위도와 경도를 알려준다.
		LatLng latLng = mGoogleMap.getProjection().fromScreenLocation(screenPt);

		Log.d("맵좌표", "좌표: 위도(" + String.valueOf(point.latitude) + "), 경도("
				+ String.valueOf(point.longitude) + ")");
		Log.d("화면좌표", "화면좌표: X(" + String.valueOf(screenPt.x) + "), Y("
				+ String.valueOf(screenPt.y) + ")");
	}

	/**
	 * 초기화
	 * 
	 * @author
	 */
	private void init() {

		GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		// 맵의 이동
		// mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,
		// 15));

		// GPS 사용유무 가져오기
		if (Info.getLatitude() !=0.0) {
			// Creating a LatLng object for the current location
			LatLng latLng = new LatLng(Info.getLatitude(), Info.getLongitude()); //Info 클래스에 저장해둔 환자 위치 가져오기.

			// Showing the current location in Google Map
			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

			// Map 을 zoom 합니다.
			mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

			// 마커 설정.
			MarkerOptions optFirst = new MarkerOptions();
			optFirst.position(latLng);// 위도 • 경도
			optFirst.title("여기에 있어요!");// 제목 미리보기
//			optFirst.snippet("");
			optFirst.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.ic_launcher));
			mGoogleMap.addMarker(optFirst).showInfoWindow();
		}
	}
}