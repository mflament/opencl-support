package org.yah.tools.opencl.codegen.builder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodBodyGeneratorFactories implements Iterable<MethodBodyGeneratorFactory<?>> {

    public static MethodBodyGeneratorFactories empty() {
        return EMPTY;
    }

    private static final MethodBodyGeneratorFactories EMPTY = new MethodBodyGeneratorFactories(Collections.emptyList());

    private final List<MethodBodyGeneratorFactory<?>> factories;

    public MethodBodyGeneratorFactories() {
        this(new ArrayList<>());
    }

    private MethodBodyGeneratorFactories(List<MethodBodyGeneratorFactory<?>> factories) {
        this.factories = Objects.requireNonNull(factories, "factories is null");
    }

    public boolean isEmpty() {
        return factories.isEmpty();
    }

    public Iterator<MethodBodyGeneratorFactory<?>> iterator() {
        return factories.iterator();
    }

    public void add(MethodBodyGeneratorFactory<?> element) {
        factories.add(element);
    }

    public Stream<MethodBodyGeneratorFactory<?>> stream() {
        return factories.stream();
    }

    @SuppressWarnings("unchecked")
    public <G> List<MethodBodyGeneratorFactory<G>> find(G generator) {
        return stream().filter(f -> f.accept(generator))
                .map(f -> (MethodBodyGeneratorFactory<G>) f)
                .collect(Collectors.toList());
    }
}
