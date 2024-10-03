package lowcoder.sql.interfaces;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
public class SQLGenerator {
	private static final Collector<CharSequence, ?, String> COMMA_JOINING = Collectors.joining(",");
	private static final Collector<CharSequence, ?, String> SPACE_JOINING = Collectors.joining(" ");

	public String insertSQL(String table, Collection<String> columns) {
		String insertTemplate = "INSERT INTO {0}({1}) VALUES ({2})";
		String columnsNames = columns.stream().collect(COMMA_JOINING);
		String columnsValues = columns.stream().map(column -> "?").collect(COMMA_JOINING);

		return MessageFormat.format(insertTemplate, table, columnsNames, columnsValues);
	}

	public String deleteSQL(String table, Collection<String> constraints) {
		String deleteSQL = this.deleteSQL(table);
		String whereClause = this.whereClause(constraints);

		return MessageFormat.format("{0} {1}", deleteSQL, whereClause);
	}

	public String deleteBySQL(String table, String constraint) {
		String deleteSQL = this.deleteSQL(table);
		String whereClause = this.whereClause(List.of(constraint));

		return MessageFormat.format("{0} {1}", deleteSQL, whereClause);
	}

	public String updateSQL(String table, Collection<String> columns, Collection<String> constraints) {
		String updateSQLTemplate = "UPDATE {0} SET {1} {2}";
		String columnSet = columns.stream().map(column -> column + " = ?").collect(COMMA_JOINING);
		String whereClause = this.whereClause(constraints);

		return MessageFormat.format(updateSQLTemplate, table, columnSet, whereClause);
	}

	public String selectSQL(String table, Collection<String> columns, Collection<String> constraints) {
		String selectAllSQL = this.selectAllSQL(table, columns);
		String whereClause = this.whereClause(constraints);

		return MessageFormat.format("{0} {1}", selectAllSQL, whereClause);
	}

	public String selectBySQL(String table, Collection<String> columns, String constraint) {
		String selectAll = this.selectAllSQL(table, columns);
		String whereClause = this.whereClause(List.of(constraint));

		return MessageFormat.format("{0} {1}", selectAll, whereClause);
	}

	/**
	 * <b>CAUTION</b> It has no WHERE Clause, so it will do a full table scan
	 */
	public String selectAllSQL(String table, Collection<String> columns) {
		String selectTemplate = "SELECT {0} FROM {1}";
		String columnSet = columns.stream().collect(COMMA_JOINING);

		return MessageFormat.format(selectTemplate, columnSet, table);
	}

	/**
	 * <b>CAUTION</b> It has no WHERE Clause, so it will clean table entirely
	 */
	public String deleteSQL(String table) {
		String deleteSQLTemplate = "DELETE FROM {0}";

		return MessageFormat.format(deleteSQLTemplate, table);
	}

	private String whereClause(Collection<String> columns) {
		return columns.stream().map(column -> "AND " + column + " = ?").collect(SPACE_JOINING).replaceFirst("AND ", "WHERE ");
	}
}