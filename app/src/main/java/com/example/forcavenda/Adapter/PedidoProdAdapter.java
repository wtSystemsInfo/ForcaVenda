package com.example.forcavenda.Adapter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forcavenda.Model.ItemPedido;
import com.example.forcavenda.R;

public class PedidoProdAdapter extends RecyclerView.Adapter<PedidoProdAdapter.PedidoProdViewHolder> {
    private List<ItemPedido> listaItemPed;

    private OnClickListener listener;

    public interface OnClickListener  {
        void onClick(ItemPedido itemPedido);
    }

    public void setOnItemClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public PedidoProdAdapter(List<ItemPedido> listaItemPed){
        this.listaItemPed = listaItemPed;
    }

    @NonNull
    @Override
    public PedidoProdAdapter.PedidoProdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedprod, parent, false);
        return new PedidoProdAdapter.PedidoProdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoProdAdapter.PedidoProdViewHolder holder, int position) {
        ItemPedido itemPedidoLista = listaItemPed.get(position);

        Double vlrProd = itemPedidoLista.getProdutoVlr();
        Double subProd = itemPedidoLista.getProdutoSub();

        // Converter double para Currency
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        Currency currencyVlr = Currency.getInstance("BRL"); // Código ISO para Real brasileiro
        String valorFormatadoVlr = numberFormat.format(vlrProd);

        Currency currencySub = Currency.getInstance("BRL"); // Código ISO para Real brasileiro
        String valorFormatadoSub = numberFormat.format(subProd);

        holder.txtCodPedProduto.setText(itemPedidoLista.getCodProduto().toString());
        holder.txtDescPedProduto.setText(itemPedidoLista.getProdutoDesc().toString());
        holder.txtQtdePedProduto.setText(itemPedidoLista.getProdutoQtde().toString());
        holder.txtValorPedProduto.setText(valorFormatadoVlr);
        holder.txtSubPedProduto.setText(valorFormatadoSub);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if(itemPedidoLista != null){
                        listener.onClick(itemPedidoLista);
                    }
                }
            }
        });

        //Definição das Margens

        //A partir do Codigo

        if(itemPedidoLista.getCodProduto() < 100){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.txtCodPedProduto.getLayoutParams();
            layoutParams.setMarginEnd(100); // Substitua novaMargemEmPixels pelo valor desejado em pixels
            holder.txtCodPedProduto.setLayoutParams(layoutParams);
        }

        if(itemPedidoLista.getCodProduto() >= 100){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.txtCodPedProduto.getLayoutParams();
            layoutParams.setMarginEnd(80); // Substitua novaMargemEmPixels pelo valor desejado em pixels
            holder.txtCodPedProduto.setLayoutParams(layoutParams);
        }

        //A partir da Descrição

        if(itemPedidoLista.getProdutoQtde() <= 9.99){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.txtDescPedProduto.getLayoutParams();
            layoutParams.setMarginEnd(85); // Substitua novaMargemEmPixels pelo valor desejado em pixels
            holder.txtDescPedProduto.setLayoutParams(layoutParams);
        }

        if(itemPedidoLista.getProdutoQtde() > 9.99){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.txtDescPedProduto.getLayoutParams();
            layoutParams.setMarginEnd(75); // Substitua novaMargemEmPixels pelo valor desejado em pixels
            holder.txtDescPedProduto.setLayoutParams(layoutParams);
        }

        if(itemPedidoLista.getProdutoQtde() > 99.99){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.txtDescPedProduto.getLayoutParams();
            layoutParams.setMarginEnd(50); // Substitua novaMargemEmPixels pelo valor desejado em pixels
            holder.txtDescPedProduto.setLayoutParams(layoutParams);
        }

        //A partir do Valor Unitario

        if(itemPedidoLista.getProdutoVlr() <= 9.99){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.txtValorPedProduto.getLayoutParams();
            layoutParams.setMarginEnd(100); // Substitua novaMargemEmPixels pelo valor desejado em pixels
            holder.txtValorPedProduto.setLayoutParams(layoutParams);
        }

        if(itemPedidoLista.getProdutoVlr() > 9.99){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.txtValorPedProduto.getLayoutParams();
            layoutParams.setMarginEnd(75); // Substitua novaMargemEmPixels pelo valor desejado em pixels
            holder.txtValorPedProduto.setLayoutParams(layoutParams);
        }

        if(itemPedidoLista.getProdutoVlr() > 99.99){
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.txtValorPedProduto.getLayoutParams();
            layoutParams.setMarginEnd(60); // Substitua novaMargemEmPixels pelo valor desejado em pixels
            holder.txtValorPedProduto.setLayoutParams(layoutParams);
        }



    }

    public int getItemCount(){
        return listaItemPed.size();
    }

    public static class PedidoProdViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCodPedProduto;
        public TextView txtDescPedProduto;
        public TextView txtQtdePedProduto;
        public TextView txtValorPedProduto;
        public TextView txtSubPedProduto;


        public PedidoProdViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicialize os componentes do item da peça aqui
            txtCodPedProduto = itemView.findViewById(R.id.txtCodPedProd);
            txtDescPedProduto = itemView.findViewById(R.id.txtDescPedProd);
            txtQtdePedProduto = itemView.findViewById(R.id.txtQtdePedProd);
            txtValorPedProduto = itemView.findViewById(R.id.txtValorPedProd);
            txtSubPedProduto = itemView.findViewById(R.id.txtSubPedProd);


        }

    }

}
