package com.capimgrosso.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static java.sql.Types.*;

public class SqlTemplate implements JdbcTemplate {
  private DataSource dataSource;

  public SqlTemplate(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public <T> List<T> queryForList(String sql, Class<T> type, RowMapper<T> fn, Object ...values) {
    List<T> list = new ArrayList<>();

    try (Connection conn = dataSource.getConnection()) {
      PreparedStatement stmt = conn.prepareStatement(sql);

      for (int i = 0; i < values.length; i++) {
        stmt.setObject(i+1, values[i]);
      }
      ResultSet rs = stmt.executeQuery();
      int i = 0;
      while (rs.next()) {
        list.add(fn.rowMapper(rs, ++i));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return list;
  }

  @Override
  public <T> Optional<T> queryForObject(String sql, Class<T> type, RowMapper<T> fn, Object... values) {
    T result = null;
    try (Connection conn = dataSource.getConnection()) {
      PreparedStatement stmt = conn.prepareStatement(sql);

      for (int i = 0; i < values.length; i++) {
        stmt.setObject(i+1, values[i]);
      }

      ResultSet rs = stmt.executeQuery();
      if (rs.next()){
        result = fn.rowMapper(rs,0);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return Optional.ofNullable(result);
  }

  @Override
  public Optional<Map<String, Object>> first(String sql, Object ...itens) {
    Map<String, Object> result = null;
    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(sql);

      for (int i = 0; i < itens.length; i++) {
        stmt.setObject(i+1, itens[i]);
      }

      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        ResultSetMetaData rsm = rs.getMetaData();
        int columnCount = rsm.getColumnCount();
        result = new LinkedHashMap<>();

        for (int i = 1; i <= columnCount; i++) {
          result.put(rsm.getColumnName(i), rs.getObject(i));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.ofNullable(result);
  }

  @Override
  public List<Map<String, Object>> list(String sql, Object... values) {
    List<Map<String, Object>> result = new ArrayList<>();

    try (Connection connection = dataSource.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(sql);

      for (int i = 0; i < values.length; i++) {
        stmt.setObject(i+1, values[i]);
      }
      ResultSet rs = stmt.executeQuery();
      ResultSetMetaData rsm = rs.getMetaData();
      int columnCount = rsm.getColumnCount();

      while (rs.next()) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (int i = 1; i <= columnCount; i++) {
          row.put(rsm.getColumnName(i), rs.getObject(i));
        }
        result.add(row);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
}
