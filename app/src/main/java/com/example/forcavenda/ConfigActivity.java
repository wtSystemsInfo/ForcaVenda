package com.example.forcavenda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.forcavenda.Connection.SQLConnection;
import com.example.forcavenda.Connection.SQLConnectionExt;
import com.example.forcavenda.DAO.VendedorDAO;
import com.google.android.material.textfield.TextInputEditText;
import com.example.forcavenda.Model.Vendedor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ConfigActivity extends AppCompatActivity {

    private ImageButton imgBtnVoltar;
    private Spinner vendSpinner;
    private ImageButton imgBtnSincVend;

    private String codVendedor;

    private int indexOfHyphen;

    private ImageButton imgBtnTest;

    private ImageButton imgBtnSalvarConfig;

    private Toolbar toolbar;

    private String teste;

    private TextInputEditText inputRedeInt;

    private TextInputEditText inputRedeExt;

    private TextView txtCarregando;

    private ProgressBar progressBar;

    private Boolean connection = false;

    private int progress = 0;
    private Handler handler = new Handler();

    private List<Vendedor> vendedores;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        //Incializando Componentes e configurando a Toolbar
        getWindow().setStatusBarColor(Color.parseColor("#164EBC"));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Configurações");
        toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        inputRedeInt = findViewById(R.id.txtInpRedeInt);
        inputRedeInt.requestFocus();
        inputRedeExt = findViewById(R.id.txtInpRedeExt);
        vendSpinner = findViewById(R.id.spinnerVendedor);

        imgBtnSincVend = findViewById(R.id.btnSincVendedor);
        txtCarregando = findViewById(R.id.txtSincVend);
        progressBar = findViewById(R.id.progressBar3);
        imgBtnTest = findViewById(R.id.btnTeste);

        carregaConfig(inputRedeInt, inputRedeExt);

        inputRedeInt.setText("192.168.15.19");
        inputRedeExt.setText("8.8.8.8");

        //Comportamento dos botões

        imgBtnVoltar = findViewById(R.id.btnVoltarConfig);
        imgBtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfigActivity.this, MenuPrincipal.class);
                startActivity(intent);
                boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                finishAffinity();
            }
        });

        imgBtnSalvarConfig = findViewById(R.id.btnSalvarConfig);

        imgBtnSalvarConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(String.valueOf(inputRedeInt.getText()).isEmpty()){
                    Toast.makeText(ConfigActivity.this, "CAMPO REDE INTERNA ESTÁ VAZIO!", Toast.LENGTH_SHORT).show();
                }else{
                    if(String.valueOf(inputRedeExt.getText()).isEmpty()){
                        Toast.makeText(ConfigActivity.this, "CAMPO REDE EXTERNA ESTÁ VAZIO!", Toast.LENGTH_SHORT).show();
                    }else {
                        if(vendSpinner.getSelectedItem().toString().equals("")){
                            Toast.makeText(ConfigActivity.this, "CAMPO DO VENDEDOR ESTÁ VAZIO!", Toast.LENGTH_SHORT).show();
                        }else {
                            SQLiteDatabase bancoDados = openOrCreateDatabase("ForcaVenda", MODE_PRIVATE, null);
                            indexOfHyphen = vendSpinner.getSelectedItem().toString().indexOf("-");
                            if (indexOfHyphen != -1) {
                                codVendedor = vendSpinner.getSelectedItem().toString().substring(0, indexOfHyphen);
                            }
                            String sql = "UPDATE CONFIG SET RedeInt = ?, RedeExt = ?, CodVendedor = ?, Vendedor = ?";
                            Object[] bindArgs = {inputRedeInt.getText().toString(), inputRedeExt.getText().toString(), codVendedor,  vendSpinner.getSelectedItem().toString()};
                            bancoDados.execSQL(sql, bindArgs);
                            bancoDados.close();
                            Toast.makeText(ConfigActivity.this, "CONFIGURAÇÕES SALVAS!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });


        imgBtnSincVend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCarregando.setVisibility(View.VISIBLE);
                txtCarregando.setText("Sincronizando");
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(100);
                startUpProgress();
                connection = false;
                teste = "vendedor";
            }
        });


        imgBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCarregando.setVisibility(View.VISIBLE);
                txtCarregando.setText("Conectando");
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(100);
                startUpProgress();
                connection = false;
                teste = "conexao";

            }
        });

    }

    private void updateProgress() {
        // Aumenta o progresso em 20 unidades
        progress += 20;

        if(teste.equals("conexao") && !connection) {
            connectionIntTest();
            connection = true;
        }
        if(teste.equals("vendedor") && !connection){

            connection = true;
            VendedorDAO vendedorDAO = new VendedorDAO(this);
            Connection conn = SQLConnection.conectar(inputRedeInt.getText().toString());
            if (conn != null) {
                vendedorDAO.baixaVendedor(inputRedeInt.getText().toString());
            }else{
                conn = SQLConnectionExt.conectar(inputRedeExt.getText().toString());
                if (conn != null) {
                    vendedorDAO.baixaVendedorExt(inputRedeExt.getText().toString());
                }else{
                    showAlertDialog("Sinc - Fail");
                }
            }
            vendedores = vendedorDAO.obterVendedores();
            fillSpinnnerSinc(vendSpinner, vendedores);
            showAlertDialog("vendedor");

        }

        // Verifica se o progresso atingiu o máximo
        if (progress <= progressBar.getMax()) {
            progressBar.setProgress(progress);
            // Executa novamente a tarefa após 1 segundo
            handler.postDelayed(this::updateProgress, 1000);
        } else {
            // Reseta o progresso quando atingir o máximo
            progress = 0;
            progressBar.setProgress(progress);
            // Oculta a mensagem de carregando e a ProgressBar
            txtCarregando.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(progress == 100){
                    txtCarregando.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }

            }
        }, 1500); // Tempo em milissegundos (5 segundos neste exemplo)
    }

    public void connectionIntTest(){
        Connection conn = SQLConnection.conectar(inputRedeInt.getText().toString());
        if (conn != null) {
            showAlertDialog("Conexao - Sucess");
        }else{
            showAlertDialog("Conexao - Fail");
        }

    }

    public void connectionExtTest(){
        Connection conn = SQLConnectionExt.conectar(inputRedeExt.getText().toString());
        if (conn != null) {
            showAlertDialog("Conexao Ext - Sucess");
        }else{
            showAlertDialog("Conexao Ext - Fail");
        }
    }



    private void startUpProgress() {
        // Inicia a tarefa de atualização do progresso
        handler.postDelayed(this::updateProgress, 1000);
    }



    //Preenchimento do Spinner(Combobox)
    private void fillSpinnnerSinc(Spinner spinner, List<Vendedor> vendedores){
        List<String> itensVend = new ArrayList<>();

        for (Vendedor vendedor : vendedores) {
            String item = vendedor.getCodigo() + " - " + vendedor.getNome();
            itensVend.add(item);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itensVend);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }



    private void carregaConfig(TextInputEditText rdInt, TextInputEditText rdExt) {
        SQLiteDatabase bancoDados = openOrCreateDatabase("ForcaVenda", MODE_PRIVATE, null);
        Cursor cursor = bancoDados.rawQuery("Select * from CONFIG", null);
        if (cursor != null) {
            cursor.moveToFirst();
            rdInt.setText(cursor.getString(1));
            rdExt.setText(cursor.getString(2));
            fillSpinnnerVend(vendSpinner,cursor.getInt(3));
        }
        cursor.close();
        bancoDados.close();
    }


    private void fillSpinnnerVend(Spinner spinner, Integer codVendedor){
        List<String> itensVend = new ArrayList<>();
        VendedorDAO vendedorDAO = new VendedorDAO(this);
        Vendedor vendedor = vendedorDAO.getVendedorByCode(codVendedor);

        if (vendedor != null) {
            String item = vendedor.getCodigo() + " - " + vendedor.getNome();
            itensVend.add(item);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itensVend);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private void showAlertDialog(String funcao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(funcao.equals("Conexao - Sucess")){
            builder.setTitle("Conexão")
                    .setMessage("Conexão interna realizada com sucesso! Preparando para a conexão externa!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            connectionExtTest();

                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        if(funcao.equals("Conexao - Fail")){
            builder.setTitle("Conexão")
                    .setMessage("Conexão interna falhou! Preparando para a conexão externa!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            connectionExtTest();

                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        if(funcao.equals("Conexao Ext - Sucess")){
            builder.setTitle("Conexão")
                    .setMessage("Conexão externa realizada com sucesso!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.setTitle("Conexão")
                                    .setMessage("FINALIZADO TESTE DE CONEXÃO")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        if(funcao.equals("Conexao Ext - Fail")){
            builder.setTitle("Conexão")
                    .setMessage("Conexão externa falhou!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.setTitle("Conexão")
                                    .setMessage("FINALIZADO TESTE DE CONEXÃO")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        if(funcao.equals("Sinc - Fail")){
            builder.setTitle("Sincronização")
                    .setMessage("Sincronização Falhou! Verifique dados de conexão!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.setTitle("Conexão")
                                    .setMessage("FINALIZADO SINCRONIZAÇÃO")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }



        if(teste.equals("vendedor")){
            builder.setTitle("Conexão")
                    .setMessage("FINALIZADO SINCRONIZAÇÃO DE VENDEDORES!")
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