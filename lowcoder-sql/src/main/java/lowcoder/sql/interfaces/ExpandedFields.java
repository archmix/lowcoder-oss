package lowcoder.sql.interfaces;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import morphos.api.interfaces.ForeignKey;
import morphos.api.interfaces.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpandedFields implements Iterable<ForeignKey>{
  private final Collection<ForeignKey> fields;

  public static ExpandedFields create(Table table, String expand){
    if(Objects.requireNonNullElse(expand, "").isBlank()) {
      return new ExpandedFields(Collections.emptySet());
    }

    var fields = new HashSet<ForeignKey>();

    if(expand.contains(",")) {
      var expandedFkNames = expand.split(",");
      for (var expandedFkName : expandedFkNames){
        var expandedFk = getExpandedFk(table, expandedFkName);
        fields.add(expandedFk);
      }
      return new ExpandedFields(fields);
    }

    fields.add(getExpandedFk(table, expand));
    return new ExpandedFields(fields);
  }

  private static ForeignKey getExpandedFk(Table table, String field) {
    return table.getForeignKey(field)
      .orElseThrow(() -> new IllegalArgumentException("Expanded field is not a foreign key"));
  }

  @Override
  public Iterator<ForeignKey> iterator() {
    return this.fields.iterator();
  }

  public Stream<ForeignKey> stream(){
    return this.fields.stream();
  }
}
