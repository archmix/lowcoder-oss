package lowcoder.metadata.interfaces;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "value")
public class Name {
    private final String value;

    public static Name create(String name) {
        return new Name(name);
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
