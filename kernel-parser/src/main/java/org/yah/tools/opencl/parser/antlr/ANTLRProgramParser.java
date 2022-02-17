package org.yah.tools.opencl.parser.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yah.tools.opencl.parser.OpenCL12Lexer;
import org.yah.tools.opencl.parser.OpenCL12Parser;
import org.yah.tools.opencl.parser.ProgramParser;
import org.yah.tools.opencl.parser.model.CLAddressSpace;
import org.yah.tools.opencl.parser.model.ParsedKernel;
import org.yah.tools.opencl.parser.model.ParsedKernelArgument;
import org.yah.tools.opencl.parser.model.ParsedProgram;
import org.yah.tools.opencl.parser.type.CLConstType;
import org.yah.tools.opencl.parser.type.CLPointerType;
import org.yah.tools.opencl.parser.type.CLType;
import org.yah.tools.opencl.parser.type.TypeResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.yah.tools.opencl.parser.OpenCL12Parser.*;
import static org.yah.tools.opencl.parser.type.TypeResolver.declaratorIdentifier;

public class ANTLRProgramParser implements ProgramParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ANTLRProgramParser.class);

    @Override
    public ParsedProgram parse(Path file, Charset charset) throws IOException {
        CharStream sourceStream = CharStreams.fromPath(file, charset);
        return parse(sourceStream);
    }

    @Override
    public ParsedProgram parse(InputStream is, Charset charset) throws IOException {
        CharStream sourceStream = CharStreams.fromStream(is, charset);
        return parse(sourceStream);
    }

    @Override
    public ParsedProgram parse(String source) {
        CharStream sourceStream = CharStreams.fromString(source);
        return parse(sourceStream);
    }

    private static ParsedProgram parse(CharStream sourceStream) {
        OpenCL12Lexer lexer = new OpenCL12Lexer(sourceStream);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        OpenCL12Parser parser = new OpenCL12Parser(tokenStream);
        CompilationUnitContext compilationUnit = parser.compilationUnit();
        ParsedProgram.Builder builder = ParsedProgram.builder();
        if (compilationUnit.translationUnit() == null)
            return builder.build();

        List<ExternalDeclarationContext> externalDeclarations = compilationUnit.translationUnit().externalDeclaration();
        if (externalDeclarations.isEmpty())
            return builder.build();
        TypeResolver typeResolver = new TypeResolver(externalDeclarations);
        externalDeclarations.stream()
                .filter(ANTLRProgramParser::isKernel)
                .map(ctx -> parseKernel(ctx, typeResolver))
                .forEach(builder::withKernel);
        return builder.build();
    }

    private static ParsedKernel parseKernel(ExternalDeclarationContext ctx, TypeResolver typeResolver) {
        DirectDeclaratorContext directDeclarator = ctx.functionDefinition().declarator().directDeclarator();
        String kernelName = declaratorIdentifier(directDeclarator);
        ParsedKernel.Builder kernelBuilder = ParsedKernel.builder().withName(kernelName);
        if (directDeclarator.parameterTypeList() == null)
            throw new IllegalArgumentException("No parameterTypeList in " + ctx.getText());

        List<ParameterDeclarationContext> parameterDeclarations = directDeclarator
                .parameterTypeList()
                .parameterList()
                .parameterDeclaration();

        IntStream.range(0, parameterDeclarations.size())
                .mapToObj(index -> createKernelArgument(index, parameterDeclarations.get(index), typeResolver))
                .forEach(kernelBuilder::withArgument);

        return kernelBuilder.build();
    }


    private static ParsedKernelArgument createKernelArgument(int index, ParameterDeclarationContext ctx, TypeResolver typeResolver) {
        Objects.requireNonNull(ctx, "ctx is null");
        DirectDeclaratorContext declarator = ctx.declarator().directDeclarator();
        ParsedKernelArgument.Builder builder = ParsedKernelArgument.builder()
                .withIndex(index)
                .withName(declaratorIdentifier(declarator));
        List<DeclarationSpecifierContext> declarationSpecifiers = ctx.declarationSpecifiers().declarationSpecifier();
        CLAddressSpace addressSpace = CLAddressSpace.PRIVATE;
        CLType type = null;
        boolean cnst = false;
        for (DeclarationSpecifierContext declarationSpecifier : declarationSpecifiers) {
            if (declarationSpecifier.storageClassSpecifier() != null)
                addressSpace = parseAddressSpace(declarationSpecifier.storageClassSpecifier().getText());
            else if (declarationSpecifier.typeQualifier() != null && declarationSpecifier.typeQualifier().Const() != null)
                cnst = true;
            else if (declarationSpecifier.typeSpecifier() != null) {
                TypeSpecifierContext typeSpecifier = declarationSpecifier.typeSpecifier();
                if (type != null)
                    throw new IllegalStateException("type already parsed for declaration " + ctx.getText());
                type = parseType(typeSpecifier, typeResolver);
            }
        }
        if (type == null)
            throw new IllegalStateException("No type found in declaration " + ctx.getText());
        if (cnst)
            type = new CLConstType(type);

        return builder.withAddressSpace(addressSpace)
                .withType(type)
                .build();
    }

    private static CLType parseType(TypeSpecifierContext ctx, TypeResolver typeResolver) {
        if (ctx.dataTypeSpecifier() != null)
            return typeResolver.getType(ctx.dataTypeSpecifier().getText());
        if (ctx.typedefName() != null)
            return typeResolver.getType(ctx.typedefName().Identifier().getText());
        if (ctx.pointer() != null)
            return new CLPointerType(parseType(ctx.typeSpecifier(), typeResolver));
        throw new UnsupportedOperationException("Unhandled type specifier " + ctx.getText());
    }

    private static CLAddressSpace parseAddressSpace(ParameterDeclarationContext ctx) {
        return ctx.declarationSpecifiers().declarationSpecifier().stream()
                .map(ds -> ds.storageClassSpecifier() == null ? null : ds.storageClassSpecifier().getText())
                .map(ANTLRProgramParser::parseAddressSpace)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(CLAddressSpace.PRIVATE);
    }

    private static CLAddressSpace parseAddressSpace(String text) {
        if (text == null) return null;
        switch (text) {
            case "__global":
            case "global":
                return CLAddressSpace.GLOBAL;
            case "__constant":
            case "constant":
                return CLAddressSpace.CONSTANT;
            case "__local":
            case "local":
                return CLAddressSpace.LOCAL;
            case "__private":
            case "private":
                return CLAddressSpace.PRIVATE;
            default:
                return null;
        }
    }

    private static boolean isKernel(ExternalDeclarationContext ctx) {
        FunctionDefinitionContext fdCtx = ctx.functionDefinition();
        return fdCtx != null && fdCtx.declarationSpecifiers().declarationSpecifier().stream()
                .map(DeclarationSpecifierContext::functionSpecifier)
                .filter(Objects::nonNull)
                .map(RuleContext::getText)
                .anyMatch(s -> s.equals("kernel") || s.equals("__kernel"));
    }
}
