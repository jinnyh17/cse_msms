/* 졸업 프로젝트
 * 무시무시
 * 
 * SQLite Helper.
 * 내장 디비 테이블 생성하고 업데이트 함.
 */
package com.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MySQLiteOpenHelper extends SQLiteOpenHelper{
	
	private static final String TABLE_NAME = "USERS"; //내장 디비 table name
	private static final String TABLE_CREATE =  //테이블 생성
			"CREATE TABLE " +TABLE_NAME+
					"(Gphone text not null Primary Key," +
					" Pphone text not null,	" +
					"Gcmid_g text not null,	" +
					"Gcmid_p text not null) ";
			
	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		//테이블 삭제
		String sql="drop table if exist test";
		db.execSQL(sql);
		onCreate(db);
	}
}