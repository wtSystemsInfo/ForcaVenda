package com.example.forcavenda.Model;

public class Vendedor {

    private Integer Codigo;

    private String Nome;

    public Vendedor(Integer codigo, String nome) {
        Codigo = codigo;
        Nome = nome;
    }

    public Integer getCodigo() {
        return Codigo;
    }

    public void setCodigo(Integer codigo) {
        Codigo = codigo;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }
}
