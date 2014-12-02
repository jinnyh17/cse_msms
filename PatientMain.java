/* ���� ������Ʈ
 * ���ù���
 * 
 * ȯ�� ȭ�� ��Ƽ��Ƽ.
 * ȯ�ڰ� ��޽ÿ� ��ȣ�ڿ��� ��ȭ�� �� �ִ�.
 * ��ȣ�ڿ��� �޽����� ���� ȯ���� ��ġ�� �˾ƿ� ��ȣ�ڿ��� �˷��ش�. 
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

	private String gphoneNum; //��ȣ�� �ڵ��� ��ȣ
	Sender sender;
	AsyncTask<Void, Void, Void> mSendTask;
	
	public  boolean condition; //�޽��� ���� �÷���.
	
	private GpsInfo gps; // ��ġ ����	

	private BackPressCloseHandler backpressclosehandler;
	
	public boolean isCondition() {
		return condition;
	}
	
	public void setCondition(boolean condition) { //��ȣ�ڷ� ���� �޽����� ���� ��� �ڽ��� ��ġ�� ���� ��ȣ�ڿ��� �޽����� ����.
		this.condition = condition;		
		if(this.isCondition() == true){ //��ȣ�ڿ��� �޽��� ����.
			sender = new Sender(Info.API_Key);
			sendToDevice(Info.getLatitude()+","+Info.getLongitude()); // �ڽ��� ��ġ�� Info Ŭ�������� ������ ��ȣ�ڿ��� ������.
		}															// Info Ŭ�������� ������ �ð� �������� ȯ���� ��ġ�� ������Ʈ ��.
		this.condition =false; // �÷��� �ʱ�ȭ
	}
	
	@Override
	protected void onResume() { //���ù� ���
		// TODO Auto-generated method stub
		registerReceiver(mToastMessageReceiver, new IntentFilter("com.project.GCM_INTENT_FILLTER"));
		super.onResume();
	}
	@Override
	protected void onPause() { //���ù� ����
		// TODO Auto-generated method stub
		unregisterReceiver(mToastMessageReceiver);
		super.onPause();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_main);
		
/**GPS ��������.***/
		TimerTask task = new TimerTask(){ 
			public void run(){ //������ �ð� �������� ��ġ ������. 
				try{
					gps = new GpsInfo(PatientMain.this); // ȯ���� ��ġ�� �����´�.
					if(gps.isGetLocation()){						
						Info.setLatitude(gps.getLatitude()); //Info Ŭ������ �浵 ���� ����.
						Info.setLongitude(gps.getLongitude());
					}
					else { //��ġ�� �������� ���� ���
						sender = new Sender(Info.API_Key);
						sendToDevice("error");
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};
			
		Timer mTimer = new Timer();
		mTimer.schedule(task, 1000, 20000); //������ �ð� �������� ��ġ�� ���� Info Ŭ������ ����.
		
		gphoneNum = Info.getG_phoneNum();
		ImageButton callBt = (ImageButton) findViewById(R.id.callBtn);
		callBt.setOnClickListener(new View.OnClickListener() {          // ��� ��ȭ ��ư
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				call(gphoneNum);				
			}
		});		

		backpressclosehandler = new BackPressCloseHandler(this); //���� Ŭ����
	}

    /**
     * GCMIntentService.onMessage���� GCM ���Ž� intentFillter�� broadCast ����
     * ȯ�ڰ� ��ȣ�ڷ� ���� �޽��� ���� ��� ó���ϴ� �Լ�.
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

	// num�� ��� ��ȣ�� ��ȭ�� ����.
	private void call(String num) {
		try {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + num));
			startActivity(callIntent);
		} catch (ActivityNotFoundException e) {
			Log.e("�����ȭ", "�߽��� ���� �ʽ��ϴ�.", e);
		}	
	}
	
	@Override
	public void onBackPressed(){  //�ι� ������ �� ����.
		backpressclosehandler.onBackPressed();
	}	
	
	/*
     * ���� : �����ϱ�
     */
    private void sendToDevice(final String msg) 
    {
        mSendTask = new AsyncTask<Void, Void, Void>() 
        {
            protected Void doInBackground(Void... params) 
            {
                Message.Builder messageBuilder = new Message.Builder(); //�޽��� �����
                messageBuilder.addData("msg", msg);
                messageBuilder.addData("sendDeviceId", Info.g_RegistrationID);
                messageBuilder.addData("action", "request");
                Message message = messageBuilder.build();
                
               Log.d("gd", "sendToDevice().mSendTask : " + message.toString());

                try 
                {                			
                	Result result2 = sender.send(message, Info.g_RegistrationID, 5); //��ȣ�ڿ��� �޽����� ����.
                    if(result2.getMessageId() !=null) 
                    	Log.d("fd", "Message sent. Result : " + result2);
                    else{ //�޽��� �����ϴ� id�� null �� ���.
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
