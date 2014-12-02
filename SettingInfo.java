/* 졸업 프로젝트
 * 무시무시
 * 
 * 사용자 핸드폰 번호 확인 액티비티.
 */
package com.project;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingInfo extends Activity {

	TextView gphoneText; //보호자 핸드폰 번호 텍스트필드
	TextView pphoneText; //환자 핸드폰 번호 텍스트필드

	
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
