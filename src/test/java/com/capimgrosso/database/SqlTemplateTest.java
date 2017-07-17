package com.capimgrosso.database;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SqlTemplateTest {

  @Mock
  private DataSource dataSource;
  @Mock
  private Connection conn;
  @Mock
  Statement stmt;
  @Mock
  PreparedStatement pstmt;
  @Mock
  ResultSet rs;
  @Mock
  ResultSetMetaData rsmd;

  private SqlTemplate template;

  private final String sqlWithResult = "SELECT 1 id FROM dependencia WHERE prefixo = 1";
  private final String sqlWithNoResult = "SELECT 1 id FROM dependencia WHERE prefixo = -1";
  private final String sqlWithResultList = "...";

  @Before
  public void setup() throws SQLException {
    assertNotNull(dataSource);
    Mockito.when(dataSource.getConnection()).thenReturn(conn);
    Mockito.when(conn.prepareStatement(any(String.class))).thenReturn(pstmt);
    Mockito.when(pstmt.executeQuery()).thenReturn(rs);
    Mockito.when(pstmt.executeQuery()).thenReturn(rs);
    Mockito.when(rs.getMetaData()).thenReturn(rsmd);
    Mockito.when(rsmd.getColumnCount()).thenReturn(2);
    Mockito.when(rsmd.getColumnName(1)).thenReturn("id");
    Mockito.when(rsmd.getColumnName(2)).thenReturn("nome");

    Mockito.when(rs.getObject(1)).thenReturn(1).thenReturn(2);
    Mockito.when(rs.getObject(2)).thenReturn("hello").thenReturn("world");

    template = new SqlTemplate(dataSource);
  }

  @Test
  public void first_deveRetornarMapComUmKey() throws Exception {
    when(rs.next()).thenReturn(true);
    Mockito.when(rs.getObject(1)).thenReturn(1);

    Optional<Map<String, Object>> result = template.first(sqlWithResult, 1, 5);

    assertThat(result.get().get("id"), is(1));
    Mockito.verify(pstmt, times(1)).setObject(1, 1);
    Mockito.verify(pstmt, times(1)).setObject(2, 5);
  }

  @Test
  public void first_deveRetornarMapComDoisKeys() throws Exception {
    when(rs.next()).thenReturn(true);
    Optional<Map<String, Object>> result = template.first(sqlWithResult, 2);

    assertThat(result.get().get("id"), is(1));
    assertThat(result.get().get("nome"), is("hello"));
    Mockito.verify(pstmt, times(1)).setObject(1, 2);
  }

  @Test
  public void list_deveRetornarListaCom2Mapas() throws Exception {
    when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false);
    List<Map<String, Object>> result = template.list(sqlWithResultList, 2);

    assertThat(result.size(), is(2));
    assertThat(1, is(result.get(0).get("id")));
    assertThat("hello", is(result.get(0).get("nome")));
    assertThat(2, is(result.get(1).get("id")));
    assertThat("world", is(result.get(1).get("nome")));
    Mockito.verify(pstmt, times(1)).setObject(1, 2);
  }

  @Test
  public void queryForObject_deveRetornarVazioSeRegistroNaoEncontrado() throws Exception {
    when(rs.next()).thenReturn(false);

    Optional<Integer> i = template.queryForObject(sqlWithNoResult, Integer.class, (rs, idx) -> {
      return rs.getInt("id");
    });
    assertThat(i.isPresent(), is(false));
  }

  @Test
  public void queryForObject_deveRetornarUmRegistro() throws Exception {
    when(rs.next()).thenReturn(true);
    when(rs.getInt(any(String.class))).thenReturn(1);

    Optional<Integer> r = template.queryForObject(sqlWithResult, Integer.class, (rs, idx) -> {
      return rs.getInt("id");
    }, 3);
    assertTrue(r.isPresent());
    assertEquals((Integer)1, r.get());
    Mockito.verify(pstmt, times(1)).setObject(1, 3);
  }

  @Test
  public void queryForList_deveRetornar3Registros() throws Exception {
    when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
    when(rs.getInt(any(String.class))).thenReturn(1).thenReturn(2).thenReturn(3);

    List<Integer> list = template.queryForList(sqlWithResultList, Integer.class, (rs, i) -> {
      return rs.getInt("id");
    }, 4);

    assertThat(list.size(), is(3));
    assertArrayEquals(list.toArray(), new Integer[]{1,2,3});
    Mockito.verify(pstmt, times(1)).setObject(1, 4);
  }
}
