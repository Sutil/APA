package br.com.sutil.apa.repository;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.Cursor;

public interface ConverterType {
	
	/**
	 * 
	 * @param column
	 * @param object
	 * @param values
	 */
	public void putValue(String column, Object object, ContentValues values);
	
	/**
	 * 
	 * @param field
	 * @param c
	 * @param index
	 */
	public void getValue(Object instance, Field field, Cursor c, int index);

}
