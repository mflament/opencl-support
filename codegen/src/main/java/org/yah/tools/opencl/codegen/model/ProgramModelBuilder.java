package org.yah.tools.opencl.codegen.model;

import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.codegen.DefaultNamingStrategy;
import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.SetKernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.methods.*;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.model.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.model.type.CLType;
import org.yah.tools.opencl.codegen.parser.model.type.ScalarDataType;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;

import java.nio.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.yah.tools.opencl.codegen.model.program.ProgramMethods.CloseProgram;
import static org.yah.tools.opencl.codegen.model.program.ProgramMethods.CreateKernel;

public class ProgramModelBuilder {

    private final NamingStrategy namingStrategy;
    private final boolean generateDirectInvokes;

    public ProgramModelBuilder() {
        this(DefaultNamingStrategy.get(), false);
    }

    public ProgramModelBuilder(NamingStrategy namingStrategy, boolean generateDirectInvokes) {
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy is null");
        this.generateDirectInvokes = generateDirectInvokes;
    }

    public ProgramModel build(String basePackage, ParsedProgram parsedProgram) {
        ProgramModel programModel = new ProgramModel(parsedProgram, namingStrategy, basePackage);
        parsedProgram.getKernels().stream()
                .map(pk -> createKernelModel(programModel, pk))
                .map(CreateKernel::new)
                .forEach(programModel::addMethod);
        programModel.addMethod(new CloseProgram(programModel));
        return programModel;
    }

    private KernelModel createKernelModel(ProgramModel programModel, ParsedKernel parsedKernel) {
        KernelModel kernelModel = new KernelModel(programModel, parsedKernel);

        parsedKernel.getArguments().stream()
                .flatMap(pka -> createKernelArgumentMethods(kernelModel, pka).stream())
                .forEach(kernelModel::addMethod);

        kernelModel.addMethods(createInvokeMethods(kernelModel));

        kernelModel.addMethod(new CloseKernel(kernelModel));

        return kernelModel;
    }

    private List<KernelArgumentMethod> createKernelArgumentMethods(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        if (parsedKernelArgument.isPointer())
            return createPointerArgumentMethods(kernelModel, parsedKernelArgument);

        CLType type = parsedKernelArgument.getType();
        List<KernelArgumentMethod> methods = new ArrayList<>();
        if (type.isMemObjectType()) {
            methods.add(new SetValue(kernelModel, parsedKernelArgument, Long.TYPE));
        } else {
            if (type.isVector()) {
                ScalarDataType componentType = type.asVector().getScalarType();
                methods.add(new SetValue(kernelModel, parsedKernelArgument, componentType.getBufferClass()));
                int vectorSize = type.asVector().getSize();
                if (vectorSize < 4)
                    methods.add(new SetValue(kernelModel, parsedKernelArgument, componentType.getValueClass(), vectorSize));
            } else if (type.isScalar()) {
                ScalarDataType scalarType = type.asScalar();
                methods.add(new SetValue(kernelModel, parsedKernelArgument, scalarType.getValueClass()));
            } else if (type.isUnresolved()) {
                methods.add(new SetValue(kernelModel, parsedKernelArgument, ByteBuffer.class));
            }
        }

        return methods;
    }

    private List<KernelArgumentMethod> createPointerArgumentMethods(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        List<KernelArgumentMethod> methods = new ArrayList<>();
        KernelArgAddressQualifier addressQualifier = parsedKernelArgument.getAddressQualifier();
        Class<?> bufferClass = getBufferClass(parsedKernelArgument);

        if (addressQualifier == KernelArgAddressQualifier.GLOBAL || addressQualifier == KernelArgAddressQualifier.CONSTANT) {

            methods.add(new CreateBuffer(kernelModel, parsedKernelArgument, bufferClass));
            methods.add(new CreateBuffer(kernelModel, parsedKernelArgument, getBufferBytes(bufferClass)));

            // can write buffer, null buffer is supported
            methods.add(new WriteBuffer(kernelModel, parsedKernelArgument, bufferClass));

            if (parsedKernelArgument.canRead()) {
                // device can write buffer, so host could need to read it
                methods.add(new ReadBuffer(kernelModel, parsedKernelArgument, bufferClass));
            }
        } else { //  local pointer can not have data, only size
            methods.add(new SetLocalSize(kernelModel, parsedKernelArgument, getBufferBytes(bufferClass)));
        }

        return methods;
    }

    private List<Invoke> createInvokeMethods(KernelModel kernelModel) {
        List<Invoke> methods = new ArrayList<>();
        methods.add(new Invoke(kernelModel));

        if (generateDirectInvokes) {
            ParsedKernel parsedKernel = kernelModel.getParsedKernel();
            List<ParsedKernelArgument> arguments = parsedKernel.getArguments();
            SetKernelArgumentMethod[][] argumentSetters = new SetKernelArgumentMethod[arguments.size()][];
            for (int i = 0; i < argumentSetters.length; i++) {
                int currentIndex = i;
                argumentSetters[i] = kernelModel.getMethods().stream()
                        .filter(km -> isKernelArgumentInvokeSetter(km, currentIndex))
                        .map(KernelMethod::asSetKernelArgumentMethod)
                        .toArray(SetKernelArgumentMethod[]::new);
            }

            combinatories(argumentSetters, combination -> methods.add(new Invoke(kernelModel, combination)));
        }
        return methods;
    }

    private static boolean isKernelArgumentInvokeSetter(KernelMethod kernelMethod, int index) {
        if (kernelMethod.isSetKernelArgumentMethod()) {
            int actualIndex = kernelMethod.asSetKernelArgumentMethod().getParsedKernelArgument().getArgIndex();
            if (actualIndex != index)
                return false;
            return kernelMethod.asSetKernelArgumentMethod().getInvokeParameter().isPresent();
        }
        return false;
    }

    private static Class<?> getBufferClass(ParsedKernelArgument parsedKernelArgument) {
        CLType type = parsedKernelArgument.getType();
        if (type.isScalar())
            return type.asScalar().getBufferClass();
        if (type.isVector())
            return type.asVector().getScalarType().getBufferClass();
        if (type.isUnresolved())
            return ByteBuffer.class;
        throw new IllegalArgumentException("No buffer class for type " + type);
    }

    private static int getBufferBytes(Class<?> bufferClass) {
        if (bufferClass == ByteBuffer.class)
            return 1;
        if (bufferClass == Short.class)
            return 2;
        if (bufferClass == IntBuffer.class || bufferClass == FloatBuffer.class)
            return 4;
        if (bufferClass == DoubleBuffer.class || bufferClass == LongBuffer.class)
            return 8;
        if (bufferClass == PointerBuffer.class)
            return PointerBuffer.POINTER_SIZE;
        throw new IllegalArgumentException("Invalid buffer class " + bufferClass);
    }

    private static <T> void combinatories(T[][] values, Consumer<List<T>> combinationConsumer) {
        int size = values.length;
        int[] indices = new int[size];
        int argIndex = size - 1;

        List<T> combination = new ArrayList<>(size);
        for (int i = 0; i < size; i++) combination.add(null);

        while (argIndex >= 0) {
            for (int i = 0; i < size; i++)
                combination.set(i, values[i][indices[i]]);
            combinationConsumer.accept(combination);
            while (argIndex >= 0 && ++indices[argIndex] == values[argIndex].length) {
                indices[argIndex] = 0;
                argIndex--;
            }
        }
    }

}
