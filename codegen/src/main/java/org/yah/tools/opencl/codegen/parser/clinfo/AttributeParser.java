package org.yah.tools.opencl.codegen.parser.clinfo;

import org.yah.tools.opencl.codegen.parser.attribute.*;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class AttributeParser {

    private static final Pattern VEC_TYPE_HINT_PATTERN = Pattern.compile("vec_type_hint\\(([\\w\\d]+)\\)");
    private static final Pattern WG_SIZE_HINT_PATTERN = Pattern.compile("work_group_size_hint\\((\\d+),(\\d+),(\\d+)\\)");
    private static final Pattern REQD_WG_SIZE_PATTERN = Pattern.compile("reqd_work_group_size\\((\\d+),(\\d+),(\\d+)\\)");

    private final TypeResolver typeResolver;

    public AttributeParser(TypeResolver typeResolver) {
        this.typeResolver = Objects.requireNonNull(typeResolver, "typeResolver is null");
    }

    public ParsedAttribute parse(String attribute) {
        String s = attribute.replaceAll("\\s+", "");
        Matcher matcher = VEC_TYPE_HINT_PATTERN.matcher(s);
        if (matcher.matches())
            return new VectorTypeHint(typeResolver.resolve(matcher.group(1)));
        matcher = WG_SIZE_HINT_PATTERN.matcher(s);
        if (matcher.matches())
            return new WorkGroupSizeHint(parseInt(matcher.group(1)), parseInt(matcher.group(2)), parseInt(matcher.group(3)));

        matcher = REQD_WG_SIZE_PATTERN.matcher(s);
        if (matcher.matches())
            return new ReqdWorkGroupSize(parseInt(matcher.group(1)), parseInt(matcher.group(2)), parseInt(matcher.group(3)));
        return new UnknownParsedAttribute(s);
    }
}
