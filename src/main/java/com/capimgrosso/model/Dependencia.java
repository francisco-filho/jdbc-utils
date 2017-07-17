package com.capimgrosso.model;

public class Dependencia {
  private final Integer prefixo;
  private final String nome;

  public Dependencia(Integer prefixo, String nome) {
    this.prefixo = prefixo;
    this.nome = nome;
  }

  @Override
  public String toString() {
    return "Dependencia{" +
        "prefixo=" + prefixo +
        ", nome='" + nome + '\'' +
        '}';
  }
}
