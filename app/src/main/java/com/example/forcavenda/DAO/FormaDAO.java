package com.example.forcavenda.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.forcavenda.Connection.SQLiteHelper;
import com.example.forcavenda.Connection.SQLConnectionExt;

import com.example.forcavenda.Model.Forma;
import com.example.forcavenda.Connection.SQLConnection;
import com.example.forcavenda.Model.Vendedor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class FormaDAO {

    private SQLiteHelper.DatabaseHelper dbHelper;
    public FormaDAO(Context context) {

        dbHelper = new SQLiteHelper.DatabaseHelper(context);
    }

    public boolean baixaForma(){

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

            String deleteSql = "Delete from FORMA";
            bancoDados.execSQL(deleteSql);

            Connection conn = SQLConnection.conectar(ip);
            Forma forma = null;
            if(conn != null){
                String sql = "Select Codigo, Descricao from Condpgto where Venda = 1 and Codigo > 0";

                Statement st = null;
                try {
                    st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        int codigo = rs.getInt("Codigo");
                        String desc = rs.getString("Descricao");

                        // Insira os dados no banco SQLite
                        String insertSql = "INSERT INTO FORMA(Codigo, Descricao) VALUES (" + codigo + ", '" + desc + "')";
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


    public boolean baixaFormaExt(){

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

            String deleteSql = "Delete from FORMA";
            bancoDados.execSQL(deleteSql);

            Connection conn = SQLConnectionExt.conectar(ip);
            Forma forma = null;
            if(conn != null){
                String sql = "Select Codigo, Descricao from Condpgto where Venda = 1 and Codigo > 0";

                Statement st = null;
                try {
                    st = conn.createStatement();
                    ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        int codigo = rs.getInt("Codigo");
                        String desc = rs.getString("Descricao");

                        // Insira os dados no banco SQLite
                        String insertSql = "INSERT INTO FORMA(Codigo, Descricao) VALUES (" + codigo + ", '" + desc + "')";
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

    public List<Forma> obterForma() {
        List<Forma> formas = new ArrayList<>();

        // Obtenha um banco de dados legível para consulta
        SQLiteDatabase bancoDados = dbHelper.getReadableDatabase();

        // Especifique as colunas a serem retornadas na consulta
        String[] colunas = {"Codigo", "Descricao"};

        // Especifique a tabela a ser consultada
        String tabela = "FORMA";

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
                Forma forma = new Forma(codigo, descricao);
                formas.add(forma);
            }

            // Feche o Cursor após usar
            cursor.close();
        }

        // Retorne a lista de vendedores
        return formas;
    }


    public Forma getFormaByCode(int code) {
        SQLiteDatabase bancoDados = dbHelper.getReadableDatabase();

        String[] colunas = {"Codigo", "Descricao"};
        String tabela = "FORMA";
        String whereClause = "Codigo = ?";
        String[] whereArgs = {String.valueOf(code)};

        Cursor cursor = bancoDados.query(tabela, colunas, whereClause, whereArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int codigoIndex = cursor.getColumnIndex("Codigo");
            int descricaoIndex = cursor.getColumnIndex("Descricao");

            int codigo = cursor.getInt(codigoIndex);
            String descricao = cursor.getString(descricaoIndex);

            cursor.close();

            return new Forma(codigo, descricao);
        }

        return null;
    }

    public Integer getCodFormaByDesc(String desc) {
        SQLiteDatabase bancoDados = dbHelper.getReadableDatabase();

        String[] colunas = {"Codigo", "Descricao"};
        String tabela = "FORMA";
        String whereClause = "Descricao = ?";
        String[] whereArgs = {desc};

        Cursor cursor = null;
        int codigo = -1; // Valor padrão caso não seja encontrado

        try {
            cursor = bancoDados.query(tabela, colunas, whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int codigoIndex = cursor.getColumnIndex("Codigo");
                codigo = cursor.getInt(codigoIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return codigo;
    }

}
