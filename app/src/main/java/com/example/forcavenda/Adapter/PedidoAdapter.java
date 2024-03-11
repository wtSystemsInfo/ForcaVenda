package com.example.forcavenda.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forcavenda.Model.Cliente;
import com.example.forcavenda.Model.PedidoPesquisa;
import com.example.forcavenda.R;

import org.w3c.dom.Text;


public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>{

    private List<PedidoPesquisa> listaPedido;


    private OnClickListener  listener;


    public interface OnClickListener  {
        void onClick(PedidoPesquisa pedPesq);
    }

    public void setOnItemClickListener(OnClickListener  listener) {
        this.listener = listener;
    }

    public PedidoAdapter(List<PedidoPesquisa> listaPedido){
        this.listaPedido = listaPedido;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedpesq, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoAdapter.PedidoViewHolder holder, int position) {
        PedidoPesquisa pedidoLista = listaPedido.get(position);

        if(pedidoLista != null){
            String codPed = String.valueOf(pedidoLista.getCodigo()); // Converta para string
            String razao = pedidoLista.getRazaoCli(); // Certifique-se de que esse método retorne uma string
            String data = pedidoLista.getData();

            holder.txtCodped.setText(codPed);
            holder.txtNomeCli.setText(razao);
            holder.txtDataPed.setText(data);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if(pedidoLista != null){
                            listener.onClick(pedidoLista);
                        }
                    }
                }
            });
        }
    }

    public int getItemCount(){
        return listaPedido.size();
    }



    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCodped;
        public TextView txtNomeCli;

        public TextView txtDataPed;


        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicialize os componentes do item da peça aqui
            txtCodped = itemView.findViewById(R.id.txtCodPedPesq);
            txtNomeCli = itemView.findViewById(R.id.txtNomeCliPesq);
            txtDataPed = itemView.findViewById(R.id.txtDataPesq);

        }

    }
}



