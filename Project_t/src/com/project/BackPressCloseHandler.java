/* ���� ������Ʈ
 * ���ù���
 * 
 * '�ڷΰ��� ��ư' �ι� ���� �ÿ� ����Ǵ� Ŭ����.
 */
package com.project;

import com.google.android.gcm.GCMRegistrar;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {
	private long BackKeyPressedTime = 0;
	private Toast toast;
		
	private Activity activity; //���� �� ��Ƽ��Ƽ
	
	public BackPressCloseHandler(Activity context){ //������
		this.activity = context;
	}
	
	public void onBackPressed(){
		if (System.currentTimeMillis() > BackKeyPressedTime + 2000){ //�ڷ� ���� ��ư �ѹ� ������ ���.
			BackKeyPressedTime = System.currentTimeMillis(); // �ð����
			showGuide();
			return;
		}
		if(System.currentTimeMillis() <= BackKeyPressedTime + 2000){ //2�� ���� �ι� ������ ���
			GCMRegistrar.onDestroy(this.activity); /*Registrar ����. �߿���.*/
			
			activity.finish(); //activity ����
			toast.cancel(); //�佺Ʈ ����
		}
	}
	
	public void showGuide(){ //�˸� �佺Ʈ
		toast = Toast.makeText(activity, "\'�ڷ�\'��ư�� �ѹ� �� �����ø� ����˴ϴ�.", 
				Toast.LENGTH_SHORT);
		toast.show();
	}	
}
