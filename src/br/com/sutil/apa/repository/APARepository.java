package br.com.sutil.apa.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.com.sutil.apa.APAApplication;
import br.com.sutil.apa.annotation.Entity;
import br.com.sutil.apa.repository.wrapper.WrapperClass;

public abstract class APARepository<T> {

	private static final String TAG = APARepository.class.getSimpleName();
	protected static final String Lock = "dblock";

	private Class<T> type;
	protected Context context;

	protected SQLiteDatabase database;

	public APARepository(Class<T> type, Context context) {
		this.type = type;
		this.context = context;
	}

	public long save(T instance) {
		WrapperClass<T> wrapperClass = new WrapperClass<T>(type);
		Long id = wrapperClass.getId(instance);
		if (id != null && id != 0L) {
			return update(instance, wrapperClass);
		} else {
			return insert(instance);
		}
	}

	public void open() {
		APAApplication app = (APAApplication) context.getApplicationContext();
		this.database = app.getDatabaseManager().open();
	}

	public long insert(Object instance) {
		open();
		List<ColumnValues> columnValues = getColumnValues(instance);
		ContentValues contentValues = getContentValues(columnValues);
		long id = database.insert(instance.getClass().getAnnotation(Entity.class).name(), "", contentValues);
		Log.d(TAG, "inserted with id: " + id);
		this.database.close();
		return id;
	}

	public long update(T instance, WrapperClass<T> wrapper) {
		APAApplication app = (APAApplication) context.getApplicationContext();
		this.database = app.getDatabaseManager().open();
		List<ColumnValues> cvs = getColumnValuesForUpdate(instance);
		Long id = wrapper.getId(instance);
		String columnId = wrapper.getColumnNameId();
		database.update(instance.getClass().getAnnotation(Entity.class).name(), getContentValuesForUpdate(cvs), columnId + "=" + id, null);
		Log.i(TAG, "inserted with id: " + id);
		this.database.close();
		return id;
	}

	private List<ColumnValues> getColumnValuesForUpdate(Object instance) {
		List<ColumnValues> columns = new ArrayList<ColumnValues>();
		WrapperClass<T> wrapperClass = new WrapperClass<T>(type);
		List<Field> allMapedFields = wrapperClass.getAllMapedFields();

		for (Field field : allMapedFields) {
			ColumnValues cv = ColumnValues.newInstance(field, instance);
			if (cv != null) {
				columns.add(cv);
			}
		}
		return columns;
	}

	private List<ColumnValues> getColumnValues(Object instance) {
		List<ColumnValues> columns = new ArrayList<ColumnValues>();
		WrapperClass<T> wrapperClass = new WrapperClass<T>(type);
		List<Field> allMapedFields = wrapperClass.getAllMapedFields();

		for (Field field : allMapedFields) {
			ColumnValues cv = ColumnValues.newInstance(field, instance);
			if (cv != null && !cv.isId()) {
				columns.add(cv);
			}
		}
		return columns;
	}

	private ContentValues getContentValues(List<ColumnValues> cvs) {
		ContentValues values = new ContentValues();
		for (ColumnValues cv : cvs) {
			putValue(cv, values);
		}
		return values;
	}

	private ContentValues getContentValuesForUpdate(List<ColumnValues> cvs) {
		ContentValues values = new ContentValues();
		for (ColumnValues cv : cvs) {
			putValue(cv, values);
		}
		return values;
	}

	private void putValue(ColumnValues cv, ContentValues values) {
		Object value = cv.getValue();
		String columnName = cv.getColumnName();
		if (value != null) {
			if ((value.getClass().equals(Boolean.class)) || (value.getClass().equals(Boolean.TYPE))) {
				values.put(cv.getColumnName(), (Boolean) value);
			} else if (value.equals(java.util.Date.class)) {
				values.put(columnName, ((java.util.Date) value).getTime());
			} else if (value.equals(java.sql.Date.class)) {
				values.put(columnName, ((java.sql.Date) value).getTime());
			} else if ((value.equals(Double.class)) || (value.equals(Double.TYPE))) {
				values.put(columnName, (Double) value);
			} else if ((value.equals(Float.class)) || (value.equals(Float.TYPE))) {
				values.put(columnName, (Float) value);
			} else if ((value.getClass().equals(Integer.class)) || (value.getClass().equals(Integer.TYPE))) {
				values.put(columnName, (Integer) value);
			} else if ((value.getClass().equals(Long.class)) || (value.getClass().equals(Long.TYPE))) {
				values.put(columnName, (Long) value);
			} else if ((value.getClass().equals(String.class)) || (value.getClass().equals(Character.TYPE))) {
				values.put(columnName, value.toString());
			}
		}
		
	}

}
