package org.yah.tools.opencl.generated;

import org.yah.tools.opencl.cmdqueue.CLCommandQueue;
import org.yah.tools.opencl.context.CLContext;
import org.yah.tools.opencl.platform.CLDevice;
import org.yah.tools.opencl.program.CLProgram;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;

public class CLGeneratedProgramBuilder<T> {

    /**
     * @param interfaceName the fully qualified name of the interface
     * @return the fully qualified name of the implementation
     */
    public static TypeName getCLImplementationName(TypeName interfaceName) {
        return new TypeName(interfaceName.getPackageName().map(p -> p + ".cl").orElse("cl"),
                "CL" + interfaceName.getSimpleName());
    }

    private final CLContext context;
    private final Class<T> programInterface;
    private final Class<? extends T> programImplementation;
    private final Constructor<? extends T> constructor;

    private CLDevice device;
    private String programFile;
    private String options;

    public CLGeneratedProgramBuilder(CLContext context, Class<T> programInterface) {
        this.context = Objects.requireNonNull(context, "context is null");
        this.programInterface = Objects.requireNonNull(programInterface, "programInterface is null");
        programImplementation = findImplementation(programInterface);
        constructor = findConstructor(programImplementation);
    }

    public CLGeneratedProgramBuilder<T> withProgramFile(String programFile) {
        this.programFile = programFile;
        return this;
    }

    public CLGeneratedProgramBuilder<T> withOptions(String options) {
        this.options = options;
        return this;
    }

    public CLGeneratedProgramBuilder<T> withDevice(CLDevice device) {
        this.device = device;
        return this;
    }

    public T build() {
        if (device == null)
            device = context.getFirstDevice();
        else if (!context.getDevices().contains(device))
            throw new IllegalStateException("devcie " + device + " is not in context " + context);

        if (programFile == null || options == null) {
            ProgramMetadata metadata = getProgramMetadata();
            if (programFile == null) programFile = metadata.getProgramFile();
            if (options == null) options = metadata.getCompilerOptions();
        }

        CLProgram program = CLProgram.builder(context)
                .withDevice(device)
                .withFile(programFile)
                .withOptions(options)
                .build();
        CLCommandQueue cmdQueue = CLCommandQueue.builder(context, device).build();

        try {
            return constructor.newInstance(program, cmdQueue);
        } catch (Exception e) {
            throw new IllegalStateException("Error creating program", e);
        }
    }

    private ProgramMetadata getProgramMetadata() {
        try {
            Field field = programImplementation.getField("PROGRAM_METADATA");
            return (ProgramMetadata) field.get(null);
        } catch (Exception e) {
            throw new IllegalStateException("Error getting PROGRAM_METADATA field in " + programInterface.getName(), e);
        }
    }

    private static <T> Constructor<T> findConstructor(Class<T> programInterface) {
        try {
            return programInterface.getConstructor(CLProgram.class, CLCommandQueue.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(programInterface.getName() + " has no contructor(CLProgram program, CLCommandQueue commandQueue)");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? extends T> findImplementation(Class<T> programInterface) {
        TypeName interfaceName = new TypeName(programInterface);
        TypeName implementationName = getCLImplementationName(interfaceName);
        Class<?> implementationClass;
        try {
            implementationClass = Class.forName(implementationName.toString());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("CL implementation " + implementationName + " for interface " + interfaceName + " not found", e);
        }
        if (!programInterface.isAssignableFrom(implementationClass))
            throw new IllegalArgumentException("CL implementation " + implementationName + " is not an instanceof " + interfaceName);
        return (Class<? extends T>) implementationClass;
    }


}
