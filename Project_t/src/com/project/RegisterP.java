/* ���� ������Ʈ
 * ���ù���
 * 
 * ȯ�� ��� ��Ƽ��Ƽ.
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
	private String skey; //������ȣ
	private String pphoneStr; //�Է¹��� ȯ�� �ڵ��� ��ȣ
	private String strPhoneNumber; //����̽� ��ü �ڵ��� ��ȣ
	Intent start_intent;
	private static final int ACTIVITY_START_MAIN = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_p);

		StrictMode.ThreadPolicy p = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(p);

		// ����̽� �ڵ��� ��ȣ ��������
		TelephonyManager clsManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		strPhoneNumber = clsManager.getLine1Number();

		if (strPhoneNumber.length() > 12) {  //+8210 �� 010 ����. +82 �� 0���� ġȯ.
			strPhoneNumber = "0" + strPhoneNumber.substring(3);
		}

		GCMRegistrar.checkDevice(this);
		// �ܸ��� GCM�� �����ϴ��� �˻�
		Log.e("pass", "üũ 1����� ");
		GCMRegistrar.checkManifest(this);
		// �������α׷��� �Ŵ��佺Ʈ�� ����� �����Ǿ� �ִ� �� Ȯ��
		Log.e("pass", "üũ 2�����");

		Info.p_RegistrationID = GCMRegistrar.getRegistrationId(this); // ��� ID�� ����

		if (Info.p_RegistrationID != null && !"".equals(Info.p_RegistrationID)) // ��ϵ� ���̵� �ִٸ�
			Log.e("pass", "if���� ����. RegistrationID : " + Info.p_RegistrationID);
		else // GCM �� ����ϱ����� ��� ID�� ����
		{
			GCMRegistrar.register(this, Info.SenderID); // ���� ID�� ��� �� ����
			Info.p_RegistrationID = GCMRegistrar.getRegistrationId(this); // ��� ID�� ����
			Log.e("pass", "else���� ����. RegistrationID : "
					+ Info.p_RegistrationID);
		}
		
		start_intent = new Intent(this, StartMain.class);
		ImageButton createKeyBt = (ImageButton) findViewById(R.id.createKeyBtn);
		createKeyBt.setOnClickListener(new View.OnClickListener() { //������ȥ �ޱ� ��ư ������ ��.

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ����Ű ��������
				Random rand = new Random();
				int key = rand.nextInt(900000) + 100000; //�������� 6�ڸ� ����Ű ����
				skey = String.valueOf(key);

				// ȯ���� �ڵ��� ��ȣ �޾ƿ���
				EditText pphoneEdit1 = (EditText) findViewById(R.id.inputPPhone1);
				EditText pphoneEdit2 = (EditText) findViewById(R.id.inputPPhone2);
				EditText pphoneEdit3 = (EditText) findViewById(R.id.inputPPhone3);

				CheckBox cb = (CheckBox) findViewById(R.id.checkBox1); //üũ�ڽ�
				pphoneStr = pphoneEdit1.getText().toString()
						+ pphoneEdit2.getText().toString()
						+ pphoneEdit3.getText().toString(); // �ڵ��� ��ȣ

				if (pphoneEdit1.getText().toString().length() != 3) {
					new AlertDialog.Builder(RegisterP.this)
							.setTitle("�ڵ��� ��ȣ �Է� ����")
							.setMessage("ù ��° ĭ�� 3�ڸ� ���ڸ� �Է��ϼž� �մϴ�.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else if (pphoneEdit2.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterP.this)
							.setTitle("�ڵ��� ��ȣ �Է� ����")
							.setMessage("�� ��° ĭ�� 4�ڸ� ���ڸ� �Է��ϼž� �մϴ�.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else if (pphoneEdit3.getText().toString().length() != 4) {
					new AlertDialog.Builder(RegisterP.this)
							.setTitle("�ڵ��� ��ȣ �Է� ����")
							.setMessage("�� ��° ĭ�� 4�ڸ� ���ڸ� �Է��ϼž� �մϴ�.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else if (!cb.isChecked()) { //�������� ó�� üũ�ڽ��� üũ�Ǿ��ִ��� Ȯ��.
					new AlertDialog.Builder(RegisterP.this)
							.setTitle("�������� ó�� ���� ����")
							.setMessage("�������� ó���� ���� ���ּ���.")
							.setCancelable(false).setPositiveButton("Ȯ��", null)
							.show();
				} else {
					if (strPhoneNumber.equals(pphoneStr)) { //�Է¹��� �ڵ��� ��ȣ�� ����̽� ��ü �ڵ��� ��ȣ�� ����.
						new HttpTask().execute();
					
						new AlertDialog.Builder(RegisterP.this) 	// ������ȣ  ���̾󷯱�
								.setTitle("�Ʒ� ������ȣ�� ��ȣ�� ���� �Է��� �ּ���")
								.setMessage(skey + "\n��ȣ�� ������ ����� �Ϸ�Ǹ� Ȯ�� ��ư�� �����ּ���.")
								.setCancelable(false)
								.setPositiveButton("��ϿϷ�",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												new AlertDialog.Builder(
														RegisterP.this)
														.setTitle("���Ȯ��")
														.setMessage( skey + "\n��ȣ�� ������ ����� �Ϸ�Ǿ����ϱ�?\n '��'�� ������ ����� ȭ������ �Ѿ�ϴ�.")
														.setCancelable(false)
														.setPositiveButton("��",new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick( DialogInterface dialog, int which) {
																		// TODO
																		// Auto-generated method stub
																		new HttpTask3().execute(); //guide id ��������
																		try {
																			Info.setWho("2"); //ȯ�ڷ� set
																			Info.setWho(2);
																		} catch (IOException e) {
																			// TODO
																			// Auto-generated catch block
																			e.printStackTrace();
																		}
																		//ó�� ȭ������ ���ư�.
																		startActivityForResult( start_intent, ACTIVITY_START_MAIN);
																	}
																})
								/*������ȣ �ƴϿ� ����*/		.setNegativeButton( "�ƴϿ�", new DialogInterface.OnClickListener() {
																	@Override
																	public void onClick(
																			DialogInterface dialog,
																			int which) {
																		// TODO
																		// Auto-generated  method stub
																		new HttpTask2().execute();
																		new AlertDialog.Builder(RegisterP.this)
																				.setTitle("������")
																				.setMessage(
																						"�ƴϿ��� �����ϼ̽��ϴ�.\n �ٽ� ������ȣ�� �޾��ּ���.")
																				.setPositiveButton("Ȯ��",null)
																				.show();
																	}
																}).show();
											}
										}).show();
					} else {
						System.out.println(strPhoneNumber);  //�ڵ�����ȣ ���� �ʴ� ���
						System.out.println(pphoneStr);
						new AlertDialog.Builder(RegisterP.this)
								.setTitle("�ڵ��� ��ȣ �Է� ����")
								.setMessage("�ڵ��� ��ȣ�� Ȯ�����ּ���.")
								.setCancelable(false)
								.setPositiveButton("Ȯ��", null).show();
					}
				}
			}
		});
	}

	class HttpTask extends AsyncTask<Void, Void, String> { //�Է¹��� ȯ�� ��ȣ�� ������ȣ, ȯ�� regId ��� ����.

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

	class HttpTask2 extends AsyncTask<Void, Void, String> { //��ҹ�ư�� ������ ��� ������ ������ ���ڵ� ����.

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

	/*** ��ȣ�� ��ȣ, regid �������� **/
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
////////////split �ؼ� ������ ����
				DBAdapter handler = DBAdapter.open(getApplicationContext());
				handler.delete(pphoneStr);
				handler.insert(result.substring(0,12), pphoneStr, result.substring(12), Info.p_RegistrationID);
				Cursor c = handler.gSelect(pphoneStr);
//Ȯ��..			while(c.moveToNext()){
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
