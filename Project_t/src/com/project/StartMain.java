/* ���� ������Ʈ
 * ���ù���
 * 
 * ���ø����̼� ���� ��Ƽ��Ƽ. �̶� ����� ������ ������ �����Ѵ�.
 */
package com.project;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.android.gcm.GCMRegistrar;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class StartMain extends ActionBarActivity {

	private static final int ACTIVITY_INIT_MAIN = 0;
	private static final int ACTIVITY_GMAIN = 0;
	private static final int ACTIVITY_PMAIN = 0;
	Intent init_intent, pMain_intent, gMain_intent;
	
	private String myphoneNum; //����̽� ��ü ����ȣ
	private String regid; //����̽� regID
	private int who; //ȯ�� ��ȣ�� �Ǵ� �̵�� ����� 
	
	CountDownTimer CountTimer = null; //�ð� ī��Ʈ�ٿ� Ŭ����.
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_main);
		
		// Intent
		init_intent = new Intent(this, InitMain.class);
		gMain_intent = new Intent(this, GuideMain.class);
		pMain_intent = new Intent(this, PatientMain.class);

		// ����̽� �ڵ��� ��ȣ ��������
		TelephonyManager clsManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		myphoneNum = clsManager.getLine1Number();
		
		if (myphoneNum.length() > 12) { //+8210 �� 010 ����. +82 �� 0���� ġȯ.
			myphoneNum = "0" + myphoneNum.substring(3);
		}
		
		GCMRegistrar.checkDevice(this);
		//�ܸ��� GCM�� �����ϴ��� �˻�
		Log.e("pass", "üũ 1����� ");
		GCMRegistrar.checkManifest(this);
		//�������α׷��� �Ŵ��佺Ʈ�� ����� �����Ǿ� �ִ� �� Ȯ��
		Log.e("pass", "üũ 2�����");						
		
		regid = GCMRegistrar.getRegistrationId(this); // ��� ID�� ����

		if (regid != null && !"".equals(regid)) // ��ϵ� ���̵� �ִٸ�
		{
			Log.e("pass", "if���� ����. RegistrationID : " + regid);
		} else // GCM �� ����ϱ����� ��� ID�� ����
		{
			GCMRegistrar.register(this, Info.SenderID); // ���� ID�� ��� �� ����
			regid = GCMRegistrar.getRegistrationId(this); // ��� ID�� ����
			Log.e("pass", "else���� ����. RegistrationID : " + regid);
		}

		CountTimer = new CountDownTimer(3000, 1000) { //�ð� ī��Ʈ. �����ð����� ȭ�� ����.
			@Override
			public void onTick(long millisUntilFinished) { //ȭ���� ������ ���� �� ����.
				// TODO Auto-generated method stub			
				new HttpTask().execute();		
			}

			@Override
			public void onFinish() { //���� ȭ���� ������ ����.
				try {
					who = Info.getWho(); //Who are you?	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				
				if (who == 10){// �̵�� ����� -> ���ȭ������ 
					startActivityForResult(init_intent, ACTIVITY_INIT_MAIN);
					finish(); //��Ƽ��Ƽ �����丮 �����
			}
				else {
					DBAdapter handler = DBAdapter.open(getApplicationContext()); //������ ����
					if (who == 1) { // ��ȣ�� -> ��ȣ�� ȭ������
						Info.setG_RegistrationID(regid); //����̽� redID �� �ڵ��� ��ȣ ��ȣ�� ������ set
						Info.setG_phoneNum(myphoneNum);
						Cursor cursor = handler.pSelect(myphoneNum);
						while(cursor.moveToNext()){ //�����񿡼� ȯ�� ���������� Info�� set.
							String g = cursor.getString(cursor.getColumnIndex("Pphone"));
							String d = cursor.getString(cursor.getColumnIndex("Gcmid_p"));
							Info.setP_phoneNum(g);
							Info.setP_RegistrationID(d);
						}
						
						startActivityForResult(gMain_intent, ACTIVITY_GMAIN); //��ȣ�� ��Ƽ��Ƽ��
						finish(); //��Ƽ��Ƽ �����丮 �����
					} else if (who == 2) { // ȯ�� ->ȯ�� ȭ������
						Info.setP_RegistrationID(regid); //����̽� redID �� �ڵ��� ��ȣ ȯ�� ������ set
						Info.setP_phoneNum(myphoneNum);
						Cursor cursor = handler.gSelect(myphoneNum);
						while(cursor.moveToNext()){ //�����񿡼� ��ȣ�� ���������� Info�� set.
							String g = cursor.getString(cursor.getColumnIndex("Gphone"));
							String d = cursor.getString(cursor.getColumnIndex("Gcmid_g"));
							Info.setG_phoneNum(g);
							Info.setG_RegistrationID(d);
						}
						startActivityForResult(pMain_intent, ACTIVITY_PMAIN);//ȯ�� ��Ƽ��Ƽ��
						finish(); //��Ƽ��Ƽ �����丮 �����
					}
				}
			}
		}.start();
	}

	class HttpTask extends AsyncTask<Void, Void, String> { //����ڰ� ��ϵǾ��ִ��� Ȯ��		
		@Override
		protected String doInBackground(Void... voids) {
			// TODO Auto-generated method stub
			try {				
				HttpClient client = new DefaultHttpClient();
				String postURL = Info.host + "/get_phoneNumber.php/";
				HttpPost post = new HttpPost(postURL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("phone", myphoneNum));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();
				String result = EntityUtils.toString(resEntity, HTTP.UTF_8);
				System.out.println("result : " +result);
				System.out.println("who : " +result.charAt(1));
				System.out.println("length : " + result.length());
				if (result.length() > 5) // user not found
					Info.setWho("10");
				else {
					if (result.charAt(1) =='1'){ //��ȣ��
						Info.setWho("1");
					}
					else if(result.charAt(1) == '2') { //ȯ��
						Info.setWho("2");
					}
					else ;	
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		GCMRegistrar.onDestroy(getApplicationContext());
		super.onDestroy();
	}	
}
