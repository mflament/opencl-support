package org.yah.tools.opencl.annotations;

import java.lang.annotation.*;

/**
 * Decorate kernel argument declaration.<br/>
 * If argument type is int, will be used as the global work size.<br/>
 * If the argument is an array or a buffer, the size of the buffer will be used.<br/>
 * In bothe cases, the size will be divided by the {@link #stride()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface GlobalWorkSize {
    int stride() default 1;
}
