package br.com.sutil.apa.table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import br.com.sutil.apa.annotation.Column;
import br.com.sutil.apa.annotation.Id;
import br.com.sutil.apa.type.Type;

public class FactoryColumn {

	private static final String TAG = "FactoryColumn";
		
	private Class<?> clazz;
	private List<Coluna> colunas = new ArrayList<Coluna>();

	public FactoryColumn(Class<?> clazz) {
		this.clazz = clazz;
		for (Field f : getFields()) {
			Coluna coluna = processaField(f);
			if(coluna != null){
				colunas.add(coluna);
			}
		}
	}

	public String getColumns() {
		return colunasToString();
	}
	
	public List<Coluna> getColunas() {
		return colunas;
	}

	private String colunasToString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < colunas.size(); i++){
			if(i == colunas.size() -1 ){
				sb.append(colunas.get(i).toString());
			}
			else{
				sb.append(colunas.get(i).toString()).append(", ");
			}
		}
		return sb.toString();
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

	private Coluna processaField(Field field) {
		Column annotation = field.getAnnotation(Column.class);
		if (annotation != null) {
			return new Coluna(annotation.name(), Type.getType(field), notNull(annotation));
		}
		else{
			Id id = field.getAnnotation(Id.class);
			if(id != null){
				Log.d(TAG, "encontramos o id:"+id.name());
				return new Coluna(id.name(), "INTEGER", "PRIMARY KEY AUTOINCREMENT");
			}
		}
		return null;
	}

	private String notNull(Column annotation) {
		return annotation.notNull() ? "NOT NULL" : "";
	}

}
