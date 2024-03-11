package com.example.forcavenda.Model;

public class Pedido {

    private Integer CodCli;

    private Integer Codigo;

    private String Data;

    private String Enviado;

    private Double VlrTotal;

    private String Forma;

    private String Obs;


    /*public Pedido(Integer codCli, Integer codigo, String data, String enviado, Double vlrTotal, String forma, String obs) {
        CodCli = codCli;
        Codigo = codigo;
        Data = data;
        Enviado = enviado;
        VlrTotal = vlrTotal;
        Forma = forma;
        Obs = obs;
    }*/

    public Integer getCodCli() {
        return CodCli;
    }

    public void setCodCli(Integer codCli) {
        CodCli = codCli;
    }

    public Integer getCodigo() {
        return Codigo;
    }

    public void setCodigo(Integer codigo) {
        Codigo = codigo;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getEnviado() {
        return Enviado;
    }

    public void setEnviado(String enviado) {
        Enviado = enviado;
    }

    public Double getVlrTotal() {
        return VlrTotal;
    }

    public void setVlrTotal(Double vlrTotal) {
        VlrTotal = vlrTotal;
    }

    public String getForma() {
        return Forma;
    }

    public void setForma(String forma) {
        Forma = forma;
    }

    public String getObs() {
        return Obs;
    }

    public void setObs(String obs) {
        Obs = obs;
    }
}
