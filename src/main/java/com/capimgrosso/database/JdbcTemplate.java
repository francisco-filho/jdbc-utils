package com.capimgrosso.database;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JdbcTemplate {
  <T> List<T> queryForList(String sql, Class<T> type, RowMapper<T> consumer, Object ...values);

  <T> Optional<T> queryForObject(String sql, Class<T> type, RowMapper<T> consumer, Object... values);

  Optional<Map<String, Object>> first(String sql, Object ...values);

  List<Map<String, Object>> list(String sql, Object... values);
}