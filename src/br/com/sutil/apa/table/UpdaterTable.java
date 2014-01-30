package br.com.sutil.apa.table;

import java.util.Arrays;
import java.util.List;

import br.com.sutil.apa.annotation.Entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class UpdaterTable {
	
	private static final String TAG = UpdaterTable.class.getSimpleName();

	private Class<?> clazz;
	private SQLiteDatabase database;
	private List<Coluna> newColumns;
	

	public UpdaterTable(Class<?> clazz, SQLiteDatabase database) {
		this.clazz = clazz;
		this.database = database;
		this.newColumns = new FactoryColumn(clazz).getColunas();
	}
	
	public void execute(){
		try{
		List<String> oldColumns = getOldColumns(clazz.getAnnotation(Entity.class).name());
		for(Coluna coluna : newColumns){
			if(!oldColumns.contains(coluna.getNome())){
				createColumn(coluna);
			}
		}
		new FactoryOneToMany(clazz, database).execute();
		new FactoryManyToOne(clazz, database).execute();
		new FactoryOneToOne(clazz, database).execute();
		}
		catch(SQLiteException se){
			Log.e(TAG, "Table not exists. Creating table");
			new FactoryTable(clazz, database).createTable();
		}
	}
	
	private void createColumn(Coluna coluna){
		String tableName = clazz.getAnnotation(Entity.class).name();
		String sql = String.format("ALTER TABLE %s ADD COLUMN %s", tableName, coluna.toString());
		database.execSQL(sql);
	}
	
	
	private List<String> getOldColumns(String tableName){
		Cursor c = database.query(false, tableName, null, null, null, null, null, null, null);
		List<String> asList = Arrays.asList(c.getColumnNames());
		c.close();
		return asList;
	}

}
