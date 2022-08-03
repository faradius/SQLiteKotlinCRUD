package com.example.sqlitekotlin

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.sqlitekotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var amigosDBHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        amigosDBHelper = SQLiteHelper(this)

        binding.btnGuardar.setOnClickListener {
            if (binding.etNombre.text.isNotBlank() && binding.etEmail.text.isNotBlank()){
                amigosDBHelper.agregarDatos(binding.etNombre.text.toString(), binding.etEmail.text.toString())

                binding.etNombre.text.clear()
                binding.etEmail.text.clear()

                Toast.makeText(this, "Se ha guardado correctamente", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "No se ha podido guardar", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnConsultar.setOnClickListener {
            binding.tvConsulta.text = ""
            val db:SQLiteDatabase = amigosDBHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM amigos", null)
            if (cursor.moveToFirst()){
                do{
                    binding.tvConsulta.append(cursor.getInt(0).toString() + ": ")
                    binding.tvConsulta.append(cursor.getString(1).toString()+ " , ")
                    binding.tvConsulta.append(cursor.getString(2).toString()+ "\n")
                }while (cursor.moveToNext())
            }
        }
    }
}