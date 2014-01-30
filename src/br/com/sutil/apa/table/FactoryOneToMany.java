package br.com.sutil.apa.table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.com.sutil.apa.annotation.Entity;
import br.com.sutil.apa.annotation.OneToMany;

public class FactoryOneToMany {
	
	private static final String TAG = "FactoryRelationship";
	
	private Class<?> clazz;
	private SQLiteDatabase database;
	
	public FactoryOneToMany(Class<?> clazz, SQLiteDatabase database) {
		this.clazz = clazz;
		this.database = database;
	}
	
	public void execute(){
		Log.d(TAG, "verificando One To Many");
		for(Field field : getFields()){
			Class<?> otherTable = getTypeListOneToMany(field);
			if(otherTable != null){
				List<String> oldColumns = getOldColumns(otherTable);
				String referencedColumn = field.getAnnotation(OneToMany.class).referencedColumn();
				if(!oldColumns.contains(referencedColumn)){
					createColumnFk(otherTable, referencedColumn);
				}
			}
		}
	}
	
	private Field[] getFields() {
		Field[] fields = clazz.getDeclaredFields();
		if (fields.length == 0) {
			fields = clazz.getFields();
		}
		if (fields.length == 0) {
			fields = clazz.getSuperclass().getDeclaredFields();
		}
		return fields;
	}
	
	
	private List<String> getOldColumns(Class<?> clazz){
		String tableName = clazz.getAnnotation(Entity.class).name();
		Cursor c = database.query(false, tableName, null, null, null, null, null, null, null);
		List<String> asList = Arrays.asList(c.getColumnNames());
		c.close();
		return asList;
	}
	
	private Class<?> getTypeListOneToMany(Field field){
		OneToMany annotation = field.getAnnotation(OneToMany.class);
		if(annotation != null){
			Log.d(TAG, "encontrou OneToMany "+annotation.referencedColumn());
			ParameterizedType type =  (ParameterizedType) field.getGenericType();
			return (Class<?>) type.getActualTypeArguments()[0];
		}
		Log.d(TAG, field.getName()+" nao contem annotatio OneToMay");
		return null;
	}

	private void createColumnFk(Class<?> otherTable, String columnName) {
		String tableName = otherTable.getAnnotation(Entity.class).name();
		String sql = "ALTER TABLE "+tableName+" ADD COLUMN "+columnName+" INTEGER;";
		Log.d("Criando relacionamento", sql);
		database.execSQL(sql);
	}

}
