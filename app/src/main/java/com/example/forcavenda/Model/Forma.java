package com.example.forcavenda.Model;

public class Forma {

    private Integer Codigo;

    private String Descricao;


    public Forma(Integer codigo, String descricao) {
        Codigo = codigo;
        Descricao = descricao;
    }

    public Integer getCodigo() {
        return Codigo;
    }

    public void setCodigo(Integer codigo) {
        Codigo = codigo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
}
