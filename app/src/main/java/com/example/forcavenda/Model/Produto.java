package com.example.forcavenda.Model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Produto {

    private Integer Codigo;

    private String Descricao;

    private Double Valor;

    /*public Produto(Integer codigo, String descricao, Double valor) {
        Codigo = codigo;
        Descricao = descricao;
        Valor = valor;
    }*/

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

    public Double getValor() {
        return Valor;
    }

    public void setValor(Double valor) {
        Valor = valor;
    }


    public String getValorFormatado() {
        NumberFormat numberFormat = new DecimalFormat("#,##0.00");
        return numberFormat.format(Valor);
    }

}
