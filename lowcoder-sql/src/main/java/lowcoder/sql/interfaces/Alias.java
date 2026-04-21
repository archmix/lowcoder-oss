package lowcoder.sql.interfaces;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import morphos.api.interfaces.Field;

@RequiredArgsConstructor(staticName = "create")
@EqualsAndHashCode
@Getter
public class Alias {
  private final String fullName;
  private final Field field;

  public String getAlias(){
    return this.fullName + " AS " + this.getColumnName();
  }

  public String getColumnName(){
    return this.fullName.replaceAll("\\.", "_");
  }
}
