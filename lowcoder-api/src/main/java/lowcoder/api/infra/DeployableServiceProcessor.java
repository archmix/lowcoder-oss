package lowcoder.api.infra;

import com.google.common.collect.Sets;
import compozitor.processor.core.interfaces.Processor;
import compozitor.processor.core.interfaces.ServiceProcessor;
import lowcoder.api.interfaces.DeployableService;
import lowcoder.api.interfaces.DeployableServiceSpecification;

import java.util.Collections;
import java.util.Set;

@Processor
public class DeployableServiceProcessor extends ServiceProcessor {
    @Override
    protected Iterable<Class<?>> serviceClasses() {
        return Collections.singletonList(DeployableService.class);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(DeployableServiceSpecification.class.getName());
    }
}
