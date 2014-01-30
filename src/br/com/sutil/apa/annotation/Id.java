package br.com.sutil.apa.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public abstract @interface Id {
	public abstract String name() default "id";
	public abstract boolean autoincrement() default true;
}
