/* 졸업 프로젝트
 * 무시무시
 * 
 * 내장 디비 adapter 클래스.
 * 상대방 사용자의 정보와 자신의 정보를 저장한다.
 * 저장하는 데이터는 핸드폰 번호와 regID.
 */
package com.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {

	private static final String TABLE_NAME = "USERS"; //내장 디비 table name
	MySQLiteOpenHelper helper;
	SQLiteDatabase db;
	
	public DBAdapter(Context context){
		helper = new MySQLiteOpenHelper(context, TABLE_NAME+".db" ,null, 1);
	}
	
	public static DBAdapter open(Context context){
		return new DBAdapter(context);
	}
	
	public void close(){
		helper.close();
	}
	
	public void insert(String gphone, String pphone, String gcmid_g, String gcmid_p){ //테이블에 레코드 삽입.
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("Gphone", gphone);
		values.put("Pphone", pphone);
		values.put("Gcmid_g", gcmid_g);
		values.put("Gcmid_p", gcmid_p);
		
		long r = db.insert(TABLE_NAME, null, values);
		
	}
	
	public void delete(String pphone){ //레코드 삭제.
		db = helper.getWritableDatabase();
		long r = db.delete(TABLE_NAME, "Pphone=?", new String[]{pphone});
	}
	
	public void pphoneUpdate(String gphone, String pphone){  //환자 핸드폰 바꾸는 경우.
		db = helper.getWritableDatabase();		
		String sql = "UPDATE "+TABLE_NAME +" SET Pphone = '"+pphone+"' WHERE Gphone ='"+gphone+"'";
		db.execSQL(sql);
	}
	
	public void gphoneUpdate(String gphone, String newgphone){ //보호자 핸드폰 바꾸는 경우.
		db = helper.getWritableDatabase();		
		String sql = "UPDATE "+TABLE_NAME +" SET Gphone = '"+newgphone+"' WHERE Gphone ='"+gphone+"'";
		db.execSQL(sql);
	}
	
	public Cursor gSelect(String pphone){ //사용자가 환자일 경우, 보호자 정보 가져오기.
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE Pphone='"+pphone+"' " , null);
		return cursor;		
	}
	
	public Cursor pSelect(String gphone){ //사용자가 보호자일 경우, 환자 정보 가져오기.
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE Gphone='"+gphone+"' " , null);
		return cursor;	
	}
}
