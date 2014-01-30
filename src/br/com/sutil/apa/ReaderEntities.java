package br.com.sutil.apa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import br.com.sutil.apa.annotation.Entity;
import dalvik.system.DexFile;

public class ReaderEntities {

	private static final String TAG = "ReaderEntities";

	private Context context;
	private String packageName;
	private ArrayList<Class<?>> entityClasses = new ArrayList<Class<?>>();

	public ReaderEntities(Context context) {
		this.context = context;
		this.packageName = context.getPackageName();
	}

	public ArrayList<Class<?>> getEntityClasses() {
		Log.d(TAG, "Reader class");
		Enumeration<String> classNames = getClassNames();
		while (classNames.hasMoreElements()) {
			String name = (String) classNames.nextElement();
			discoverClass(name);
		}
		return entityClasses;
	}

	private Enumeration<String> getClassNames() {
		try {
			String path = context.getPackageManager().getApplicationInfo(packageName, 0).sourceDir;
			DexFile dexfile = new DexFile(path);
			return dexfile.entries();
		} catch (NameNotFoundException e) {
			Log.e(TAG, "package name not found");
		} catch (IOException e) {
			Log.d(TAG, "Dexfile error");
		}
		return null;
	}

	private void discoverClass(String name) {
		if (name.contains(packageName)) {
			try {

				Class<?> discoveredClass = Class.forName(name, true, context.getClass().getClassLoader());
				Entity entity = discoveredClass.getAnnotation(Entity.class);
				if (entity != null) {
					this.entityClasses.add(discoveredClass);
					Log.i(TAG, "Class found: " + entity.name());
				}
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "Class not found " + name);
			}
		}
	}

}
