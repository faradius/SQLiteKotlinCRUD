package com.example.sqlitekotlin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context:Context): SQLiteOpenHelper(context,"amigos.db",null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        var ordenCreacion = "CREATE TABLE amigos " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT, email TEXT)"
        db!!.execSQL(ordenCreacion)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val ordenBorrado = "DROP TABLE IF EXISTS amigos"
        db!!.execSQL(ordenBorrado)
        onCreate(db)
    }

    fun agregarDatos(nombre:String, email: String){
        val datos = ContentValues()
        datos.put("nombre", nombre)
        datos.put("email", email)

        val db = this.writableDatabase
        db.insert("amigos",null, datos)
        db.close()
    }

}