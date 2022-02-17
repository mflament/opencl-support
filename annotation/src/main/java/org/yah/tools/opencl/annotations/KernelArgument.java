package org.yah.tools.opencl.annotations;

import java.lang.annotation.*;

/**
 * Used to define kernel arguments.<br/>
 * Used to set the arguments value once and call the kernel method with the same value multiple times.<br/>
 * <p>
 * Must be placed on a setter method like <p><code>void set<i>XXX</i>(<i>type</i> value);</code></p> that will be used to set the kernel
 * argument.<br/>
 * <p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Documented
public @interface KernelArgument {

    /**
     * The argument index. Default to method parameter index if annoation is on parameter, or an exception is
     * thrown otherwise.
     */
    int index() default -1;

    /**
     * Control the argument adress space.<br/>
     */
    CLAddressSpace addressSpace() default CLAddressSpace.PRIVATE;

    /**
     *
     */
    BufferUsage bufferUsage() default BufferUsage.WRITE_ONLY;
}
