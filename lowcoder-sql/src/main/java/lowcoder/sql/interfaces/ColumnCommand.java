package lowcoder.sql.interfaces;

import morphos.api.interfaces.Column;
import morphos.api.interfaces.ForeignKey;
import morphos.api.interfaces.PrimaryKey;
import morphos.api.interfaces.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class ColumnCommand {
  protected Collection<Column> columnsForInsert(Table table) {
    var columns = columnsForUpdate(table);
    columns.addAll(table.getPrimaryKeys().stream().filter(pk -> !pk.getGenerated()).map(PrimaryKey::getColumn)
      .collect(Collectors.toList()));
    return columns;
  }

  protected Collection<Column> columnsForUpdate(Table table) {
    var columns = new ArrayList<Column>();
    columns.addAll(table.getColumns());
    columns.addAll(table.getForeignKeys().stream().map(ForeignKey::getColumn).collect(Collectors.toList()));
    return columns;
  }
}