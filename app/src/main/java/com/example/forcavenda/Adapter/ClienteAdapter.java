package com.example.forcavenda.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forcavenda.Model.Cliente;
import com.example.forcavenda.R;


public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder>{

    private List<Cliente> listaCliente;


    private OnClickListener  listener;


    public interface OnClickListener  {
        void onClick(Cliente cliente);
    }

    public void setOnItemClickListener(OnClickListener  listener) {
        this.listener = listener;
    }

    public ClienteAdapter(List<Cliente> listaCliente){
        this.listaCliente = listaCliente;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteAdapter.ClienteViewHolder holder, int position) {

        //Responsável pela criação de cada item recuperado da lista

        Cliente clienteLista = listaCliente.get(position);

        if(clienteLista != null){
            String codigoApp = String.valueOf(clienteLista.getCodigoApp()); // Converta para string
            String razao = clienteLista.getRazao(); // Certifique-se de que esse método retorne uma string

            holder.txtCodCli.setText(codigoApp);
            holder.txtNomeCli.setText(razao);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if(clienteLista != null){
                            listener.onClick(clienteLista);
                        }
                    }
                }
            });
        }
    }

    public int getItemCount(){
        return listaCliente.size();
    }



    public static class ClienteViewHolder extends RecyclerView.ViewHolder {

        //Responsável pela configuração de cada item individualmente da lista.
        public TextView txtCodCli;
        public TextView txtNomeCli;


        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicialize os componentes do item da peça aqui
            txtCodCli = itemView.findViewById(R.id.txtCodCli);
            txtNomeCli = itemView.findViewById(R.id.txtNomeCli);


        }

    }
}



