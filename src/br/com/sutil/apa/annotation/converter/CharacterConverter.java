package br.com.sutil.apa.annotation.converter;

import java.lang.reflect.Field;

import com.google.common.base.Strings;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import br.com.sutil.apa.annotation.APAConverter;
import br.com.sutil.apa.repository.ConverterType;

@APAConverter(forClass = char.class)
public class CharacterConverter implements ConverterType {
	
	private static final String TAG = CharacterConverter.class.getCanonicalName();

	@Override
	public void putValue(String column, Object value, ContentValues values) {
		if(value != null){
			values.put(column, value.toString());
		}
		
	}

	@Override
	public void getValue(Object instance, Field field, Cursor c, int index) {
		String value = c.getString(index);
		if(!Strings.isNullOrEmpty(value)){
			try {
				field.set(instance, value.charAt(0));
			} catch (IllegalArgumentException e) {
				Log.d(TAG, "IllegalArgument");
			} catch (IllegalAccessException e) {
				Log.d(TAG, "IllegalAccess");
			}
		}
		
	}

}
