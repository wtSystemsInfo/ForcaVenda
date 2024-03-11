package com.example.forcavenda.DAO;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.forcavenda.Connection.SQLConnection;
import com.example.forcavenda.Connection.SQLConnectionExt;
import com.example.forcavenda.Connection.SQLiteHelper;
import com.example.forcavenda.Model.Cliente;
import com.example.forcavenda.Model.Forma;
import com.example.forcavenda.SincActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClienteDAO {

    private SQLiteHelper.DatabaseHelper dbHelper;

    private Context mContext;
    public ClienteDAO (Context context) {

        dbHelper = new SQLiteHelper.DatabaseHelper(context);

        mContext = context;
    }

    public boolean baixaCliente(){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        int codVendedor = 0;

        if (bancoDados != null && bancoDados.isOpen()) {
            Log.d("SQLiteDatabase", "Banco de dados aberto com sucesso.");

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

            //Recuperando o codigo do vendedor para importar somente os clientes do vendedor configurado
            String[] vendColuna = {"CodVendedor"};

            String vendTabela = "CONFIG";

            Cursor cursor = bancoDados.query(vendTabela, vendColuna, null, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int codVendedorIndex = cursor.getColumnIndex("CodVendedor");
                    codVendedor =  cursor.getInt(codVendedorIndex);
                }
            }

            cursor.close();

            cursor = bancoDados.query("CLIENTE", null, null, null, null, null, null);
            if(cursor!=null) {
                //Verificando se a tabela Cliente do app está zerada
                if (cursor.getCount() > 0) {
                    Connection conn = SQLConnection.conectar(ip);
                    if (conn != null) {
                        String sql = "Select Cliente.Codigo, Cliente.Razao, COALESCE( Cliente.DDDRes, '000' ) as Ddd, Cliente.TelefoneRes, Cliente.Pessoa, Cliente.CNPJ_CPF," +
                                " COALESCE(Cliente.Email, 'SEM EMAIL') as Email, Cidade.Descricao as Cidade, Cliente.UF from Cliente " +
                                " INNER JOIN Cidade on Cliente.Codcidade = Cidade.Codigo" +
                                " WHERE Cliente.codvendint = " + Integer.toString(codVendedor);

                        Statement st = null;
                        try {
                            st = conn.createStatement();
                            ResultSet rs = st.executeQuery(sql);
                            while (rs.next()) {
                                int codigo = rs.getInt("Codigo");
                                String raz = rs.getString("Razao");
                                raz = raz.replace("'", "''");
                                String ddd = rs.getString("Ddd");
                                String telefone = rs.getString("TelefoneRes");
                                String pessoa = rs.getString("Pessoa");
                                String doc = rs.getString("CNPJ_CPF");
                                String email = rs.getString("Email");
                                String cidade = rs.getString("Cidade");
                                String estado = rs.getString("UF");


                                // Dá o update com os dados do banco do cliente no banco SQLite
                                String insertSql = "UPDATE CLIENTE SET Razao = '" + raz + "', Ddd = '" + ddd + "', Telefone = '" + telefone + "'," +
                                        " Pessoa = '" + pessoa + "', CodSistema = " + codigo + ", Email = '" + email + "'," +
                                        " Cidade = '" + cidade + "', UF = '" + estado + "' WHERE CnpjCpf = '" + doc + "'";
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
                } else {
                    Connection conn = SQLConnectionExt.conectar(ip);
                    if (conn != null) {
                        String sql = "Select Cliente.Codigo, Cliente.Razao, COALESCE( Cliente.DDDRes, '000' ) as Ddd, Cliente.TelefoneRes, Cliente.Pessoa, Cliente.CNPJ_CPF," +
                                " COALESCE(Cliente.Email, 'SEM EMAIL') as Email, Cidade.Descricao as Cidade, Cliente.UF from Cliente " +
                                " INNER JOIN Cidade on Cliente.Codcidade = Cidade.Codigo" +
                                " WHERE Cliente.codvendint = " + Integer.toString(codVendedor);

                        Statement st = null;
                        try {
                            st = conn.createStatement();
                            ResultSet rs = st.executeQuery(sql);


                            while (rs.next()) {
                                int codigo = rs.getInt("Codigo");
                                String raz = rs.getString("Razao");
                                String ddd = rs.getString("Ddd");
                                String telefone = rs.getString("TelefoneRes");
                                String pessoa = rs.getString("Pessoa");
                                String doc = rs.getString("CNPJ_CPF");
                                String email = rs.getString("Email");
                                String cidade = rs.getString("Cidade");
                                String estado = rs.getString("UF");


                                String insertSql = "INSERT INTO CLIENTE(CodSistema, Razao, Ddd, Telefone, Pessoa, CnpjCpf, Email, Cidade, UF, Exportado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

                                try {
                                    bancoDados.execSQL(insertSql, new Object[]{codigo, raz, ddd, telefone, pessoa, doc, email, cidade, estado, 1});
                                } catch (android.database.SQLException e) {
                                    e.printStackTrace();
                                }

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
                }
            }
            bancoDados.close();
        } else {
            Log.d("SQLiteDatabase", "Erro ao abrir o banco de dados.");
            return false;
        }
        return true;
    }

    public boolean  baixaClienteExt() {

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        int codVendedor = 0;


        //ABRINDO O BANCO SQLITE E PEGANDO OS DADOS DE CONEXÃO
        if (bancoDados != null && bancoDados.isOpen()) {
            Log.d("SQLiteDatabase", "Banco de dados aberto com sucesso.");

            String ip = null;
            if (bancoDados != null && bancoDados.isOpen())
            {
                String[] Col = {"RedeExt"};
                String Tabela = "CONFIG";

                Cursor cursorIP = bancoDados.query(Tabela, Col, null, null, null, null, null);
                if (cursorIP != null && cursorIP.moveToFirst()) {
                    int redExtIndex = cursorIP.getColumnIndex("RedeExt");
                    ip = String.valueOf(cursorIP.getString(redExtIndex));
                    cursorIP.close(); // Feche o cursor após obter o valor máximo
                }
            }

            //Recuperando o codigo do vendedor para importar somente os clientes do vendedor configurado
            String[] vendColuna = {"CodVendedor"};

            String vendTabela = "CONFIG";

            Cursor cursor = bancoDados.query(vendTabela, vendColuna, null, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int codVendedorIndex = cursor.getColumnIndex("CodVendedor");
                    codVendedor =  cursor.getInt(codVendedorIndex);
                }
            }

            cursor.close();

            cursor = bancoDados.query("CLIENTE", null, null, null, null, null, null);
            if(cursor!=null) {
                //Verificando se a tabela Cliente do app está zerada
                if (cursor.getCount() > 0) {
                    Connection conn = SQLConnectionExt.conectar(ip);
                    if (conn != null) {
                        String sql = "Select Cliente.Codigo, Cliente.Razao, COALESCE( Cliente.DDDRes, '000' ) as Ddd, Cliente.TelefoneRes, Cliente.Pessoa, Cliente.CNPJ_CPF," +
                                " COALESCE(Cliente.Email, 'SEM EMAIL') as Email, Cidade.Descricao as Cidade, Cliente.UF, PreCliente.CodClienteExt from Cliente " +
                                " INNER JOIN Cidade on Cliente.Codcidade = Cidade.Codigo" +
                                " INNER JOIN PreCliente on PreCliente.CodClienteSistema = Cliente.codigo" +
                                " WHERE Cliente.codvendint = " + Integer.toString(codVendedor);

                        Statement st = null;
                        try {
                            st = conn.createStatement();
                            ResultSet rs = st.executeQuery(sql);
                            while (rs.next()) {
                                int codigo = rs.getInt("Codigo");
                                String raz = rs.getString("Razao");
                                raz = raz.replace("'", "''");
                                String ddd = rs.getString("Ddd");
                                String telefone = rs.getString("TelefoneRes");
                                String pessoa = rs.getString("Pessoa");
                                String doc = rs.getString("CNPJ_CPF");
                                String email = rs.getString("Email");
                                String cidade = rs.getString("Cidade");
                                String estado = rs.getString("UF");
                                int codigoApp = rs.getInt("CodClienteExt");


                                // Dá o update com os dados do banco do cliente no banco SQLite
                                String insertSql = "UPDATE CLIENTE SET Razao = '" + raz + "', Ddd = '" + ddd + "', Telefone = '" + telefone + "'," +
                                        " Pessoa = '" + pessoa + "', CodSistema = " + codigo + ", Email = '" + email + "'," +
                                        " Cidade = '" + cidade + "', UF = '" + estado + "', CnpjCpf = '" + doc + "' WHERE Codigo = " + codigoApp ;
                                bancoDados.execSQL(insertSql);

                            }
                            rs.close();
                            st.close();


                        } catch (SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                            return false;
                        }


                        //ADICIONANDO NOVOS CLIENTES CADASTRADOS
                        sql = "Select Cliente.Codigo, Cliente.Razao, COALESCE( Cliente.DDDRes, '000' ) as Ddd, Cliente.TelefoneRes, Cliente.Pessoa, Cliente.CNPJ_CPF," +
                                " COALESCE(Cliente.Email, 'SEM EMAIL') as Email, Cidade.Descricao as Cidade, Cliente.UF, PreCliente.CodClienteExt  from Cliente " +
                                " INNER JOIN Cidade on Cliente.Codcidade = Cidade.Codigo" +
                                " LEFT JOIN PreCliente on PreCliente.CodClienteSistema = Cliente.codigo " +
                                " WHERE Cliente.codvendint = " + Integer.toString(codVendedor) + " and Cliente.Codigo not in(Select PreCliente.CodClienteSistema from PreCliente where PreCliente.CodVendedor = " + Integer.toString(codVendedor) + " );";

                        Statement st2 = null;
                        try {
                            st2 = conn.createStatement();
                            ResultSet rs = st2.executeQuery(sql);
                            while (rs.next()) {
                                int codigo = rs.getInt("Codigo");
                                String raz = rs.getString("Razao");
                                raz = raz.replace("'", "''");
                                String ddd = rs.getString("Ddd");
                                String telefone = rs.getString("TelefoneRes");
                                String pessoa = rs.getString("Pessoa");
                                String doc = rs.getString("CNPJ_CPF");
                                String email = rs.getString("Email");
                                String cidade = rs.getString("Cidade");
                                String estado = rs.getString("UF");


                                String insertSql = "INSERT INTO CLIENTE(CodSistema, Razao, Ddd, Telefone, Pessoa, CnpjCpf, Email, Cidade, UF, Exportado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

                                try {
                                    bancoDados.execSQL(insertSql, new Object[]{codigo, raz, ddd, telefone, pessoa, doc, email, cidade, estado, 0});
                                } catch (android.database.SQLException e) {
                                    e.printStackTrace();
                                    return false;
                                }

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
                } else {
                    Connection conn = SQLConnectionExt.conectar(ip);
                    if (conn != null) {
                        String sql = "Select Cliente.Codigo, Cliente.Razao, COALESCE( Cliente.DDDRes, '000' ) as Ddd, Cliente.TelefoneRes, Cliente.Pessoa, Cliente.CNPJ_CPF," +
                                " COALESCE(Cliente.Email, 'SEM EMAIL') as Email, Cidade.Descricao as Cidade, Cliente.UF from Cliente " +
                                " INNER JOIN Cidade on Cliente.Codcidade = Cidade.Codigo" +
                                " WHERE Cliente.codvendint = " + Integer.toString(codVendedor);


                        //APAGA TABELA PRECLIENTE PAR PREPARAR PARA EXPORTAÇÃO
                        PreparedStatement pst2 = null;
                        try{
                            pst2 = conn.prepareStatement("Delete from PreCliente where Codvendedor = " + codVendedor );
                            pst2.executeUpdate();

                        } catch (SQLException e) {
                            // Trate a exceção adequadamente
                            e.printStackTrace();
                            return false;
                        }


                        Statement st = null;
                        try {
                            st = conn.createStatement();
                            ResultSet rs = st.executeQuery(sql);


                            while (rs.next()) {
                                int codigo = rs.getInt("Codigo");
                                String raz = rs.getString("Razao");
                                String ddd = rs.getString("Ddd");
                                String telefone = rs.getString("TelefoneRes");
                                String pessoa = rs.getString("Pessoa");
                                String doc = rs.getString("CNPJ_CPF");
                                String email = rs.getString("Email");
                                String cidade = rs.getString("Cidade");
                                String estado = rs.getString("UF");


                                String insertSql = "INSERT INTO CLIENTE(CodSistema, Razao, Ddd, Telefone, Pessoa, CnpjCpf, Email, Cidade, UF, Exportado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

                                try {
                                    bancoDados.execSQL(insertSql, new Object[]{codigo, raz, ddd, telefone, pessoa, doc, email, cidade, estado, 0});
                                } catch (android.database.SQLException e) {
                                    e.printStackTrace();
                                    return false;
                                }

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
                }
            }
            bancoDados.close();
        } else {
            Log.d("SQLiteDatabase", "Erro ao abrir o banco de dados.");
            return false;
        }
        return true;
    }




    public List<Cliente> pesquisaCli(String cliente) {
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Cliente clientePesq = null;
        List<Cliente> listaClientes = new ArrayList<>();


        if (bancoDados != null && bancoDados.isOpen()) {

            Cursor cursor = bancoDados.rawQuery("Select Codigo, Razao from Cliente Where Razao like '%" + cliente + "%'", null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
               do{
                    clientePesq = new Cliente();
                    int codClienteIndex = cursor.getColumnIndex("Codigo");
                    int codCliente =  cursor.getInt(codClienteIndex);
                    clientePesq.setCodigoApp(codCliente);
                    int razaoClienteIndex = cursor.getColumnIndex("Razao");
                    String razaoCliente =  cursor.getString(razaoClienteIndex);
                    clientePesq.setRazao(razaoCliente);
                    listaClientes.add(clientePesq);
                } while (cursor.moveToNext());
            }
        }

        return listaClientes;
    }


    public List<Cliente> pesquisaCliByCod(String codigo) {
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Cliente clientePesq = null;
        List<Cliente> listaClientes = new ArrayList<>();


        if (bancoDados != null && bancoDados.isOpen()) {


            String[] cliColunas = {"Codigo", "Razao"};
            String cliTabela = "CLIENTE";
            String whereClause = "Codigo = ?";
            String[] whereArgs = new String[]{codigo};

            Cursor cursor = bancoDados.query(cliTabela, cliColunas, whereClause, whereArgs, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    clientePesq = new Cliente();
                    int codClienteIndex = cursor.getColumnIndex("Codigo");
                    int codCliente =  cursor.getInt(codClienteIndex);
                    clientePesq.setCodigoApp(codCliente);
                    int razaoClienteIndex = cursor.getColumnIndex("Razao");
                    String razaoCliente =  cursor.getString(razaoClienteIndex);
                    clientePesq.setRazao(razaoCliente);
                    listaClientes.add(clientePesq);
                }
            }
        }

        return listaClientes;
    }


    public Cliente carregaCliPed(String codigo){
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Cliente clientePesq = null;
        if (bancoDados != null && bancoDados.isOpen()) {
            String[] cliColunas = {"Codigo", "Razao", "Ddd", "Telefone", "Pessoa", "CnpjCpf", "Cidade", "UF"};
            String cliTabela = "CLIENTE";
            String whereClause = "Codigo = ?";
            String[] whereArgs = new String[]{codigo};

            Cursor cursor = bancoDados.query(cliTabela, cliColunas, whereClause, whereArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                clientePesq = new Cliente();
                int razaoClienteIndex = cursor.getColumnIndex("Razao");
                if (!cursor.isNull(razaoClienteIndex)) {
                    String razaoCliente = cursor.getString(razaoClienteIndex);
                    clientePesq.setRazao(razaoCliente);
                }

                int dddClienteIndex = cursor.getColumnIndex("Ddd");
                int telefoneClienteIndex = cursor.getColumnIndex("Telefone");
                if (!cursor.isNull(telefoneClienteIndex)){
                    if (!cursor.isNull(dddClienteIndex)){
                        String telCliente = cursor.getString(dddClienteIndex) + " " + cursor.getString(telefoneClienteIndex);
                        clientePesq.setTelefone(telCliente);
                    }else{
                        String telCliente = cursor.getString(telefoneClienteIndex);
                        clientePesq.setTelefone(telCliente);
                    }
                }


                int tipoClienteIndex = cursor.getColumnIndex("Pessoa");
                if (!cursor.isNull(tipoClienteIndex)) {
                    String tipoCliente = cursor.getString(tipoClienteIndex);
                    clientePesq.setTipoCli(tipoCliente);
                }

                int docClienteIndex = cursor.getColumnIndex("CnpjCpf");
                if (!cursor.isNull(docClienteIndex)) {
                    String docCliente = cursor.getString(docClienteIndex);
                    clientePesq.setDocCli(docCliente);
                }

                int cidadeClienteIndex = cursor.getColumnIndex("Cidade");
                if (!cursor.isNull(cidadeClienteIndex)) {
                    String cidadeCliente = cursor.getString(cidadeClienteIndex);
                    clientePesq.setCidade(cidadeCliente);
                }

                int ufClienteIndex = cursor.getColumnIndex("UF");
                if (!cursor.isNull(ufClienteIndex)) {
                    String ufCliente = cursor.getString(ufClienteIndex);
                    clientePesq.setUf(ufCliente);
                }
            }
        }

        return clientePesq;
    }



    public Cliente carregaCliPedByPed(Integer pedido){
        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Cliente clientePesq = null;
        if (bancoDados != null && bancoDados.isOpen()) {

            String consultaSql = "SELECT Pedido.Codigo, Cliente.Codigo, Cliente.Razao, Pedido.Data, Cliente.Ddd, Cliente.telefone, Cliente.Pessoa, Cliente.CnpjCpf, Cliente.Cidade, Cliente.UF  " +
                    "FROM Pedido " +
                    "INNER JOIN CLIENTE ON Pedido.CodCli = Cliente.Codigo " +
                    "WHERE Pedido.Codigo = ?";

            String[] whereArgs = new String[]{String.valueOf(pedido)};

            Cursor cursor = bancoDados.rawQuery(consultaSql, whereArgs);


            if (cursor != null && cursor.moveToFirst()) {
                clientePesq = new Cliente();
                int razaoClienteIndex = cursor.getColumnIndex("Razao");
                if (!cursor.isNull(razaoClienteIndex)) {
                    String razaoCliente = cursor.getString(razaoClienteIndex);
                    clientePesq.setRazao(razaoCliente);
                }

                int codClienteIndex = cursor.getColumnIndex("Codigo");
                if (!cursor.isNull(codClienteIndex)) {
                    Integer codCliente = cursor.getInt(codClienteIndex);
                    clientePesq.setCodigoApp(codCliente);
                }

                int dddClienteIndex = cursor.getColumnIndex("Ddd");
                int telefoneClienteIndex = cursor.getColumnIndex("Telefone");
                if (!cursor.isNull(telefoneClienteIndex)){
                    if (!cursor.isNull(dddClienteIndex)){
                        String telCliente = cursor.getString(dddClienteIndex) + " " + cursor.getString(telefoneClienteIndex);
                        clientePesq.setTelefone(telCliente);
                    }else{
                        String telCliente = cursor.getString(telefoneClienteIndex);
                        clientePesq.setTelefone(telCliente);
                    }
                }


                int tipoClienteIndex = cursor.getColumnIndex("Pessoa");
                if (!cursor.isNull(tipoClienteIndex)) {
                    String tipoCliente = cursor.getString(tipoClienteIndex);
                    clientePesq.setTipoCli(tipoCliente);
                }

                int docClienteIndex = cursor.getColumnIndex("CnpjCpf");
                if (!cursor.isNull(docClienteIndex)) {
                    String docCliente = cursor.getString(docClienteIndex);
                    clientePesq.setDocCli(docCliente);
                }

                int cidadeClienteIndex = cursor.getColumnIndex("Cidade");
                if (!cursor.isNull(cidadeClienteIndex)) {
                    String cidadeCliente = cursor.getString(cidadeClienteIndex);
                    clientePesq.setCidade(cidadeCliente);
                }

                int ufClienteIndex = cursor.getColumnIndex("UF");
                if (!cursor.isNull(ufClienteIndex)) {
                    String ufCliente = cursor.getString(ufClienteIndex);
                    clientePesq.setUf(ufCliente);
                }
            }
        }

        return clientePesq;
    }






    public void salvaCliente(String razao, String ddd, String tel, String tipo, String doc,
                             String cidade, String uf, String email) {
        SQLiteDatabase bancoDados = dbHelper.getReadableDatabase();


        try {
            // Insira os dados no banco SQLite
            String insertSql;
            String insertSql1 = "INSERT INTO CLIENTE(CodSistema, Razao, Pessoa, CnpjCpf ";
            String insertSql2 = "VALUES(" + 0 + ", '" + razao + "', '" + tipo + "', '" + doc + "'";
            if (!ddd.isEmpty()) {
                insertSql1 = insertSql1 + ", Ddd";
                insertSql2 = insertSql2 + ", '" + ddd + "'";
            }
            if (!tel.isEmpty()) {
                insertSql1 = insertSql1 + ", Telefone";
                insertSql2 = insertSql2 + ", '" + tel + "'";
            }
            if (!uf.isEmpty()) {
                insertSql1 = insertSql1 + ", UF";
                insertSql2 = insertSql2 + ", '" + uf + "'";
            }
            if (!cidade.isEmpty()) {
                insertSql1 = insertSql1 + ", Cidade";
                insertSql2 = insertSql2 + ", '" + cidade + "'";
            }
            if (!email.isEmpty()) {
                insertSql1 = insertSql1 + ", Email";
                insertSql2 = insertSql2 + ", '" + email + "'";
            }
            insertSql1 = insertSql1 + ", Exportado) ";
            insertSql2 = insertSql2 + ", " + 0 + ") ";
            insertSql = insertSql1 + insertSql2;
            bancoDados.execSQL(insertSql);
        } catch (android.database.SQLException e) {
            // Trate a exceção adequadamente
            e.printStackTrace();
        }

    }


    public int retornaUltimoCli(){
        int codigoCli = 0;

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();

        if (bancoDados != null && bancoDados.isOpen()) {


            String[] prodColunas = {"MAX(Codigo) AS Codigo"};
            String prodTabela = "CLIENTE";
            String whereClause = null;
            String[] whereArgs = null;

            Cursor cursor = bancoDados.query(prodTabela, prodColunas, whereClause, whereArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int codCliIndex = cursor.getColumnIndex("Codigo");
                codigoCli = cursor.getInt(codCliIndex);
                cursor.close(); // Feche o cursor após obter o valor máximo
            }

        }

        return codigoCli;
    }

    public boolean verificaCadastro(String cpfcnpj){


        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();

        if (bancoDados != null && bancoDados.isOpen()) {


            String[] prodColunas = {"Codigo"};
            String prodTabela = "CLIENTE";
            String whereClause = "CnpjCpf = ?";
            String[] whereArgs = new String[]{cpfcnpj};

            Cursor cursor = bancoDados.query(prodTabela, prodColunas, whereClause, whereArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return true;
            }

        }

        return false ;
    }






    public boolean exportaClienteInt(){

        SQLiteDatabase bancoDados = dbHelper.getWritableDatabase();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat simpleData = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String dataAtual = simpleData.format(currentDate);
        String CodVendedor=null;
        String ip = null;

        if (bancoDados != null && bancoDados.isOpen()) {
            String[] Col = {"CodVendedor", "RedeInt"};
            String Tabela = "CONFIG";
            String whereClause = null;

            Cursor cursorVendedor = bancoDados.query(Tabela, Col, null, null, null, null, null);
            if (cursorVendedor != null && cursorVendedor.moveToFirst()) {
                int codVendIndex = cursorVendedor.getColumnIndex("CodVendedor");
                CodVendedor = String.valueOf(cursorVendedor.getInt(codVendIndex));
                int redIntIndex = cursorVendedor.getColumnIndex("RedeInt");
                ip = String.valueOf(cursorVendedor.getString(redIntIndex));
                cursorVendedor.close(); // Feche o cursor após obter o valor máximo
            }

            Connection conn = SQLConnection.conectar(ip);
            if(conn != null){
                PreparedStatement pst = null;
                String sql = "INSERT INTO PreCliente (CodVendedor, CodClienteExt,  Razao, Pessoa, CNPJ_CPF, " +
                        "DDDRes, Telefoneres, Email, Datainclusao) " +
                " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
                String[] Col2 = {"Codigo", "CodSistema", "Razao", "Ddd", "Telefone", "Pessoa", "CnpjCpf", "Email"};
                String Tabela2 = "CLIENTE";
                whereClause = "Exportado = 0";
                Cursor cursorCliente = bancoDados.query(Tabela2, Col2, whereClause, null, null, null, null);
                if (cursorCliente != null) {
                    if(cursorCliente.getCount() > 0 ) {
                        while (cursorCliente.moveToNext()) {
                            int codCliIndex = cursorCliente.getColumnIndex("Codigo");
                            String codCliApp = String.valueOf(cursorCliente.getInt(codCliIndex));
                            int razaoIndex = cursorCliente.getColumnIndex("Razao");
                            String razao = String.valueOf(cursorCliente.getString(razaoIndex));
                            int dddIndex = cursorCliente.getColumnIndex("Ddd");
                            String ddd = cursorCliente.isNull(dddIndex) ? null : cursorCliente.getString(dddIndex);
                            int telIndex = cursorCliente.getColumnIndex("Telefone");
                            String telefone = cursorCliente.isNull(telIndex) ? null : cursorCliente.getString(telIndex);
                            int pessoaIndex = cursorCliente.getColumnIndex("Pessoa");
                            String pessoa = String.valueOf(cursorCliente.getString(pessoaIndex));
                            int docIndex = cursorCliente.getColumnIndex("CnpjCpf");
                            String doc = String.valueOf(cursorCliente.getString(docIndex));
                            int emailIndex = cursorCliente.getColumnIndex("Email");
                            String email = cursorCliente.isNull(emailIndex) ? null : cursorCliente.getString(emailIndex);

                            try {
                                pst = conn.prepareStatement(sql);
                                pst.setString(1, CodVendedor);
                                pst.setString(2, codCliApp);
                                pst.setString(3, razao);
                                pst.setString(4, pessoa);
                                pst.setString(5, doc);
                                pst.setString(6, ddd);
                                pst.setString(7, telefone);
                                pst.setString(8, email);
                                pst.setString(9, dataAtual);
                                pst.executeUpdate();

                                String sqlUpdateCli = "UPDATE Cliente set Exportado = 1 WHERE Codigo = " + codCliApp;
                                bancoDados.execSQL(sqlUpdateCli);

                            } catch (SQLException e) {
                                // Trate a exceção adequadamente
                                e.printStackTrace();
                                return false;
                            }

                        }
                    }

                }else{
                    return false;
                }
                cursorCliente.close(); // Feche o cursor após obter o valor máximo
            }else{
                return false;
            }

        }

        return true;

    }

    public boolean exportaClienteExt(){

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

            Cursor cursorVendedor = bancoDados.query(Tabela, Col, null, null, null, null, null);
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
                String sql = "INSERT INTO PreCliente (CodVendedor, CodClienteExt, CodClienteSistema,  Razao, Pessoa, CNPJ_CPF, " +
                        "DDDRes, Telefoneres, Email, Datainclusao) " +
                        " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
                String[] Col2 = {"Codigo", "CodSistema", "Razao", "Ddd", "Telefone", "Pessoa", "CnpjCpf", "Email"};
                String Tabela2 = "CLIENTE";
                whereClause = "Exportado = 0";
                Cursor cursorCliente = bancoDados.query(Tabela2, Col2, whereClause, null, null, null, null);
                if (cursorCliente != null) {
                    if(cursorCliente.getCount() > 0 ) {
                        while (cursorCliente.moveToNext()) {
                            int codCliIndex = cursorCliente.getColumnIndex("Codigo");
                            String codCliApp = String.valueOf(cursorCliente.getInt(codCliIndex));
                            int codSisIndex = cursorCliente.getColumnIndex("CodSistema");
                            String codCliSis = String.valueOf(cursorCliente.getString(codSisIndex));
                            int razaoIndex = cursorCliente.getColumnIndex("Razao");
                            String razao = String.valueOf(cursorCliente.getString(razaoIndex));
                            int dddIndex = cursorCliente.getColumnIndex("Ddd");
                            String ddd = cursorCliente.isNull(dddIndex) ? null : cursorCliente.getString(dddIndex);
                            int telIndex = cursorCliente.getColumnIndex("Telefone");
                            String telefone = cursorCliente.isNull(telIndex) ? null : cursorCliente.getString(telIndex);
                            int pessoaIndex = cursorCliente.getColumnIndex("Pessoa");
                            String pessoa = String.valueOf(cursorCliente.getString(pessoaIndex));
                            int docIndex = cursorCliente.getColumnIndex("CnpjCpf");
                            String doc = String.valueOf(cursorCliente.getString(docIndex));
                            int emailIndex = cursorCliente.getColumnIndex("Email");
                            String email = cursorCliente.isNull(emailIndex) ? null : cursorCliente.getString(emailIndex);




                            try {
                                pst = conn.prepareStatement(sql);
                                pst.setString(1, CodVendedor);
                                pst.setString(2, codCliApp);
                                pst.setString(3, codCliSis);
                                pst.setString(4, razao);
                                pst.setString(5, pessoa);
                                pst.setString(6, doc);
                                pst.setString(7, ddd);
                                pst.setString(8, telefone);
                                pst.setString(9, email);
                                pst.setString(10, dataAtual);
                                pst.executeUpdate();

                                String sqlUpdateCli = "UPDATE Cliente set Exportado = 1 WHERE Codigo = " + codCliApp;
                                bancoDados.execSQL(sqlUpdateCli);

                            } catch (SQLException e) {
                                // Trate a exceção adequadamente
                                e.printStackTrace();
                                return false;
                            }

                        }
                    }
                }else{
                    return false;
                }
                cursorCliente.close(); // Feche o cursor após obter o valor máximo
            }else{
                return false;
            }
        }

        return true;

    }



}
