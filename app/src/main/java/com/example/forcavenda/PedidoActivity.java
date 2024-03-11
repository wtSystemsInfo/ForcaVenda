package com.example.forcavenda;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.forcavenda.Model.ViewModelCompart;
import com.example.forcavenda.ui.cliente.ClienteFragment;
import com.example.forcavenda.ui.outro.OutroFragment;
import com.example.forcavenda.ui.produto.ProdutoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.forcavenda.databinding.ActivityPedidoBinding;

public class PedidoActivity extends AppCompatActivity {


    private ActivityPedidoBinding binding;

    private String pesquisa = null;

    private String codigoCliente = null;

    private int codigoProduto = 0;

    private String descricaoProduto = null;

    private Double vlrProduto = null;

    private Double qtdeProduto = null;

    private Double subProduto = null;

    private String codPedido = null;

    private BroadcastReceiver receiver;

    private ViewModelCompart viewModelCompart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(Color.parseColor("#164EBC"));

        viewModelCompart = new ViewModelProvider(this).get(ViewModelCompart.class);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_cliente, R.id.navigation_produto, R.id.navigation_outros)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_pedido);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_voltar) {
                // Chama a função para iniciar a atividade "MenuPrincipal"
                goToMenuPrincipal();
                return true;
            }
            return NavigationUI.onNavDestinationSelected(item, navController);
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("ENVIAR_CLIENTE")) {
                    codigoCliente = intent.getStringExtra("CODIGO_CLIENTE");
                    // Faça o que for necessário com o dado recebido
                }else{
                    if(intent.getAction().equals("ENVIAR_PRODUTO")){
                        pesquisa = intent.getStringExtra("PESQUISA");
                        codigoProduto = intent.getIntExtra("CODIGO_PRODUTO", 0);
                        descricaoProduto = intent.getStringExtra("DESC_PRODUTO");
                        vlrProduto = intent.getDoubleExtra("VLR_PRODUTO", 0.00);
                        qtdeProduto = intent.getDoubleExtra("QTDE_PRODUTO", 0.00);
                        subProduto = intent.getDoubleExtra("SUB_PRODUTO", 0.00);
                    }else{
                        if (intent.getAction().equals("PESQUISA_PEDIDO")){
                            codPedido = intent.getStringExtra("CODIGO_PEDIDO");
                        }
                    }
                }
            }
        };

    }


    // Função para iniciar a atividade "MenuPrincipal"
    private void goToMenuPrincipal() {
        Intent intent = new Intent(PedidoActivity.this, MenuPrincipal.class);
        startActivity(intent);
        boolean hasClearTaskFlag = (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) != 0;
        finishAffinity();
    }




    @Override
    protected void onResume(){
        super.onResume();

        // Recupere o NavHostFragment atual
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_pedido);

        // Recupere o fragmento atualmente visível
        Fragment currentFragment = navHostFragment.getChildFragmentManager().getFragments().get(0);

        IntentFilter filterCli = new IntentFilter("ENVIAR_CLIENTE");
        registerReceiver(receiver, filterCli);
        IntentFilter filterProd = new IntentFilter("ENVIAR_PRODUTO");
        registerReceiver(receiver, filterProd);
        IntentFilter filterPesPesq = new IntentFilter("PESQUISA_PEDIDO");
        registerReceiver(receiver, filterPesPesq);

        if(codigoCliente != null){
            if (currentFragment instanceof ClienteFragment){
                Bundle bundle = new Bundle();
                bundle.putString("CODIGO_CLIENTE", codigoCliente);
                currentFragment.setArguments(bundle);
            }
            getIntent().removeExtra("CODIGO_CLIENTE");
            codigoCliente = null;

        }
        if(codigoProduto > 0){
            if (currentFragment instanceof ProdutoFragment){
                Bundle bundle = new Bundle();
                bundle.putInt("CODIGO_PRODUTO", codigoProduto);
                bundle.putString("DESC_PRODUTO", descricaoProduto);
                bundle.putDouble("QTDE_PRODUTO", qtdeProduto);
                bundle.putDouble("VLR_PRODUTO", vlrProduto);
                bundle.putDouble("SUB_PRODUTO", subProduto);
                bundle.putString("PESQUISA", pesquisa);
                currentFragment.setArguments(bundle);
            }
            getIntent().removeExtra("CODIGO_PRODUTO");
            codigoProduto = 0;
        }

        if( codPedido != null ){
            if( Integer.valueOf(codPedido) > 0 ) {
                if (currentFragment instanceof ClienteFragment){
                    Bundle bundle = new Bundle();
                    bundle.putString("CODIGO_PEDIDO", codPedido);
                    currentFragment.setArguments(bundle);
                }
                getIntent().removeExtra("CODIGO_PEDIDO");
                codPedido = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


}