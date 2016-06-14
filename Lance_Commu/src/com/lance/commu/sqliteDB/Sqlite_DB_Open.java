package com.lance.commu.sqliteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite_DB_Open extends SQLiteOpenHelper{
	
	public Sqlite_DB_Open(Context context){
		super(context,"db_user",null,2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		 String sql = "create table db_user("+  
		            "_code integer primary key autoincrement,"+  
		            "name text,"+
		            "phone_number text)"; 
		 db.execSQL(sql); 
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS db_user");
		onCreate(db);
	}
}
