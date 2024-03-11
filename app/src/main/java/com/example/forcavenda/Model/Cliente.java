package com.example.forcavenda.Model;

public class Cliente {
    private Integer CodigoSistema;

    private Integer CodigoApp;

    private String Razao;

    private String Ddd;

    private String Telefone;

    private String TipoCli;

    private  String DocCli;

    private String Cidade;

    private String Uf;

    private String Email;

    /*public Cliente(Integer codigoSistema, Integer codigoApp, String razao, String ddd, String telefone, String tipoCli, String docCli, String cidade, String uf, String email) {
        CodigoSistema = codigoSistema;
        CodigoApp = codigoApp;
        Razao = razao;
        Ddd = ddd;
        Telefone = telefone;
        TipoCli = tipoCli;
        DocCli = docCli;
        Cidade = cidade;
        Uf = uf;
        Email = email;
    }*/

    public Integer getCodigoSistema() {
        return CodigoSistema;
    }

    public void setCodigoSistema(Integer codigoSistema) {
        CodigoSistema = codigoSistema;
    }

    public Integer getCodigoApp() {
        return CodigoApp;
    }

    public void setCodigoApp(Integer codigoApp) {
        CodigoApp = codigoApp;
    }

    public String getRazao() {
        return Razao;
    }

    public void setRazao(String razao) {
        Razao = razao;
    }

    public String getDdd() {
        return Ddd;
    }

    public void setDdd(String ddd) {
        Ddd = ddd;
    }

    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String telefone) {
        Telefone = telefone;
    }

    public String getTipoCli() {
        return TipoCli;
    }

    public void setTipoCli(String tipoCli) {
        TipoCli = tipoCli;
    }

    public String getDocCli() {
        return DocCli;
    }

    public void setDocCli(String docCli) {
        DocCli = docCli;
    }

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }

    public String getUf() {
        return Uf;
    }

    public void setUf(String uf) {
        Uf = uf;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}

