package br.com.sutil.apa.annotation.converter;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import br.com.sutil.apa.annotation.APAConverter;
import br.com.sutil.apa.repository.ConverterType;

@APAConverter(forClass = String.class)
public class StringConverter implements ConverterType{
	private static final String TAG = StringConverter.class.getCanonicalName();

	@Override
	public void putValue(String column, Object value, ContentValues values) {
		if(value != null){
			values.put(column, (String) value);
		}
		
	}

	@Override
	public void getValue(Object instance, Field field, Cursor c, int index) {
		String value = c.getString(index);
		if(value != null){
			try {
				field.set(instance, value);
			} catch (IllegalArgumentException e) {
				Log.d(TAG, "IllegalArgument");
			} catch (IllegalAccessException e) {
				Log.d(TAG, "IllegalAccess");
			}
		}
		
	}

}
