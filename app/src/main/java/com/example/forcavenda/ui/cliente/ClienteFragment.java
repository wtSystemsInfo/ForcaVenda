package com.example.forcavenda.ui.cliente;

import static android.content.Intent.getIntent;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.forcavenda.ClienteActivity;
import com.example.forcavenda.DAO.ClienteDAO;
import com.example.forcavenda.Model.Cliente;
import com.example.forcavenda.Model.ViewModelCompart;
import com.example.forcavenda.PesquisaPedActivity;
import com.example.forcavenda.ProdutoActivity;
import com.example.forcavenda.R;
import com.example.forcavenda.databinding.FragmentClienteBinding;

public class ClienteFragment extends Fragment {

    ImageButton imgBtnAddCli;

    TextView txtCodCli;
    TextView txtRazCli;
    TextView txtTelCli;
    TextView txtTipoCli;
    TextView txtDocCli;
    TextView txtCidCli;
    TextView txtUfCli;

    private String nomeCli;

    private Integer numPedido = 0;

    private ViewModelCompart viewModelCompart;

    private ImageButton imgBtnBuscaPed;


    private FragmentClienteBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ClienteViewModel homeViewModel =
                new ViewModelProvider(this).get(ClienteViewModel.class);

        binding = FragmentClienteBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        imgBtnAddCli = binding.btnAddCliPed;
        imgBtnBuscaPed = binding.btnLocPed;
        txtCodCli = binding.txtCodCliPed;
        txtRazCli = binding.txtRazCliPed;
        txtTelCli = binding.txtTelCliPed;
        txtTipoCli = binding.txtTipoCliPed;
        txtDocCli = binding.txtDocCliPed;
        txtCidCli = binding.txtCidCliPed;
        txtUfCli = binding.txtUfCliPed;

        nomeCli = "";

        viewModelCompart = new ViewModelProvider(requireActivity()).get(ViewModelCompart.class);


        imgBtnAddCli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ClienteActivity.class);
                intent.putExtra("flagPedido", "pedidocliente");
                startActivity(intent);
            }
        });


        imgBtnBuscaPed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PesquisaPedActivity.class);
                startActivity(intent);
            }
        });



        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null) {
            if(arguments.containsKey("CODIGO_CLIENTE")){
                txtCodCli.setText(arguments.getString("CODIGO_CLIENTE"));
                if(Integer.valueOf(txtCodCli.getText().toString()) > 0){
                    ClienteDAO clienteDAO = new ClienteDAO(getContext());
                    Cliente novoCliente = clienteDAO.carregaCliPed(txtCodCli.getText().toString());
                    txtRazCli.setText(novoCliente.getRazao());
                    txtTelCli.setText(novoCliente.getTelefone());
                    txtTipoCli.setText(novoCliente.getTipoCli());
                    txtDocCli.setText(novoCliente.getDocCli());
                    txtCidCli.setText(novoCliente.getCidade());
                    txtUfCli.setText(novoCliente.getUf());
                    nomeCli = txtRazCli.getText().toString();
                    viewModelCompart.setnomeCliente(nomeCli);
                    viewModelCompart.setcodigoCliente(Integer.valueOf(txtCodCli.getText().toString()));
                }
            }
            if(arguments.containsKey("CODIGO_PEDIDO")){
                numPedido = Integer.valueOf(arguments.getString("CODIGO_PEDIDO"));
                if(numPedido > 0 ){
                    ClienteDAO clienteDAO = new ClienteDAO(getContext());
                    Cliente novoCliente = clienteDAO.carregaCliPedByPed(numPedido);
                    if(novoCliente != null){
                        imgBtnAddCli.setVisibility(View.INVISIBLE);
                        txtRazCli.setText(novoCliente.getRazao());
                        txtTelCli.setText(novoCliente.getTelefone());
                        txtTipoCli.setText(novoCliente.getTipoCli());
                        txtDocCli.setText(novoCliente.getDocCli());
                        txtCidCli.setText(novoCliente.getCidade());
                        txtUfCli.setText(novoCliente.getUf());
                        nomeCli = txtRazCli.getText().toString();
                        txtCodCli.setText(novoCliente.getCodigoApp().toString());
                        viewModelCompart.setnomeCliente(nomeCli);
                        viewModelCompart.setcodigoCliente(Integer.valueOf(txtCodCli.getText().toString()));
                        viewModelCompart.setcodPedido(numPedido);
                    }

                }
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}