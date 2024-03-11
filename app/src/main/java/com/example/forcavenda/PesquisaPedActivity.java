package com.example.forcavenda;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.net.ParseException;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.forcavenda.Adapter.PedidoAdapter;
import com.example.forcavenda.DAO.PedidoDAO;
import com.example.forcavenda.DAO.ProdutoDAO;
import com.example.forcavenda.Model.PedidoPesquisa;
import com.example.forcavenda.Model.Produto;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PesquisaPedActivity extends AppCompatActivity {

    private Spinner spinnerTipo;

    private ImageButton btnVoltar, btnFiltrarPed;

    private TextInputEditText txtPesquisa;

    private RecyclerView recyclerView;

    private PedidoAdapter pedidoAdapter = null;

    private String pedSel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_ped);

        btnVoltar = findViewById(R.id.btnVoltarPesq);
        btnFiltrarPed = findViewById(R.id.btnFiltrarPed);
        spinnerTipo = findViewById(R.id.spinnerFiltroPed);
        txtPesquisa = findViewById(R.id.txtPedPesqCod);
        recyclerView = findViewById(R.id.recyclerViewPed);
        fillSpinnnerPed(spinnerTipo);


        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                // Verifica cada caractere sendo inserido
                for (int i = start; i < end; i++) {
                    // Permite apenas dígitos de 0 a 9 e a barra "/"
                    if (!Character.isDigit(source.charAt(i)) && source.charAt(i) != '/') {
                        return "";
                    }
                }
                return null; // Aceita o texto inserido
            }
        };



        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        Toolbar prodToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(prodToolbar);
        getSupportActionBar().setTitle("Pesquisa Pedidos");
        prodToolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PesquisaPedActivity.this, MenuPrincipal.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        btnFiltrarPed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPesquisa.getText().toString().isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PesquisaPedActivity.this);
                    builder.setTitle("Pesquisa Pedido")
                            .setMessage("Campo de pesquisa está em branco!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{

                    //Validação dos dados do campo
                    if(spinnerTipo.getSelectedItem().toString().equals("Codigo")){
                        if(!isNumeric(txtPesquisa.getText().toString())){
                            AlertDialog.Builder builder = new AlertDialog.Builder(PesquisaPedActivity.this);
                            builder.setTitle("Pesquisa Pedido")
                                    .setMessage("Digite um número para procurar por Código!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }else {
                            List<PedidoPesquisa> listaPedido = criarListaPedido();
                            if (isKeyboardActive()) {
                                // Teclado está ativo, então ocultá-lo
                                hideKeyboard();
                            }
                        }
                    } else if (spinnerTipo.getSelectedItem().toString().equals("Data")) {
                        if(!isData(txtPesquisa.getText().toString())){
                            AlertDialog.Builder builder = new AlertDialog.Builder(PesquisaPedActivity.this);
                            builder.setTitle("Pesquisa Pedido")
                                    .setMessage("Digite uma data válida!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }else {
                            List<PedidoPesquisa> listaPedido = criarListaPedido();
                            if (isKeyboardActive()) {
                                // Teclado está ativo, então ocultá-lo
                                hideKeyboard();
                            }
                        }

                    } else{
                        List<PedidoPesquisa> listaPedido = criarListaPedido();
                        if (isKeyboardActive()) {
                            // Teclado está ativo, então ocultá-lo
                            hideKeyboard();
                        }

                    } 

                }
            }

        });

    }

    private void fillSpinnnerPed(Spinner spinner) {
        List<String> itensConn = new ArrayList<>();
        itensConn.add("Codigo");
        itensConn.add("Data");
        itensConn.add("Cliente");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itensConn);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public boolean isDate(String input, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(input);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isNumeric(String str) {
        // Usa uma expressão regular para verificar se a string contém apenas números
        return str.matches("\\d+");
    }

    private List<PedidoPesquisa> criarListaPedido() {
        List<PedidoPesquisa> listaPedidos = new ArrayList<>();
        String pedido = txtPesquisa.getText().toString();

        listaPedidos.clear();

        // Crie uma instância do PecaDAO
        PedidoDAO pedidoDAO = new PedidoDAO(this);

        // Chame o método selectPecaCod para obter as peças do banco de dados
        switch (spinnerTipo.getSelectedItem().toString()) {
            case "Codigo":
                if(isNumeric(pedido)){
                    listaPedidos = pedidoDAO.pesquisaPedByCod(Integer.parseInt(pedido));
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(PesquisaPedActivity.this);
                    builder.setTitle("Pesquisa Pedido")
                            .setMessage("Código inválido!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                break;
            case "Cliente":
                listaPedidos = pedidoDAO.pesquisaPedByCli(pedido);
                break;
            case "Data":
                if(isDate(pedido, "dd/MM/yyyy")) {
                    listaPedidos = pedidoDAO.pesquisaPedByData(pedido);
                    break;
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(PesquisaPedActivity.this);
                    builder.setTitle("Pesquisa Pedido")
                            .setMessage("Data inválida!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
        }

        if (!listaPedidos.isEmpty()) {
            for (PedidoPesquisa pesquisaPed : listaPedidos) {
                // Faça o que for necessário com cada PecaOS
                // Exemplo: exibir informações em um log
                //Log.d("PecaOS", "Código: " + pecaOS.getCodpeca() + ", Quantidade: " + pecaOS.getPecaqtde());
            }
            pedidoAdapter = new PedidoAdapter(listaPedidos); // Inicialize o clienteAdapter aqui
            recyclerView.setAdapter(pedidoAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PesquisaPedActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);

            //Método do clique no ADAPTER
            pedidoAdapter.setOnItemClickListener(new PedidoAdapter.OnClickListener() {
                @Override
                public void onClick(PedidoPesquisa pedPesq) {
                    pedSel = pedPesq.getCodigo().toString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(PesquisaPedActivity.this);
                    builder.setTitle("Pesquisa Pedido")
                            .setMessage("O pedido selecionado foi o pedido : " + pedSel + " ?")
                            .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent broadcastIntent = new Intent("PESQUISA_PEDIDO");
                                    broadcastIntent.putExtra("CODIGO_PEDIDO", pedSel);
                                    sendBroadcast(broadcastIntent);
                                    finish();

                                }
                            })
                            .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Lógica para lidar com o clique em "NÃO"

                                }
                            });


                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });




        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(PesquisaPedActivity.this);
            builder.setTitle("Pesquisa Pedido")
                    .setMessage("Pedido não foi localizado!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return listaPedidos;
    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtPesquisa.getWindowToken(), 0);
    }

    private boolean isKeyboardActive() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive(); // Retorna true se o teclado estiver ativo (visível)
    }

    private boolean isData(String data) {
        String conteudoEditText = data;
        String formatoEsperado = "\\d+/\\d+/\\d+";

        Pattern pattern = Pattern.compile(formatoEsperado);
        Matcher matcher = pattern.matcher(conteudoEditText);

        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

}