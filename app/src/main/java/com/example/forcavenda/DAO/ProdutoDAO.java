package com.example.forcavenda.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.forcavenda.Connection.SQLConnection;
import com.example.forcavenda.Connection.SQLConnectionExt;
import com.example.forcavenda.Connection.SQLiteHelper;
import com.example.forcavenda.Model.Cliente;
import com.example.forcavenda.Model.Produto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class ProdutoDAO {

    private SQLiteHelper.DatabaseHelper dbHelper;

    private Context mContext;
    public ProdutoDAO(Context context) {

        mContext = context;
        dbHelper = new SQLiteHelper.DatabaseHelper(context);
    }

    public boolean baixaProduto(){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();


        String ip = null;
        if (bancoDados != null && bancoDados.isOpen())
        {
            String[] Col = {"RedeInt"};
            String Tabela = "CONFIG";

            Cursor cursorIP = bancoDados.query(Tabela, Col, null, null, null, null, null);
            if (cursorIP != null && cursorIP.moveToFirst()) {
                int redIntIndex = cursorIP.getColumnIndex("RedeInt");
                ip = String.valueOf(cursorIP.getString(redIntIndex));
                cursorIP.close(); // Feche o cursor após obter o valor máximo
            }
        }



        if (bancoDados != null && bancoDados.isOpen()) {
            Log.d("SQLiteDatabase", "Banco de dados aberto com sucesso.");

            String deleteSql = "DROP TABLE IF EXISTS PRODUTO";
            bancoDados.execSQL(deleteSql);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS PRODUTO (Codigo Integer NOT NULL PRIMARY KEY, Descricao VARCHAR(100), Valor Double, Estoque Double)");

            Connection conn = SQLConnection.conectar(ip);
            if(conn != null){
                String sql = "Select Produto.Codigo, Produto.Descricao, Produto.PrecoVenda, vEstoque.Estoque as Estoque from Produto " +
                       "INNER JOIN vEstoque on vEstoque.Codigo = Produto.Codigo  Where Produto.Ativo = 'S'" ;

                Statement st = null;
                try {
                    st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        int codigo = rs.getInt("Codigo");
                        String desc = rs.getString("Descricao");
                        desc = desc.replace("'", "''");
                        double preco = rs.getDouble("PrecoVenda");
                        double estoque = rs.getDouble("Estoque");

                        // Insira os dados no banco SQLite
                        String insertSql = "INSERT INTO PRODUTO(Codigo, Descricao, Valor, Estoque) VALUES (" + codigo + ", '" + desc + "', " + preco + ", " + estoque + " )";
                        bancoDados.execSQL(insertSql);

                    }
                    rs.close();
                    st.close();


                } catch (SQLException e) {
                    // Trate a exceção adequadamente
                    e.printStackTrace();
                    return false;
                }
            }else{
                return false;
            }
            bancoDados.close();
        } else {
            Log.d("SQLiteDatabase", "Erro ao abrir o banco de dados.");
            return false;
        }
        return true;
    }

    public boolean baixaProdutoExt(){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();


        String ip = null;
        if (bancoDados != null && bancoDados.isOpen())
        {
            String[] Col = {"RedeExt"};
            String Tabela = "CONFIG";

            Cursor cursorIP = bancoDados.query(Tabela, Col, null, null, null, null, null);
            if (cursorIP != null && cursorIP.moveToFirst()) {
                int redIntIndex = cursorIP.getColumnIndex("RedeExt");
                ip = String.valueOf(cursorIP.getString(redIntIndex));
                cursorIP.close(); // Feche o cursor após obter o valor máximo
            }
        }



        if (bancoDados != null && bancoDados.isOpen()) {
            Log.d("SQLiteDatabase", "Banco de dados aberto com sucesso.");

            String deleteSql = "DROP TABLE IF EXISTS PRODUTO";
            bancoDados.execSQL(deleteSql);

            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS PRODUTO (Codigo Integer NOT NULL PRIMARY KEY, Descricao VARCHAR(100), Valor Double, Estoque Double)");


            Connection conn = SQLConnectionExt.conectar(ip);
            if(conn != null){
                String sql = "Select Produto.Codigo, Produto.Descricao, Produto.PrecoVenda, vEstoque.Estoque as Estoque from Produto " +
                        "INNER JOIN vEstoque on vEstoque.Codigo = Produto.Codigo Where Produto.Ativo = 'S'" ;

                Statement st = null;
                try {
                    st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        int codigo = rs.getInt("Codigo");
                        String desc = rs.getString("Descricao");
                        desc = desc.replace("'", "''");
                        double preco = rs.getDouble("PrecoVenda");
                        double estoque = rs.getDouble("Estoque");

                        // Insira os dados no banco SQLite
                        String insertSql = "INSERT INTO PRODUTO(Codigo, Descricao, Valor, Estoque) VALUES (" + codigo + ", '" + desc + "', " + preco + ", " + estoque + " )";
                        bancoDados.execSQL(insertSql);

                    }
                    rs.close();
                    st.close();


                } catch (SQLException e) {
                    // Trate a exceção adequadamente
                    e.printStackTrace();
                    return false;
                }
            }
            bancoDados.close();
        } else {
            Log.d("SQLiteDatabase", "Erro ao abrir o banco de dados.");
            return false;
        }
        return true;
    }


    public List<Produto> pesquisaProd(String produto) {
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Produto prodPesq = null;
        List<Produto> listaProdutos = new ArrayList<>();


        if (bancoDados != null && bancoDados.isOpen()) {


            String[] prodColunas = {"Codigo", "Descricao", "Valor"};
            String prodTabela = "PRODUTO";
            String whereClause = "Descricao LIKE ?";
            String[] whereArgs = new String[]{"%" + produto + "%"};

            Cursor cursor = bancoDados.query(prodTabela, prodColunas, whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    prodPesq = new Produto();
                    int codProdutoIndex = cursor.getColumnIndex("Codigo");
                    int codProduto =  cursor.getInt(codProdutoIndex);
                    prodPesq.setCodigo(codProduto);
                    int razaoProdutoIndex = cursor.getColumnIndex("Descricao");
                    String descProduto =  cursor.getString(razaoProdutoIndex);
                    prodPesq.setDescricao(descProduto);
                    int valorProdutoIndex = cursor.getColumnIndex("Valor");
                    double valorProduto =  cursor.getDouble(valorProdutoIndex);
                    prodPesq.setValor(valorProduto);
                    listaProdutos.add(prodPesq);
                }while (cursor.moveToNext());
            }
        }

        return listaProdutos;
    }

}
