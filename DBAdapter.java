/* ���� ������Ʈ
 * ���ù���
 * 
 * ���� ��� adapter Ŭ����.
 * ���� ������� ������ �ڽ��� ������ �����Ѵ�.
 * �����ϴ� �����ʹ� �ڵ��� ��ȣ�� regID.
 */
package com.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {

	private static final String TABLE_NAME = "USERS"; //���� ��� table name
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
	
	public void insert(String gphone, String pphone, String gcmid_g, String gcmid_p){ //���̺� ���ڵ� ����.
		db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("Gphone", gphone);
		values.put("Pphone", pphone);
		values.put("Gcmid_g", gcmid_g);
		values.put("Gcmid_p", gcmid_p);
		
		long r = db.insert(TABLE_NAME, null, values);
		
	}
	
	public void delete(String pphone){ //���ڵ� ����.
		db = helper.getWritableDatabase();
		long r = db.delete(TABLE_NAME, "Pphone=?", new String[]{pphone});
	}
	
	public void pphoneUpdate(String gphone, String pphone){  //ȯ�� �ڵ��� �ٲٴ� ���.
		db = helper.getWritableDatabase();		
		String sql = "UPDATE "+TABLE_NAME +" SET Pphone = '"+pphone+"' WHERE Gphone ='"+gphone+"'";
		db.execSQL(sql);
	}
	
	public void gphoneUpdate(String gphone, String newgphone){ //��ȣ�� �ڵ��� �ٲٴ� ���.
		db = helper.getWritableDatabase();		
		String sql = "UPDATE "+TABLE_NAME +" SET Gphone = '"+newgphone+"' WHERE Gphone ='"+gphone+"'";
		db.execSQL(sql);
	}
	
	public Cursor gSelect(String pphone){ //����ڰ� ȯ���� ���, ��ȣ�� ���� ��������.
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE Pphone='"+pphone+"' " , null);
		return cursor;		
	}
	
	public Cursor pSelect(String gphone){ //����ڰ� ��ȣ���� ���, ȯ�� ���� ��������.
		db = helper.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE Gphone='"+gphone+"' " , null);
		return cursor;	
	}
}
