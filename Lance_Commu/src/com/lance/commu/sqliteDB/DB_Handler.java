package com.lance.commu.sqliteDB;

import java.sql.SQLException;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB_Handler {
	private Context ctx;  
    private Sqlite_DB_Open helper;  
    private SQLiteDatabase db; 
    
    public DB_Handler(Context ctx) {  
        this.ctx = ctx;  
        helper = new Sqlite_DB_Open(ctx);  
        db = helper.getWritableDatabase(); //DB∞° open µ   
    }  
    
    public static DB_Handler open(Context ctx) throws SQLException{  
    	DB_Handler handler = new DB_Handler(ctx);  
        return handler;  
    }  
      
    public void close(){  
    	if(helper !=null)
    		helper.close();  
    }  
    
    //SQLπÆ ¿€º∫  
    //INSERT  
    public void insert(String name, String phone_number){  
    	ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("phone_number", phone_number);
		if(db.isOpen()){
			db.insert("db_user", null, values);
		}
    }  
    
    //SELECT  
    public Cursor selectAll(){
    	Cursor cursor=null;
    	
    	if(db.isOpen()){
    		cursor = db.query(true, "db_user",   
	            new String[]{"_code", "name", "phone_number"},   
	            null, null, null, null, null, null);  
    	}
    	
        return cursor;  
    }  
    
    //delete
    public void removeData() {
        //String sql = "delete from " + "db_user" + " where phone_number = " + phone_number + ";";
    	String sql = "delete from " + "db_user;";
        if(db.isOpen()){
        	db.execSQL(sql);
        }
    }
}
