package com.example.forcavenda.ui.outro;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.forcavenda.DAO.FormaDAO;
import com.example.forcavenda.DAO.ItemPedidoDAO;
import com.example.forcavenda.DAO.PedidoDAO;
import com.example.forcavenda.DAO.VendedorDAO;
import com.example.forcavenda.Model.Forma;
import com.example.forcavenda.Model.ItemPedido;
import com.example.forcavenda.Model.Pedido;
import com.example.forcavenda.Model.Vendedor;
import com.example.forcavenda.Model.ViewModelCompart;
import com.example.forcavenda.databinding.FragmentOutroBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OutroFragment extends Fragment {


    Spinner formaSP;

    List<Forma> formas;

    TextView txtData;

    TextView txtObservacao;

    TextView txtVlrtotal;

    TextView txtCodigoPedido;

    TextView txtEnviado;

    ViewModelCompart viewModelCompart;

    ImageButton salvarBtn;

    Integer numProdPed;
    Integer codPed = 0;

    String nomeCliente;

    int codCliente;
    int codFormaPgto;

    List<ItemPedido> productList;

    private FragmentOutroBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OutroViewModel notificationsViewModel =
                new ViewModelProvider(this).get(OutroViewModel.class);

        viewModelCompart = new ViewModelProvider(requireActivity()).get(ViewModelCompart.class);

        binding = FragmentOutroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        numProdPed = 0;
        nomeCliente = "";

        formaSP = binding.formaSpinner;
        FormaDAO formaDAO = new FormaDAO(getContext());
        formas = formaDAO.obterForma();
        fillSpinnnerForma(formaSP, formas);
        salvarBtn = binding.btnSalvarOutPed;
        txtObservacao = binding.txtInpObsPed;
        txtCodigoPedido = binding.txtCodPedido;
        txtEnviado = binding.txtEnvPed;


        txtData = binding.txtDataPed;
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat simpleData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = simpleData.format(currentDate);
        txtData.setText(formattedDate);
        txtVlrtotal = binding.txtTotalPed;

        salvarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                salvarBtn.setVisibility(View.INVISIBLE);
                if(numProdPed > 0){
                    if(!nomeCliente.isEmpty()){
                        salvarPedido();
                    }else{
                        builder.setTitle("SALVAR")
                                .setMessage("SEM CLIENTE SELECIONADO! POR FAVOR ADICIONE UM CLIENTE PARA SAVAR O PEDIDO!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        salvarBtn.setVisibility(View.VISIBLE);

                                    }
                                });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }else{
                    builder.setTitle("SALVAR")
                            .setMessage("PEDIDO NÃO POSSUI PRODUTOS ADICIONADOS! POR FAVOR ADICIONE PELO MENOS UM PRODUTO PARA SAVAR O PEDIDO!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    salvarBtn.setVisibility(View.VISIBLE);

                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });


        return root;
    }

    private void fillSpinnnerForma(Spinner spinner, List<Forma> formas){
        List<String> itensForma = new ArrayList<>();

        for (Forma forma : formas) {
            String item = forma.getDescricao();
            itensForma.add(item);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, itensForma);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public String calcularSomaSubProdutosFormat(List<ItemPedido> productList) {
        double soma = 0.0;

        for (ItemPedido itemPedido : productList) {
            double subProduto = itemPedido.getProdutoSub();
            soma += subProduto;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        String somaFormatada = df.format(soma);

        return somaFormatada;
    }

    public double calcularSomaSubProdutos(List<ItemPedido> productList) {
        double soma = 0.0;

        for (ItemPedido itemPedido : productList) {
            double subProduto = itemPedido.getProdutoSub();
            soma += subProduto;
        }


        return soma;
    }

    private void salvarPedido(){

        FormaDAO formaDAO = new FormaDAO(getContext());
        codFormaPgto = formaDAO.getCodFormaByDesc(formaSP.getSelectedItem().toString());
        PedidoDAO pedidoDAO = new PedidoDAO(getContext());
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(getContext());
        if(txtCodigoPedido.getText().toString().isEmpty()){
            pedidoDAO.salvaPedido(codCliente, txtData.getText().toString(), 0, txtObservacao.getText().toString(),
                    codFormaPgto,  calcularSomaSubProdutos(productList) );
            txtCodigoPedido.setText(String.valueOf(pedidoDAO.retornaUltimoPed()));
        }else{
            pedidoDAO.updatePedido(Integer.parseInt(txtCodigoPedido.getText().toString()), codCliente, txtData.getText().toString(),
                    txtObservacao.getText().toString(),
                    codFormaPgto,  calcularSomaSubProdutos(productList));
        }


        if(!txtCodigoPedido.getText().toString().isEmpty()){
            itemPedidoDAO.salvarProdPed(productList, Integer.parseInt(txtCodigoPedido.getText().toString()));
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
            builder2.setTitle("SALVAR")
                    .setMessage("PEDIDO SALVO COM SUCESSO!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });

            AlertDialog alertDialog = builder2.create();
            alertDialog.show();
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        productList = viewModelCompart.getProductList();
        numProdPed = productList.size();
        nomeCliente = viewModelCompart.getnomeCliente();
        txtVlrtotal.setText(calcularSomaSubProdutosFormat(productList));
        codCliente = viewModelCompart.getcodigoCliente();
        codPed = viewModelCompart.getccodPedido();

        if(codPed > 0 ){
            PedidoDAO pedidoDAO = new PedidoDAO(getContext());
            Pedido pedido = pedidoDAO.carregaOutrosPedido(codPed);
            if(pedido!=null){
                txtCodigoPedido.setText(codPed.toString());
                if (pedido.getData() != null) {
                    txtData.setText(pedido.getData().toString());
                }

                if (pedido.getObs() != null) {
                    txtObservacao.setText(pedido.getObs().toString());
                }

                if (pedido.getVlrTotal() != null) {
                    txtVlrtotal.setText(pedido.getVlrTotal().toString());
                }
                int position = -1;
                for(int i = 0; i < formaSP.getCount(); i ++){
                    String opcao = formaSP.getItemAtPosition(i).toString();
                    if(opcao.equals(pedido.getForma().toString())){
                        position = i;
                        break;
                    }
                }
                if(position != -1){
                    formaSP.setSelection(position);
                }

                txtEnviado.setText(pedido.getEnviado().toString());
                salvarBtn.setVisibility(View.INVISIBLE);
            }

        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}