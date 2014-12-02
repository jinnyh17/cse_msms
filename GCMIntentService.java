/* ���� ������Ʈ
 * ���ù���
 * 
 * GCM Ǫ�� �˶� �޽����� �޾� ó���ϴ� Ŭ����.
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
		int icon = R.drawable.app_img; //�˸��޽����� ��� �̹���.
		long when = System.currentTimeMillis(); //�ð�

		//notification ����.
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
	protected void onError(Context arg0, String arg1) { //���� ���� �ÿ� ȣ���.
		System.out.println("Error");
	}

	@Override
	protected void onMessage(Context context, Intent intent) { //�޽����� �޾��� �� ó���ϴ� �Լ�.
		Log.d(TAG, "onMessage()");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			if (Info.get__who() == 2) { // ȯ�ڰ� ��ȣ�ڷ� ���� ����
				Log.d(TAG, "onMessage(), bundle: " + bundle);
				Intent newintent = new Intent("com.project.GCM_INTENT_FILLTER");
				newintent.putExtra("message", bundle);
				context.sendBroadcast(newintent); // intentFillter�� broadCast ����
							
				// notifies user
				generateNotification(context, "��ġ�� �ľ��մϴ�.");
				GCMRegistrar.onDestroy(context);
			
				PatientMain p = new PatientMain(); //ȯ�� ��ġ�� �˱� ���� ȯ�� ���� ����.
				p.setCondition(true); // ��ȣ�ڰ� �޽����� ���´ٴ� ���� �÷����� ��ȭ�� �˸�.-> ��ġ ������.
			}
		
			if (Info.get__who() == 1) {// ��ȣ�ڰ� ȯ�ڷ� ���� ����
				Log.d(TAG, "onMessage(), bundle: " + bundle);
				Intent newintent = new Intent("com.project.GCM_INTENT_FILLTER");
				newintent.putExtra("message", bundle);
				context.sendBroadcast(newintent); // intentFillter�� broadCast ����

				// notifies user
				generateNotification(context, "��ġ���� �޽��� �Դϴ�.");
			}
		}
	}

	@Override
	protected void onRegistered(Context context, String reg_id) {// Ű ���
		Log.e("Ű�� ����մϴ�.(GCM INTENTSERVICE)", reg_id);
		Info.setP_RegistrationID(reg_id);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) { //Ű ����
		Log.e("Ű�� �����մϴ�.(GCM INTENTSERVICE)", "���ŵǾ����ϴ�.");
	}

}
