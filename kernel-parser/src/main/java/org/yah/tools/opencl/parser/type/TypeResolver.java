package org.yah.tools.opencl.parser.type;

import org.yah.tools.opencl.parser.OpenCL12Parser.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class TypeResolver {
    private final Map<String, CLType> types = new HashMap<>();
    private final Map<String, CLStruct> structs = new HashMap<>();

    private final List<ExternalDeclarationContext> externalDeclarations;

    public TypeResolver(List<ExternalDeclarationContext> externalDeclarations) {
        this.externalDeclarations = externalDeclarations;
    }

    public CLType getType(String name) {
        CLType type = types.get(name);
        if (type == null) {
            type = resolve(name);
            types.put(type.getName(), type);
        }
        return type;
    }

    private CLType resolve(String name) {
        CLType type = CLScalarDataType.resolve(name);
        if (type != null) return type;

        type = CLVectorType.resolve(name);
        if (type != null) return type;

        type = CLVoidType.resolve(name);
        if (type != null) return type;

        type = CLBoolType.resolve(name);
        if (type != null) return type;

        type = CLBuiltinDataType.resolve(name);
        if (type != null) return type;

        return createType(name);
    }

    private CLType createType(String name) {
        for (ExternalDeclarationContext edc : externalDeclarations) {
            if (isTypeDef(edc, name))
                return resolveTypeDef(edc, name);
        }
        throw new IllegalArgumentException("Unresolved type " + name);
    }

    private CLType resolveTypeDef(ExternalDeclarationContext edc, String name) {
        InitDeclaratorListContext declaratorList = edc.declaration().initDeclaratorList();
        boolean pointer = declarationSpecifiers(edc).anyMatch(ds -> ds.typeSpecifier() != null && ds.typeSpecifier().pointer() != null);

        if (declaratorList != null) {
            declaratorList.initDeclarator().stream()
                    .map(id -> declaratorIdentifier(id.declarator().directDeclarator()))
                    .anyMatch(n -> Objects.equals(n, name));
        }
//        return declarationSpecifiers(edc)
//                .filter(ds -> ds.typeSpecifier() != null && ds.typeSpecifier().typedefName() != null)
//                .map(ds -> ds.typeSpecifier().typedefName().Identifier().getText())
//                .reduce((a, b) -> b).map(n -> Objects.equals(n, name)).orElse(false);
        return null;
    }

    private static boolean isTypeDef(ExternalDeclarationContext edc, String name) {
        DeclarationContext declaration = edc.declaration();
        if (declaration == null)
            return false;
        DeclarationSpecifierContext firstSpecifier = declaration.declarationSpecifiers().declarationSpecifier(0);
        if (firstSpecifier.storageClassSpecifier() == null || firstSpecifier.storageClassSpecifier().Typedef() == null)
            return false;

        InitDeclaratorListContext declaratorList = declaration.initDeclaratorList();
        if (declaratorList != null) {
            return declaratorList.initDeclarator().stream()
                    .map(id -> declaratorIdentifier(id.declarator().directDeclarator()))
                    .anyMatch(n -> Objects.equals(n, name));
        }
        return declarationSpecifiers(edc)
                .filter(ds -> ds.typeSpecifier() != null && ds.typeSpecifier().typedefName() != null)
                .map(ds -> ds.typeSpecifier().typedefName().Identifier().getText())
                .reduce((a, b) -> b).map(n -> Objects.equals(n, name)).orElse(false);
    }

    private static Stream<TypeSpecifierContext> typeSpecifiers(ExternalDeclarationContext edc) {
        return declarationSpecifiers(edc)
                .map(DeclarationSpecifierContext::typeSpecifier)
                .filter(Objects::nonNull);
    }

    private static Stream<DeclarationSpecifierContext> declarationSpecifiers(ExternalDeclarationContext edc) {
        return edc.declaration().declarationSpecifiers().declarationSpecifier().stream();
    }

    public static String declaratorIdentifier(DirectDeclaratorContext declaratorContext) {
        Objects.requireNonNull(declaratorContext, "declaratorContext is null");
        DirectDeclaratorContext ctx = declaratorContext;
        while (ctx != null) {
            if (ctx.Identifier() != null)
                break;
            if (ctx.declarator() != null && ctx.declarator().directDeclarator() != null)
                return declaratorIdentifier(ctx.declarator().directDeclarator());
            ctx = ctx.directDeclarator();
        }
        if (ctx == null)
            throw new IllegalArgumentException("No identifier found in " + declaratorContext.getText());
        return ctx.Identifier().getText();
    }

}
