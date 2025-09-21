package lowcoder.api.infra;

import com.google.common.collect.Sets;
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
        return Sets.newHashSet(StartupServiceSpecification.class.getName());
    }
}
