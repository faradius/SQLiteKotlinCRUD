package com.example.sqlitekotlin

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cursoradapter.widget.CursorAdapter
import com.example.sqlitekotlin.databinding.ActivityListaBinding
import com.example.sqlitekotlin.databinding.ItemListviewBinding

class ActivityLista : AppCompatActivity() {

    //Declaramos el binding
    lateinit var binding: ActivityListaBinding
    //Declaramos el uso de la clase SQLiteHelper
    lateinit var amigosDBHelper: SQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //A la variable que contiene el SQLiteHelper le pasamos el contexto de la actividad
        amigosDBHelper = SQLiteHelper(this)

        //Abrimos la base de datos en modo escritura
        val db: SQLiteDatabase = amigosDBHelper.readableDatabase

        //Creamos un cursor para recorrer la tabla llamada amigos
        val cursor:Cursor = db.rawQuery("SELECT * FROM amigos", null)

        //Creamos una variable que contendrá la instancia de la clase CursorAdapterListView y este requerira de dos parametros que es el contexto y el cursor
        val adapter = CursorAdapterListView(this, cursor)
        //Despues al listview le pasamos nuestro adaptador para que se muestre la lista de elementos extraidos de la base de datos
        binding.lvDatos.adapter = adapter
        //finalizamos con el cierre de la conexión de base de datos
        db.close()
    }

    //Preparamos un adaptador para poder llenar de información el listView con los datos de la base de datos (se puede implementar en una clase aparte pero como es poco codigo se implemento aqui)

    /*/Se crea una clase que va estar dentro o internamente de la clase ActivityLista y para que pueda acceder algunos atributos o metodos de la clase
    //principal es necesario agregarle la palabra reservada inner a la clase interna llamada CursorAdapterListView, esta clase va a extender de un cursorAdapter
    //esto es muy diferente a la implementación de una clase proveniente de un arrayAdapter, ya que este es para trabajar bajo cursores.

    //La clase que extiende de CursorAdapter requiere de un contexto, un cursor y un valor entero que representa el comportamiento del observador
    //dentro del cursor (no se a que se refiere pero android studio propone usar  FLAG_REGISTER_CONTENT_OBSERVER para el uso de la clase CursorAdapter),
    //al saber lo que pide la clase CursorAdpater, le pondremos como paramertros al CursorAdapterListView el contexto y el cursor siendo estos los valores que se le
    //pasará a la clase extendida llamada cursorAdapter */
    inner class CursorAdapterListView(context: Context, cursor: Cursor):CursorAdapter(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER){
        //Despues android studio nos pedira implementar los metodos que son newView y bindView

        //new view es el que va a crear la vista del listView
        override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
            //Creamos la variable inflater y le asignamos el contexto sobre el cual este trabajará la vista de cada elemento de la listview
            val inflater = LayoutInflater.from(context)
            /*Aqui regresamos apartir del contexto quiero mostrar la vista de cada elemento que va obtener la listview (del recurso xml)
            //sobre el widget listview (parent representa el listView que viene siendo el padre de tipo ViewGroup ya que contendrá cada item con su respectiva vista)
            // y en el attachRoot le ponemos false por que no queremos que se añada directamente como un hijo de la vista padre por que el listView es el que se encargará de hacer
            // la administración de la vista */
            return inflater.inflate(R.layout.item_listview, parent, false)
        }

        //Este metodo se encargará de enlazar los widgets con la vista
        override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
            /*Esta variable vamos almacenar la configuración de enlazamiento de cada wiget haciendo uso del binding
            //con ItemListviewBinding indicamos que los widgets que estan en ese xml los vamos a meter en el binding para la implementacón del viewbinding
            //con bind indicamos que vamos a enlazar esos elementos
            //y con view indicamos que serian los elementos o widgets que heredan de la clase View y con !! garantizamos de que no va hacer nullo */
            val bindingItems = ItemListviewBinding.bind(view!!)

            /*ya creado la configuración del binding vamos a colocar en cada textView los valores que contenga el cursor siendo este los datos que se extrajeron de la base de datos
            //es decir con esta linea de codigo cursor!!.getString(1) le estamos pidiendo sacar un valor de tipo string del cursor del primer campo que viene siendo el nombre,
            //esto se hace tambien para el segundo textView solicitando el valor del segundo campo que es el correo representado por la columna numero 2 */
            bindingItems.tvItemNombre.text = cursor!!.getString(1)
            bindingItems.tvItemEmail.text = cursor!!.getString(2)

            //Aqui habilitamos el evento setOnClickListener de cada elemento de la lista y mostraremos un mensaje por medio de un toast el nombre y correo del item
            //persionado del listView
            view.setOnClickListener {
                //el this@ActivtityLista estamos haciendo referencia al contexto de la actividad llamado ActivityLista
                Toast.makeText(this@ActivityLista, "${bindingItems.tvItemNombre.text}, ${bindingItems.tvItemEmail.text}", Toast.LENGTH_SHORT).show()
            }
        }

    }
}