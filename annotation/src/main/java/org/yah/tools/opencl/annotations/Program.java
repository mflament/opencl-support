package org.yah.tools.opencl.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Program {
    /**
     * @return the program resource path. Default to file path. Can be prefixed with "classpath:" to load from ... classpath.
     */
    String value();

    String options() default "";
}
