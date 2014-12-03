/* ���� ������Ʈ
 * ���ù���
 * 
 * ��ȣ�� ȭ�� ��Ƽ��Ƽ.
 * gcm �޽����� ������ ������, ����� ������ Ȯ�� �����ϴ�.
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

	private BackPressCloseHandler backpressclosehandler; // ���� Ŭ����.

	Sender sender;
	AsyncTask<Void, Void, Void> mSendTask;

	private String pphoneNum;
	private String myphoneNum;

	@Override
	protected void onResume() { // ���ù� ���
		// TODO Auto-generated method stub
		registerReceiver(mToastMessageReceiver, new IntentFilter(
				"com.project.GCM_INTENT_FILLTER"));
		super.onResume();
	}

	@Override
	protected void onPause() { // ���ù� ����
		// TODO Auto-generated method stub
		unregisterReceiver(mToastMessageReceiver);
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_main);

		setting_info_intent = new Intent(this, SettingInfo.class); // ����Ʈ ����
		search_location_intent = new Intent(this, Map.class);

		pphoneNum = Info.getP_phoneNum(); // ��ȣ�� �ڵ��� ��ȣ ��������.
		sender = new Sender(Info.API_Key); // API key�� ���� ���.
		// ����� ���� ����
		ImageButton myInfoBt = (ImageButton) findViewById(R.id.settinginfoBtn);
		myInfoBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(setting_info_intent,
						ACTIVITY_SETTING_INFO); // ����� ���� Ȯ�� ��Ƽ��Ƽ
			}
		});

		// ȯ���� ��ġ���� ��������
		ImageButton sendMsgBt = (ImageButton) findViewById(R.id.searchlocationBtn);
		sendMsgBt.setOnClickListener(new View.OnClickListener() { // ��ġ ���� ��������.
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sendToDevice("gogo");
					}
				});

		backpressclosehandler = new BackPressCloseHandler(this); // ���� Ŭ����.
	}

	@Override
	public void onBackPressed() { // �ڷ� �ι� ������ ��� ����.
		backpressclosehandler.onBackPressed();
	}

	/** �޽��� ����
	 * ���� : �޽��� �����ϱ�
	 */
	private void sendToDevice(final String msg) {
		mSendTask = new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				Message.Builder messageBuilder = new Message.Builder();
				messageBuilder.addData("msg", msg);
				messageBuilder.addData("sendDeviceId",
						Info.getP_RegistrationID()); // ȯ�� regID
				messageBuilder.addData("action", "request");
				Message message = messageBuilder.build();
				Log.d("gd", "sendToDevice().mSendTask : " + message.toString());
				try {
					Result result = sender.send(message, Info.p_RegistrationID, 5); // ������.
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
		mSendTask.execute(null, null, null); // ����
	}

	/** �޽��� ����
	 * GCMIntentService.onMessage���� GCM ���Ž� intentFillter�� broadCast ����
	 * ��ȣ�ڰ� ȯ�ڷ� ���� �޽���(��ġ����)�� ���� ��� ó���ϴ� �Լ�.
	 */
	private final BroadcastReceiver mToastMessageReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			Bundle bundle = intent.getBundleExtra("message");
			String msg = (String) bundle.get("msg");
			String sendDeviceId = (String) bundle.get("sendDeviceId");
			String action = (String) bundle.get("action");

			if (msg.length() < 8) //����� ��ġ�� ���������� ���
				new AlertDialog.Builder(GuideMain.this)
						.setTitle("GPS ��� ���� ����")
						.setMessage("GPS ������ ���� �ʾ������� �ֽ��ϴ�. \n ����â���� ���� Ȯ���ϼ���")
						.setPositiveButton("Ȯ��",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int which) {
										dialog.cancel();
									}
								}).show();
			else { //���� �浵 ǥ��.
				Toast.makeText(context, "msg: " + msg, Toast.LENGTH_LONG).show();
				String[] arr = msg.split(","); //�޽��� ���� �浵 ������.
				Info.setLatitude(Double.parseDouble(arr[0]));
				Info.setLongitude(Double.parseDouble(arr[1]));
				//�޽����� ���� ���� �浵�� ���������� ǥ��.
				startActivityForResult(search_location_intent,ACTIVITY_SEARCH_LOCATION);
			}
		}
	};
}
