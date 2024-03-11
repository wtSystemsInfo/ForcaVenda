package com.example.forcavenda;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forcavenda.Adapter.ProdutoAdapter;
import com.example.forcavenda.DAO.ProdutoDAO;
import com.example.forcavenda.Model.Produto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ProdutoActivity extends AppCompatActivity {

    ImageButton imgBtnVoltar;

    ImageButton imgBtnAddProd;

    ImageButton imgBtnFiltrar;

    String flag;

    TextInputEditText inpProduto;
    private RecyclerView recyclerView;
    private ProdutoAdapter produtoAdapter;

    private Double qtdeProd;

    private int codProd = 0;
    private Double descProd;

    private Double vlrProd;

    private Double subProd;

    private String descricaoProd;

    private AlertDialog alertQtde;

    private AlertDialog alertDesc;

    String pesquisa = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);
        getWindow().setStatusBarColor(Color.parseColor("#164EBC"));

        inpProduto = findViewById(R.id.txtInpProd);
        inpProduto.requestFocus();

        inpProduto.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);


        recyclerView = findViewById(R.id.recyclerView);

        Toolbar prodToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(prodToolbar);
        getSupportActionBar().setTitle("Pesquisa Produtos");
        prodToolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("flagPedido")) {
            flag = intent.getStringExtra("flagPedido");
            if (flag.equals("PesquisaPedidoProd")) {
                imgBtnAddProd = findViewById(R.id.btnAddProd);
                imgBtnAddProd.setVisibility(View.VISIBLE);
            }
        }
        if(intent != null && intent.hasExtra("pesquisa")){
            inpProduto.setText(intent.getStringExtra("pesquisa"));
        }

        imgBtnVoltar = findViewById(R.id.btnVoltarProd);
        imgBtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent != null && intent.hasExtra("flagPedido")) {
                    if (flag.equals("PesquisaPedidoProd")) {
                        onBackPressed();
                    }
                }else {
                    Intent intent = new Intent(ProdutoActivity.this, MenuPrincipal.class);
                    startActivity(intent);
                    boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                    finishAffinity();
                }
            }
        });

        imgBtnFiltrar = findViewById(R.id.btnFiltrarProd);
        imgBtnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String produtoDesc = inpProduto.getText().toString();
                if(TextUtils.isEmpty(produtoDesc)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProdutoActivity.this);
                    builder.setTitle("Pesquisa Produto")
                            .setMessage("Campo Produto está em branco!Preencha corretamente!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{

                    List<Produto> listProd = criarListaProduto();
                    if (isKeyboardActive()) {
                        // Teclado está ativo, então ocultá-lo
                        hideKeyboard();
                    }

                    produtoAdapter = new ProdutoAdapter(listProd);
                    recyclerView.setAdapter(produtoAdapter);

                    produtoAdapter.setOnItemClickListener(new ProdutoAdapter.OnClickListener() {
                        @Override
                        public void onClick(Produto produto) {
                            if(isKeyboardActive()){
                                hideKeyboard();
                            }
                            if(flag == null){
                                Toast.makeText(ProdutoActivity.this, produto.getCodigo().toString() + " - "  + produto.getDescricao().toString(), Toast.LENGTH_LONG).show();
                            }else{
                                if(flag.equals("PesquisaPedidoProd")){
                                    if (produto != null && produto.getCodigo() != null){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ProdutoActivity.this);
                                        builder.setTitle("Pesquisa Cliente")
                                                .setMessage("Selecionar produto " + produto.getDescricao() + " para o pedido ? ")
                                                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        codProd = produto.getCodigo();
                                                        descricaoProd = produto.getDescricao().toString();
                                                        vlrProd = produto.getValor();
                                                        carregaItem(produto.getCodigo().toString());


                                                    }
                                                })
                                                .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Não acontece nada!

                                                    }
                                                });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        inpProduto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER ||  actionId == EditorInfo.IME_ACTION_NEXT)) {
                    // Chame o comando ou ação que você deseja executar aqui
                    imgBtnFiltrar.callOnClick();
                    return true; // Indica que o evento foi tratado
                }
                return false; // Evento não tratado
            }
        });

        if(imgBtnAddProd != null) {
            imgBtnAddProd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag.equals("PesquisaPedidoProd")) {
                        if (codProd > 0) {
                            Intent broadcastIntent = new Intent("ENVIAR_PRODUTO");
                            broadcastIntent.putExtra("CODIGO_PRODUTO", codProd);
                            broadcastIntent.putExtra("DESC_PRODUTO", descricaoProd);
                            broadcastIntent.putExtra("QTDE_PRODUTO", qtdeProd);
                            vlrProd = vlrProd - descProd;
                            broadcastIntent.putExtra("VLR_PRODUTO", vlrProd);
                            subProd = vlrProd * qtdeProd;
                            broadcastIntent.putExtra("SUB_PRODUTO", subProd);
                            pesquisa =  inpProduto.getText().toString();
                            broadcastIntent.putExtra("PESQUISA",pesquisa);
                            sendBroadcast(broadcastIntent);
                            finish();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProdutoActivity.this);
                            builder.setTitle("Pesquisa Produto")
                                    .setMessage("Escolha um produto para ser adicionado ao pedido!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                }
            });
        }

        if(!inpProduto.getText().toString().isEmpty()){
            imgBtnFiltrar.callOnClick();
        }

    }

    private List<Produto> criarListaProduto() {
        List<Produto> listaProdutos = new ArrayList<>();
        String produto = inpProduto.getText().toString();

        listaProdutos.clear();

        // Crie uma instância do PecaDAO
        ProdutoDAO produtoDAO = new ProdutoDAO(this);

        // Chame o método selectPecaCod para obter as peças do banco de dados
        listaProdutos = produtoDAO.pesquisaProd(produto);

        if (!listaProdutos.isEmpty()) {
            for (Produto pesquisaProd : listaProdutos) {
                // Faça o que for necessário com cada PecaOS
                // Exemplo: exibir informações em um log
                //Log.d("PecaOS", "Código: " + pecaOS.getCodpeca() + ", Quantidade: " + pecaOS.getPecaqtde());
            }
            produtoAdapter = new ProdutoAdapter(listaProdutos); // Inicialize o clienteAdapter aqui
            recyclerView.setAdapter(produtoAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProdutoActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProdutoActivity.this);
            builder.setTitle("Pesquisa Produto")
                    .setMessage("Produto não foi localizado!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return listaProdutos;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inpProduto.getWindowToken(), 0);
    }

    private boolean isKeyboardActive() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive(); // Retorna true se o teclado estiver ativo (visível)
    }

    private void carregaItem(String codProd) {

        descProd = 0.00;
        View viewAlertQtde = LayoutInflater.from(ProdutoActivity.this).inflate(R.layout.dialog_qtde_layout, null);
        TextInputEditText editTextQtde = viewAlertQtde.findViewById(R.id.qtdeAlertDialog);
        //editTextQtde.setText("1.00");
        alertQtde = new MaterialAlertDialogBuilder(ProdutoActivity.this)
                .setTitle("Digite a Quantidade")
                .setView(viewAlertQtde)
                .setPositiveButton("ADICIONAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(editTextQtde.getText().toString().isEmpty()){
                            editTextQtde.setText("1.00");
                        }else {
                            double qtdeDigitada = Double.parseDouble(editTextQtde.getText().toString());
                            if(qtdeDigitada == 0){
                                editTextQtde.setText("1.00");
                            }
                        }
                        qtdeProd = Double.valueOf(editTextQtde.getText().toString());
                        imgBtnAddProd.callOnClick();
                        dialog.dismiss();



                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).create();

        alertQtde.show();
    }

}



