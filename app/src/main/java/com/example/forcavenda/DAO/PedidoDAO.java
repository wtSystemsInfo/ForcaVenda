package com.example.forcavenda.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.forcavenda.Connection.SQLConnection;
import com.example.forcavenda.Connection.SQLConnectionExt;
import com.example.forcavenda.Connection.SQLiteHelper;
import com.example.forcavenda.Model.Cliente;
import com.example.forcavenda.Model.Pedido;
import com.example.forcavenda.Model.PedidoPesquisa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PedidoDAO {
    private SQLiteHelper.DatabaseHelper dbHelper;

    public PedidoDAO(Context context) {

        dbHelper = new SQLiteHelper.DatabaseHelper(context);
    }

    public void salvaPedido(int CodCliente, String DataPed, int EnviadoPed, String ObsPed, int CodFormPgto, double TotalPed) {
        SQLiteDatabase bancoDados = dbHelper.getReadableDatabase();


        try {
            // Insira os dados no banco SQLite
            String insertSql;
            String insertSql1 = "INSERT INTO PEDIDO(CodCli, Data, Enviado, CodCondPgto, TotalPed";
            String insertSql2 = "VALUES(" + CodCliente + ", '" + DataPed + "', " + EnviadoPed + ", " + CodFormPgto + ", " + TotalPed;
            if (!ObsPed.isEmpty()) {
                insertSql1 = insertSql1 + ", Obs";
                insertSql2 = insertSql2 + ", '" + ObsPed + "'";
            }
            insertSql1 = insertSql1 + ") ";
            insertSql2 = insertSql2 + ") ";
            insertSql = insertSql1 + insertSql2;
            bancoDados.execSQL(insertSql);
        } catch (SQLException e) {
            // Trate a exceção adequadamente
            e.printStackTrace();
        }

    }


    public void updatePedido(int codPedido, int CodCliente, String DataPed, String ObsPed, int CodFormPgto, double TotalPed) {
        SQLiteDatabase bancoDados = dbHelper.getReadableDatabase();

        try {
            // Insira os dados no banco SQLite
            String insertSql;
            insertSql = "UPDATE PEDIDO SET CodCli = ?, CodCondPgto = ?, TotalPed = ?";
            ArrayList<Object> args = new ArrayList<>();
            args.add(CodCliente);
            args.add(CodFormPgto);
            args.add(TotalPed);
            if (!ObsPed.isEmpty()) {
                insertSql = insertSql + ", Obs = ?";
                args.add(ObsPed);
            }
            insertSql = insertSql + " WHERE Codigo = ?";
            args.add(codPedido);
            bancoDados.execSQL(insertSql, args.toArray());
            bancoDados.close();
        } catch (SQLException e) {
            // Trate a exceção adequadamente
            e.printStackTrace();
        }

    }


    public int retornaUltimoPed(){
        int codigoPedido = 0;

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();

        if (bancoDados != null && bancoDados.isOpen()) {


            String[] prodColunas = {"MAX(Codigo) AS Codigo"};
            String prodTabela = "PEDIDO";
            String whereClause = null;
            String[] whereArgs = null;

            Cursor cursor = bancoDados.query(prodTabela, prodColunas, whereClause, whereArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int codPedidoIndex = cursor.getColumnIndex("Codigo");
                codigoPedido = cursor.getInt(codPedidoIndex);
                cursor.close(); // Feche o cursor após obter o valor máximo
            }

        }

        return codigoPedido;
    }


    public boolean exportaPedidoInt(Context context){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat simpleData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataAtual = simpleData.format(currentDate);
        String CodVendedor=null;
        String ip = null;

        if (bancoDados != null && bancoDados.isOpen()) {
            String[] Col = {"CodVendedor", "RedeInt"};
            String Tabela = "CONFIG";
            String whereClause = null;
            String[] whereArgs = null;

            Cursor cursorVendedor = bancoDados.query(Tabela, Col, whereClause, whereArgs, null, null, null);
            if (cursorVendedor != null && cursorVendedor.moveToFirst()) {
                int codVendIndex = cursorVendedor.getColumnIndex("CodVendedor");
                CodVendedor = String.valueOf(cursorVendedor.getInt(codVendIndex));
                int redExtIndex = cursorVendedor.getColumnIndex("RedeInt");
                ip = String.valueOf(cursorVendedor.getString(redExtIndex));
                cursorVendedor.close(); // Feche o cursor após obter o valor máximo
            }
            Connection conn = SQLConnection.conectar(ip);
            if(conn != null){
                PreparedStatement pst = null;
                String sql = "INSERT INTO PrePedidoCelular (CodVendedor, CodPedidoExt, CodClienteExt, CodPreCliente, codCondPgto, Data, TotalFinal, " +
                        "Obs, DataInclusao, EnvioCompleto) " +
                        " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
                String posSql = "Select MAX(Codigo) as Codigo from PrePedidoCelular";
                String[] Col2 = {"Codigo", "CodCli", "CodCondPgto", "Data", "Obs", "TotalPed"};
                String Tabela2 = "PEDIDO";
                whereClause = "Enviado = ?";
                whereArgs = new String[]{"0"};
                Cursor cursorPedido = bancoDados.query(Tabela2, Col2, whereClause, whereArgs, null, null, null);
                if (cursorPedido != null) {
                    while(cursorPedido.moveToNext()){
                        int codPedIndex = cursorPedido.getColumnIndex("Codigo");
                        String codPed = String.valueOf(cursorPedido.getInt(codPedIndex));

                        int codCliIndex = cursorPedido.getColumnIndex("CodCli");
                        String codCli = String.valueOf(cursorPedido.getInt(codCliIndex));

                        int codCondPgtoIndex = cursorPedido.getColumnIndex("CodCondPgto");
                        String condPgto = String.valueOf(cursorPedido.getInt(codCondPgtoIndex));

                        int dataIndex = cursorPedido.getColumnIndex("Data");
                        String dataString = cursorPedido.getString(dataIndex);

                        // Crie um SimpleDateFormat para o formato de entrada (dd/MM/yyyy)
                        SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");

                        // Crie um SimpleDateFormat para o formato de saída (yyyy/MM/dd)
                        SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy/MM/dd");

                        try {
                            Date data = formatoEntrada.parse(dataString); // Converte a string para um objeto Date
                            dataString = formatoSaida.format(data); // Formata a data no formato desejado

                            // Agora 'dataFormatada' contém a data no formato "yyyy/MM/dd"
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        int obsIndex = cursorPedido.getColumnIndex("Obs");
                        String obs = cursorPedido.isNull(obsIndex) ? null : cursorPedido.getString(obsIndex);

                        int totalIndex = cursorPedido.getColumnIndex("TotalPed");
                        Double total = cursorPedido.getDouble(totalIndex);

                        String codPreCli = null;


                        String sqlaux = "Select Codigo from PreCliente where CodClienteExt = " + codCli + " and Codvendedor = " + CodVendedor ;
                        Statement staux = null;
                        try{
                            staux = conn.createStatement();
                            ResultSet rs = staux.executeQuery(sqlaux);
                            while (rs.next()) {
                                codPreCli = rs.getString("Codigo");
                            }
                        }catch (SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                        } catch (java.sql.SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try{
                            pst = conn.prepareStatement(sql);
                            pst.setString(1, CodVendedor);
                            pst.setString(2, codPed);
                            pst.setString(3, codCli);
                            pst.setString(4, codPreCli);
                            pst.setString(5, condPgto);
                            pst.setString(6, dataString);
                            pst.setDouble(7, total);
                            pst.setString(8, obs);
                            pst.setString(9, dataAtual);
                            pst.setString(10, "S");
                            pst.executeUpdate();


                        }catch (java.sql.SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                            return false;
                        }

                        String codPrePed = null;
                        Statement st = null;
                        try{
                            staux = conn.createStatement();
                            ResultSet rsPos = staux.executeQuery(posSql);
                            while (rsPos.next()) {
                                codPrePed = rsPos.getString("Codigo");
                            }
                        }catch (SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                        } catch (java.sql.SQLException e) {
                            throw new RuntimeException(e);
                        }

                        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(context);
                        if(!itemPedidoDAO.exportaItemPedidoInt(Integer.valueOf(codPrePed), codPed)){
                            return false;
                        }

                    }

                }else {
                    return false;
                }
                cursorPedido.close(); // Feche o cursor após obter o valor máximo
            }else{
                return false;
            }
            ContentValues valores = new ContentValues();
            valores.put("Enviado", 1);
            bancoDados.update("PEDIDO", valores, null, null);

        }
        return true;

    }

    public boolean exportaPedidoExtOLD(Context context){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat simpleData = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String dataAtual = simpleData.format(currentDate);
        String CodVendedor=null;
        String ip = null;

        if (bancoDados != null && bancoDados.isOpen()) {
            String[] Col = {"CodVendedor", "RedeExt"};
            String Tabela = "CONFIG";
            String whereClause = null;
            String[] whereArgs = null;

            Cursor cursorVendedor = bancoDados.query(Tabela, Col, whereClause, whereArgs, null, null, null);
            if (cursorVendedor != null && cursorVendedor.moveToFirst()) {
                int codVendIndex = cursorVendedor.getColumnIndex("CodVendedor");
                CodVendedor = String.valueOf(cursorVendedor.getInt(codVendIndex));
                int redExtIndex = cursorVendedor.getColumnIndex("RedeExt");
                ip = String.valueOf(cursorVendedor.getString(redExtIndex));
                cursorVendedor.close(); // Feche o cursor após obter o valor máximo
            }
            Connection conn = SQLConnectionExt.conectar(ip);
            if(conn != null){
                PreparedStatement pst = null;
                String sql = "INSERT INTO PrePedidoCelular (CodVendedor, CodPedidoExt, CodClienteExt, CodPreCliente, codCondPgto, Data, TotalFinal, " +
                        "Obs, DataInclusao, EnvioCompleto) " +
                        " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
                String posSql = "Select MAX(Codigo) as Codigo from PrePedidoCelular";
                String[] Col2 = {"Codigo", "CodCli", "CodCondPgto", "Data", "Obs", "TotalPed"};
                String Tabela2 = "PEDIDO";
                whereClause = "Enviado = ?";
                whereArgs = new String[]{"0"};
                Cursor cursorPedido = bancoDados.query(Tabela2, Col2, whereClause, whereArgs, null, null, null);
                if (cursorPedido != null) {
                    while(cursorPedido.moveToNext()){
                        int codPedIndex = cursorPedido.getColumnIndex("Codigo");
                        String codPed = String.valueOf(cursorPedido.getInt(codPedIndex));

                        int codCliIndex = cursorPedido.getColumnIndex("CodCli");
                        String codCli = String.valueOf(cursorPedido.getInt(codCliIndex));

                        int codCondPgtoIndex = cursorPedido.getColumnIndex("CodCondPgto");
                        String condPgto = String.valueOf(cursorPedido.getInt(codCondPgtoIndex));

                        int dataIndex = cursorPedido.getColumnIndex("Data");
                        String dataString = cursorPedido.getString(dataIndex);

                        // Crie um SimpleDateFormat para o formato de entrada (dd/MM/yyyy)
                        SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");

                        // Crie um SimpleDateFormat para o formato de saída (yyyy/MM/dd)
                        SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy/MM/dd");

                        try {
                            Date data = formatoEntrada.parse(dataString); // Converte a string para um objeto Date
                            dataString = formatoSaida.format(data); // Formata a data no formato desejado

                            // Agora 'dataFormatada' contém a data no formato "yyyy/MM/dd"
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        int obsIndex = cursorPedido.getColumnIndex("Obs");
                        String obs = cursorPedido.isNull(obsIndex) ? null : cursorPedido.getString(obsIndex);

                        int totalIndex = cursorPedido.getColumnIndex("TotalPed");
                        Double total = cursorPedido.getDouble(totalIndex);

                        String codPreCli = null;


                        String sqlaux = "Select Codigo from PreCliente where CodClienteExt = " + codCli + " and Codvendedor = " + CodVendedor ;
                        Statement staux = null;
                        try{
                            staux = conn.createStatement();
                            ResultSet rs = staux.executeQuery(sqlaux);
                            while (rs.next()) {
                                codPreCli = rs.getString("Codigo");
                            }
                        }catch (SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                        } catch (java.sql.SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try{
                            pst = conn.prepareStatement(sql);
                            pst.setString(1, CodVendedor);
                            pst.setString(2, codPed);
                            pst.setString(3, codCli);
                            pst.setString(4, codPreCli);
                            pst.setString(5, condPgto);
                            pst.setString(6, dataString);
                            pst.setDouble(7, total);
                            pst.setString(8, obs);
                            pst.setString(9, dataAtual);
                            pst.setString(10, "S");
                            pst.executeUpdate();


                        }catch (java.sql.SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                            return false;
                        }

                        String codPrePed = null;
                        Statement st = null;
                        try{
                            staux = conn.createStatement();
                            ResultSet rsPos = staux.executeQuery(posSql);
                            while (rsPos.next()) {
                                codPrePed = rsPos.getString("Codigo");
                            }
                        }catch (SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                        } catch (java.sql.SQLException e) {
                            throw new RuntimeException(e);
                        }

                        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(context);
                        if(!itemPedidoDAO.exportaItemPedidoExt(Integer.valueOf(codPrePed), codPed)){
                            return false;
                        }

                    }

                }else {
                    return false;
                }
                cursorPedido.close(); // Feche o cursor após obter o valor máximo
            }else{
                return false;
            }
            ContentValues valores = new ContentValues();
            valores.put("Enviado", 1);
            bancoDados.update("PEDIDO", valores, null, null);

        }
        return true;

    }



    public int exportaPedidoExt(Context context){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat simpleData = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String dataAtual = simpleData.format(currentDate);
        String CodVendedor=null;
        String ip = null;

        if (bancoDados != null && bancoDados.isOpen()) {
            String[] Col = {"CodVendedor", "RedeExt"};
            String Tabela = "CONFIG";
            String whereClause = null;
            String[] whereArgs = null;

            Cursor cursorVendedor = bancoDados.query(Tabela, Col, whereClause, whereArgs, null, null, null);
            if (cursorVendedor != null && cursorVendedor.moveToFirst()) {
                int codVendIndex = cursorVendedor.getColumnIndex("CodVendedor");
                CodVendedor = String.valueOf(cursorVendedor.getInt(codVendIndex));
                int redExtIndex = cursorVendedor.getColumnIndex("RedeExt");
                ip = String.valueOf(cursorVendedor.getString(redExtIndex));
                cursorVendedor.close(); // Feche o cursor após obter o valor máximo
            }
            Connection conn = SQLConnectionExt.conectar(ip);
            if(conn != null){
                PreparedStatement pst = null;
                String sql = "INSERT INTO PrePedidoCelular (CodVendedor, CodPedidoExt, CodClienteExt, CodPreCliente, codCondPgto, Data, TotalFinal, " +
                        "Obs, DataInclusao, EnvioCompleto) " +
                        " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
                String posSql = "Select MAX(Codigo) as Codigo from PrePedidoCelular";
                String[] Col2 = {"Codigo", "CodCli", "CodCondPgto", "Data", "Obs", "TotalPed"};
                String Tabela2 = "PEDIDO";
                whereClause = "Enviado = ?";
                whereArgs = new String[]{"0"};
                Cursor cursorPedido = bancoDados.query(Tabela2, Col2, whereClause, whereArgs, null, null, null);
                if (cursorPedido != null) {
                    while(cursorPedido.moveToNext()){
                        int codPedIndex = cursorPedido.getColumnIndex("Codigo");
                        String codPed = String.valueOf(cursorPedido.getInt(codPedIndex));

                        int codCliIndex = cursorPedido.getColumnIndex("CodCli");
                        String codCli = String.valueOf(cursorPedido.getInt(codCliIndex));

                        int codCondPgtoIndex = cursorPedido.getColumnIndex("CodCondPgto");
                        String condPgto = String.valueOf(cursorPedido.getInt(codCondPgtoIndex));

                        int dataIndex = cursorPedido.getColumnIndex("Data");
                        String dataString = cursorPedido.getString(dataIndex);

                        // Crie um SimpleDateFormat para o formato de entrada (dd/MM/yyyy)
                        SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");

                        // Crie um SimpleDateFormat para o formato de saída (yyyy/MM/dd)
                        SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy/MM/dd");

                        try {
                            Date data = formatoEntrada.parse(dataString); // Converte a string para um objeto Date
                            dataString = formatoSaida.format(data); // Formata a data no formato desejado

                            // Agora 'dataFormatada' contém a data no formato "yyyy/MM/dd"
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        int obsIndex = cursorPedido.getColumnIndex("Obs");
                        String obs = cursorPedido.isNull(obsIndex) ? null : cursorPedido.getString(obsIndex);

                        int totalIndex = cursorPedido.getColumnIndex("TotalPed");
                        Double total = cursorPedido.getDouble(totalIndex);

                        String codPreCli = null;


                        String sqlaux = "Select Codigo from PreCliente where CodClienteExt = " + codCli + " and Codvendedor = " + CodVendedor ;
                        Statement staux = null;
                        try{
                            staux = conn.createStatement();
                            ResultSet rs = staux.executeQuery(sqlaux);
                            while (rs.next()) {
                                codPreCli = rs.getString("Codigo");
                            }
                        }catch (SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                        } catch (java.sql.SQLException e) {
                            throw new RuntimeException(e);
                        }

                        try{
                            pst = conn.prepareStatement(sql);
                            pst.setString(1, CodVendedor);
                            pst.setString(2, codPed);
                            pst.setString(3, codCli);
                            pst.setString(4, codPreCli);
                            pst.setString(5, condPgto);
                            pst.setString(6, dataString);
                            pst.setDouble(7, total);
                            pst.setString(8, obs);
                            pst.setString(9, dataAtual);
                            pst.setString(10, "S");
                            pst.executeUpdate();


                        }catch (java.sql.SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                            return 101; //Falha na exportação do pedido
                        }

                        String codPrePed = null;
                        Statement st = null;
                        try{
                            staux = conn.createStatement();
                            ResultSet rsPos = staux.executeQuery(posSql);
                            while (rsPos.next()) {
                                codPrePed = rsPos.getString("Codigo");
                            }
                        }catch (SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                        } catch (java.sql.SQLException e) {
                            throw new RuntimeException(e);
                        }

                        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAO(context);
                        if(!itemPedidoDAO.exportaItemPedidoExt(Integer.valueOf(codPrePed), codPed)){
                            return 102; // Falha na exportação dos itens
                        }

                    }

                }else {
                    return 155; //Não localizou pedido para exportar
                }
                cursorPedido.close(); // Feche o cursor após obter o valor máximo
            }else{
                return 500; //Sem conecxão
            }
            ContentValues valores = new ContentValues();
            valores.put("Enviado", 1);
            bancoDados.update("PEDIDO", valores, null, null);

        }
        return 200; //Sucesso

    }



    public List<PedidoPesquisa> pesquisaPedByCli(String cliente) {
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        PedidoPesquisa pedidoPesq = null;
        List<PedidoPesquisa> listaPedidos = new ArrayList<>();


        if (bancoDados != null && bancoDados.isOpen()) {


            String consultaSql = "SELECT Pedido.Codigo, Cliente.Razao, Pedido.Data " +
                    "FROM Pedido " +
                    "INNER JOIN CLIENTE ON Pedido.CodCli = Cliente.Codigo " +
                    "WHERE Cliente.Razao LIKE ?";

            String[] whereArgs = new String[]{"%" + cliente + "%"};

            Cursor cursor = bancoDados.rawQuery(consultaSql, whereArgs);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToNext();
               do {
                    pedidoPesq = new PedidoPesquisa();
                    int codPedidoIndex = cursor.getColumnIndex("Pedido.Codigo");
                    int codPedido =  cursor.getInt(codPedidoIndex);
                    pedidoPesq.setCodigo(codPedido);
                    int razaoClienteIndex = cursor.getColumnIndex("Cliente.Razao");
                    String razaoCliente =  cursor.getString(razaoClienteIndex);
                    pedidoPesq.setRazaoCli(razaoCliente);
                    int dataPedidoIndex = cursor.getColumnIndex("Pedido.Data");
                    String dataPedido =  cursor.getString(dataPedidoIndex);
                    pedidoPesq.setData(dataPedido);

                    listaPedidos.add(pedidoPesq);
                }while (cursor.moveToNext());
            }
        }

        return listaPedidos;
    }


    public List<PedidoPesquisa> pesquisaPedByCod(int codigo) {
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        PedidoPesquisa pedidoPesq = null;
        List<PedidoPesquisa> listaPedidos = new ArrayList<>();


        if (bancoDados != null && bancoDados.isOpen()) {


            String consultaSql = "SELECT Pedido.Codigo, Cliente.Razao, Pedido.Data " +
                    "FROM Pedido " +
                    "INNER JOIN CLIENTE ON Pedido.CodCli = Cliente.Codigo " +
                    "WHERE Pedido.Codigo = ?";

            String[] whereArgs = new String[]{String.valueOf(codigo)};

            Cursor cursor = bancoDados.rawQuery(consultaSql, whereArgs);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    pedidoPesq = new PedidoPesquisa();
                    int codPedidoIndex = cursor.getColumnIndex("Pedido.Codigo");
                    int codPedido =  cursor.getInt(codPedidoIndex);
                    pedidoPesq.setCodigo(codPedido);
                    int razaoClienteIndex = cursor.getColumnIndex("Cliente.Razao");
                    String razaoCliente =  cursor.getString(razaoClienteIndex);
                    pedidoPesq.setRazaoCli(razaoCliente);
                    int dataPedidoIndex = cursor.getColumnIndex("Pedido.Data");
                    String dataPedido =  cursor.getString(dataPedidoIndex);
                    pedidoPesq.setData(dataPedido);

                    listaPedidos.add(pedidoPesq);
                }while (cursor.moveToNext());
            }
        }

        return listaPedidos;
    }

    public List<PedidoPesquisa> pesquisaPedByData(String data) {
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        PedidoPesquisa pedidoPesq = null;
        List<PedidoPesquisa> listaPedidos = new ArrayList<>();


        if (bancoDados != null && bancoDados.isOpen()) {


            String consultaSql = "SELECT Pedido.Codigo, Cliente.Razao, Pedido.Data " +
                    "FROM Pedido " +
                    "INNER JOIN CLIENTE ON Pedido.CodCli = Cliente.Codigo " +
                    "WHERE Pedido.Data = ?";

            String[] whereArgs = new String[]{data};

            Cursor cursor = bancoDados.rawQuery(consultaSql, whereArgs);

            if (cursor != null && cursor.getCount() > 0 ) {
                cursor.moveToFirst();
                do {
                    pedidoPesq = new PedidoPesquisa();
                    int codPedidoIndex = cursor.getColumnIndex("Pedido.Codigo");
                    int codPedido =  cursor.getInt(codPedidoIndex);
                    pedidoPesq.setCodigo(codPedido);
                    int razaoClienteIndex = cursor.getColumnIndex("Cliente.Razao");
                    String razaoCliente =  cursor.getString(razaoClienteIndex);
                    pedidoPesq.setRazaoCli(razaoCliente);
                    int dataPedidoIndex = cursor.getColumnIndex("Pedido.Data");
                    String dataPedido =  cursor.getString(dataPedidoIndex);
                    pedidoPesq.setData(dataPedido);

                    listaPedidos.add(pedidoPesq);
                }while (cursor.moveToNext());
            }
        }

        return listaPedidos;
    }

    public Pedido carregaOutrosPedido(Integer codPed){
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Pedido pedidoPesq = null;

        if (bancoDados != null && bancoDados.isOpen()) {
            String consultaSql = "SELECT Pedido.Codigo, Pedido.Data, Pedido.Enviado, Pedido.Obs, Forma.Descricao as Forma, Pedido.TotalPed " +
                    "FROM Pedido " +
                    "INNER JOIN Forma on Pedido.CodCondPgto = Forma.Codigo " +
                    "WHERE Pedido.Codigo = ?";

            String[] whereArgs = new String[]{String.valueOf(codPed)};

            Cursor cursor = bancoDados.rawQuery(consultaSql, whereArgs);
            if (cursor != null && cursor.moveToFirst()) {
                pedidoPesq = new Pedido();

                int dataIndex = cursor.getColumnIndex("Data");
                if (!cursor.isNull(dataIndex)) {
                    String dataPed = cursor.getString(dataIndex);
                    pedidoPesq.setData(dataPed);
                }

                int pedidoEnviadoIndex = cursor.getColumnIndex("Enviado");
                if (!cursor.isNull(pedidoEnviadoIndex)) {
                    String enviadoPed = cursor.getString(pedidoEnviadoIndex);
                    if(enviadoPed.equals("0")){
                        pedidoPesq.setEnviado("Não Enviado");
                    }else{
                        pedidoPesq.setEnviado("Enviado");
                    }

                }

                int pedidoObsIndex = cursor.getColumnIndex("Obs");
                if (!cursor.isNull(pedidoObsIndex)) {
                    String obsPed = cursor.getString(pedidoObsIndex);
                    pedidoPesq.setObs(obsPed);
                }

                int pedidoPgtoIndex = cursor.getColumnIndex("Forma");
                if (!cursor.isNull(pedidoPgtoIndex)) {
                    String pgtoPed = cursor.getString(pedidoPgtoIndex);
                    pedidoPesq.setForma(pgtoPed);
                }

                int pedidoTotalIndex = cursor.getColumnIndex("TotalPed");
                if (!cursor.isNull(pedidoTotalIndex)) {
                    Double totalPed = cursor.getDouble(pedidoTotalIndex);
                    pedidoPesq.setVlrTotal(totalPed);
                }
            }
        }

        return pedidoPesq;
    }

}
