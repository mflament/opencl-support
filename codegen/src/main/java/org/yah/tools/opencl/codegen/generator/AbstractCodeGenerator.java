package org.yah.tools.opencl.codegen.generator;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import org.lwjgl.PointerBuffer;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethod;
import org.yah.tools.opencl.codegen.model.kernel.KernelMethodParameter;
import org.yah.tools.opencl.codegen.model.kernel.KernelModel;
import org.yah.tools.opencl.codegen.model.kernel.param.Buffer;
import org.yah.tools.opencl.codegen.model.kernel.param.Value;
import org.yah.tools.opencl.codegen.model.kernel.param.ValueComponent;
import org.yah.tools.opencl.codegen.model.program.ProgramMethod;
import org.yah.tools.opencl.codegen.model.program.ProgramModel;
import org.yah.tools.opencl.codegen.parser.type.CLType;
import org.yah.tools.opencl.codegen.parser.type.ScalarDataType;
import org.yah.tools.opencl.enums.BufferProperty;
import org.yah.tools.opencl.ndrange.NDRange;

import java.nio.*;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public abstract class AbstractCodeGenerator<T> {

    protected final T source;
    protected final String packageName;
    protected final String simpleName;

    protected final CompilationUnit compilationUnit;
    protected final ClassOrInterfaceDeclaration declaration;

    protected AbstractCodeGenerator(T source, String packageName, String simpleName) {
        this.source = Objects.requireNonNull(source, "source is null");
        this.packageName = Objects.requireNonNull(packageName, "packageName is null");
        this.simpleName = Objects.requireNonNull(simpleName, "simpleName is null");

        compilationUnit = new CompilationUnit(packageName);
        declaration = new ClassOrInterfaceDeclaration()
                .setName(simpleName)
                .setModifiers(Modifier.Keyword.PUBLIC);
        compilationUnit.addType(declaration);
    }

    public abstract CompilationUnit generate();

    protected Type getThisType() {
        ClassOrInterfaceType thisType = new ClassOrInterfaceType(null, declaration.getNameAsString());
        if (!declaration.getTypeParameters().isEmpty()) {
            thisType.setTypeArguments(declaration.getTypeParameters().stream()
                    .map(t -> (Type) t)
                    .collect(toNodeList()));
        }
        return thisType;
    }

    protected final void addTypeParameters(Collection<String> typeParameters) {
        if (typeParameters.isEmpty())
            return;
        typeParameters.stream()
                .map(TypeParameter::new)
                .forEach(declaration::addTypeParameter);
    }

    protected final ClassOrInterfaceType addImport(Class<?> type) {
        compilationUnit.addImport(type);
        return new ClassOrInterfaceType(null, type.getSimpleName());
    }

    protected final ClassOrInterfaceType addImport(CompilationUnit cu) {
        String packageName = cu.getPackageDeclaration()
                .map(PackageDeclaration::getNameAsString).orElseThrow(IllegalArgumentException::new);
        String simpleName = cu.getType(0).getNameAsString();
        compilationUnit.addImport(packageName + "." + simpleName);
        return new ClassOrInterfaceType(null, simpleName);
    }

    protected final void addPublicConstant(Class<?> type, String name, String stmt) {
        ClassOrInterfaceType astType = addImport(type);
        FieldDeclaration fieldDeclaration = declaration.addField(astType, name, Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC, Modifier.Keyword.FINAL);
        fieldDeclaration.getVariable(0).setInitializer(stmt);
    }

    protected final Type resolveParameterType(KernelMethodParameter parameter) {
        if (parameter.isBufferSize() || parameter.isBufferOffset())
            return PrimitiveType.longType();

        if (parameter.isBufferProperties())
            return addImport(BufferProperty.class);

        if (parameter.isInvokeRangeParameter())
            return addImport(NDRange.class);

        if (parameter.isEventBuffer())
            return addImport(PointerBuffer.class);

        if (parameter.isInvokeArgument())
            return resolveParameterType(parameter.asInvokeArgument().getSetterParameter());

        if (parameter.isBuffer())
            return resolveBufferType(parameter.asBuffer());

        if (parameter.isValue())
            return resolveValueType(parameter.asValue());

        if (parameter.isValueComponent())
            return resolveValueComponentType(parameter.asValueComponent());

        throw unresolvedTypeError(parameter);
    }

    protected Type resolveBufferType(Buffer parameter) {
        CLType type = parameter.getParsedKernelArgument().getType();
        if (type.isCLTypeParameter())
            return new TypeParameter(type.getName());

        if (!type.isPointer())
            throw new IllegalArgumentException("parameter " + parameter + " is not a pointer");

        CLType targetType = type.asPointer().getTargetType();

        if (targetType.isVector())
            targetType = targetType.asVector().getComponentType();

        if (targetType.isScalar())
            return resolveBufferType(targetType.asScalar());

        if (targetType.isUnresolved())
            return addImport(ByteBuffer.class);

        if (targetType.isCLTypeParameter())
            return new TypeParameter(targetType.getName());

        throw unresolvedTypeError(parameter);
    }

    protected Type resolveValueType(Value parameter) {
        CLType type = parameter.getParsedKernelArgument().getType();
        CLType componentType = type.getComponentType();

        if (componentType.isCLTypeParameter())
            return new TypeParameter(componentType.getName());

        if (componentType.isMemObjectType())
            return PrimitiveType.longType();

        if (componentType.isScalar())
            return resolveScalarType(componentType.asScalar());

        throw unresolvedTypeError(parameter);
    }

    protected Type resolveValueComponentType(ValueComponent parameter) {
        CLType type = parameter.getParsedKernelArgument().getType();
        CLType componentType = type.getComponentType();

        if (componentType.isCLTypeParameter())
            return new TypeParameter(componentType.getName());

        if (componentType.isScalar())
            return resolveScalarType(componentType.asScalar());

        throw unresolvedTypeError(parameter);
    }

    protected final ClassOrInterfaceType resolveBufferType(ScalarDataType scalarType) {
        switch (scalarType) {
            case BOOL:
            case INT:
            case UINT:
                return addImport(IntBuffer.class);
            case SHORT:
            case USHORT:
            case HALF:
                return addImport(ShortBuffer.class);
            case FLOAT:
                return addImport(FloatBuffer.class);
            case DOUBLE:
                return addImport(DoubleBuffer.class);
            case LONG:
            case ULONG:
                return addImport(LongBuffer.class);
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                return addImport(PointerBuffer.class);
            default:
                return addImport(ByteBuffer.class);
        }
    }

    protected static PrimitiveType resolveScalarType(ScalarDataType parameterType) {
        switch (parameterType) {
            case BOOL:
                return PrimitiveType.booleanType();
            case SHORT:
            case USHORT:
            case HALF:
                return PrimitiveType.shortType();
            case INT:
            case UINT:
                return PrimitiveType.intType();
            case FLOAT:
                return PrimitiveType.floatType();
            case DOUBLE:
                return PrimitiveType.doubleType();
            case LONG:
            case ULONG:
            case SIZE_T:
            case PTRDIFF_T:
            case INTPTR_T:
            case UINTPTR_T:
                return PrimitiveType.longType();
            default:
                return PrimitiveType.byteType();
        }
    }

    public static final DataKey<ProgramModel> PROGRAM_MODEL = new DataKey<ProgramModel>() {
    };

    public static final DataKey<KernelModel> KERNEL_MODEL = new DataKey<KernelModel>() {
    };

    public static final DataKey<ProgramMethod> PROGRAM_METHOD = new DataKey<ProgramMethod>() {
    };

    public static final DataKey<KernelMethod> KERNEL_METHOD = new DataKey<KernelMethod>() {
    };

    public static final DataKey<KernelMethodParameter> KERNEL_METHOD_PARAMETER = new DataKey<KernelMethodParameter>() {
    };

    protected static <T> T getData(CompilationUnit compilationUnit, DataKey<T> key) {
        return compilationUnit.getType(0).getData(key);
    }

    protected static BlockStmt buildBlockStmt(String... statements) {
        BlockStmt blockStmt = new BlockStmt();
        for (String statement : statements) {
            if (!statement.endsWith(";"))
                statement += ";";
            blockStmt.addStatement(statement);
        }
        return blockStmt;
    }

    protected static String quote(String s) {
        if (s == null) return "\"\"";
        return '"' + s + '"';
    }

    protected static IllegalArgumentException unresolvedTypeError(Object source) {
        return new IllegalArgumentException("Unresoled ast type from " + source);
    }

    protected static IllegalArgumentException invalidTypeError(Object t) {
        return new IllegalArgumentException("Invalid type " + t);
    }

    protected static <N extends Node> Collector<N, NodeList<N>, NodeList<N>> toNodeList() {
        return new Collector<N, NodeList<N>, NodeList<N>>() {
            @Override
            public Supplier<NodeList<N>> supplier() {
                return NodeList::new;
            }

            @Override
            public BiConsumer<NodeList<N>, N> accumulator() {
                return NodeList::add;
            }

            @Override
            public BinaryOperator<NodeList<N>> combiner() {
                return (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                };
            }

            @Override
            public Function<NodeList<N>, NodeList<N>> finisher() {
                return l -> l;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.noneOf(Characteristics.class);
            }
        };
    }
}
