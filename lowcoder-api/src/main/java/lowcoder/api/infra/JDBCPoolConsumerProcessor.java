package lowcoder.api.infra;

import com.google.common.collect.Sets;
import compozitor.processor.core.interfaces.Processor;
import compozitor.processor.core.interfaces.ServiceProcessor;
import lowcoder.api.interfaces.JDBCPoolConsumer;
import lowcoder.api.interfaces.JDBCPoolConsumerSpecification;

import java.util.Collections;
import java.util.Set;

@Processor
public class JDBCPoolConsumerProcessor extends ServiceProcessor {
    @Override
    protected Iterable<Class<?>> serviceClasses() {
        return Collections.singletonList(JDBCPoolConsumer.class);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Sets.newHashSet(JDBCPoolConsumerSpecification.class.getName());
    }
}
