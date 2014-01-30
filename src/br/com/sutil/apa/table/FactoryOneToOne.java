package br.com.sutil.apa.table;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.com.sutil.apa.annotation.Entity;
import br.com.sutil.apa.annotation.OneToOne;

public class FactoryOneToOne {

	private static final String TAG = "FactoryOneToOne";

	private Class<?> clazz;
	private SQLiteDatabase database;

	public FactoryOneToOne(Class<?> clazz, SQLiteDatabase database) {
		this.clazz = clazz;
		this.database = database;
	}

	public void execute() {
		createRelationship(getFields(clazz), clazz.getAnnotation(Entity.class).name());
	}

	private void createRelationship(Field[] fields, String tableName) {
		List<String> oldColumns = getOldColumns(clazz);
		for (Field field : fields) {
			addColumnInTable(field, tableName, oldColumns);
		}
	}
	
	private List<String> getOldColumns(Class<?> clazz){
		String tableName = clazz.getAnnotation(Entity.class).name();
		Cursor c = database.query(false, tableName, null, null, null, null, null, null, null);
		List<String> asList = Arrays.asList(c.getColumnNames());
		c.close();
		return asList;
	}

	private void addColumnInTable(Field field, String tableName, List<String> oldColumns) {
		String columnName = getColumnName(field);
		if (columnName != null && !oldColumns.contains(columnName)) {
			String sql = String.format("ALTER TABLE %s ADD COLUMN %s INTEGER", tableName, columnName);
			Log.d(TAG, sql);
			database.execSQL(sql);
		}
	}

	private String getColumnName(Field field) {
		OneToOne annotation = field.getAnnotation(OneToOne.class);
		if (annotation != null) {
			return annotation.columnFkName();
		}
		return null;
	}

	private Field[] getFields(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		if (fields.length == 0) {
			fields = clazz.getFields();
		}
		if (fields.length == 0) {
			fields = clazz.getSuperclass().getDeclaredFields();
		}
		return fields;
	}

}
