package com.example.forcavenda.Connection;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper {

    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "ForcaVenda";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Crie as tabelas necessárias aqui
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Atualize as tabelas conforme necessário aqui
        }


        public String getEnderecoIPParaTipoDeRede(String tipoDeRede) {
            SQLiteDatabase db = this.getReadableDatabase();
            String enderecoIP = null;


            if(tipoDeRede.equals("Rede Externa")) {
                String query = "SELECT RedeExt FROM CONFIG";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("RedeExt");
                    if(columnIndex >= 0) { // Verifica se a coluna foi encontrada
                        enderecoIP = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
                db.close();
            }

            if(tipoDeRede.equals("Rede Interna")) {
                String query = "SELECT RedeInt FROM CONFIG";
                Cursor cursor = db.rawQuery(query, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex("RedeInt");
                    if(columnIndex >= 0) { // Verifica se a coluna foi encontrada
                        enderecoIP = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
                db.close();
            }
            return enderecoIP;
        }

    }
}
