package lowcoder.metadata.interfaces;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class Tables implements Iterable<Table> {
    private final Map<String, Table> tables;

    public List<Table> find(Predicate<Table> predicate) {
        return this.stream(predicate).collect(Collectors.toList());
    }

    public Table get(String name) {
        return this.tables.get(name);
    }

    public Stream<Table> stream(Predicate<Table> predicate) {
        return this.tables.values().stream().filter(predicate);
    }

    @Override
    public Iterator<Table> iterator() {
        return this.tables.values().iterator();
    }
}