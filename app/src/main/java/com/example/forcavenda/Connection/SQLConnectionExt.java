package com.example.forcavenda.Connection;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SQLConnectionExt {

    public static Connection conectar(String ip){
        //Objeto conexão
        Connection sql = null;
        String ipConnection, database, userSQL, passSQL, port, instance, connecionString;

        ipConnection = ip;
        database = "SIIMEDDB";
        userSQL = "sa";
        passSQL = "123";
        port= "1433";
        instance = "sql2008";
        connecionString = "jdbc:jtds:sqlserver://"+ ipConnection + ":" + port +  ";encrypt=true;databasename=" + database + ";user=" + userSQL +
                ";password=" + passSQL + ";integratedSecurity=true;";


        try {
            //Adicionar política de criação da thread
            StrictMode.ThreadPolicy politica;
            politica = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(politica);

            //Verificar se o driver de conexão está correto no projeto
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            //Configurar conexão
            sql = DriverManager.getConnection(connecionString);





        }catch (SQLException se) {
            Log.e("ERRO0", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO1", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO2", e.getMessage());
        }
        return sql;
    }
}
