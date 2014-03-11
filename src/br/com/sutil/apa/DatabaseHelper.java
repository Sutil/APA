package br.com.sutil.apa;

import java.util.ArrayList;
import java.util.Map;

import br.com.sutil.apa.table.FactoryManyToOne;
import br.com.sutil.apa.table.FactoryOneToMany;
import br.com.sutil.apa.table.FactoryOneToOne;
import br.com.sutil.apa.table.FactoryTable;
import br.com.sutil.apa.table.UpdaterTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final String TAG = "DatabaseHelper";
	
	private Context context;

	public DatabaseHelper(Context context) {
		super(context, MetaData.getDBName(context), null, MetaData.getDBVersion(context));
		Log.d(TAG, "DatabaseHelper");
		this.context = context;
		executaUpdates(getWritableDatabase());
		scanConverters();
	}

	private void executaUpdates(SQLiteDatabase db) {
		Log.i(TAG, "verificando e executando updates na base de dados");
		ArrayList<Class<?>> entityClasses = new ReaderEntities(context).getEntityClasses();
		for(Class<?> clazz : entityClasses){
			new UpdaterTable(clazz, db).execute();
		}
	}
	
	private void scanConverters() {
		Map<Class<?>, Class<?>> converters = new ReaderConverters(context).getConverters();
		APAApplication app = (APAApplication) context.getApplicationContext();
		app.getConverters().putAll(converters);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate");
		ArrayList<Class<?>> entityClasses = new ReaderEntities(context).getEntityClasses();
		for(Class<?> clazz : entityClasses){
			new FactoryTable(clazz, db).createTable();
		}
		for(Class<?> clazz : entityClasses){
			new FactoryOneToMany(clazz, db).execute();
		}
		for(Class<?> clazz : entityClasses){
			new FactoryManyToOne(clazz, db).execute();
		}
		for(Class<?> clazz : entityClasses){
			new FactoryOneToOne(clazz, db).execute();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
