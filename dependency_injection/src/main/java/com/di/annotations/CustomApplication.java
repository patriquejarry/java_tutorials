package com.di.annotations;

/**
* Client class should use this annotation
*/
import java.lang.annotation.*;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface  CustomApplication {
}
