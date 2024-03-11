package com.example.forcavenda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler();

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(Color.parseColor("#FFFFFF"));

        try{

            //Criando Banco
            SQLiteDatabase bancoDados = openOrCreateDatabase("ForcaVenda", MODE_PRIVATE, null);

            //Criar Tabelas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CLIENTE (Codigo Integer NOT NULL PRIMARY KEY AUTOINCREMENT, CodSistema Integer, Razao VARCHAR(60), Ddd VARCHAR(3), Telefone VARCHAR(90), " +
                    "Pessoa VARCHAR(1), CnpjCpf VARCHAR(20), Email VARCHAR(80), " +
                    "Cidade VARCHAR(90), UF VARCHAR(2), Exportado Integer )");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS PRODUTO (Codigo Integer NOT NULL PRIMARY KEY, Descricao VARCHAR(100), Valor Double, Estoque Double)");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS PEDIDO (Codigo Integer NOT NULL PRIMARY KEY AUTOINCREMENT, CodCli Integer, Data Datetime, Enviado Integer(1), " +
                    "Obs VARCHAR(500), CodCondPgto INTEGER, TotalPed DOUBLE)");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS PEDIDOPRODUTO (Codigo Integer NOT NULL PRIMARY KEY AUTOINCREMENT, CodPedido INTEGER, CodProduto INTEGER, " +
                    "Qtd Double, Vlrunit Double, VlrDesc Double,SubTot Double, Enviado Integer(1))");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS FORMA (Codigo Integer NOT NULL PRIMARY KEY, Descricao VARCHAR(100))");

                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS CONFIG (Codigo Integer NOT NULL PRIMARY KEY AUTOINCREMENT, RedeInt VARCHAR(50), RedeExt VARCHAR(50), CodVendedor INTEGER, Vendedor VARCHAR(50))");

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS VENDEDOR (Codigo Integer NOT NULL PRIMARY KEY, Descricao VARCHAR(100))");

            Cursor cursor = bancoDados.rawQuery("Select * from CONFIG", null);
            if (cursor != null){
                if( cursor.getCount() == 0){
                    bancoDados.execSQL("INSERT INTO CONFIG(RedeInt, RedeExt, CodVendedor, Vendedor) VALUES('127.0.0.1', '127.0.0.1', 0, 'NULL' )");
                }
                cursor.close();
            }
            bancoDados.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        //Apresentação do app

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MenuPrincipal.class);
                startActivity(intent);
                boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                if (hasClearTaskFlag) {
                    finishAffinity();
                }
            }
        }, 5000);
    }
}