package lowcoder.metadata.interfaces;

import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.Tuple;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@RequiredArgsConstructor(staticName = "create")
@Builder
public class Column implements Field {
  private final Table table;

  @Getter
  private final String name;

  @Getter
  private final ColumnType type;

  private final Long size;

  private final Long decimalDigits;

  private final Boolean autoIncrement;

  private final Boolean generated;

  @Getter
  private final Constraints constraints = new Constraints();

  public void add(ConstraintType constraintType, Object value){
    this.constraints.add(constraintType, value);
  }

  public static enum ColumnType {
    BIT(Types.BIT){
      @Override
      protected void set(Tuple tuple, Object value) {
        setBoolean(tuple, value);
      }
    },
    TINYINT(Types.TINYINT){
      @Override
      protected void set(Tuple tuple, Object value) {
        setInteger(tuple, value);
      }
    },
    SMALLINT(Types.SMALLINT){
      @Override
      protected void set(Tuple tuple, Object value) {
        setInteger(tuple, value);
      }
    },
    INTEGER(Types.INTEGER){
      @Override
      protected void set(Tuple tuple, Object value) {
        setInteger(tuple, value);
      }
    },
    BIGINT(Types.BIGINT){
      @Override
      protected void set(Tuple tuple, Object value) {
        setLong(tuple, value);
      }
    },
    FLOAT(Types.FLOAT){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDouble(tuple, value);
      }
    },
    REAL(Types.REAL){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDouble(tuple, value);
      }
    },
    DOUBLE(Types.DOUBLE){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDouble(tuple, value);
      }
    },
    NUMERIC(Types.NUMERIC){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDouble(tuple, value);
      }
    },
    DECIMAL(Types.DECIMAL){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDouble(tuple, value);
      }
    },
    CHAR(Types.CHAR){
      @Override
      protected void set(Tuple tuple, Object value) {
        setString(tuple, value);
      }
    },
    VARCHAR(Types.VARCHAR){
      @Override
      protected void set(Tuple tuple, Object value) {
        setString(tuple, value);
      }
    },
    LONGVARCHAR(Types.LONGVARCHAR){
      @Override
      protected void set(Tuple tuple, Object value) {
        setString(tuple, value);
      }
    },
    DATE(Types.DATE){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDate(tuple, value);
      }
    },
    TIME(Types.TIME){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDate(tuple, value);
      }
    },
    TIMESTAMP(Types.TIMESTAMP){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDate(tuple, value);
      }
    },
    BINARY(Types.BINARY){
      @Override
      protected void set(Tuple tuple, Object value) {
        setBinary(tuple, value);
      }
    },
    VARBINARY(Types.VARBINARY){
      @Override
      protected void set(Tuple tuple, Object value) {
        setBinary(tuple, value);
      }
    },
    LONGVARBINARY(Types.LONGVARBINARY){
      @Override
      protected void set(Tuple tuple, Object value) {
        setBinary(tuple, value);
      }
    },
    NULL(Types.NULL){
      @Override
      protected void set(Tuple tuple, Object value) {
        setNull(tuple);
      }
    },
    BLOB(Types.BLOB){
      @Override
      protected void set(Tuple tuple, Object value) {
        setBinary(tuple, value);
      }
    },
    CLOB(Types.CLOB){
      @Override
      protected void set(Tuple tuple, Object value) {
        setString(tuple, value);
      }
    },
    BOOLEAN(Types.BOOLEAN){
      @Override
      protected void set(Tuple tuple, Object value) {
        setBoolean(tuple, value);
      }
    },
    NCHAR(Types.NCHAR){
      @Override
      protected void set(Tuple tuple, Object value) {
        setString(tuple, value);
      }
    },
    NVARCHAR(Types.NVARCHAR){
      @Override
      protected void set(Tuple tuple, Object value) {
        setString(tuple, value);
      }
    },
    LONGNVARCHAR(Types.LONGNVARCHAR){
      @Override
      protected void set(Tuple tuple, Object value) {
        setString(tuple, value);
      }
    },
    NCLOB(Types.NCLOB){
      @Override
      protected void set(Tuple tuple, Object value) {
        setString(tuple, value);
      }
    },
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDate(tuple, value);
      }
    },
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE){
      @Override
      protected void set(Tuple tuple, Object value) {
        setDate(tuple, value);
      }
    },
    NOT_SUPPORTED(Integer.MIN_VALUE) {
      @Override
      protected void set(Tuple tuple, Object value) {
        throw new UnsupportedOperationException("Column datatype is not supported.");
      }
    };

    private static void setBinary(Tuple tuple, Object value) {
      tuple.addBuffer(Buffer.buffer(value.toString()));
    }

    private static void setString(Tuple tuple, Object value) {
      tuple.addString(value.toString());
    }

    private final int value;

    ColumnType(int value) {
      this.value = value;
    }

    public static Optional<ColumnType> of(int code){
      for(ColumnType type : ColumnType.values()) {
        if(type.value == code){
          return Optional.of(type);
        }
      }

      return Optional.of(NOT_SUPPORTED);
    }

    public int value() {
      return this.value;
    }

    protected abstract void set(Tuple tuple, Object value);

    protected void setBoolean(Tuple tuple, Object value) {
      if(value instanceof Boolean) {
        tuple.addBoolean((Boolean) value);
        return;
      }
      if(value instanceof String) {
        tuple.addBoolean(Boolean.parseBoolean(value.toString()));
      }
      if(value instanceof Number) {
        tuple.addInteger(((Number) value).intValue());
      }
    }

    protected void setInteger(Tuple tuple, Object value) {
      if(value instanceof Number) {
        tuple.addInteger(((Number) value).intValue());
        return;
      }
      if(value instanceof String) {
        tuple.addInteger(Integer.parseInt(value.toString()));
      }
    }

    protected void setLong(Tuple tuple, Object value) {
      if(value instanceof Number) {
        tuple.addLong(((Number) value).longValue());
        return;
      }
      if(value instanceof String) {
        tuple.addLong(Long.parseLong(value.toString()));
      }
    }

    protected void setDouble(Tuple tuple, Object value) {
      if(value instanceof Number) {
        tuple.addDouble(((Number) value).doubleValue());
        return;
      }
      if(value instanceof String) {
        tuple.addDouble(Double.parseDouble(value.toString()));
      }
    }

    protected void setDate(Tuple tuple, Object value) {
      if(value instanceof java.sql.Date) {
        tuple.addLocalDate(((java.sql.Date) value).toLocalDate());
        return;
      }
      if(value instanceof java.util.Date) {
        LocalDate localDate = ((java.util.Date)value).toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();

        tuple.addLocalDate(localDate);
        return;
      }
      if(value instanceof String) {
        tuple.addLocalDate(LocalDate.parse(value.toString()));
      }
    }

    public void setValue(Tuple tuple, Object value) {
      if(value == null) {
        setNull(tuple);
        return;
      }

      this.set(tuple, value);
    }

    private static void setNull(Tuple tuple) {
      tuple.addValue(null);
    }
  }

  Boolean getGenerated() {
    Boolean defaultValue = this.constraints.ifConstraint(ConstraintType.DEFAULT).isPresent();
    return generated || autoIncrement || defaultValue;
  }

  public static class ColumnBuilder  {
    private Table.TableBuilder root;

    ColumnBuilder root(Table.TableBuilder root) {
      this.root = root;
      return this;
    }

    public Table.TableBuilder add() {
      return this.root.add(this.build());
    }

    public Table.TableBuilder asPrimaryKey(String indexName) {
      var primaryKey = PrimaryKey.create(indexName, this.build());
      this.root.add(primaryKey);
      return this.root;
    }

    public ForeignKey.ForeignKeyBuilder asForeignKey(String indexName) {
      return ForeignKey.builder().root(this.root).column(this.build());
    }
  }
}
