/* 졸업 프로젝트
 * 무시무시
 * 
 * 사용자 정보 및 GCM을 이용할 때 필요한 정보를 저장하는 클래스.
 */
package com.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Environment;

public class Info {
	public static final String host = "http://168.188.128.127"; // server
	
	public final static String SenderID = "369155757248"; // 프로젝트 ID
	public static String p_RegistrationID = "";  // 환자 등록 ID
	public static String g_RegistrationID = "";  // 보호자 등록 ID
	public static String API_Key = "AIzaSyDj_5n4kcSVeCHsCABuBl95l7GIMpMOxMQ"; // API_Key
	public static String p_phoneNum = ""; // 전화번호
	public static String g_phoneNum = ""; // 전화번호
	public static int who=10;// 1 or 2
	 
	public static double latitude =0.0; //한자 위치 위도
	public static double longitude =0.0; //환자 위치 경도
	
	public static double getLatitude() {
		return latitude;
	}

	public static void setLatitude(double latitude) {
		Info.latitude = latitude;
	}

	public static double getLongitude() {
		return longitude;
	}

	public static void setLongitude(double longitude) {
		Info.longitude = longitude;
	}
	
	public static String getP_RegistrationID() {
		return p_RegistrationID;
	}

	public static void setP_RegistrationID(String p_RegistrationID) {
		Info.p_RegistrationID = p_RegistrationID;
	}

	public static String getG_RegistrationID() {
		return g_RegistrationID;
	}

	public static void setG_RegistrationID(String g_RegistrationID) {
		Info.g_RegistrationID = g_RegistrationID;
	}

	public static String getP_phoneNum() {
		return p_phoneNum;
	}

	public static void setP_phoneNum(String p_phoneNum) {
		Info.p_phoneNum = p_phoneNum;
	}

	public static String getG_phoneNum() {
		return g_phoneNum;
	}

	public static void setG_phoneNum(String g_phoneNum) {
		Info.g_phoneNum = g_phoneNum;
	}

	public static void setWho(int who) {
		Info.who = who;
	}
	
	public static int get__who() {
		return who;
	}
	
	public static void setWho(String w) throws IOException { //I'am patient !(2) / I'am guide !(1) /not user(10)저장
		
		String str_Path = Environment.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(str_Path + "/who.txt");
	
		if (file.exists() == false) {
			try {
			file.createNewFile();
			} catch (IOException e) {			
			} 
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file , false));
		bw.write(w);
		setWho(Integer.parseInt(w));
		bw.close();		
	}
	
	public static int getWho() throws IOException //who are you? 
	{
		int w= 10;
		String temp;
		String str_Path = Environment.getExternalStorageDirectory().getAbsolutePath();
		FileInputStream file =null;
		try {
			file = new FileInputStream(str_Path + "/who.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block		
			return w;
		}	
		
		BufferedReader br = new BufferedReader(new InputStreamReader(file));
		try {
			while((temp = br.readLine())!= null){					
				w=Integer.parseInt(temp,10);		
		}			
			br.close();					
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return w;		
	}
}
