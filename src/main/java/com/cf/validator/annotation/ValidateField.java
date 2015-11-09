package com.cf.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidateField {
	boolean isNull() default true;

	Validator format() default Validator.TEXT;

	String min() default "";

	String max() default "";

	String minLength() default "";

	String maxLength() default "";

	String decimalDigits() default "";

	String prefix() default "";
}
