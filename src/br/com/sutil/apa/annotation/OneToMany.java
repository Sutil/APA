package br.com.sutil.apa.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public abstract @interface OneToMany {
	public abstract String referencedColumn();
}
