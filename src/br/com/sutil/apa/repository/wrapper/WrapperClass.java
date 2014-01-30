package br.com.sutil.apa.repository.wrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import br.com.sutil.apa.annotation.Column;
import br.com.sutil.apa.annotation.Entity;
import br.com.sutil.apa.annotation.Id;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class WrapperClass<T> {

	private final Class<T> clazz;

	public WrapperClass(Class<T> clazz) {
		if (!clazz.isAnnotationPresent(Entity.class)) {
			throw new IllegalArgumentException("Annotation Entity not found");
		}
		this.clazz = clazz;
	}

	public List<Field> getAllFields() {
		return getFields(clazz);
	}
	
	public List<Field> getAllMapedFields(){
		List<Field> fields = Lists.newArrayList();
		fields.addAll(getFieldForAnnotation(Column.class));
		fields.addAll(getFieldForAnnotation(Id.class));
		return fields;
	}

	private List<Field> getFields(Class<?> clazz) {
		List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
		if (!clazz.getSuperclass().equals(Object.class)) {
			fields.addAll(getFields(clazz.getSuperclass()));
		}
		return fields;
	}

	public Long getId(T instance) {
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
		Preconditions.checkArgument(Long.class.isAssignableFrom(field.getType()));
		return field;
	}
	
	public String getColumnNameId(){
		return getFieldId().getAnnotation(Id.class).name();
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
	
	
	

}
