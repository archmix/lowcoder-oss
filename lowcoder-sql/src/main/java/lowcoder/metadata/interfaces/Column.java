package lowcoder.metadata.interfaces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Types;
import java.util.Optional;

@Getter
@RequiredArgsConstructor(staticName = "create")
public class Column {
  private final Table table;

  private final Name name;

  private final ColumnType type;

  private final Long size;

  private final Long decimalDigits;

  private final Boolean autoIncrement;

  private final Boolean generated;

  private final Constraints constraints = new Constraints();

  public void add(ConstraintType constraintType, Object value){
    this.constraints.add(constraintType, value);
  }

  public static enum ColumnType {
    BIT(Types.BIT),
    TINYINT(Types.TINYINT),
    SMALLINT(Types.SMALLINT),
    INTEGER(Types.INTEGER),
    BIGINT(Types.BIGINT),
    FLOAT(Types.FLOAT),
    REAL(Types.REAL),
    DOUBLE(Types.DOUBLE),
    NUMERIC(Types.NUMERIC),
    DECIMAL(Types.DECIMAL),
    CHAR(Types.CHAR),
    VARCHAR(Types.VARCHAR),
    LONGVARCHAR(Types.LONGVARCHAR),
    DATE(Types.DATE),
    TIME(Types.TIME),
    TIMESTAMP(Types.TIMESTAMP),
    BINARY(Types.BINARY),
    VARBINARY(Types.VARBINARY),
    LONGVARBINARY(Types.LONGVARBINARY),
    NULL(Types.NULL),
    OTHER(Types.OTHER),
    JAVA_OBJECT(Types.JAVA_OBJECT),
    DISTINCT(Types.DISTINCT),
    STRUCT(Types.STRUCT),
    ARRAY(Types.ARRAY),
    BLOB(Types.BLOB),
    CLOB(Types.CLOB),
    REF(Types.REF),
    DATALINK(Types.DATALINK),
    BOOLEAN(Types.BOOLEAN),
    ROWID(Types.ROWID),
    NCHAR(Types.NCHAR),
    NVARCHAR(Types.NVARCHAR),
    LONGNVARCHAR(Types.LONGNVARCHAR),
    NCLOB(Types.NCLOB),
    SQLXML(Types.SQLXML),
    REF_CURSOR(Types.REF_CURSOR),
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE);

    private int value;

    ColumnType(int value) {
      this.value = value;
    }

    public static Optional<ColumnType> of(int code){
      for(ColumnType type : ColumnType.values()) {
        if(type.value == code){
          return Optional.of(type);
        }
      }

      return Optional.empty();
    }

    public int value() {
      return this.value;
    }
  }
}
