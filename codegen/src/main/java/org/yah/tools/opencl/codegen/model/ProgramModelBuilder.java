package org.yah.tools.opencl.codegen.model;

import org.yah.tools.opencl.codegen.NamingStrategy;
import org.yah.tools.opencl.codegen.model.kernel.KernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.SetKernelArgumentMethod;
import org.yah.tools.opencl.codegen.model.kernel.methods.*;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;
import org.yah.tools.opencl.codegen.parser.ParsedKernel;
import org.yah.tools.opencl.codegen.parser.ParsedKernelArgument;
import org.yah.tools.opencl.codegen.parser.ParsedProgram;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.codegen.parser.type.CLTypeParameter;
import org.yah.tools.opencl.enums.KernelArgAddressQualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.yah.tools.opencl.codegen.model.program.ProgramMethods.CloseProgram;
import static org.yah.tools.opencl.codegen.model.program.ProgramMethods.CreateKernel;

public class ProgramModelBuilder {

    private final NamingStrategy namingStrategy;
    private final boolean generateDirectInvokes;

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
        CLType type = parsedKernelArgument.getType();
        if (type.isCLTypeParameter()) {
            CLTypeParameter typeParameter = type.asCLTypeParameter();
            type = typeParameter.getReferenceType();
        }

        if (type.isPointer())
            return createPointerArgumentMethods(kernelModel, parsedKernelArgument);

        List<KernelArgumentMethod> methods = new ArrayList<>();

        methods.add(new SetValue(kernelModel, parsedKernelArgument));

        if (type.isVector() && type.asVector().getSize() <= 4)
            methods.add(new SetValueComponent(kernelModel, parsedKernelArgument, type.asVector().getSize()));

        return methods;
    }

    private List<KernelArgumentMethod> createPointerArgumentMethods(KernelModel kernelModel, ParsedKernelArgument parsedKernelArgument) {
        List<KernelArgumentMethod> methods = new ArrayList<>();
        KernelArgAddressQualifier addressQualifier = parsedKernelArgument.getAddressQualifier();

        if (addressQualifier == KernelArgAddressQualifier.GLOBAL || addressQualifier == KernelArgAddressQualifier.CONSTANT) {
            methods.add(CreateBuffer.fromBuffer(kernelModel, parsedKernelArgument));
            methods.add(CreateBuffer.fromSize(kernelModel, parsedKernelArgument));

            // can write buffer, null buffer is supported
            methods.add(new WriteBuffer(kernelModel, parsedKernelArgument));

            if (parsedKernelArgument.canRead()) {
                // device can write buffer, so host could need to read it
                methods.add(new ReadBuffer(kernelModel, parsedKernelArgument));
            }
        } else { //  local pointer can not have data, only size
            methods.add(new SetLocalSize(kernelModel, parsedKernelArgument));
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
