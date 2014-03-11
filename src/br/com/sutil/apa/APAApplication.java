package br.com.sutil.apa;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.util.Log;

public class APAApplication extends Application {
	
	private DatabaseManager databaseManager;
	private final Map<Class<?>, Class<?>> converters = new HashMap<Class<?>, Class<?>>();
	
	@Override
	public void onCreate() {
		Log.d("APA", "application start");
		super.onCreate();
		this.databaseManager = new DatabaseManager(this);
	}
	
	@Override
	public void onTerminate() {
		if(this.databaseManager != null){
			this.databaseManager.close();
		}
		super.onTerminate();
	}
	
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	public Map<Class<?>, Class<?>> getConverters() {
		return converters;
	}

}
