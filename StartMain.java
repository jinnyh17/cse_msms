/* 졸업 프로젝트
 * 무시무시
 * 
 * 애플리케이션 시작 액티비티. 이때 사용자 정보를 가져와 저장한다.
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
	
	private String myphoneNum; //디바이스 자체 폰번호
	private String regid; //디바이스 regID
	private int who; //환자 보호자 또는 미등록 사용자 
	
	CountDownTimer CountTimer = null; //시간 카운트다운 클래스.
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_main);
		
		// Intent
		init_intent = new Intent(this, InitMain.class);
		gMain_intent = new Intent(this, GuideMain.class);
		pMain_intent = new Intent(this, PatientMain.class);

		// 디바이스 핸드폰 번호 가져오기
		TelephonyManager clsManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		myphoneNum = clsManager.getLine1Number();
		
		if (myphoneNum.length() > 12) { //+8210 과 010 구별. +82 를 0으로 치환.
			myphoneNum = "0" + myphoneNum.substring(3);
		}
		
		GCMRegistrar.checkDevice(this);
		//단말이 GCM을 지원하는지 검사
		Log.e("pass", "체크 1번통과 ");
		GCMRegistrar.checkManifest(this);
		//응용프로그램의 매니페스트가 제대로 구성되어 있는 지 확인
		Log.e("pass", "체크 2번통과");						
		
		regid = GCMRegistrar.getRegistrationId(this); // 등록 ID를 얻어옴

		if (regid != null && !"".equals(regid)) // 등록된 아이디가 있다면
		{
			Log.e("pass", "if문에 들어옴. RegistrationID : " + regid);
		} else // GCM 을 사용하기위한 등록 ID가 없음
		{
			GCMRegistrar.register(this, Info.SenderID); // 고유 ID로 등록 및 얻어옴
			regid = GCMRegistrar.getRegistrationId(this); // 등록 ID를 얻어옴
			Log.e("pass", "else문에 들어옴. RegistrationID : " + regid);
		}

		CountTimer = new CountDownTimer(3000, 1000) { //시간 카운트. 일정시간동안 화면 정지.
			@Override
			public void onTick(long millisUntilFinished) { //화면이 정지해 있을 때 실행.
				// TODO Auto-generated method stub			
				new HttpTask().execute();		
			}

			@Override
			public void onFinish() { //정지 화면이 끝나면 실행.
				try {
					who = Info.getWho(); //Who are you?	
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	
				
				if (who == 10){// 미등록 사용자 -> 등록화면으로 
					startActivityForResult(init_intent, ACTIVITY_INIT_MAIN);
					finish(); //액티비티 히스토리 지우기
			}
				else {
					DBAdapter handler = DBAdapter.open(getApplicationContext()); //내장디비 오픈
					if (who == 1) { // 보호자 -> 보호자 화면으로
						Info.setG_RegistrationID(regid); //디바이스 redID 와 핸드폰 번호 보호자 정보에 set
						Info.setG_phoneNum(myphoneNum);
						Cursor cursor = handler.pSelect(myphoneNum);
						while(cursor.moveToNext()){ //내장디비에서 환자 정보가져와 Info에 set.
							String g = cursor.getString(cursor.getColumnIndex("Pphone"));
							String d = cursor.getString(cursor.getColumnIndex("Gcmid_p"));
							Info.setP_phoneNum(g);
							Info.setP_RegistrationID(d);
						}
						
						startActivityForResult(gMain_intent, ACTIVITY_GMAIN); //보호자 액티비티로
						finish(); //액티비티 히스토리 지우기
					} else if (who == 2) { // 환자 ->환자 화면으로
						Info.setP_RegistrationID(regid); //디바이스 redID 와 핸드폰 번호 환자 정보에 set
						Info.setP_phoneNum(myphoneNum);
						Cursor cursor = handler.gSelect(myphoneNum);
						while(cursor.moveToNext()){ //내장디비에서 보호자 정보가져와 Info에 set.
							String g = cursor.getString(cursor.getColumnIndex("Gphone"));
							String d = cursor.getString(cursor.getColumnIndex("Gcmid_g"));
							Info.setG_phoneNum(g);
							Info.setG_RegistrationID(d);
						}
						startActivityForResult(pMain_intent, ACTIVITY_PMAIN);//환자 액티비티로
						finish(); //액티비티 히스토리 지우기
					}
				}
			}
		}.start();
	}

	class HttpTask extends AsyncTask<Void, Void, String> { //사용자가 등록되어있는지 확인		
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
					if (result.charAt(1) =='1'){ //보호자
						Info.setWho("1");
					}
					else if(result.charAt(1) == '2') { //환자
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
