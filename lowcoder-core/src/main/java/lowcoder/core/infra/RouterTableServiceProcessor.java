package lowcoder.core.infra;

import compozitor.processor.core.interfaces.Processor;
import compozitor.processor.core.interfaces.ServiceProcessor;
import lowcoder.core.interfaces.RouterTableService;
import lowcoder.core.interfaces.RouterTableServiceSpecification;

import java.util.Collections;
import java.util.Set;

@Processor
public class RouterTableServiceProcessor extends ServiceProcessor {
    @Override
    protected Iterable<Class<?>> serviceClasses() {
        return Collections.singletonList(RouterTableService.class);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(RouterTableServiceSpecification.class.getName());
    }
}
