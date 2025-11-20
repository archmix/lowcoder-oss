package lowcoder.sql.interfaces;

import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.Tuple;
import morphos.api.interfaces.Column;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

enum TupleType {
  BIT(Column.Type.BIT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBoolean(tuple, value);
    }
  },
  TINYINT(Column.Type.TINYINT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setInteger(tuple, value);
    }
  },
  SMALLINT(Column.Type.SMALLINT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setInteger(tuple, value);
    }
  },
  INTEGER(Column.Type.INTEGER) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setInteger(tuple, value);
    }
  },
  BIGINT(Column.Type.BIGINT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setLong(tuple, value);
    }
  },
  FLOAT(Column.Type.FLOAT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  REAL(Column.Type.REAL) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  DOUBLE(Column.Type.DOUBLE) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  NUMERIC(Column.Type.NUMERIC) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  DECIMAL(Column.Type.DECIMAL) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDouble(tuple, value);
    }
  },
  CHAR(Column.Type.CHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  VARCHAR(Column.Type.VARCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  LONGVARCHAR(Column.Type.LONGVARCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  DATE(Column.Type.DATE) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  TIME(Column.Type.TIME) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  TIMESTAMP(Column.Type.TIMESTAMP) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  BINARY(Column.Type.BINARY) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBinary(tuple, value);
    }
  },
  VARBINARY(Column.Type.VARBINARY) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBinary(tuple, value);
    }
  },
  LONGVARBINARY(Column.Type.LONGVARBINARY) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBinary(tuple, value);
    }
  },
  NULL(Column.Type.NULL) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setNull(tuple);
    }
  },
  BLOB(Column.Type.BLOB) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBinary(tuple, value);
    }
  },
  CLOB(Column.Type.CLOB) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  BOOLEAN(Column.Type.BOOLEAN) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setBoolean(tuple, value);
    }
  },
  NCHAR(Column.Type.NCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  NVARCHAR(Column.Type.NVARCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  LONG_NVARCHAR(Column.Type.LONG_NVARCHAR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  NCLOB(Column.Type.NCLOB) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  TIME_WITH_TIMEZONE(Column.Type.TIME_WITH_TIMEZONE) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  TIMESTAMP_WITH_TIMEZONE(Column.Type.TIMESTAMP_WITH_TIMEZONE) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setDate(tuple, value);
    }
  },
  DISTINCT(Column.Type.DISTINCT) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setNull(tuple);
    }
  },
  OTHER(Column.Type.OTHER) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  UUID(Column.Type.UUID) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  ENUM(Column.Type.ENUM) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  XML(Column.Type.XML) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  MACADDR(Column.Type.MACADDR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  INET(Column.Type.INET) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  CIDR(Column.Type.CIDR) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  JSON(Column.Type.JSON) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  ARRAY(Column.Type.ARRAY) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
    }
  },
  INTERVAL(Column.Type.INTERVAL) {
    @Override
    protected void set(Tuple tuple, Object value) {
      setString(tuple, value);
      }
    };

  private final Column.Type type;

  TupleType(Column.Type type) {
    this.type = type;
  }

  public static Optional<TupleType> valueOf(Column.Type type) {
    for (TupleType tupleType : TupleType.values()) {
      if (tupleType.type == type) {
        return Optional.of(tupleType);
      }
    }
    return Optional.empty();
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