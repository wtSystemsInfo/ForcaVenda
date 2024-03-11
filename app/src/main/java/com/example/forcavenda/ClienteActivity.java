package com.example.forcavenda;

import static android.app.PendingIntent.getActivity;

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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forcavenda.Adapter.ClienteAdapter;
import com.example.forcavenda.DAO.ClienteDAO;
import com.example.forcavenda.Model.Cliente;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClienteActivity extends AppCompatActivity {
    Toolbar toolbar;

    ImageButton imgbtnVoltar;

    ImageButton imgbtnNovo;

    String flag;

    ImageButton imgBtnAddCli;

    TextInputEditText inputCliente;

    ImageButton imgBtnFiltrar;

    private RecyclerView recyclerView;
    private ClienteAdapter clienteAdapter;

    String clienteSel;

    ImageButton imgBtnSelCli;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        getWindow().setStatusBarColor(Color.parseColor("#164EBC"));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pesquisa Clientes");
        toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        inputCliente = findViewById(R.id.txtInpCli);
        inputCliente.requestFocus();

        recyclerView = findViewById(R.id.recyclerView);


        inputCliente.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        flag = null;

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("flagPedido")) {
            flag = intent.getStringExtra("flagPedido");
            if (flag.equals("pedidocliente")) {
                imgBtnAddCli = findViewById(R.id.btnSelCli);
                imgBtnAddCli.setVisibility(View.VISIBLE);
                imgbtnNovo = findViewById(R.id.btnAddCli);
                imgbtnNovo.setVisibility(View.INVISIBLE);

            }
        }


        imgbtnVoltar = findViewById(R.id.btnVoltarCli);
        imgbtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent != null && intent.hasExtra("flagPedido")) {
                    if (flag.equals("pedidocliente")) {
                        onBackPressed();
                    }
                }else {
                    Intent intent = new Intent(ClienteActivity.this, MenuPrincipal.class);
                    startActivity(intent);
                    boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                    finishAffinity();
                }
            }
        });

        imgbtnNovo = findViewById(R.id.btnAddCli);
        imgbtnNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent != null && intent.hasExtra("flagPedido")) {
                    if (flag.equals("pedidocliente")) {
                        Intent intent = new Intent(ClienteActivity.this, CadastroCliActivity.class);
                        intent.putExtra("flagNovoCli", "verdadeiro");
                        startActivity(intent);
                    }
                }else {
                    Intent intent = new Intent(ClienteActivity.this, CadastroCliActivity.class);
                    startActivity(intent);
                    boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                    finishAffinity();
                }
            }
        });

        imgBtnFiltrar = findViewById(R.id.btnFiltrarCli);
        imgBtnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clientNome = inputCliente.getText().toString();
                if(TextUtils.isEmpty(clientNome)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
                    builder.setTitle("Pesquisa Cliente")
                            .setMessage("Campo cliente está em branco!Preencha corretamente!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{

                    List<Cliente> listaCli = criarListaCliente();
                    if (isKeyboardActive()) {
                        // Teclado está ativo, então ocultá-lo
                        hideKeyboard();
                    }

                    clienteAdapter = new ClienteAdapter(listaCli);
                    recyclerView.setAdapter(clienteAdapter);

                    clienteAdapter.setOnItemClickListener(new ClienteAdapter.OnClickListener() {
                        @Override
                        public void onClick(Cliente cliente) {
                            if(isKeyboardActive()){
                                hideKeyboard();
                            }
                            if(flag == null){
                                clienteSel = cliente.getCodigoApp().toString();
                                Toast.makeText(ClienteActivity.this, cliente.getRazao().toString() , Toast.LENGTH_LONG).show();
                            }else{
                                if(flag.equals("pedidocliente")){
                                    if (cliente != null && cliente.getCodigoApp() != null) {
                                        clienteSel = cliente.getCodigoApp().toString();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
                                        builder.setTitle("Pesquisa Cliente")
                                                .setMessage("Cliente " + cliente.getCodigoApp().toString() + " foi escolhido, pressione SELECIONAR!")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

        inputCliente.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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


        imgBtnSelCli = findViewById(R.id.btnSelCli);
        imgBtnSelCli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag.equals("pedidocliente")){
                    if(clienteSel != null && !clienteSel.isEmpty()){
                        if (intent != null && intent.hasExtra("flagPedido")) {
                            finalizarComResultado(clienteSel);
                        }
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
                        builder.setTitle("Pesquisa Cliente")
                                .setMessage("Selecione primeiro um cliente!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        });

    }


    private List<Cliente> criarListaCliente() {
        List<Cliente> listaClientes = new ArrayList<>();
        String cliente = inputCliente.getText().toString();
        // Defina o padrão regex para aceitar apenas números
        Pattern pattern = Pattern.compile("^[0-9]+$");

        // Crie um objeto Matcher para a sua String
        Matcher matcher = pattern.matcher(cliente);

        listaClientes.clear();

        // Crie uma instância do PecaDAO
        ClienteDAO clienteDAO = new ClienteDAO(this);

        // Chame o método selectPecaCod para obter as peças do banco de dados
        if(matcher.matches()){
            listaClientes = clienteDAO.pesquisaCliByCod(cliente);
        }else{
            listaClientes = clienteDAO.pesquisaCli(cliente);
        }


        if (!listaClientes.isEmpty()) {
            for (Cliente pesqCliente : listaClientes) {
                // Faça o que for necessário com cada PecaOS
                // Exemplo: exibir informações em um log
                //Log.d("PecaOS", "Código: " + pecaOS.getCodpeca() + ", Quantidade: " + pecaOS.getPecaqtde());
            }
            clienteAdapter = new ClienteAdapter(listaClientes); // Inicialize o clienteAdapter aqui
            recyclerView.setAdapter(clienteAdapter);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ClienteActivity.this);
            recyclerView.setLayoutManager(linearLayoutManager);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ClienteActivity.this);
            builder.setTitle("Pesquisa Cliente")
                    .setMessage("Não foi localizado clientes!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return listaClientes;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputCliente.getWindowToken(), 0);
    }

    private boolean isKeyboardActive() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive(); // Retorna true se o teclado estiver ativo (visível)
    }

    private void finalizarComResultado(String valor) {
        Intent broadcastIntent = new Intent("ENVIAR_CLIENTE");
        broadcastIntent.putExtra("CODIGO_CLIENTE", valor);
        sendBroadcast(broadcastIntent);
        finish();
    }

}