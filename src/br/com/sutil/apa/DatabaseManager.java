package br.com.sutil.apa;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseManager {
	
	private static final String TAG = "DatabaseManager";
	
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase database;
	
	
	public DatabaseManager(Context context) {
		Log.d(TAG, "DatabaseManager");
		this.databaseHelper = new DatabaseHelper(context);
	}
	
	public SQLiteDatabase getDatabase() {
		return database;
	}
	
	public SQLiteDatabase open(){
		if(this.database == null || !this.database.isOpen()){
			this.database = databaseHelper.getWritableDatabase();
		}
		return this.database;
	}
	
	public void close(){
		if(this.database != null && this.database.isOpen()){
			this.database.close();
		}
	}

}
