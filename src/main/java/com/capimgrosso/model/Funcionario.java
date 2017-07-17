package com.capimgrosso.model;

import java.sql.Date;
import java.time.LocalDate;

public class Funcionario {
  private final String chave;
  private final String nome;
  private final LocalDate dataDeNascimento;

  public Funcionario(String chave, String nome, LocalDate dataDeNascimento) {
    this.chave = chave;
    this.nome = nome;
    this.dataDeNascimento = dataDeNascimento;
  }

  @Override
  public String toString() {
    return "Funcionario{" +
        "chave='" + chave + '\'' +
        ", nome='" + nome + '\'' +
        ", dataDeNascimento=" + dataDeNascimento +
        '}';
  }
}
