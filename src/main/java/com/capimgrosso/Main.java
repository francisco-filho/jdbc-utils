package com.capimgrosso;

import com.capimgrosso.database.SqlTemplate;
import com.capimgrosso.model.Dependencia;
import com.capimgrosso.model.Funcionario;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {
  public static final String uri = "jdbc:postgresql://localhost:5432/portal";
  public static final String sql = "select * from dependencia";
  public static final String sqlFunci = "select * from funcionario";
  public static final String sqlWhere = "SELECT * FROM funcionario WHERE chave='F3445038'";
  public static final String user = "postgres";
  public static final String pass = "12345678";

  public static void main(String[] args) {
    SqlTemplate template = new SqlTemplate(getDataSource());

    List<Dependencia> ints = template.queryForList(sql, Dependencia.class, (rs, i) -> {
      return new Dependencia(rs.getInt("prefixo"), rs.getString("nome"));
    });

    List<Funcionario> funcis = template.queryForList(sqlFunci, Funcionario.class, (rs, i) -> {
      return new Funcionario(text(rs, "chave"), text(rs, "nome"), localDate(rs, "data_de_nascimento"));
    });

    Optional<Funcionario> funci = template.queryForObject(sqlWhere, Funcionario.class, (rs, i) -> {
        return new Funcionario(
          text(rs, "chave"),
          text(rs, "nome"),
          localDate(rs, "data_de_nascimento"));
    });

    funci.ifPresent(System.out::println);
  }

  private static String text(ResultSet rs, String key) throws SQLException {
    return rs.getString(key);
  }

  private static LocalDate localDate(ResultSet rs, String key) throws SQLException {
    return rs.getDate(key) != null ? rs.getDate(key).toLocalDate() : null;
  }


  public static DataSource getDataSource() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(uri);
    config.setUsername(user);
    config.setPassword(pass);
    return new HikariDataSource(config);
  }
}
