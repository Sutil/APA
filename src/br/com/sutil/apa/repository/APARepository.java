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
import br.com.sutil.apa.annotation.Id;
import br.com.sutil.apa.repository.wrapper.WrapperClass;

public abstract class APARepository {

	private static final String TAG = APARepository.class.getSimpleName();
	protected static final String Lock = "dblock";

	private Class<?> type;
	protected Context context;

	protected SQLiteDatabase database;

	public APARepository(Class<?> type, Context context) {
		this.type = type;
		this.context = context;
	}

	public long save(Object instance) {
		WrapperClass wrapperClass = new WrapperClass(type);
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
		this.database.close();
		return id;
	}

	public long update(Object instance, WrapperClass wrapper) {
		APAApplication app = (APAApplication) context.getApplicationContext();
		this.database = app.getDatabaseManager().open();
		List<ColumnValues> cvs = getColumnValuesForUpdate(instance);
		Long id = wrapper.getId(instance);
		String columnId = wrapper.getColumnNameId();
		database.update(instance.getClass().getAnnotation(Entity.class).name(), getContentValuesForUpdate(cvs), columnId + "=" + id, null);
		this.database.close();
		return id;
	}
	
	

	private List<ColumnValues> getColumnValuesForUpdate(Object instance) {
		List<ColumnValues> columns = new ArrayList<ColumnValues>();
		WrapperClass wrapperClass = new WrapperClass(type);
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
		WrapperClass wrapperClass = new WrapperClass(type);
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
			} else if (value.getClass().equals(java.util.Date.class)) {
				values.put(columnName, ((java.util.Date) value).getTime());
			} else if (value.getClass().equals(java.sql.Date.class)) {
				values.put(columnName, ((java.sql.Date) value).getTime());
			} else if ((value.getClass().equals(Double.class)) || (value.getClass().equals(Double.TYPE))) {
				values.put(columnName, (Double) value);
			} else if ((value.getClass().equals(Float.class)) || (value.getClass().equals(Float.TYPE))) {
				values.put(columnName, (Float) value);
			} else if ((value.getClass().equals(Integer.class)) || (value.getClass().equals(Integer.TYPE))) {
				values.put(columnName, (Integer) value);
			} else if ((value.getClass().equals(Long.class)) || (value.getClass().equals(Long.TYPE))) {
				values.put(columnName, (Long) value);
			} else if ((value.getClass().equals(String.class)) || (value.getClass().equals(Character.TYPE))) {
				values.put(columnName, value.toString());
			}
			else{
				try {
					putCustomValue(values, cv);
				} catch (IllegalAccessException e) {
					Log.e(TAG, e.getMessage());
				} catch (InstantiationException e) {
					Log.e(TAG, e.getMessage());
				}
				
			}
		}
		
	}
	
	private void putCustomValue(ContentValues values, ColumnValues cv) throws IllegalAccessException, InstantiationException{
		APAApplication app = (APAApplication) context.getApplicationContext();
		Class<?> converterClass = app.getConverters().get(cv.getType());
		if(converterClass != null){
			Object instance = converterClass.newInstance();
			ConverterType converter = (ConverterType) instance;
			converter.putValue(cv.getColumnName(), cv.getValue(), values);
		}
		else{
			getValueRelashionshipType(cv, values, cv.getValue());
		}
		
	}
	
	private void getValueRelashionshipType(ColumnValues cv, ContentValues values, Object owner) throws IllegalArgumentException, IllegalAccessException{
		Field[] fields = cv.getValue().getClass().getDeclaredFields();
		for(Field f : fields){
			f.setAccessible(true);
			if(f.getAnnotation(Id.class) != null){
				Object value = f.get(owner);
				values.put(cv.getColumnName(), (Long) value);
			}
		}
	}
	
	public List<Object> find(String whereClause, String[] whereArgs){
		open();
		Finder finder = new Finder(database, new WrapperClass(type), context);
		return finder.find(whereClause, whereArgs);
	}
	
	public List<Object> findAll(){
		open();
		Finder finder = new Finder(database, new WrapperClass(type), context);
		return finder.findAll();
	}
	
	public Object findOne(long id){
		open();
		Finder finder = new Finder(database, new WrapperClass(type), context);
		return finder.findOne(id);
	}
	
	public void delete(String where, String[] args){
		open();
		database.delete(type.getAnnotation(Entity.class).name(), where, args);
	}

}
