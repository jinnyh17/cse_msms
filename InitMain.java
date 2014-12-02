/* 졸업 프로젝트
 * 무시무시
 * 
 * 등록화면 액티비티. 
 */package com.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class InitMain extends Activity {

	private static final int ACTIVITY_REGISTERG_MAIN=0;
	private static final int ACTIVITY_REGISTERP_MAIN=0;
	Intent registerG_intent;
	Intent registerP_intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.init_main);
		
		//Intent
		registerG_intent = new Intent(this, RegisterG.class); 
				
		ImageButton regGBtn = (ImageButton)findViewById(R.id.registerGBtn);
		regGBtn.setOnClickListener(new View.OnClickListener(){ //보호자 등록버튼

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(registerG_intent, ACTIVITY_REGISTERG_MAIN);
			}				
		});
		
		registerP_intent = new Intent(this, RegisterP.class); 
		ImageButton regPBtn = (ImageButton)findViewById(R.id.registerPBtn);
		regPBtn.setOnClickListener(new View.OnClickListener(){ //환자 등록버튼

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(registerP_intent, ACTIVITY_REGISTERP_MAIN);
			}				
		});
		
	}

}
