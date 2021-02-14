package com.di.annotations;

import java.lang.annotation.*;

/** Client class should use this annotation */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomComponent {
}