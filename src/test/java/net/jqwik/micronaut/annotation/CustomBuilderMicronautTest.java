package net.jqwik.micronaut.annotation;

import net.jqwik.micronaut.beans.CustomContextBuilder;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Documented
@JqwikMicronautTest(contextBuilder = CustomContextBuilder.class)
public @interface CustomBuilderMicronautTest {
}
