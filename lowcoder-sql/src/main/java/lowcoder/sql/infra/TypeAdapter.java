package lowcoder.sql.infra;

import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.Tuple;
import morphos.api.interfaces.Field;

import java.time.LocalDate;
import java.time.ZoneId;

public enum TypeAdapter {
  BIT(Field.Type.BIT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBoolean(tuple, value);
    }
  },
  TINYINT(Field.Type.TINYINT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setInteger(tuple, value);
    }
  },
  SMALLINT(Field.Type.SMALLINT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setInteger(tuple, value);
    }
  },
  INTEGER(Field.Type.INTEGER) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setInteger(tuple, value);
    }
  },
  BIGINT(Field.Type.BIGINT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setLong(tuple, value);
    }
  },
  FLOAT(Field.Type.FLOAT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  REAL(Field.Type.REAL) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  DOUBLE(Field.Type.DOUBLE) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  NUMERIC(Field.Type.NUMERIC) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  DECIMAL(Field.Type.DECIMAL) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  CHAR(Field.Type.CHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  VARCHAR(Field.Type.VARCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  LONGVARCHAR(Field.Type.LONGVARCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  DATE(Field.Type.DATE) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  TIME(Field.Type.TIME) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  TIMESTAMP(Field.Type.TIMESTAMP) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  BINARY(Field.Type.BINARY) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBinary(tuple, value);
    }
  },
  VARBINARY(Field.Type.VARBINARY) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBinary(tuple, value);
    }
  },
  LONGVARBINARY(Field.Type.LONGVARBINARY) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBinary(tuple, value);
    }
  },
  NULL(Field.Type.NULL) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setNull(tuple);
    }
  },
  BLOB(Field.Type.BLOB) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBinary(tuple, value);
    }
  },
  CLOB(Field.Type.CLOB) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  BOOLEAN(Field.Type.BOOLEAN) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBoolean(tuple, value);
    }
  },
  NCHAR(Field.Type.NCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  NVARCHAR(Field.Type.NVARCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  LONG_NVARCHAR(Field.Type.LONG_NVARCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  NCLOB(Field.Type.NCLOB) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  TIME_WITH_TIMEZONE(Field.Type.TIME_WITH_TIMEZONE) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  TIMESTAMP_WITH_TIMEZONE(Field.Type.TIMESTAMP_WITH_TIMEZONE) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  DISTINCT(Field.Type.DISTINCT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setNull(tuple);
    }
  },
  OTHER(Field.Type.OTHER) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  UUID(Field.Type.UUID) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  ENUM(Field.Type.ENUM) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  XML(Field.Type.XML) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  MACADDR(Field.Type.MACADDR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  INET(Field.Type.INET) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  CIDR(Field.Type.CIDR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  JSON(Field.Type.JSON) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  ARRAY(Field.Type.ARRAY) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  INTERVAL(Field.Type.INTERVAL) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
      }
    };

  private final Field.Type type;

  TypeAdapter(Field.Type type) {
    this.type = type;
  }

  public static TypeAdapter valueOf(Field field) {
    for (TypeAdapter adapter : TypeAdapter.values()) {
      if (field.getType().equals(adapter.type)) {
        return adapter;
      }
    }
    return TypeAdapter.OTHER;
  }

  protected abstract void set(Tuple tuple, Object value);

  private static void setBinary(Tuple tuple, Object value) {
    tuple.addBuffer(Buffer.buffer(value.toString()));
  }

  private static void setString(Tuple tuple, Object value) {
    tuple.addString(value.toString());
  }

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