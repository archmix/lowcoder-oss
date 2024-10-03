package lowcoder.metadata.interfaces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.DatabaseMetaData;

@RequiredArgsConstructor(staticName = "create")
@Getter
public class ForeignKey {
  private final String indexName;

  private final String tableName;

  private final Column column;

  private final Rule onUpdate;

  private final Rule onDelete;

  public static enum Rule {
    CASCADE,
    RESTRICT,
    SET_NULL,
    NO_ACTION,
    SET_DEFAULT;

    public static Rule of(int value) {
      if(value == DatabaseMetaData.importedKeyCascade){
        return CASCADE;
      }

      if(value == DatabaseMetaData.importedKeyRestrict){
        return RESTRICT;
      }

      if(value == DatabaseMetaData.importedKeySetNull){
        return SET_NULL;
      }

      if(value == DatabaseMetaData.importedKeyNoAction){
        return NO_ACTION;
      }

      if(value == DatabaseMetaData.importedKeySetDefault){
        return SET_DEFAULT;
      }

      throw new RuntimeException(String.format("Invalid value for Foreign Key Rule %d", value));
    }
  }
}
