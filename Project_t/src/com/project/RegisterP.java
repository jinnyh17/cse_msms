/* 졸업 프로젝트
 * 무시무시
 * 
 * 환자 등록 액티비티.
 */
package com.project;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

public class RegisterP extends Activity {
	private String skey; //인증번호
	private String pphoneStr; //입력받은 환자 핸드폰 번호
	private String strPhoneNumber; //디바이스 자체 핸드폰 번호
	Intent start_intent;
	private static final int ACTIVITY_START_MAIN = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_p);

		StrictMode.ThreadPolicy p = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(p);

		// 디바이스 핸드폰 번호 가져오기
		TelephonyManager clsManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		strPhoneNumber = clsManager.getLine1Number();

		if (strPhoneNumber.length() > 12) {  //+8210 과 010 구별. +82 를 0으로 치환.
			strPhoneNumber = "0" + strPhoneNumber.substring(3);
		}

		GCMRegistrar.checkDevice(this);
		// 단말이 GCM을 지원하는지 검사
		Log.e("pass", "체크 1번통과 ");
		GCMRegistrar.checkManifest(this);
		// 응용프로그램의 매니페스트가 제대로 구성되어 있는 지 확인
		Log.e("pass", "체크 2번통과");

		Info.p_RegistrationID = GCMRegistrar.getRegistrationId(this); // 등록 ID를 얻어옴

		if (Info.p_RegistrationID != null && !"".equals(Info.p_RegistrationID)) // 등록된 아이디가 있다면
			Log.e("pass", "if문에 들어옴. RegistrationID : " + Info.p_RegistrationID);
		else // GCM 을 사용하기위한 등록 ID가 없음
		{
			GCMRegistrar.register(this, Info.SenderID); // 고유 ID로 등록 및 얻어옴
			Info.p_RegistrationID = GCMRegistrar.getRegistrationId(this); // 등록 ID를 얻어옴
			Log.e("pass", "else문에 들어옴. RegistrationID : "
					+ Info.p_RegistrationID);
		}
		
		start_intent = new Intent(this, StartMain.class);
		ImageButton createKeyBt = (ImageButton) findViewById(R.id.createKeyBtn);
		createKeyBt.setOnClickListener(new View.OnClickListener() { //인증번혼 받기 버튼 눌렀을 때.

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 인증키 랜덤생성
				Random rand = new Random();
				int key = rand.nextInt(900000) + 100000; //랜덤으로 6자리 인증키 생성
				skey = String.valueOf(key);

				// 환자의 핸드폰 번호 받아오기
				EditText pphoneEdit1 = (EditText) findViewById(R.id.inputPPhone1);
				EditText pphoneEdit2 = (EditText) findViewById(R.id.inputPPhone2);
				EditText pphoneEdit3 = (EditText) findViewById(R.id.inputPPhone3);

				CheckBox cb = (CheckBox) findViewById(R.id.checkBox1); //체크박스
				pphoneStr = pphoneEdit1.getText().toString()
						+ pphoneEdit2.getText().toString()
						+ pphoneEdit3.getText().toString(); // 핸드폰 번호

				if (pphoneEdit1.getText().toString().length() != 3) {
					new AlertDialog.Builder(RegisterP.this)
							.setTitle("핸드폰 번호 입력 오류")
							.setMessage("첫 번째 칸에 3자리 숫자를 입력하셔야 합니다.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else if (pphoneEdit2.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterP.this)
							.setTitle("핸드폰 번호 입력 오류")
							.setMessage("두 번째 칸에 4자리 숫자를 입력하셔야 합니다.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else if (pphoneEdit3.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterP.this)
							.setTitle("핸드폰 번호 입력 오류")
							.setMessage("세 번째 칸에 4자리 숫자를 입력하셔야 합니다.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else if (!cb.isChecked()) { //개인정보 처리 체크박스에 체크되어있는지 확인.
					new AlertDialog.Builder(RegisterP.this)
							.setTitle("개인정보 처리 동의 오류")
							.setMessage("개인정보 처리에 동의 해주세요.")
							.setCancelable(false).setPositiveButton("확인", null)
							.show();
				} else {
					if (strPhoneNumber.equals(pphoneStr)) { //입력받은 핸드폰 번호가 디바이스 자체 핸드폰 번호와 같음.
						new HttpTask().execute();
					
						new AlertDialog.Builder(RegisterP.this) 	// 인증번호  다이얼러그
								.setTitle("아래 인증번호를 보호자 폰에 입력해 주세요")
								.setMessage(skey + "\n보호자 폰에서 등록이 완료되면 확인 버튼을 눌러주세요.")
								.setCancelable(false)
								.setPositiveButton("등록완료",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												new AlertDialog.Builder(
														RegisterP.this)
														.setTitle("등록확인")
														.setMessage( skey + "\n보호자 폰에서 등록이 완료되었습니까?\n '예'를 누르면 사용자 화면으로 넘어갑니다.")
														.setCancelable(false)
														.setPositiveButton("예",new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick( DialogInterface dialog, int which) {
																		// TODO
																		// Auto-generated method stub
																		new HttpTask3().execute(); //guide id 가져오기
																		try {
																			Info.setWho("2"); //환자로 set
																			Info.setWho(2);
																		} catch (IOException e) {
																			// TODO
																			// Auto-generated catch block
																			e.printStackTrace();
																		}
																		//처음 화면으로 돌아감.
																		startActivityForResult( start_intent, ACTIVITY_START_MAIN);
																	}
																})
								/*인증번호 아니오 선택*/		.setNegativeButton( "아니오", new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {
																		// TODO
																		// Auto-generated  method stub
																		new HttpTask2().execute();
																		new AlertDialog.Builder(RegisterP.this)
																				.setTitle("등록취소")
																				.setMessage(
																						"아니오를 선택하셨습니다.\n 다시 인증번호를 받아주세요.")
																				.setPositiveButton("확인",null)
																				.show();
																	}
																}).show();
											}
										}).show();
					} else {
						System.out.println(strPhoneNumber);  //핸드폰번호 맞지 않는 경우
						System.out.println(pphoneStr);
						new AlertDialog.Builder(RegisterP.this)
								.setTitle("핸드폰 번호 입력 오류")
								.setMessage("핸드폰 번호를 확인해주세요.")
								.setCancelable(false)
								.setPositiveButton("확인", null).show();
					}
				}
			}
		});
	}

	class HttpTask extends AsyncTask<Void, Void, String> { //입력받은 환자 번호와 인증번호, 환자 regId 디비에 저장.

		@Override
		protected String doInBackground(Void... voids) {
			// TODO Auto-generated method stub
			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = Info.host + "/connect_insert.php/";
				HttpPost post = new HttpPost(postURL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("skey", skey));
				params.add(new BasicNameValuePair("pphoneStr", pphoneStr));
				params.add(new BasicNameValuePair("pregid",	Info.p_RegistrationID));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();
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

	class HttpTask2 extends AsyncTask<Void, Void, String> { //취소버튼을 눌렀을 경우 저장한 데이터 레코드 삭제.

		@Override
		protected String doInBackground(Void... voids) {
			// TODO Auto-generated method stub
			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = Info.host + "/connect_cancel.php/";
				HttpPost post = new HttpPost(postURL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("pphoneStr", pphoneStr));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();
				if (resEntity != null) {
					Log.i("fd", EntityUtils.toString(resEntity));
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

	/*** 보호자 번호, regid 가져오기 **/
	class HttpTask3 extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... voids) {
			// TODO Auto-generated method stub
			try {
				HttpClient client = new DefaultHttpClient();
				String postURL = Info.host + "/getGphoneNum.php/";
				HttpPost post = new HttpPost(postURL);
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("phone", pphoneStr));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				post.setEntity(ent);
				HttpResponse responsePOST = client.execute(post);
				HttpEntity resEntity = responsePOST.getEntity();
				String result = EntityUtils.toString(resEntity, HTTP.UTF_8);
////////////split 해서 내장디비에 저장
				DBAdapter handler = DBAdapter.open(getApplicationContext());
				handler.delete(pphoneStr);
				handler.insert(result.substring(0,12), pphoneStr, result.substring(12), Info.p_RegistrationID);
				Cursor c = handler.gSelect(pphoneStr);
//확인..			while(c.moveToNext()){
//					String g = c.getString(c.getColumnIndex("Gphone"));
//					String d = c.getString(c.getColumnIndex("Pphone"));
//				}
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
