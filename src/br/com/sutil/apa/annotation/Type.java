package br.com.sutil.apa.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import br.com.sutil.apa.type.Types;

@Retention(RetentionPolicy.RUNTIME)
public abstract @interface Type {
	public abstract Types value();
}
