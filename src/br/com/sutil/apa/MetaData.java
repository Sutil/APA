package br.com.sutil.apa;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class MetaData {
	
	private static final String DB_NAME = "DB_NAME";
    private static final String DB_VERSION = "DB_VERSION";
    private static final String TAG = "MetaData";
    
    
    public static String getDBName(Context context) {
		String dbName = getMetaData(context, DB_NAME);
		if (dbName == null) {
			dbName = "Application.db";
		}
		return dbName;
	}

	public static int getDBVersion(Context context) {
		int dbVersion = getMetaDataInt(context, DB_VERSION);
		if (dbVersion > 0) {
			return dbVersion;
		}
		return 1;
	}
	
	private static int getMetaDataInt(Context context, String name) {
		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 128);
			return ai.metaData.getInt(name);
		} catch (Exception e) {
			Log.w(TAG, "Couldn't find meta data string: " + name);
		}
		return 0;
	}
    
    
    private static String getMetaData(Context context, String name) {
		PackageManager pm = context.getPackageManager();
		try {
			ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 128);
			return ai.metaData.getString(name);

		} catch (Exception e) {
			Log.w(TAG, "Couldn't find meta data string: " + name);
		}
		return null;
    }

}
