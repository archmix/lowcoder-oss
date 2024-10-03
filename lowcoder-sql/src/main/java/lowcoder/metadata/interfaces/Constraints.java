package lowcoder.metadata.interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Constraints {
    private final Map<ConstraintType, Object> constraints = new HashMap<>();

    public void add(ConstraintType type, Object value) {
        this.constraints.put(type, value);
    }

    public <T> T get(ConstraintType type) {
        return (T) this.constraints.get(type);
    }

    public Optional<Object> ifConstraint(ConstraintType type) {
        return Optional.ofNullable(this.constraints.get(type));
    }
}
