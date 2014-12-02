/* 졸업 프로젝트
 * 무시무시
 * 
 * 환자 화면 액티비티.
 * 환자가 긴급시에 보호자에게 전화걸 수 있다.
 * 보호자에게 메시지가 오면 환자의 위치를 알아와 보호자에게 알려준다. 
 */package com.project;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class PatientMain extends Activity {

	private String gphoneNum; //보호자 핸드폰 번호
	Sender sender;
	AsyncTask<Void, Void, Void> mSendTask;
	
	public  boolean condition; //메시지 수신 플래그.
	
	private GpsInfo gps; // 위치 정보	

	private BackPressCloseHandler backpressclosehandler;
	
	public boolean isCondition() {
		return condition;
	}
	
	public void setCondition(boolean condition) { //보호자로 부터 메시지를 받은 경우 자신의 위치를 구해 보호자에게 메시지를 보냄.
		this.condition = condition;		
		if(this.isCondition() == true){ //보호자에게 메시지 수신.
			sender = new Sender(Info.API_Key);
			sendToDevice(Info.getLatitude()+","+Info.getLongitude()); // 자신의 위치를 Info 클래스에서 가져와 보호자에게 보낸다.
		}															// Info 클래스에는 일정한 시간 간격으로 환자의 위치가 업데이트 됨.
		this.condition =false; // 플래그 초기화
	}
	
	@Override
	protected void onResume() { //리시버 등록
		// TODO Auto-generated method stub
		registerReceiver(mToastMessageReceiver, new IntentFilter("com.project.GCM_INTENT_FILLTER"));
		super.onResume();
	}
	@Override
	protected void onPause() { //리시버 해제
		// TODO Auto-generated method stub
		unregisterReceiver(mToastMessageReceiver);
		super.onPause();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_main);
		
/**GPS 가져오기.***/
		TimerTask task = new TimerTask(){ 
			public void run(){ //일정한 시간 간격으로 위치 가져옴. 
				try{
					gps = new GpsInfo(PatientMain.this); // 환자의 위치를 가져온다.
					if(gps.isGetLocation()){						
						Info.setLatitude(gps.getLatitude()); //Info 클래스에 경도 위도 저장.
						Info.setLongitude(gps.getLongitude());
					}
					else { //위치를 가져오지 못한 경우
						sender = new Sender(Info.API_Key);
						sendToDevice("error");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};
			
		Timer mTimer = new Timer();
		mTimer.schedule(task, 1000, 20000); //일정한 시간 간격으로 위치를 구해 Info 클래스에 저장.
		
		gphoneNum = Info.getG_phoneNum();
		ImageButton callBt = (ImageButton) findViewById(R.id.callBtn);
		callBt.setOnClickListener(new View.OnClickListener() {          // 긴급 통화 버튼
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				call(gphoneNum);				
			}
		});		

		backpressclosehandler = new BackPressCloseHandler(this); //종료 클래스
	}

    /**
     * GCMIntentService.onMessage에서 GCM 수신시 intentFillter로 broadCast 수행
     * 환자가 보호자로 부터 메시지 받은 경우 처리하는 함수.
     */
    private final BroadcastReceiver mToastMessageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            
            Log.d("fd", "mToastMessageReceiver.onReceive()");
           
            Bundle bundle = intent.getBundleExtra("message");
            String msg = (String) bundle.get("msg");
            String sendDeviceId = (String) bundle.get("sendDeviceId");
            String action = (String) bundle.get("action");            
        }
    };

	// num에 담긴 번호로 전화를 걸음.
	private void call(String num) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + num));
			startActivity(callIntent);
		} catch (ActivityNotFoundException e) {
			Log.e("긴급통화", "발신이 되지 않습니다.", e);
		}	
	}
	
	@Override
	public void onBackPressed(){  //두번 눌렀을 시 종료.
		backpressclosehandler.onBackPressed();
	}	
	
	/*
     * 서버 : 전송하기
     */
    private void sendToDevice(final String msg) 
    {
        mSendTask = new AsyncTask<Void, Void, Void>() 
        {
            protected Void doInBackground(Void... params) 
            {
                Message.Builder messageBuilder = new Message.Builder(); //메시지 만들기
                messageBuilder.addData("msg", msg);
                messageBuilder.addData("sendDeviceId", Info.g_RegistrationID);
                messageBuilder.addData("action", "request");
                Message message = messageBuilder.build();
                
               Log.d("gd", "sendToDevice().mSendTask : " + message.toString());

                try 
                {                			
                	Result result2 = sender.send(message, Info.g_RegistrationID, 5); //보호자에게 메시지를 보냄.
                    if(result2.getMessageId() !=null) 
                    	Log.d("fd", "Message sent. Result : " + result2);
                    else{ //메시지 수신하는 id가 null 인 경우.
                    	System.out.println(result2.getErrorCodeName());
                    } 
                } catch(Exception ex) {
                    ex.printStackTrace();
                }                
                return null;
            }
 
            protected void onPostExecute(Void result) {            
                mSendTask = null;
            } 
        };
        mSendTask.execute(null, null, null); 
    }	
}
