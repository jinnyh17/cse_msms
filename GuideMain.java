/* 졸업 프로젝트
 * 무시무시
 * 
 * 보호자 화면 액티비티.
 * gcm 메시지를 보내고 받으며, 사용자 정보가 확인 가능하다.
 */
package com.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gcm.server.Sender;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;

public class GuideMain extends Activity {
	private static final int ACTIVITY_SETTING_INFO = 0;
	Intent setting_info_intent;
	private static final int ACTIVITY_SEARCH_LOCATION = 0;
	Intent search_location_intent;

	private BackPressCloseHandler backpressclosehandler; // 종료 클래스.

	Sender sender;
	AsyncTask<Void, Void, Void> mSendTask;

	private String pphoneNum;
	private String myphoneNum;

	@Override
	protected void onResume() { // 리시버 등록
		// TODO Auto-generated method stub
		registerReceiver(mToastMessageReceiver, new IntentFilter(
				"com.project.GCM_INTENT_FILLTER"));
		super.onResume();
	}

	@Override
	protected void onPause() { // 리시버 해제
		// TODO Auto-generated method stub
		unregisterReceiver(mToastMessageReceiver);
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_main);

		setting_info_intent = new Intent(this, SettingInfo.class); // 인텐트 설정
		search_location_intent = new Intent(this, Map.class);

		pphoneNum = Info.getP_phoneNum(); // 보호자 핸드폰 번호 가져오기.
		sender = new Sender(Info.API_Key); // API key로 센더 등록.
		// 사용자 정보 버툰
		ImageButton myInfoBt = (ImageButton) findViewById(R.id.settinginfoBtn);
		myInfoBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(setting_info_intent,
						ACTIVITY_SETTING_INFO); // 사용자 정보 확인 액티비티
			}
		});

		// 환자의 위치정보 가져오기
		ImageButton sendMsgBt = (ImageButton) findViewById(R.id.searchlocationBtn);
		sendMsgBt.setOnClickListener(new View.OnClickListener() { // 위치 정보 가져오기.
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sendToDevice("gogo");
					}
				});

		backpressclosehandler = new BackPressCloseHandler(this); // 종료 클래스.
	}

	@Override
	public void onBackPressed() { // 뒤로 두번 눌렀을 경우 종료.
		backpressclosehandler.onBackPressed();
	}

	/** 메시지 전송
	 * 서버 : 메시지 전송하기
	 */
	private void sendToDevice(final String msg) {
		mSendTask = new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				Message.Builder messageBuilder = new Message.Builder();
				messageBuilder.addData("msg", msg);
				messageBuilder.addData("sendDeviceId",
						Info.getP_RegistrationID()); // 환자 regID
				messageBuilder.addData("action", "request");
				Message message = messageBuilder.build();
				Log.d("gd", "sendToDevice().mSendTask : " + message.toString());
				try {
					Result result = sender.send(message, Info.p_RegistrationID, 5); // 보내기.
					Log.d("fd", "Message sent. Result : " + result);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				mSendTask = null;
			}
		};
		mSendTask.execute(null, null, null); // 실행
	}

	/** 메시지 수신
	 * GCMIntentService.onMessage에서 GCM 수신시 intentFillter로 broadCast 수행
	 * 보호자가 환자로 부터 메시지(위치정보)를 받은 경우 처리하는 함수.
	 */
	private final BroadcastReceiver mToastMessageReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			Bundle bundle = intent.getBundleExtra("message");
			String msg = (String) bundle.get("msg");
			String sendDeviceId = (String) bundle.get("sendDeviceId");
			String action = (String) bundle.get("action");

			if (msg.length() < 8) //사용자 위치를 못가져왔을 경우
				new AlertDialog.Builder(GuideMain.this)
						.setTitle("GPS 사용 유무 설정")
						.setMessage("GPS 셋팅이 되지 않았을수도 있습니다. \n 설정창으로 가서 확인하세요")
						.setPositiveButton("확인",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int which) {
										dialog.cancel();
									}
								}).show();
			else { //위도 경도 표시.
				Toast.makeText(context, "msg: " + msg, Toast.LENGTH_LONG).show();
				String[] arr = msg.split(","); //메시지 위도 경도 나누기.
				Info.setLatitude(Double.parseDouble(arr[0]));
				Info.setLongitude(Double.parseDouble(arr[1]));
				//메시지로 받은 위도 경도로 구글지도에 표시.
				startActivityForResult(search_location_intent,ACTIVITY_SEARCH_LOCATION);
			}
		}
	};
}
