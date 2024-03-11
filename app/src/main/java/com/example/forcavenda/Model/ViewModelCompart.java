package com.example.forcavenda.Model;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ViewModelCompart extends ViewModel {
    private List<ItemPedido> productList = new ArrayList<>();
    private String nomeCliente = "";

    private int codigoCliente = 0;

    private int codPedido = 0;


    // Métodos para acessar e atualizar os dados
    public List<ItemPedido> getProductList() {
        return productList;
    }

    public void setProductList(List<ItemPedido> productList) {
        this.productList = productList;
    }


    public String getnomeCliente() {
        return nomeCliente;
    }

    public void setnomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public int getcodigoCliente() {
        return codigoCliente;
    }

    public void setcodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public int getccodPedido() {
        return codPedido;
    }

    public void setcodPedido(int codPedido) {
        this.codPedido = codPedido;
    }
}
