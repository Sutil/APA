package br.com.sutil.apa.table;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import br.com.sutil.apa.annotation.Entity;

public class FactoryTable {

	private static final String TAG = "FactoryTable";

	private SQLiteDatabase db;
	private Class<?> clazz;
	private static final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS ";

	public FactoryTable(Class<?> clazz, SQLiteDatabase db) {
		this.clazz = clazz;
		this.db = db;
	}

	public void createTable() {
		StringBuilder sb = new StringBuilder();
		String sql = sb.append(CREATE_TABLE).append(getTableName(clazz)).append("(").append(getColumns(clazz)).append(");").toString();
		Log.i(TAG, sql);
		db.execSQL(sql);
	}

	private String getTableName(Class<?> clazz) {
		Entity annotation = clazz.getAnnotation(Entity.class);
		return annotation.name();
	}

	private String getColumns(Class<?> clazz) {
		FactoryColumn factoryColumn = new FactoryColumn(clazz);
		return factoryColumn.getColumns();
	}
}
