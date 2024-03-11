package com.example.forcavenda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.forcavenda.Connection.SQLiteHelper;
import com.example.forcavenda.DAO.ClienteDAO;
import com.example.forcavenda.DAO.FormaDAO;
import com.example.forcavenda.DAO.ItemPedidoDAO;
import com.example.forcavenda.DAO.PedidoDAO;
import com.example.forcavenda.DAO.ProdutoDAO;

import java.util.ArrayList;
import java.util.List;

public class SincActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton imgBtnExport;
    private ImageButton imgBtnVoltar;
    private ImageButton imgBtnImport;

    private TextView info;
    private ProgressBar progressBar;

    private Spinner spinnerRede;

    private Spinner spinnerOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinc);
        getWindow().setStatusBarColor(Color.parseColor("#164EBC"));

        //Incializando componentes
        info = findViewById(R.id.txtJobSinc);
        progressBar = findViewById(R.id.progressBar);

        //Configurando a toolbar
        toolbar = findViewById(R.id.toolbar_sinc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sincronizar");
        toolbar.getOverflowIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        spinnerRede = findViewById(R.id.spinnerRede);
        spinnerOp = findViewById(R.id.spinnerOption);
        fillSpinnnerRede(spinnerRede);
        //fillSpinnnerOption(spinnerOp);
        ClienteDAO clienteDAO = new ClienteDAO(SincActivity.this);
        PedidoDAO pedidoDAO = new PedidoDAO(SincActivity.this);
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(SincActivity.this);
        ProdutoDAO produtoDAO = new ProdutoDAO(SincActivity.this);
        FormaDAO formaDAO = new FormaDAO(SincActivity.this);

        imgBtnVoltar = findViewById(R.id.btnVoltarProd);
        imgBtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SincActivity.this, MenuPrincipal.class);
                startActivity(intent);
                boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
                finishAffinity();
            }
        });

        //Comportamento do botão de exportação

        imgBtnExport = findViewById(R.id.btnExportarSinc);
        imgBtnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] exportaCli = {0};
                final int[] exportaPed = {0};
                Handler handler = new Handler(Looper.getMainLooper());
                long delayMillis = 2000;
                info.setText("Exportando");
                info.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                imgBtnImport.setVisibility(View.INVISIBLE);
                imgBtnExport.setVisibility(View.INVISIBLE);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //Rede Interna
                        if (spinnerRede.getSelectedItem().toString().equals("Rede Interna")) {
                            exportaCli[0] = exportaCliente(1);

                            if (exportaCli[0] == 1) {
                                exportaPed[0] = exportaPedido(1);
                            }

                        } else {
                            //REDE EXTERNA
                            exportaCli[0] = exportaCliente(0);

                            if (exportaCli[0] == 1) {
                                exportaPed[0] = exportaPedido(0);

                            }
                        }
                    }
                };
                handler.postDelayed(runnable, delayMillis);
            }
        });

        //Comportamento do botão de Importação

        imgBtnImport = findViewById(R.id.btnImportarSinc);
        imgBtnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] importaCli = {0};
                final int[] importaProd = {0};
                final int[] importaForma = {0};
                Handler handler = new Handler(Looper.getMainLooper());
                long delayMillis = 2000;
                info.setText("Importando");
                info.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                imgBtnImport.setVisibility(View.INVISIBLE);
                imgBtnExport.setVisibility(View.INVISIBLE);

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(spinnerRede.getSelectedItem().toString().equals("Rede Interna")){
                                importaCli[0] = importaCliente(1);
                                if(importaCli[0] == 1 ){
                                    importaProd[0] = importaProduto(1);
                                    if(importaProd[0] == 1){
                                        importaForma[0] = importaForma(1);
                                    }
                                }
                        }else{
                            importaCli[0] = importaCliente(0);
                            if(importaCli[0] == 1 ){
                                importaProd[0] = importaProduto(0);
                                if(importaProd[0] == 1){
                                    importaForma[0] = importaForma(0);
                                }
                            }
                        }


                    }
                };
                handler.postDelayed(runnable, delayMillis);
            }
        });
    }

    //Preenchedno os spinners(Combobox)
    private void fillSpinnnerRede(Spinner spinner) {
        List<String> itensConn = new ArrayList<>();
        itensConn.add("Rede Externa");
        itensConn.add("Rede Interna");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itensConn);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }




    private int exportaCliente(int opcao){
        final int[] resposta = {0};
        int tentativas = 0;
        ClienteDAO clienteDAO = new ClienteDAO(SincActivity.this);
        //Opção 1 é rede interna
        if(opcao == 1) {
            while (resposta[0] == 0) {
                if (clienteDAO.exportaClienteInt()) {
                    resposta[0] = 1;
                    tentativas++;

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    break;
                }

            }
        }else {
            while (resposta[0] == 0) {
                if (clienteDAO.exportaClienteExt()) {
                    resposta[0] = 1;
                    tentativas++;

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    break;
                }

            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
        if (resposta[0] == 1) {
            builder.setTitle("Exportação ")
                    .setMessage("Exportação de Clientes realizada com sucesso!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            builder.setTitle("Exportação ")
                    .setMessage("Exportação de Clientes falhou!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            info.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            imgBtnImport.setVisibility(View.VISIBLE);
                            imgBtnExport.setVisibility(View.VISIBLE);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return resposta[0];

    }


    private int exportaPedido(int opcao){
        final int[] resposta = {0};
        int tentativas = 0;
        PedidoDAO pedidoDAO = new PedidoDAO(SincActivity.this);
        //Opção 1 é rede interna
        if(opcao == 1) {
            while (resposta[0] == 0) {
                if (pedidoDAO.exportaPedidoInt(SincActivity.this)) {
                    resposta[0] = 1;
                    tentativas++;

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    break;
                }

            }
        }else {
            while (resposta[0] == 0) {
                resposta[0] = pedidoDAO.exportaPedidoExt(SincActivity.this);
                if (resposta[0] == 200) {
                    tentativas++;

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    break;
                }

            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
        if (resposta[0] == 200 ) {
            builder.setTitle("Exportação ")
                    .setMessage("Exportação de Pedidos realizada com sucesso!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            info.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            imgBtnImport.setVisibility(View.VISIBLE);
                            imgBtnExport.setVisibility(View.VISIBLE);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            builder.setTitle("Exportação ")
                    .setMessage("Exportação de Pedidos falhou! Erro : " + resposta[0])
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            info.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            imgBtnImport.setVisibility(View.VISIBLE);
                            imgBtnExport.setVisibility(View.VISIBLE);
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


        return resposta[0];
    }




    private int importaCliente(int opcao){
        final int[] resposta = {0};
        int tentativas = 0;
        ClienteDAO clienteDAO = new ClienteDAO(SincActivity.this);
        if(opcao == 1) { //opção 1 é rede interna
            while (resposta[0] == 0) {
                if (clienteDAO.baixaCliente()) {
                    resposta[0] = 1;
                    tentativas++;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação de clientes finalizada com sucesso!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nada
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação de clientes falhou no meio do procedimento!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    info.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    imgBtnImport.setVisibility(View.VISIBLE);
                                    imgBtnExport.setVisibility(View.VISIBLE);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }

            }
        }else {
            while (resposta[0] == 0) {
                if (clienteDAO.baixaClienteExt()) {
                    resposta[0] = 1;
                    tentativas++;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação de clientes finalizada com sucesso!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nada
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação de clientes falhou no meio do procedimento!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    info.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    imgBtnImport.setVisibility(View.VISIBLE);
                                    imgBtnExport.setVisibility(View.VISIBLE);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }

            }
        }
        return resposta[0];
    }


    private int importaProduto(int opcao){
        final int[] resposta = {0};
        int tentativas = 0;
        ProdutoDAO produtoDAO = new ProdutoDAO(SincActivity.this);
        if(opcao == 1) { //opção 1 é rede interna
            while (resposta[0] == 0) {
                if (produtoDAO.baixaProduto()) {
                    resposta[0] = 1;
                    tentativas++;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação de produtos finalizada com sucesso!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nada
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação de produtos falhou no meio do procedimento!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    info.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    imgBtnImport.setVisibility(View.VISIBLE);
                                    imgBtnExport.setVisibility(View.VISIBLE);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }

            }
        }else {
            while (resposta[0] == 0) {
                if (produtoDAO.baixaProdutoExt()) {
                    resposta[0] = 1;
                    tentativas++;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação de produtos finalizada com sucesso!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nada
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação de produtos falhou no meio do procedimento!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    info.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    imgBtnImport.setVisibility(View.VISIBLE);
                                    imgBtnExport.setVisibility(View.VISIBLE);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }

            }
        }
        return resposta[0];
    }

    private int importaForma(int opcao){
        final int[] resposta = {0};
        int tentativas = 0;
        FormaDAO formaDAO = new FormaDAO(SincActivity.this);
        if(opcao == 1) { //opção 1 é rede interna
            while (resposta[0] == 0) {
                if (formaDAO.baixaForma()) {
                    resposta[0] = 1;
                    tentativas++;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação da forma de pagamento finalizada com sucesso!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    info.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    imgBtnImport.setVisibility(View.VISIBLE);
                                    imgBtnExport.setVisibility(View.VISIBLE);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação de forma de pagamento falhou no meio do procedimento!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    info.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    imgBtnImport.setVisibility(View.VISIBLE);
                                    imgBtnExport.setVisibility(View.VISIBLE);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }

            }
        }else {
            while (resposta[0] == 0) {
                if (formaDAO.baixaFormaExt()) {
                    resposta[0] = 1;
                    tentativas++;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação da forma de pagamento finalizada com sucesso!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    info.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    imgBtnImport.setVisibility(View.VISIBLE);
                                    imgBtnExport.setVisibility(View.VISIBLE);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    tentativas++;
                }
                if (tentativas == 3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SincActivity.this);
                    builder.setTitle("Importação ")
                            .setMessage("Importação da forma de pagamento falhou no meio do procedimento!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    info.setVisibility(View.INVISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    imgBtnImport.setVisibility(View.VISIBLE);
                                    imgBtnExport.setVisibility(View.VISIBLE);
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    break;
                }

            }
        }
        return resposta[0];
    }


}


