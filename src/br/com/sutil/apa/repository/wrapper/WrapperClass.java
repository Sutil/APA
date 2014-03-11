package br.com.sutil.apa.repository.wrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import br.com.sutil.apa.annotation.Column;
import br.com.sutil.apa.annotation.Entity;
import br.com.sutil.apa.annotation.Id;
import br.com.sutil.apa.annotation.ManyToOne;
import br.com.sutil.apa.annotation.OneToOne;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class WrapperClass {

	private final Class<?> clazz;

	public WrapperClass(Class<?> clazz) {
		if (!clazz.isAnnotationPresent(Entity.class)) {
			throw new IllegalArgumentException("Annotation Entity not found");
		}
		this.clazz = clazz;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}

	public List<Field> getAllFields() {
		return getFields(clazz);
	}
	
	public List<Field> getAllMapedFields(){
		List<Field> fields = Lists.newArrayList();
		fields.addAll(getFieldForAnnotation(Column.class));
		fields.addAll(getFieldForAnnotation(Id.class));
		fields.addAll(getFieldForAnnotation(ManyToOne.class));
		fields.addAll(getFieldForAnnotation(OneToOne.class));
		return fields;
	}

	private List<Field> getFields(Class<?> clazz) {
		List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
		if (!clazz.getSuperclass().equals(Object.class)) {
			fields.addAll(getFields(clazz.getSuperclass()));
		}
		return fields;
	}

	public Long getId(Object instance) {
		try {
			Field fieldId = getFieldId();
			fieldId.setAccessible(true);
			return (Long) fieldId.get(instance);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return 0L;
	}

	private Field getFieldId() {
		List<Field> fieldForAnnotation = getFieldForAnnotation(Id.class);
		Preconditions.checkArgument(fieldForAnnotation.size() == 1);
		Field field = fieldForAnnotation.get(0);
		Preconditions.checkArgument(isLong(field), "Id is not long type");
		return field;
	}

	private boolean isLong(Field field) {
		return long.class.isAssignableFrom(field.getType()) || Long.class.isAssignableFrom(field.getType());
	}
	
	public String getColumnNameId(){
		return getFieldId().getAnnotation(Id.class).name();
	}
	
	public String getColumnName(Field field){
		Column column = field.getAnnotation(Column.class);
		if(column != null){
			return column.name();
		}
		Id id = field.getAnnotation(Id.class);
		if(id != null){
			return id.name();
		}
		ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
		if(manyToOne != null){
			return manyToOne.columnFkName();
		}
		OneToOne oneToOne = field.getAnnotation(OneToOne.class);
		if(oneToOne != null){
			return oneToOne.columnFkName();
		}
		return field.getName();
	}

	public List<Field> getFieldForAnnotation(final Class<? extends Annotation> annotation) {
		Collection<Field> filtered = Collections2.filter(getAllFields(), new Predicate<Field>() {

			@Override
			public boolean apply(Field field) {
				return field.isAnnotationPresent(annotation);
			}
		});
		return Lists.newArrayList(filtered);
	}
	
	public String getTableName(){
		return clazz.getAnnotation(Entity.class).name();
	}

	
	

}
