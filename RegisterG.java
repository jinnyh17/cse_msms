/* 졸업 프로젝트
 * 무시무시
 * 
 * 보호자 등록 액티비티.
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gcm.GCMRegistrar;

public class RegisterG extends Activity {

	private String pphoneStr; //입력받는 환자 핸드폰 번호
	private String gphoneStr; //입력받는 보호자 핸드폰 번호
	private String skey; //인증번호
	private String strPhoneNumber; //디바이스 핸드폰 번호
	Intent start_intent;
	private static final int ACTIVITY_START_MAIN = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_g);

		StrictMode.ThreadPolicy p = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(p);
		start_intent = new Intent(this, StartMain.class);

		// 디바이스 핸드폰 번호 가져오기
		TelephonyManager clsManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);		
		strPhoneNumber= clsManager.getLine1Number(); 
		
		if (strPhoneNumber.length() > 12) {     //+8210 과 010 구별. +82 를 0으로 치환.
			strPhoneNumber = "0" + strPhoneNumber.substring(3);
		}

		GCMRegistrar.checkDevice(this);
		//단말이 GCM을 지원하는지 검사
		Log.e("pass", "체크 1번통과 ");
		GCMRegistrar.checkManifest(this);
		//응용프로그램의 매니페스트가 제대로 구성되어 있는 지 확인
		Log.e("pass", "체크 2번통과");						

		Info.g_RegistrationID = GCMRegistrar.getRegistrationId(this); // 등록 ID를 얻어옴
		
		if( Info.g_RegistrationID != null && !"".equals(Info.g_RegistrationID)) // 등록된 아이디가 있다면		
			Log.e("pass", "if문에 들어옴. RegistrationID : " + Info.g_RegistrationID);		
		else { // GCM 을 사용하기위한 등록 ID가 없음
			GCMRegistrar.register(this, Info.SenderID); // 고유 ID로 등록 및 얻어옴
			Info.g_RegistrationID = GCMRegistrar.getRegistrationId(this); // 등록 ID를 얻어옴
			Log.e("pass", "else문에 들어옴. RegistrationID : " + Info.g_RegistrationID);
		}
		
		ImageButton okBt = (ImageButton) findViewById(R.id.registerokBtn);
		okBt.setOnClickListener(new View.OnClickListener() { //확인 버튼을 눌렀을 시 이벤트.
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// 환자 핸드폰 번호 받아오기
				EditText pphoneEdit1 = (EditText) findViewById(R.id.inputPPhone1);
				EditText pphoneEdit2 = (EditText) findViewById(R.id.inputPPhone2);
				EditText pphoneEdit3 = (EditText) findViewById(R.id.inputPPhone3);
				pphoneStr = pphoneEdit1.getText().toString()
						+ pphoneEdit2.getText().toString()
						+ pphoneEdit3.getText().toString();

				// 보호자 핸드폰 번호 받아오기
				EditText gphoneEdit1 = (EditText) findViewById(R.id.inputGPhone1);
				EditText gphoneEdit2 = (EditText) findViewById(R.id.inputGPhone2);
				EditText gphoneEdit3 = (EditText) findViewById(R.id.inputGPhone3);
				gphoneStr = gphoneEdit1.getText().toString()
						+ gphoneEdit2.getText().toString()
						+ gphoneEdit3.getText().toString();

				// 인증키 받아오기
				EditText key = (EditText) findViewById(R.id.key);
				skey = key.getText().toString();

				if (pphoneEdit1.getText().toString().length() != 3) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("환자 핸드폰 번호 입력 오류")
							.setMessage("첫 번째 칸에 3자리 숫자를 입력하셔야 합니다.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else if (pphoneEdit2.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("환자 핸드폰 번호 입력 오류")
							.setMessage("두 번째 칸에 4자리 숫자를 입력하셔야 합니다.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else if (pphoneEdit3.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("환자 핸드폰 번호 입력 오류")
							.setMessage("세 번째 칸에 4자리 숫자를 입력하셔야 합니다.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else if (gphoneEdit1.getText().toString().length() != 3) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("보호자 핸드폰 번호 입력 오류")
							.setMessage("첫 번째 칸에 3자리 숫자를 입력하셔야 합니다.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else if (gphoneEdit2.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("보호자 핸드폰 번호 입력 오류")
							.setMessage("두 번째 칸에 4자리 숫자를 입력하셔야 합니다.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else if (gphoneEdit3.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("보호자 핸드폰 번호 입력 오류")
							.setMessage("세 번째 칸에 4자리 숫자를 입력하셔야 합니다.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else if (skey.length() != 6) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("인증번호 입력 오류")
							.setMessage("인증번호를 확인해 주세요.").setCancelable(false)
							.setPositiveButton("확인", null).show();
				} else {
					if (strPhoneNumber.equals(gphoneStr)) {			 //보호자 번호와 디바이스에 자체 핸드폰 번호가 일치하는 경우.			
						new HttpTask().execute();						
					} else { 										//일치하지 않는경우.
						System.out.println(strPhoneNumber);
						System.out.println(gphoneStr);
						new AlertDialog.Builder(RegisterG.this)
								.setTitle("핸드폰 번호 입력 오류")
								.setMessage("보호자 핸드폰 번호를 확인해주세요.")
								.setCancelable(false)
								.setPositiveButton("확인", null).show();
					}
				}
			}
		});
	}

	class HttpTask extends AsyncTask<Void, Void, String> { //환자 번호와 인증키가 맞는지 확인하고 유저로 등록.
		@Override
		protected String doInBackground(Void... voids) {
			// TODO Auto-generated method stub
			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = Info.host + "/connect_check.php/";
				HttpPost post = new HttpPost(postURL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("skey", skey));
				params.add(new BasicNameValuePair("gphoneStr", gphoneStr));
				params.add(new BasicNameValuePair("pphoneStr", pphoneStr));
				params.add(new BasicNameValuePair("gregid", Info.g_RegistrationID));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();
				String result = EntityUtils.toString(resEntity, HTTP.UTF_8);
				if (result.length() < 10) { /////////////////////////////////////인증번호 오류
					runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(RegisterG.this)
									.setTitle("입력 오류")
									.setMessage("인증번호와 환자 핸드폰 번호를 확인해주세요.")
									.setCancelable(false)
									.setPositiveButton("확인", null).show();
						}
					});

				} else { //////////////////////////////////인증성공
/////////////RESULT 환자 REGID, 환자 번호 .내장디비에 저장.
					DBAdapter handler = DBAdapter.open(getApplicationContext());
					handler.delete(gphoneStr);					
					handler.insert(gphoneStr, pphoneStr, Info.g_RegistrationID, result);
					Cursor c = handler.pSelect(gphoneStr);
		//핸드폰 번호가 제대로 내장디비에 저장되었는지 확인..
		//			while(c.moveToNext()){ 
		//				String g = c.getString(c.getColumnIndex("Gphone"));
		//				String d = c.getString(c.getColumnIndex("Pphone"));
		//			}
					runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(RegisterG.this)
									.setTitle("등록완료")
									.setMessage("등록되었습니다.\n * 사용자 화면으로 넘어 갑니다. \n * '환자핸드폰' 화면에서 \"등록완료\" 버튼을 눌러주세요.")
									.setCancelable(false)
									.setPositiveButton(
											"확인",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub
													try {
														Info.setWho("1");	//보호자로 set
														Info.setP_phoneNum(pphoneStr); // 환자 핸드폰 번호 set
														Info.setG_phoneNum(gphoneStr); // 보호자 핸드폰 번호 set
													} catch (IOException e) {
														// TODO Auto-generated
														// catch block
														e.printStackTrace();
													}
													startActivityForResult(start_intent,ACTIVITY_START_MAIN);
												}
											}).show();
						}
					});
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
}
