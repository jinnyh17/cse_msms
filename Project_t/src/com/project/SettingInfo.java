/* ���� ������Ʈ
 * ���ù���
 * 
 * ����� �ڵ��� ��ȣ Ȯ�� ��Ƽ��Ƽ.
 */
package com.project;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingInfo extends Activity {

	TextView gphoneText; //��ȣ�� �ڵ��� ��ȣ �ؽ�Ʈ�ʵ�
	TextView pphoneText; //ȯ�� �ڵ��� ��ȣ �ؽ�Ʈ�ʵ�

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_info);

		gphoneText = (TextView) findViewById(R.id.textView2);
		pphoneText = (TextView) findViewById(R.id.textView4);

		gphoneText.setText(Info.getG_phoneNum().substring(0, 3)+" - "+Info.getG_phoneNum().substring(3, 7)+" - "+Info.getG_phoneNum().substring(7));
		pphoneText.setText(Info.getP_phoneNum().substring(0, 3)+" - "+Info.getP_phoneNum().substring(3, 7)+" - "+Info.getP_phoneNum().substring(7));
	}
}
