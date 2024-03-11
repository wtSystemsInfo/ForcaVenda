package com.example.forcavenda.DAO;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.forcavenda.Connection.SQLConnectionExt;
import com.example.forcavenda.Connection.SQLiteHelper;
import com.example.forcavenda.Model.Vendedor;
import com.example.forcavenda.Connection.SQLConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VendedorDAO {

    private SQLiteHelper.DatabaseHelper dbHelper;

    public VendedorDAO(Context context) {

        dbHelper = new SQLiteHelper.DatabaseHelper(context);
    }
    public void baixaVendedor(String ip){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();

        if (bancoDados != null && bancoDados.isOpen()) {
            Log.d("SQLiteDatabase", "Banco de dados aberto com sucesso.");

            String deleteSql = "Delete from VENDEDOR";
            bancoDados.execSQL(deleteSql);

            Connection conn = SQLConnection.conectar(ip);
            Vendedor vendedor = null;
            if(conn != null){
                String sql = "Select Codigo, Nome from Vendedor where Ativo = 1";

                Statement st = null;
                try {
                    st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        int codigo = rs.getInt("Codigo");
                        String nome = rs.getString("Nome");

                        // Insira os dados no banco SQLite
                        String insertSql = "INSERT INTO VENDEDOR(Codigo, Descricao) VALUES (" + codigo + ", '" + nome + "')";
                        bancoDados.execSQL(insertSql);

                    }
                    rs.close();
                    st.close();


                } catch (SQLException e) {
                    // Trate a exceção adequadamente
                    e.printStackTrace();
                }
            }
            bancoDados.close();
        } else {
            Log.d("SQLiteDatabase", "Erro ao abrir o banco de dados.");
        }
    }


    public void baixaVendedorExt(String ip){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();

        if (bancoDados != null && bancoDados.isOpen()) {
            Log.d("SQLiteDatabase", "Banco de dados aberto com sucesso.");

            String deleteSql = "Delete from VENDEDOR";
            bancoDados.execSQL(deleteSql);

            Connection conn = SQLConnectionExt.conectar(ip);
            Vendedor vendedor = null;
            if(conn != null){
                String sql = "Select Codigo, Nome from Vendedor where Ativo = 1";

                Statement st = null;
                try {
                    st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        int codigo = rs.getInt("Codigo");
                        String nome = rs.getString("Nome");

                        // Insira os dados no banco SQLite
                        String insertSql = "INSERT INTO VENDEDOR(Codigo, Descricao) VALUES (" + codigo + ", '" + nome + "')";
                        bancoDados.execSQL(insertSql);

                    }
                    rs.close();
                    st.close();


                } catch (SQLException e) {
                    // Trate a exceção adequadamente
                    e.printStackTrace();
                }
            }
            bancoDados.close();
        } else {
            Log.d("SQLiteDatabase", "Erro ao abrir o banco de dados.");
        }
    }




    public List<Vendedor> obterVendedores() {
        List<Vendedor> vendedores = new ArrayList<>();

        // Obtenha um banco de dados legível para consulta
        SQLiteDatabase bancoDados = dbHelper.getReadableDatabase();

        // Especifique as colunas a serem retornadas na consulta
        String[] colunas = {"Codigo", "Descricao"};

        // Especifique a tabela a ser consultada
        String tabela = "VENDEDOR";

        // Execute a consulta e obtenha um Cursor para os resultados
        Cursor cursor = bancoDados.query(tabela, colunas, null, null, null, null, null);

        // Verifique se o Cursor não é nulo
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Obtenha os valores das colunas corretas "Codigo" e "Descricao" do Cursor
                int codigoIndex = cursor.getColumnIndex("Codigo");
                int codigo = cursor.getInt(codigoIndex);

                int descricaoIndex = cursor.getColumnIndex("Descricao");
                String descricao = cursor.getString(descricaoIndex);

                // Crie um objeto Vendedor com os valores obtidos e adicione à lista
                Vendedor vendedor = new Vendedor(codigo, descricao);
                vendedores.add(vendedor);
            }

            // Feche o Cursor após usar
            cursor.close();
        }

        // Retorne a lista de vendedores
        return vendedores;
    }

    public Vendedor getVendedorByCode(int code) {
        SQLiteDatabase bancoDados = dbHelper.getReadableDatabase(); // Substitua dbHelper pelo seu SQLiteOpenHelper

        String[] colunas = {"Codigo", "Descricao"};
        String tabela = "VENDEDOR";
        String whereClause = "Codigo = ?";
        String[] whereArgs = {String.valueOf(code)};

        Cursor cursor = bancoDados.query(tabela, colunas, whereClause, whereArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int codigoIndex = cursor.getColumnIndex("Codigo");
            int descricaoIndex = cursor.getColumnIndex("Descricao");

            int codigo = cursor.getInt(codigoIndex);
            String descricao = cursor.getString(descricaoIndex);

            cursor.close();

            return new Vendedor(codigo, descricao);
        }

        return null;
    }


}
