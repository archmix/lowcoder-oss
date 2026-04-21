package lowcoder.sql.interfaces;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import morphos.api.interfaces.Field;
import morphos.api.interfaces.Table;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Fields implements Iterable<Field> {
  private final Collection<Field> fields;

  public static Fields create(Table table, Collection<String> fieldNames){
    var fields = new HashSet<Field>();
    if(fieldNames.isEmpty()) {
      fields.addAll(table.getFields());
    }

    fieldNames.forEach(field -> {
      table.getField(field).ifPresent(fields::add);
    });

    return new Fields(fields);
  }

  public boolean contains(Field field) {
    return this.fields.contains(field);
  }

  public boolean contains(Alias alias) {
    return this.fields.contains(alias.getField());
  }

  @Override
  public Iterator<Field> iterator() {
    return this.fields.iterator();
  }

  public Stream<Field> stream(){
    return this.fields.stream();
  }
}
