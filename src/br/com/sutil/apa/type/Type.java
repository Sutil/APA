package br.com.sutil.apa.type;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class Type {
	
	private static final String REAL = "REAL";
	private static final String INTEGER = "INTEGER";
	private static final String NUMERIC = "NUMERIC";
	private static final String TEXT = "TEXT";
	
	public static String getType(Field field){
		if(isString(field.getType())){
			return TEXT;
		}
		else if(isFloat(field.getType())){
			return REAL;
		}
		else if(isInteger(field.getType())){
			return INTEGER;
		}
		else if(isDate(field.getType())){
			return NUMERIC;
		}
		else {
			return customType(field);
		}
	}
	
	private static String customType(Field field) {
		br.com.sutil.apa.annotation.Type annotation = field.getAnnotation(br.com.sutil.apa.annotation.Type.class);
		if(annotation != null){
			return annotation.value().getValue();
		}
		return NUMERIC;
	}





	private static boolean isFloat(Class<?> field) {
		return ((field.equals(Double.class)) || (field.equals(Double.TYPE)) || 
				(field.equals(Float.class)) || (field.equals(Float.TYPE)) ||
				(field.equals(BigDecimal.class)));
	}
	
	private static boolean isInteger(Class<?> object) {
		return (object.equals(Boolean.class) || object.equals(Boolean.TYPE)) 
		|| ((object.equals(Integer.class)) || (object.equals(Integer.TYPE))) 
		|| ((object.equals(Long.class)) || (object.equals(Long.TYPE))); 
	}
	
	private static boolean isString(Class<?> object) {
		return ((object.equals(String.class)) || (object.equals(Character.TYPE)));
	}
	
	private static boolean isDate(Class<?> type) {
		return type.equals(java.util.Date.class) || type.equals(java.sql.Date.class);
	}
	
}
