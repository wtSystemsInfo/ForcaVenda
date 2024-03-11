package com.example.forcavenda.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.forcavenda.Connection.SQLConnection;
import com.example.forcavenda.Connection.SQLConnectionExt;
import com.example.forcavenda.Connection.SQLiteHelper;
import com.example.forcavenda.Model.ItemPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemPedidoDAO {

    private SQLiteHelper.DatabaseHelper dbHelper;

    public ItemPedidoDAO(Context context) {

        dbHelper = new SQLiteHelper.DatabaseHelper(context);
    }


    public void salvarProdPed(List<ItemPedido> listaItemPedido, Integer codigoPedido){
        SQLiteDatabase bancoDados = dbHelper.getReadableDatabase();

        try {
            for (ItemPedido itemPedido : listaItemPedido) {
                ContentValues valores = new ContentValues();
                valores.put("CodPedido", codigoPedido);
                valores.put("CodProduto", itemPedido.getCodProduto());
                valores.put("Qtd", itemPedido.getProdutoQtde());
                valores.put("Vlrunit", itemPedido.getProdutoVlr());
                valores.put("VlrDesc", 0);
                valores.put("SubTot", itemPedido.getProdutoSub());
                valores.put("Enviado", 0);
                // Insira os valores na tabela
                bancoDados.insert("PEDIDOPRODUTO", null, valores);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            bancoDados.close(); // Certifique-se de fechar o banco de dados após a inserção
        }
    }

    public boolean exportaItemPedidoInt(Integer codPrePedCel, String codPedInt){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();

        String CodVendedor=null;
        String ip = null;

        if (bancoDados != null && bancoDados.isOpen()) {
            String[] Col = {"RedeInt"};
            String Tabela = "CONFIG";
            String whereClause = null;
            String[] whereArgs = null;

            Cursor cursorItem = bancoDados.query(Tabela, Col, whereClause, whereArgs, null, null, null);
            if (cursorItem != null && cursorItem.moveToFirst()) {
                int redIntIndex = cursorItem.getColumnIndex("RedeInt");
                ip = String.valueOf(cursorItem.getString(redIntIndex));
                cursorItem.close(); // Feche o cursor após obter o valor máximo
            }
            Connection conn = SQLConnection.conectar(ip);
            if(conn != null){
                PreparedStatement pst = null;
                String sql = "INSERT INTO PrePedidoCelularProduto (CodPedExt, CodPrePedidoCelular, CodProduto, Qtde, ValorUnit) " +
                        " VALUES ( ?, ?, ?, ?, ?) ";
                String consultaSql = "SELECT PEDIDOPRODUTO.CodPedido, PEDIDOPRODUTO.CodProduto, PEDIDOPRODUTO.Qtd, PEDIDOPRODUTO.Vlrunit, PEDIDOPRODUTO.Enviado " +
                        "FROM PEDIDOPRODUTO " +
                        "INNER JOIN PEDIDO ON PEDIDOPRODUTO.CodPedido = PEDIDO.Codigo " +
                        "WHERE PEDIDO.Enviado = ? and PEDIDO.Codigo = ? and PEDIDOPRODUTO.Enviado = ?";

                whereArgs = new String[]{"0", codPedInt, "0"};
                Cursor cursorPedido = bancoDados.rawQuery(consultaSql, whereArgs);
                if (cursorPedido != null) {
                    while(cursorPedido.moveToNext()){
                        int codPedIndex = cursorPedido.getColumnIndex("CodPedido");
                        String codPed = String.valueOf(cursorPedido.getInt(codPedIndex));

                        int codProdIndex = cursorPedido.getColumnIndex("CodProduto");
                        String codProd = String.valueOf(cursorPedido.getInt(codProdIndex));

                        int qtdeIndex = cursorPedido.getColumnIndex("Qtd");
                        Double qtde = cursorPedido.getDouble(qtdeIndex);

                        int vlrUnitIndex = cursorPedido.getColumnIndex("Vlrunit");
                        Double vlrUnit = cursorPedido.getDouble(vlrUnitIndex);


                        try{
                            pst = conn.prepareStatement(sql);
                            pst.setString(1, codPed);
                            pst.setInt(2, codPrePedCel);
                            pst.setString(3, codProd);
                            pst.setDouble(4, qtde);
                            pst.setDouble(5, vlrUnitIndex);

                            int rowsInserted = pst.executeUpdate();

                                if (rowsInserted > 0) {
                                    // A inserção foi bem-sucedida, agora vamos atualizar o valor 'Enviado' em PEDIDOPRODUTO para 1.
                                    String updateSql = "UPDATE PEDIDOPRODUTO SET Enviado = 1 WHERE CodPedido = ? and CodProduto = ?";
                                    PreparedStatement updatePst = conn.prepareStatement(updateSql);
                                    updatePst.setString(1, codPed);
                                    updatePst.setString(2, codProd);

                                    updatePst.executeUpdate();
                                    updatePst.close();
                                }

                            }catch (java.sql.SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                            return false;
                        }


                    }
                }else {
                    return false;
                }
                cursorItem.close(); // Feche o cursor após obter o valor máximo
            }else{
                return false;
            }

        }

        return true;

    }

    public boolean exportaItemPedidoExt(Integer codPrePedCel, String codPedInt){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();

        String CodVendedor=null;
        String ip = null;

        if (bancoDados != null && bancoDados.isOpen()) {
            String[] Col = {"RedeExt"};
            String Tabela = "CONFIG";
            String whereClause = null;
            String[] whereArgs = null;

            Cursor cursorItem = bancoDados.query(Tabela, Col, whereClause, whereArgs, null, null, null);
            if (cursorItem != null && cursorItem.moveToFirst()) {
                int redIntIndex = cursorItem.getColumnIndex("RedeExt");
                ip = String.valueOf(cursorItem.getString(redIntIndex));
                cursorItem.close(); // Feche o cursor após obter o valor máximo
            }
            Connection conn = SQLConnectionExt.conectar(ip);
            if(conn != null){
                PreparedStatement pst = null;
                String sql = "INSERT INTO PrePedidoCelularProduto (CodPedExt, CodPrePedidoCelular, CodProduto, Qtde, ValorUnit) " +
                        " VALUES ( ?, ?, ?, ?, ?) ";
                String consultaSql = "SELECT PEDIDOPRODUTO.CodPedido, PEDIDOPRODUTO.CodProduto, PEDIDOPRODUTO.Qtd, PEDIDOPRODUTO.Vlrunit , PEDIDOPRODUTO.Enviado " +
                        "FROM PEDIDOPRODUTO " +
                        "INNER JOIN PEDIDO ON PEDIDOPRODUTO.CodPedido = PEDIDO.Codigo " +
                        "WHERE PEDIDO.Enviado = ? and PEDIDO.Codigo = ? and PEDIDOPRODUTO.Enviado = ?";

                whereArgs = new String[]{"0", codPedInt, "0" };
                Cursor cursorPedido = bancoDados.rawQuery(consultaSql, whereArgs);
                if (cursorPedido != null) {
                    while(cursorPedido.moveToNext()){
                        int codPedIndex = cursorPedido.getColumnIndex("CodPedido");
                        String codPed = String.valueOf(cursorPedido.getInt(codPedIndex));

                        int codProdIndex = cursorPedido.getColumnIndex("CodProduto");
                        String codProd = String.valueOf(cursorPedido.getInt(codProdIndex));

                        int qtdeIndex = cursorPedido.getColumnIndex("Qtd");
                        Double qtde = cursorPedido.getDouble(qtdeIndex);

                        int vlrUnitIndex = cursorPedido.getColumnIndex("Vlrunit");
                        Double vlrUnit = cursorPedido.getDouble(vlrUnitIndex);


                        try{
                            pst = conn.prepareStatement(sql);
                            pst.setString(1, codPed);
                            pst.setInt(2, codPrePedCel);
                            pst.setString(3, codProd);
                            pst.setDouble(4, qtde);
                            pst.setDouble(5, vlrUnit);

                            int rowsInserted = pst.executeUpdate();

                                if (rowsInserted > 0) {
                                    // A inserção foi bem-sucedida, agora vamos atualizar o valor 'Enviado' em PEDIDOPRODUTO para 1.
                                    ContentValues values = new ContentValues();
                                    values.put("Enviado", 1);  // Defina o valor 'Enviado' como 1

                                    // Execute a atualização na tabela PEDIDOPRODUTO
                                    bancoDados.update("PEDIDOPRODUTO", values, "CodPedido = ? and CodProduto = ?", new String[]{codPed, codProd});

                                }

                        }catch (java.sql.SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                            return false;
                        }


                    }
                }else {
                    return false;
                }
                cursorItem.close(); // Feche o cursor após obter o valor máximo
            }else{
                return false;
            }

        }

        return true;

    }

    public List<ItemPedido> carregarListaProd(Integer pedido){
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        List<ItemPedido> listaItemPedido = new ArrayList<>();

        if (bancoDados != null && bancoDados.isOpen()) {
            String consultaSql = "SELECT PEDIDOPRODUTO.CodProduto, PRODUTO.Descricao as Produto, PEDIDOPRODUTO.Qtd, PEDIDOPRODUTO.Vlrunit, PEDIDOPRODUTO.SubTot" +
                    " FROM PEDIDOPRODUTO " +
                    " INNER JOIN PRODUTO on PEDIDOPRODUTO.CodProduto = PRODUTO.Codigo " +
                    " WHERE PEDIDOPRODUTO.CodPedido = ?" ;

            String[] whereArgs = new String[]{pedido.toString()};
            Cursor cursor = bancoDados.rawQuery(consultaSql, whereArgs);
            if(cursor != null && cursor.moveToFirst()) {

                do{

                    ItemPedido itemPedido = new ItemPedido();
                    int codProdutoIndex = cursor.getColumnIndex("CodProduto");
                    if (!cursor.isNull(codProdutoIndex)) {
                        Integer codProduto = cursor.getInt(codProdutoIndex);
                        itemPedido.setCodProduto(codProduto);
                    }

                    int produtoIndex = cursor.getColumnIndex("Produto");
                    if (!cursor.isNull(produtoIndex)) {
                        String produto = cursor.getString(produtoIndex);
                        itemPedido.setProdutoDesc(produto);
                    }

                    int produtoQtdIndex = cursor.getColumnIndex("Qtd");
                    if (!cursor.isNull(produtoQtdIndex)) {
                        Double produtoQtde = cursor.getDouble(produtoQtdIndex);
                        itemPedido.setProdutoQtde(produtoQtde);
                    }

                    int produtoVlrIndex = cursor.getColumnIndex("Vlrunit");
                    if (!cursor.isNull(produtoVlrIndex)) {
                        Double produtoVlr = cursor.getDouble(produtoVlrIndex);
                        itemPedido.setProdutoVlr(produtoVlr);
                    }

                    int produtoSubIndex = cursor.getColumnIndex("SubTot");
                    if (!cursor.isNull(produtoSubIndex)) {
                        Double produtoSub = cursor.getDouble(produtoSubIndex);
                        itemPedido.setProdutoSub(produtoSub);
                    }

                    listaItemPedido.add(itemPedido);
                    itemPedido = null;

                    cursor.moveToNext();

                }while((cursor.isLast()));

            }
        }

        return listaItemPedido;
    }

}
