package com.example.forcavenda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.widget.Toolbar;


public class MenuPrincipal extends AppCompatActivity {

    private ImageButton imgBtnCliente;
    private ImageButton imgBtnConfig;

    private ImageButton imgBtnSinc;
    private ImageButton imgBtnPed;

    private ImageButton imgBtnProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(Color.parseColor("#164EBC"));

        // Configurar a Toolbar como a App Bar
        Toolbar startToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(startToolbar);
        startToolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);


        //Comportamento dos botões

        imgBtnCliente = findViewById(R.id.btnCliente);
        imgBtnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imgBtnConfig = findViewById(R.id.btnConfig);
        imgBtnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, ConfigActivity.class);
                startActivity(intent);
                boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                finishAffinity();
            }
        });

        imgBtnSinc = findViewById(R.id.btnSinc);
        imgBtnSinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, SincActivity.class);
                startActivity(intent);
                boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                finishAffinity();
            }
        });

        imgBtnProduto = findViewById(R.id.btnProduto);
        imgBtnProduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, ProdutoActivity.class);
                startActivity(intent);
                boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                finishAffinity();
            }
        });

        imgBtnCliente = findViewById(R.id.btnCliente);
        imgBtnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, ClienteActivity.class);
                startActivity(intent);
                boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                finishAffinity();
            }
        });

        imgBtnPed = findViewById(R.id.btnPedidos);
        imgBtnPed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, PedidoActivity.class);
                startActivity(intent);
                boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                finishAffinity();
            }
        });


    }

    //Inflando o menu da toolbar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.superior_menu, menu);
        return true;
    }

    //Comportamento de cada item do menu da toolbar

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_exit) {

            finishAffinity();
            System.exit(0);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}