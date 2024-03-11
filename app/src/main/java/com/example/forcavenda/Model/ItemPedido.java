package com.example.forcavenda.Model;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ItemPedido {
    private Integer CodPedido;
    private Integer CodProduto;
    private String ProdutoDesc;
    private Double ProdutoQtde;
    private Double ProdutoVlr;

    private Double ProdutoSub;

    public ItemPedido(/*Integer codPedido, Integer codProduto, Integer produtoQtde, Integer produtoVlr, Integer produtoSub, String produtoDesc*/) {
        /*CodPedido = codPedido;
        CodProduto = codProduto;
        ProdutoQtde = produtoQtde;
        ProdutoVlr = produtoVlr;
        ProdutoSub = produtoSub;
        ProdutoDesc = produtoDesc;*/
    }

    public Integer getCodPedido() {
        return CodPedido;
    }

    public void setCodPedido(Integer codPedido) {
        CodPedido = codPedido;
    }

    public String getProdutoDesc() {
        return ProdutoDesc;
    }

    public void setProdutoDesc(String produtoDesc) {
        ProdutoDesc = produtoDesc;
    }

    public Integer getCodProduto() {
        return CodProduto;
    }

    public void setCodProduto(Integer codProduto) {
        CodProduto = codProduto;
    }

    public Double getProdutoQtde() {
        return ProdutoQtde;
    }

    public void setProdutoQtde(Double produtoQtde) {
        ProdutoQtde = produtoQtde;
    }

    public Double getProdutoVlr() {
        return ProdutoVlr;
    }

    public void setProdutoVlr(Double produtoVlr) {
        ProdutoVlr = produtoVlr;
    }

    public String getProdutoVlrFormatado(Double produtoVlr) {
        NumberFormat numberFormat = new DecimalFormat("#,##0.00");
        return numberFormat.format(produtoVlr);
    }

    public Double getProdutoSub() {
        return ProdutoSub;
    }

    public void setProdutoSub(Double produtoSub) {
        ProdutoSub = produtoSub;
    }

    public String getProdutoSubFormatado(Double produtoSub) {
        NumberFormat numberFormat = new DecimalFormat("#,##0.00");
        return numberFormat.format(produtoSub);
    }
}
