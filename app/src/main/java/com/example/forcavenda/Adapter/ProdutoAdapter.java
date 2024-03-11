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

import com.example.forcavenda.Model.Produto;
import com.example.forcavenda.R;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder> {

    private List<Produto> listaProduto;

    private OnClickListener listener;

    public interface OnClickListener  {
        void onClick(Produto produto);
    }

    public void setOnItemClickListener(ProdutoAdapter.OnClickListener listener) {
        this.listener = listener;
    }

    public ProdutoAdapter(List<Produto> listaProduto){
        this.listaProduto = listaProduto;
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto, parent, false);
        return new ProdutoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        Produto produtoLista = listaProduto.get(position);

        String codigoProd = String.valueOf(produtoLista.getCodigo());
        String descProd = produtoLista.getDescricao();
        Double vlrProd = produtoLista.getValor();

        // Converter double para Currency
        Currency currency = Currency.getInstance("BRL"); // Código ISO para Real brasileiro
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = numberFormat.format(vlrProd);

        holder.txtCodProd.setText(codigoProd);
        holder.txtDesc.setText(produtoLista.getDescricao());
        holder.txtValor.setText(valorFormatado);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(produtoLista);
                }
            }
        });
    }

    public int getItemCount(){
        return listaProduto.size();
    }

    public static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCodProd;
        public TextView txtDesc;
        public TextView txtValor;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicialize os componentes do item da peça aqui
            txtDesc = itemView.findViewById(R.id.txtDescProd);
            txtCodProd = itemView.findViewById(R.id.txtCodProd);
            txtValor = itemView.findViewById(R.id.txtValorProd);

        }

    }


}
