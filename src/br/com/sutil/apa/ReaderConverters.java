package br.com.sutil.apa;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import br.com.sutil.apa.annotation.APAConverter;
import dalvik.system.DexFile;

public class ReaderConverters {

	private static final String TAG = ReaderConverters.class.getCanonicalName();

	private Context context;
	private String packageName;
	private Map<Class<?>, Class<?>> convertersMap = new HashMap<Class<?>, Class<?>>();

	public ReaderConverters(Context context) {
		this.context = context;
		this.packageName = context.getPackageName();
	}

	public Map<Class<?>, Class<?>> getConverters() {
		Log.i(TAG, "Reader converter");
		Enumeration<String> classNames = getClassNames();
		while (classNames.hasMoreElements()) {
			String name = (String) classNames.nextElement();
			discoverConverters(name);
		}
		return convertersMap;
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

	private void discoverConverters(String name) {
		if (name.contains(packageName)) {
			try {
				Class<?> discoveredClass = Class.forName(name, true, context.getClass().getClassLoader());
				APAConverter annotation = discoveredClass.getAnnotation(APAConverter.class);
				if (annotation != null) {
					convertersMap.put(annotation.forClass(), discoveredClass);
					Log.i(TAG, "Converter encontrado: " + discoveredClass.getCanonicalName());
				}
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "Class not found " + name);
			}
		}
	}

}
