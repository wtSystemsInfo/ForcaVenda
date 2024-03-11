package com.example.forcavenda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.forcavenda.DAO.ClienteDAO;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CadastroCliActivity extends AppCompatActivity {

    Toolbar toolbar;

    ImageButton imgVoltar;

    ImageButton imgSalvar;

    Spinner spinnerTipo;

    Spinner spinnerUF;

    TextView txtCodigoInterno;

    TextInputEditText txtRazao;
    TextInputEditText txtDDD;
    TextInputEditText txtTel;
    TextInputEditText txtDoc;
    TextInputEditText txtCity;
    TextInputEditText txtEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cli);
        getWindow().setStatusBarColor(Color.parseColor("#164EBC"));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Novo Cliente");
        toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        txtRazao = findViewById(R.id.txtInpRazao);
        txtDDD = findViewById(R.id.txtInpDDD);
        txtTel = findViewById(R.id.txtInpTel);
        txtDoc = findViewById(R.id.txtInpDoc);
        txtCity = findViewById(R.id.txtInpCid);
        txtEmail = findViewById(R.id.txtInpEmail);
        txtCodigoInterno = findViewById(R.id.txtCodigoIntCli);
        spinnerTipo = findViewById(R.id.spinTipoCli);
        spinnerUF = findViewById(R.id.spinUf);
        imgSalvar = findViewById(R.id.btnSalvarCli);
        imgVoltar = findViewById(R.id.btnVoltarCli);


        fillSpinnnerTipo(spinnerTipo);
        fillSpinnnerUF(spinnerUF);


        imgVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CadastroCliActivity.this, ClienteActivity.class);
                startActivity(intent);
                boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                finishAffinity();
            }

        });


        imgSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinnerTipo.getSelectedItem().toString().equals("F")){
                    if(txtDoc.getText().toString().length()==11){
                        String cpfFormat = txtDoc.getText().toString().substring(0, 3) + "." + txtDoc.getText().toString().substring(3, 6) + "." + txtDoc.getText().toString().substring(6, 9) + "-" + txtDoc.getText().toString().substring(9);
                        txtDoc.setText(cpfFormat);
                    }
                }
                if(spinnerTipo.getSelectedItem().toString().equals("J")) {
                    if (txtDoc.getText().toString().length() == 14) {
                        String cnpjFormat = txtDoc.getText().toString().substring(0, 2) + "." + txtDoc.getText().toString().substring(2, 5) + "." +
                                txtDoc.getText().toString().substring(5, 8) + "/" + txtDoc.getText().toString().substring(8, 12) + "-" + txtDoc.getText().toString().substring(12);
                        txtDoc.setText(cnpjFormat);
                    }
                }

                if(!validaCampos()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(CadastroCliActivity.this);
                    builder.setTitle("Cadastro Cliente")
                            .setMessage("Não foi possível salvar cliente!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else {
                    ClienteDAO clienteDAO = new ClienteDAO(CadastroCliActivity.this);
                    clienteDAO.salvaCliente(txtRazao.getText().toString(), txtDDD.getText().toString(), txtTel.getText().toString(),
                            spinnerTipo.getSelectedItem().toString(), txtDoc.getText().toString(), txtCity.getText().toString(),
                            spinnerUF.getSelectedItem().toString(), txtEmail.getText().toString());
                    txtCodigoInterno.setText(String.valueOf(clienteDAO.retornaUltimoCli()));
                }
            }
        });
    }


    private void fillSpinnnerTipo(Spinner spinner) {
        List<String> itensVend = new ArrayList<>();
        itensVend.add("F");
        itensVend.add("J");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itensVend);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void fillSpinnnerUF(Spinner spinner) {
        List<String> itensVend = new ArrayList<>();
        itensVend.add("AC");
        itensVend.add("AL");
        itensVend.add("AP");
        itensVend.add("AM");
        itensVend.add("BA");
        itensVend.add("CE");
        itensVend.add("ES");
        itensVend.add("GO");
        itensVend.add("MA");
        itensVend.add("MT");
        itensVend.add("MS");
        itensVend.add("MG");
        itensVend.add("PA");
        itensVend.add("PB");
        itensVend.add("PR");
        itensVend.add("PE");
        itensVend.add("PI");
        itensVend.add("RJ");
        itensVend.add("RN");
        itensVend.add("RS");
        itensVend.add("RO");
        itensVend.add("RR");
        itensVend.add("SC");
        itensVend.add("SP");
        itensVend.add("SE");
        itensVend.add("TO");
        itensVend.add("DF");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itensVend);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public Boolean validaCampos() {
        if (txtRazao.getText().toString().isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CadastroCliActivity.this);
            builder.setTitle("Cadastro Cliente")
                    .setMessage("O campo Razão é obrigatório! Preencha corretamente o campo!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }

        ClienteDAO verificaClienteDAO = new ClienteDAO(CadastroCliActivity.this);
        if(verificaClienteDAO.verificaCadastro(txtDoc.getText().toString())){
            AlertDialog.Builder builder = new AlertDialog.Builder(CadastroCliActivity.this);
            builder.setTitle("Cadastro Cliente")
                    .setMessage("Já existe cadastro com esse documento salvo!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return false;
        }

        if (spinnerTipo.getSelectedItem().toString().equals("F")) {

            if(!validarCPF(txtDoc.getText().toString())){
                AlertDialog.Builder builder = new AlertDialog.Builder(CadastroCliActivity.this);
                builder.setTitle("Cadastro Cliente")
                        .setMessage("O CPF está inválido!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        }

        if (spinnerTipo.getSelectedItem().toString().equals("J")) {
            if(!validarCNPJ(txtDoc.getText().toString())){
                AlertDialog.Builder builder = new AlertDialog.Builder(CadastroCliActivity.this);
                builder.setTitle("Cadastro Cliente")
                        .setMessage("O CNPJ está inválido!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        }

        return true;

    }


    public boolean validarCPF(String cpf) {
        // Remova caracteres não numéricos do CPF
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifique se o CPF tem 11 dígitos
        if (cpf.length() != 11)
            return false;

        // Verifique se todos os dígitos são iguais (caso contrário, não é um CPF válido)
        if (cpf.matches("(\\d)\\1{10}"))
            return false;

        // Calcule o primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);

        // Verifique se o primeiro dígito verificador é válido
        if (primeiroDigito == 10 || primeiroDigito == 11) {
            if (cpf.charAt(9) != '0')
                return false;
        } else {
            if (primeiroDigito != (cpf.charAt(9) - '0'))
                return false;
        }

        // Calcule o segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);

        // Verifique se o segundo dígito verificador é válido
        if (segundoDigito == 10 || segundoDigito == 11) {
            if (cpf.charAt(10) != '0')
                return false;
        } else {
            if (segundoDigito != (cpf.charAt(10) - '0'))
                return false;
        }

        // Se todas as verificações passarem, o CPF é válido
        return true;
    }


    public boolean validarCNPJ(String cnpj) {
        // Remova caracteres não numéricos do CNPJ
        cnpj = cnpj.replaceAll("[^0-9]", "");

        // Verifique se o CNPJ tem 14 dígitos
        if (cnpj.length() != 14)
            return false;

        // Verifique se todos os dígitos são iguais (caso contrário, não é um CNPJ válido)
        if (cnpj.matches("(\\d)\\1{13}"))
            return false;

        // Calcule o primeiro dígito verificador
        int soma = 0;
        int peso = 2;
        for (int i = 11; i >= 0; i--) {
            soma += (cnpj.charAt(i) - '0') * peso;
            peso++;
            if (peso == 10)
                peso = 2;
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10)
            primeiroDigito = 0;

        // Verifique se o primeiro dígito verificador é válido
        if (primeiroDigito != (cnpj.charAt(12) - '0'))
            return false;

        // Calcule o segundo dígito verificador
        soma = 0;
        peso = 2;
        for (int i = 12; i >= 0; i--) {
            soma += (cnpj.charAt(i) - '0') * peso;
            peso++;
            if (peso == 10)
                peso = 2;
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10)
            segundoDigito = 0;

        // Verifique se o segundo dígito verificador é válido
        if (segundoDigito != (cnpj.charAt(13) - '0'))
            return false;

        // Se todas as verificações passarem, o CNPJ é válido
        return true;
    }



}