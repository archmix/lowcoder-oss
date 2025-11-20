package lowcoder.core.infra;

import compozitor.processor.core.interfaces.Processor;
import compozitor.processor.core.interfaces.ServiceProcessor;
import lowcoder.core.interfaces.RouterService;
import lowcoder.core.interfaces.RouterServiceSpecification;

import java.util.Collections;
import java.util.Set;

@Processor
public class RouterServiceProcessor extends ServiceProcessor {
    @Override
    protected Iterable<Class<?>> serviceClasses() {
        return Collections.singletonList(RouterService.class);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(RouterServiceSpecification.class.getName());
    }
}
