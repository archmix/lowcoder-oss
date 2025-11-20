package lowcoder.api.infra;

import compozitor.processor.core.interfaces.Processor;
import compozitor.processor.core.interfaces.ServiceProcessor;
import lowcoder.api.interfaces.StartupService;
import lowcoder.api.interfaces.StartupServiceSpecification;

import java.util.Collections;
import java.util.Set;

@Processor
public class StartupServiceProcessor extends ServiceProcessor {
    @Override
    protected Iterable<Class<?>> serviceClasses() {
        return Collections.singletonList(StartupService.class);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(StartupServiceSpecification.class.getName());
    }
}
