package com.example.sqlitekotlin

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.sqlitekotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //Implementamos el ViewBinding
    lateinit var binding: ActivityMainBinding
    //Declaramos una variable de tipoo SQLiteHelper para hacer uso de la clase
    lateinit var amigosDBHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //A la variable que contiene el SQLiteHelper le pasamos el contexto de la actividad
        amigosDBHelper = SQLiteHelper(this)

        //Habilitamos el OnClick en el boton de guardar
        binding.btnGuardar.setOnClickListener {
            //validamos que los campos no esten vacios o en blanco
            if (binding.etNombre.text.isNotBlank() && binding.etEmail.text.isNotBlank()){
                //mandamos a llamar de la clase SQLiteHelper la función agregarDatos este nos pedirá dos parametros los cuales son nombre y correo de la persona
                //estos datos se obtienen de los editText
                amigosDBHelper.agregarDatos(binding.etNombre.text.toString(), binding.etEmail.text.toString())

                //Limpiamos los editText
                binding.etNombre.text.clear()
                binding.etEmail.text.clear()

                //mandamos un mensaje si fue correcto la operación
                Toast.makeText(this, "Se ha guardado correctamente", Toast.LENGTH_SHORT).show()
            }else{
                //mandamos un mensaje si hubo un error
                Toast.makeText(this, "No se ha podido guardar", Toast.LENGTH_SHORT).show()
            }
        }

        //En este boton haremos la consulta a la base de datos de todos los registros que haya en la tabla amigos, las declaraciones sql que se hacen dentro del metodo
        //setOnClickListener este deberia estar en la clase SQLiteHelper como un metodo llamado consultar información pero para una mejor comprension del tema se realizará aqui
        binding.btnConsultar.setOnClickListener {
            //Declaramos el textView tvConsulta de forma vacia para despues concatenar la información extraida de la base de datos y asi mostrarla en pantalla
            binding.tvConsulta.text = ""

            //Aqui ponemos la base de datos en modo de lectura
            val db:SQLiteDatabase = amigosDBHelper.readableDatabase

            //Declaramos un cursor para despues recorrerlo, el cursor lo que hará es que por medio de la sentencia SQL es recorrer cada uno de los registros que estan en la tabla
            //amgigos y para hacer esto utilizaremos el metodo rawQuery para ejecutar una setencia SQL, esto es similar execSQL pero no se aun la diferencia entre ambos metodos,
            //pero si entrar a detalles, el metodo nos pide la sentencia SQL y será esta SELECT * FROM amigos, esta sentencia nos permite traer todos los registros de la tabla amigos
            //despues nos pedirá una seleccion de argumentos, esto quiere decir si queremos validar bajo alguna condición por ejemplo en base a un id en especifico para la persona que
            //vallamos a traer y este pasarle un arreglo que contenga los id's, pero como no queremos especificar la persona o personas que deseamos ver su información pues lo ponemos,
            //null para no hacer uso de esta opción
            val cursor:Cursor = db.rawQuery("SELECT * FROM amigos",null)

            //Despues hacemos una validación en que si el cursor se encuentra en la primera fila de la tabla si es asi empezará a recorrer el cursor por cada una de las filas que
            // tiene la tabla amigos y empezaremos a sacar registro por registro hasta sacar el ultimo, y esto lo mostraremos en pantalla de la app por medio de un textView
            if (cursor.moveToFirst()){
                do{
                    //para poner el dato obtenido en el TextView usaremos el metodo append, este nos permite poner un caracter o una secuencia de caracteres, despues dentro
                    //nos pedirá que valor de que campo queremos obtener de la tabla amigos, y para indicar eso escribimos cursor.getInt(0), el cero representa la primera columna de la tabla amigos
                    // que es el id, al obtener ese dato despues convertimos eso a string y le concatenemos ": " para que le demos un formato a la visualización del texto que se mostrará en pantalla
                    //hacemos lo mismo con cada uno de las columnas que queramos obtener su valor
                    binding.tvConsulta.append(cursor.getInt(0).toString() + ": ")
                    binding.tvConsulta.append(cursor.getString(1).toString()+ " , ")
                    binding.tvConsulta.append(cursor.getString(2).toString()+ "\n")

                    //Despues de hacer lo anterior y ya se haya mostrado la información del primer registro haremos que se pase al siguiente con la instrucción
                    //cursor.moveToNext(), esto se cumplicará hasta que ya no encuentre algun registro en la tabla por lo que mostrará en pantalla todos los registros que tenga
                    //la tabla amigos
                }while (cursor.moveToNext())
            }

            //finalmete cerramos la conexión
            db.close()
        }

        //El boton de borrar es muy similar al del boton registrar solo que hay algunos detalles que se le agregaron
        binding.btnBorrar.setOnClickListener {
            //declaramos una variable llamada cantidad para contabilizar los registros que hayan sido borrados
            var cantidad = 0

            //verificamos que el editext no este vacio o en blanco
            if (binding.etId.text.isNotBlank()){
                //si tiene algo el editText guardaremos la operación de la base de datos en la variable cantidad para poder contabilizar los registros borrados
                //Mandamos a llamar el metodo borrarDato de la clase SQLiteHelper y le pasamos como parametro el id para que la persona sea borrado mediante su ID
                cantidad= amigosDBHelper.borrarDato(binding.etId.text.toString().toInt())

                binding.etId.text.clear()

                Toast.makeText(this, "Se ha borrdado correctamente $cantidad", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "No se ha podido borrar", Toast.LENGTH_SHORT).show()
            }

        }

        //Aqui se realiza algo similar como el boton de registrar una persona
        binding.btnModificar.setOnClickListener {
            if (binding.etNombre.text.isNotBlank() && binding.etEmail.text.isNotBlank() && binding.etId.text.isNotBlank()){

                //Solo que aqui se manda a llamar de la clase SQLiteHelper la función modificarDatos y le pasamos como parametros el id, nombre y correo
                amigosDBHelper.modificarDatos(binding.etId.text.toString().toInt(),binding.etNombre.text.toString(), binding.etEmail.text.toString())

                binding.etNombre.text.clear()
                binding.etEmail.text.clear()
                binding.etId.text.clear()

                Toast.makeText(this, "Se ha modificado correctamente", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Los campos no deben de estar vacios", Toast.LENGTH_SHORT).show()
            }
        }

        //Habilitamos el onClick en el bonton llamado btnCosultarListView esto es para que pasemos a una activity que tiene implementado un listView en ella
        //en esa actividad se mostrará los datos en forma de lista y será mas amigable de poder la información de cada persona que se encuentra registrada en la base de datos
        binding.btnConsultarLV.setOnClickListener {
            val intent = Intent(this, ActivityLista::class.java)
            startActivity(intent)
        }
    }
}