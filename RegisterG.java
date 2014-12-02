/* ���� ������Ʈ
 * ���ù���
 * 
 * ��ȣ�� ��� ��Ƽ��Ƽ.
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

	private String pphoneStr; //�Է¹޴� ȯ�� �ڵ��� ��ȣ
	private String gphoneStr; //�Է¹޴� ��ȣ�� �ڵ��� ��ȣ
	private String skey; //������ȣ
	private String strPhoneNumber; //����̽� �ڵ��� ��ȣ
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

		// ����̽� �ڵ��� ��ȣ ��������
		TelephonyManager clsManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);		
		strPhoneNumber= clsManager.getLine1Number(); 
		
		if (strPhoneNumber.length() > 12) {     //+8210 �� 010 ����. +82 �� 0���� ġȯ.
			strPhoneNumber = "0" + strPhoneNumber.substring(3);
		}

		GCMRegistrar.checkDevice(this);
		//�ܸ��� GCM�� �����ϴ��� �˻�
		Log.e("pass", "üũ 1����� ");
		GCMRegistrar.checkManifest(this);
		//�������α׷��� �Ŵ��佺Ʈ�� ����� �����Ǿ� �ִ� �� Ȯ��
		Log.e("pass", "üũ 2�����");						

		Info.g_RegistrationID = GCMRegistrar.getRegistrationId(this); // ��� ID�� ����
		
		if( Info.g_RegistrationID != null && !"".equals(Info.g_RegistrationID)) // ��ϵ� ���̵� �ִٸ�		
			Log.e("pass", "if���� ����. RegistrationID : " + Info.g_RegistrationID);		
		else { // GCM �� ����ϱ����� ��� ID�� ����
			GCMRegistrar.register(this, Info.SenderID); // ���� ID�� ��� �� ����
			Info.g_RegistrationID = GCMRegistrar.getRegistrationId(this); // ��� ID�� ����
			Log.e("pass", "else���� ����. RegistrationID : " + Info.g_RegistrationID);
		}
		
		ImageButton okBt = (ImageButton) findViewById(R.id.registerokBtn);
		okBt.setOnClickListener(new View.OnClickListener() { //Ȯ�� ��ư�� ������ �� �̺�Ʈ.
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// ȯ�� �ڵ��� ��ȣ �޾ƿ���
				EditText pphoneEdit1 = (EditText) findViewById(R.id.inputPPhone1);
				EditText pphoneEdit2 = (EditText) findViewById(R.id.inputPPhone2);
				EditText pphoneEdit3 = (EditText) findViewById(R.id.inputPPhone3);
				pphoneStr = pphoneEdit1.getText().toString()
						+ pphoneEdit2.getText().toString()
						+ pphoneEdit3.getText().toString();

				// ��ȣ�� �ڵ��� ��ȣ �޾ƿ���
				EditText gphoneEdit1 = (EditText) findViewById(R.id.inputGPhone1);
				EditText gphoneEdit2 = (EditText) findViewById(R.id.inputGPhone2);
				EditText gphoneEdit3 = (EditText) findViewById(R.id.inputGPhone3);
				gphoneStr = gphoneEdit1.getText().toString()
						+ gphoneEdit2.getText().toString()
						+ gphoneEdit3.getText().toString();

				// ����Ű �޾ƿ���
				EditText key = (EditText) findViewById(R.id.key);
				skey = key.getText().toString();

				if (pphoneEdit1.getText().toString().length() != 3) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("ȯ�� �ڵ��� ��ȣ �Է� ����")
							.setMessage("ù ��° ĭ�� 3�ڸ� ���ڸ� �Է��ϼž� �մϴ�.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else if (pphoneEdit2.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("ȯ�� �ڵ��� ��ȣ �Է� ����")
							.setMessage("�� ��° ĭ�� 4�ڸ� ���ڸ� �Է��ϼž� �մϴ�.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else if (pphoneEdit3.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("ȯ�� �ڵ��� ��ȣ �Է� ����")
							.setMessage("�� ��° ĭ�� 4�ڸ� ���ڸ� �Է��ϼž� �մϴ�.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else if (gphoneEdit1.getText().toString().length() != 3) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("��ȣ�� �ڵ��� ��ȣ �Է� ����")
							.setMessage("ù ��° ĭ�� 3�ڸ� ���ڸ� �Է��ϼž� �մϴ�.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else if (gphoneEdit2.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("��ȣ�� �ڵ��� ��ȣ �Է� ����")
							.setMessage("�� ��° ĭ�� 4�ڸ� ���ڸ� �Է��ϼž� �մϴ�.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else if (gphoneEdit3.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("��ȣ�� �ڵ��� ��ȣ �Է� ����")
							.setMessage("�� ��° ĭ�� 4�ڸ� ���ڸ� �Է��ϼž� �մϴ�.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else if (skey.length() != 6) {
					new AlertDialog.Builder(RegisterG.this)
							.setTitle("������ȣ �Է� ����")
							.setMessage("������ȣ�� Ȯ���� �ּ���.").setCancelable(false)
							.setPositiveButton("Ȯ��", null).show();
				} else {
					if (strPhoneNumber.equals(gphoneStr)) {			 //��ȣ�� ��ȣ�� ����̽��� ��ü �ڵ��� ��ȣ�� ��ġ�ϴ� ���.			
						new HttpTask().execute();						
					} else { 										//��ġ���� �ʴ°��.
						System.out.println(strPhoneNumber);
						System.out.println(gphoneStr);
						new AlertDialog.Builder(RegisterG.this)
								.setTitle("�ڵ��� ��ȣ �Է� ����")
								.setMessage("��ȣ�� �ڵ��� ��ȣ�� Ȯ�����ּ���.")
								.setCancelable(false)
								.setPositiveButton("Ȯ��", null).show();
					}
				}
			}
		});
	}

	class HttpTask extends AsyncTask<Void, Void, String> { //ȯ�� ��ȣ�� ����Ű�� �´��� Ȯ���ϰ� ������ ���.
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
				if (result.length() < 10) { /////////////////////////////////////������ȣ ����
					runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(RegisterG.this)
									.setTitle("�Է� ����")
									.setMessage("������ȣ�� ȯ�� �ڵ��� ��ȣ�� Ȯ�����ּ���.")
									.setCancelable(false)
									.setPositiveButton("Ȯ��", null).show();
						}
					});

				} else { //////////////////////////////////��������
/////////////RESULT ȯ�� REGID, ȯ�� ��ȣ .������ ����.
					DBAdapter handler = DBAdapter.open(getApplicationContext());
					handler.delete(gphoneStr);					
					handler.insert(gphoneStr, pphoneStr, Info.g_RegistrationID, result);
					Cursor c = handler.pSelect(gphoneStr);
		//�ڵ��� ��ȣ�� ����� ������ ����Ǿ����� Ȯ��..
		//			while(c.moveToNext()){ 
		//				String g = c.getString(c.getColumnIndex("Gphone"));
		//				String d = c.getString(c.getColumnIndex("Pphone"));
		//			}
					runOnUiThread(new Runnable() {
						public void run() {
							new AlertDialog.Builder(RegisterG.this)
									.setTitle("��ϿϷ�")
									.setMessage("��ϵǾ����ϴ�.\n * ����� ȭ������ �Ѿ� ���ϴ�. \n * 'ȯ���ڵ���' ȭ�鿡�� \"��ϿϷ�\" ��ư�� �����ּ���.")
									.setCancelable(false)
									.setPositiveButton(
											"Ȯ��",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub
													try {
														Info.setWho("1");	//��ȣ�ڷ� set
														Info.setP_phoneNum(pphoneStr); // ȯ�� �ڵ��� ��ȣ set
														Info.setG_phoneNum(gphoneStr); // ��ȣ�� �ڵ��� ��ȣ set
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
