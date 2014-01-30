package br.com.sutil.apa.repository;

import java.lang.reflect.Field;

import br.com.sutil.apa.annotation.Column;
import br.com.sutil.apa.annotation.Id;
import br.com.sutil.apa.type.Type;

public class ColumnValues {
	
	private String columnName;
	private String columnType;
	private Field field;
	private Object value;
	private Class<?> type;
	
	
	private ColumnValues(Field field, Object instance) {
		this.columnName = getColumnName(field);
		this.columnType = getColumnType(field);
		this.field = field;
		this.value = getValueField(field, instance);
		if(value != null){
			this.type = value.getClass();
		}
	}
	
	private static Object getValueField(Field field, Object instance){
		try {
			field.setAccessible(true);
			return field.get(instance);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}
	
	public static ColumnValues newInstance(Field field, Object instance){
		ColumnValues cv = new ColumnValues(field, instance);
		if(cv.columnName != null && cv.columnType != null && cv.field != null ){
			return cv;
		}
		return null;
	}
	
	private static String getColumnName(Field field){
		Column column = field.getAnnotation(Column.class);
		if(column != null){
			return column.name();
		}
		else if (field.getAnnotation(Id.class) != null) {
			return field.getAnnotation(Id.class).name();
		}
		return null;
	}
	
	private static String getColumnType(Field field){
		return Type.getType(field);
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public String getColumnType() {
		return columnType;
	}
	
	public Field getField() {
		return field;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public boolean isId(){
		return this.field.getAnnotation(Id.class) != null;
	}

}
