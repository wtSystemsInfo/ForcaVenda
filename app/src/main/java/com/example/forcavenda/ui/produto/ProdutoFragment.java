package com.example.forcavenda.ui.produto;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forcavenda.DAO.ItemPedidoDAO;
import com.example.forcavenda.Model.ItemPedido;
import com.example.forcavenda.Model.ViewModelCompart;
import com.example.forcavenda.Adapter.PedidoProdAdapter;
import com.example.forcavenda.ProdutoActivity;
import com.example.forcavenda.databinding.FragmentProdutoBinding;

import java.util.ArrayList;
import java.util.List;

public class ProdutoFragment extends Fragment {

    ImageButton btnAddProdPed;

    //Controlador para a pesquisa de produto
    private boolean pesquisaProd;

    private int codProduto;

    private int codPedido;

    private String descProduto;

    private Double vlrProduto;

    private Double qtdeProduto;

    private Double subProduto;

    private String pesquisa = null;

    Bundle arguments;

    List<ItemPedido> listaProduto;

    PedidoProdAdapter pedidoProdAdapter;
    RecyclerView recyclerView;

    private ViewModelCompart viewModelCompart;

    private FragmentProdutoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProdutoViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ProdutoViewModel.class);

        binding = FragmentProdutoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnAddProdPed = binding.btnAddProdPed;

        listaProduto = new ArrayList<>();

        recyclerView = binding.recyclerView;

        viewModelCompart = new ViewModelProvider(requireActivity()).get(ViewModelCompart.class);

        pesquisaProd = false;



        btnAddProdPed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pesquisaProd = true;
                Activity activity = getActivity();
                Intent intent = new Intent(getActivity(), ProdutoActivity.class);
                intent.putExtra("flagPedido", "PesquisaPedidoProd");
                if (arguments != null) {
                    if(arguments.containsKey("CODIGO_PRODUTO")){
                        intent.putExtra("pesquisa", pesquisa);
                    }
                }
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        arguments = getArguments();
        codPedido = viewModelCompart.getccodPedido();
        if(codPedido>0){
            ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(getActivity());
            List<ItemPedido> productList = itemPedidoDAO.carregarListaProd(codPedido);
            // Use a lista atualizada do ViewModelCompart para inicializar o adaptador
            pedidoProdAdapter = new PedidoProdAdapter(productList);
            recyclerView.setAdapter(pedidoProdAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            btnAddProdPed.setVisibility(View.INVISIBLE);
        }else if (arguments != null) {
            if(arguments.containsKey("CODIGO_PRODUTO")){
                codProduto = arguments.getInt("CODIGO_PRODUTO");
                descProduto = arguments.getString("DESC_PRODUTO");
                vlrProduto = arguments.getDouble("VLR_PRODUTO");
                qtdeProduto = arguments.getDouble("QTDE_PRODUTO");
                subProduto = arguments.getDouble("SUB_PRODUTO");
                pesquisa = arguments.getString("PESQUISA");

                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setCodProduto(codProduto);
                itemPedido.setProdutoDesc(descProduto);
                itemPedido.setProdutoVlr(vlrProduto);
                itemPedido.setProdutoQtde(qtdeProduto);
                itemPedido.setProdutoSub(subProduto);
                List<ItemPedido> productList = viewModelCompart.getProductList();

                if( pesquisaProd == true) {
                    // Acesse o ViewModelCompart e atualize a lista de produtos
                    productList.add(itemPedido);
                    viewModelCompart.setProductList(productList);
                    pesquisaProd = false;
                }

                // Use a lista atualizada do ViewModelCompart para inicializar o adaptador
                pedidoProdAdapter = new PedidoProdAdapter(productList);
                recyclerView.setAdapter(pedidoProdAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                pedidoProdAdapter.setOnItemClickListener(new PedidoProdAdapter.OnClickListener() {
                    @Override
                    public void onClick(ItemPedido itemPedido) {
                        if (itemPedido != null && itemPedido.getCodProduto() != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Pedido - Produto")
                                    .setMessage("O produto "+ itemPedido.getCodProduto() + " - " + itemPedido.getProdutoDesc() +
                                            " foi selecionado, deseja excluir o produto ?")
                                    .setPositiveButton("EXCUIR", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            productList.remove(itemPedido);
                                            recyclerView.setAdapter(pedidoProdAdapter);


                                        }
                                    })
                                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //Não acontece nada!

                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }
                    }
                });

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}