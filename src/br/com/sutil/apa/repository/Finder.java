package br.com.sutil.apa.repository;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.com.sutil.apa.APAApplication;
import br.com.sutil.apa.annotation.Type;
import br.com.sutil.apa.repository.wrapper.WrapperClass;

public class Finder {

	private static final String TAG = Finder.class.getName();

	private SQLiteDatabase database;
	private WrapperClass<?> classWrapper;
	private Context context;

	public Finder(SQLiteDatabase database, WrapperClass<?> classWrapper, Context context) {
		this.classWrapper = classWrapper;
		this.database = database;
		this.context = context;
		Log.d(TAG, "finder criado");
	}

	private Cursor executeQuery(String whereClause, String[] whereArgs) {
		String table = classWrapper.getTableName();
		Log.d(TAG, "executando query on table: " + table);
		return this.database.query(table, null, whereClause, whereArgs, null, null, null);
	}

	public List<Object> findAll() {
		List<Object> entities = new ArrayList<Object>();
		Cursor c = executeQuery(null, null);
		if (c.moveToFirst()) {
			do {
				try {
					entities.add(processaCursor(c));
				} catch (IllegalAccessException e) {
					Log.d(TAG, e.getMessage());
				} catch (InstantiationException e) {
					Log.d(TAG, e.getMessage());
				}
			} while (c.moveToNext());
		}
		Log.i(TAG, "query returned " + c.getCount() + " results");
		Log.i(TAG, entities.size() + " objcts");
		c.close();
		return entities;
	}

	public Object findOne(long id) {
		Cursor c = executeQuery(classWrapper.getColumnNameId() + "=?", new String[] { String.valueOf(id) });
		Object obj = null;
		if (c.moveToFirst()) {
			try {
				obj = processaCursor(c);
			} catch (IllegalAccessException e) {
				Log.d(TAG, e.getMessage());
			} catch (InstantiationException e) {
				Log.d(TAG, e.getMessage());
			}
		}
		return obj;
	}

	private Object processaCursor(Cursor c) throws IllegalAccessException, InstantiationException {
		Object model = classWrapper.getClazz().newInstance();
		Log.i(TAG, "model created: " + model);
		for (Field field : classWrapper.getAllMapedFields()) {
			setValueInField(field, model, c);
		}
		return model;
	}

	private void setValueInField(Field field, Object model, Cursor c) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		String columnName = classWrapper.getColumnName(field);
		int index = c.getColumnIndex(columnName);
		field.setAccessible(true);
		Class<?> fieldType = field.getType();
		if ((fieldType.equals(Boolean.class)) || (fieldType.equals(Boolean.TYPE))) {
			field.set(model, Boolean.valueOf(c.getInt(index) != 0));
		} else if (fieldType.equals(Character.TYPE)) {
			field.set(model, Character.valueOf(c.getString(index).charAt(0)));
		} else if (fieldType.equals(java.util.Date.class) && c.getLong(index) > 0) {
			field.set(model, new java.util.Date(c.getLong(index)));
		} else if (fieldType.equals(java.sql.Date.class)) {
			field.set(model, new java.sql.Date(c.getLong(index)));
		} else if ((fieldType.equals(Double.class)) || (fieldType.equals(Double.TYPE))) {
			field.set(model, Double.valueOf(c.getDouble(index)));
		} else if ((fieldType.equals(Float.class)) || (fieldType.equals(Float.TYPE))) {
			field.set(model, Float.valueOf(c.getFloat(index)));
		} else if ((fieldType.equals(Integer.class)) || (fieldType.equals(Integer.TYPE))) {
			field.set(model, Integer.valueOf(c.getInt(index)));
		} else if ((fieldType.equals(Long.class)) || (fieldType.equals(Long.TYPE))) {
			field.set(model, Long.valueOf(c.getLong(index)));
		} else if (fieldType.equals(String.class)) {
			field.set(model, c.getString(index));
		} else if (fieldType.equals(BigDecimal.class)) {
			field.set(model, new BigDecimal(c.getDouble(index)));
		} else {
			Type type = field.getAnnotation(Type.class);
			if (type != null) {
				setValueFielCustomType(c, index, field, model);
			}
		}

	}

	private void setValueFielCustomType(Cursor c, int index, Field field, Object model) throws IllegalAccessException, InstantiationException {
		Log.i(TAG, "custom value");
		Class<?> type = field.getType();
		Class<?> converter = getConverter(type);
		if (converter != null) {
			Object instance = converter.newInstance();
			ConverterType conv = (ConverterType) instance;
			conv.getValue(model, field, c, index);
		}

	}

	private Class<?> getConverter(Class<?> typeForConverter) {
		APAApplication app = (APAApplication) context.getApplicationContext();
		return app.getConverters().get(typeForConverter);
	}

}
