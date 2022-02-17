package org.yah.tools.opencl.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Kernel {

    String METHOD_NAME = "";

    String value() default METHOD_NAME;

    Class<? extends NDRangeConfigurer> rangeConfigurer() default NDRangeConfigurer.class;

    Class<? extends KernelArgumentSetter> argumentSetter() default KernelArgumentSetter.class;

}
