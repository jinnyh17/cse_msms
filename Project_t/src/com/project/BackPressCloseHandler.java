/* 졸업 프로젝트
 * 무시무시
 * 
 * '뒤로가기 버튼' 두번 누를 시에 종료되는 클래스.
 */
package com.project;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {
	private long BackKeyPressedTime = 0;
	private Toast toast;
		
	private Activity activity; //종료 될 액티비티
	
	public BackPressCloseHandler(Activity context){ //생성자
		this.activity = context;
	}
	
	public void onBackPressed(){
		if (System.currentTimeMillis() > BackKeyPressedTime + 2000){ //뒤로 가기 버튼 한번 눌렀을 경우.
			BackKeyPressedTime = System.currentTimeMillis(); // 시간재기
			showGuide();
			return;
		}
		if(System.currentTimeMillis() <= BackKeyPressedTime + 2000){ //2초 내에 두번 눌렀을 경우
			GCMRegistrar.onDestroy(this.activity); /*Registrar 해제. 중요함.*/
			
			activity.finish(); //activity 종료
			toast.cancel(); //토스트 끄기
		}
	}
	
	public void showGuide(){ //알림 토스트
		toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", 
				Toast.LENGTH_SHORT);
		toast.show();
	}	
}
