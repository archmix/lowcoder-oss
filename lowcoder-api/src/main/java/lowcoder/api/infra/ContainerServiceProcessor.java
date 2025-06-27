package lowcoder.api.infra;

import com.google.common.collect.Sets;
import compozitor.processor.core.interfaces.Processor;
import compozitor.processor.core.interfaces.ServiceProcessor;
import lowcoder.api.interfaces.ContainerService;
import lowcoder.api.interfaces.ContainerServiceSpecification;

import java.util.Collections;
import java.util.Set;

@Processor
public class ContainerServiceProcessor extends ServiceProcessor {
    @Override
    protected Iterable<Class<?>> serviceClasses() {
        return Collections.singletonList(ContainerService.class);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(ContainerServiceSpecification.class.getName());
    }
}
