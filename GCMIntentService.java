/* 졸업 프로젝트
 * 무시무시
 * 
 * GCM 푸시 알람 메시지를 받아 처리하는 클래스.
 */
package com.project;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {	
	
	private static void generateNotification(Context context, String message) {
		int icon = R.drawable.app_img; //알림메시지에 띄울 이미지.
		long when = System.currentTimeMillis(); //시간

		//notification 구현.
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, StartMain.class);

		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}

	@Override
	protected void onError(Context arg0, String arg1) { //에러 났을 시에 호출됨.
		System.out.println("Error");
	}

	@Override
	protected void onMessage(Context context, Intent intent) { //메시지를 받았을 때 처리하는 함수.
		Log.d(TAG, "onMessage()");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			if (Info.get__who() == 2) { // 환자가 보호자로 부터 수신
				Log.d(TAG, "onMessage(), bundle: " + bundle);
				Intent newintent = new Intent("com.project.GCM_INTENT_FILLTER");
				newintent.putExtra("message", bundle);
				context.sendBroadcast(newintent); // intentFillter로 broadCast 수행
							
				// notifies user
				generateNotification(context, "위치를 파악합니다.");
				GCMRegistrar.onDestroy(context);
			
				PatientMain p = new PatientMain(); //환자 위치를 알기 위해 환자 메인 생성.
				p.setCondition(true); // 보호자가 메시지를 보냈다는 것을 플래그의 변화로 알림.-> 위치 가져옴.
			}
		
			if (Info.get__who() == 1) {// 보호자가 환자로 부터 수신
				Log.d(TAG, "onMessage(), bundle: " + bundle);
				Intent newintent = new Intent("com.project.GCM_INTENT_FILLTER");
				newintent.putExtra("message", bundle);
				context.sendBroadcast(newintent); // intentFillter로 broadCast 수행

				// notifies user
				generateNotification(context, "위치정보 메시지 입니다.");
			}
		}
	}

	@Override
	protected void onRegistered(Context context, String reg_id) {// 키 등록
		Log.e("키를 등록합니다.(GCM INTENTSERVICE)", reg_id);
		Info.setP_RegistrationID(reg_id);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) { //키 해제
		Log.e("키를 제거합니다.(GCM INTENTSERVICE)", "제거되었습니다.");
	}

}
