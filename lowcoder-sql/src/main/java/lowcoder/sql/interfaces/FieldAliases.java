package lowcoder.sql.interfaces;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import morphos.api.interfaces.Field;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldAliases implements Iterable<Alias> {
  private final Map<Field, Alias> aliases;

  public static FieldAliases create(Iterable<Field> fields){
    return create(fields, Field::getFullName);
  }

  public static FieldAliases create(Iterable<Field> fields, String aliasPrefix){
    return create(fields, (field) -> aliasPrefix + "." + field.getName());
  }

  public static FieldAliases create(Iterable<Field> fields, Function<Field, String> getAlias){
    var aliases = new HashMap<Field, Alias>();
    fields.forEach(field -> {
      aliases.put(field, Alias.create(getAlias.apply(field), field));
    });

    return new FieldAliases(aliases);
  }

  public Alias getAliasByFieldName(String fieldName) {
    Function<Field, Boolean> findField = (field) -> field.getFullName().equals(fieldName) || field.getName().equals(fieldName);

    var field = this.aliases.keySet().stream().filter(findField::apply).findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Alias not found for fieldName"));
    return this.getAlias(field);
  }

  public Alias getAlias(Field field){
    return aliases.get(field);
  }

  @Override
  public Iterator<Alias> iterator() {
    return this.aliases.values().iterator();
  }

  public Stream<Alias> stream(){
    return this.aliases.values().stream();
  }

  public void forEach(BiConsumer<Field, Alias> entry){
    this.aliases.forEach(entry);
  }
}