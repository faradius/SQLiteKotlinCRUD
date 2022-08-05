package com.example.sqlitekotlin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/*Creamos una clase llamada SQLiteHelper que extendera de la clase SQLiteOpenHelper para poder hacer uso de las declaraciones de SQlite en el proyecto de kotlin,
//La clase SQLiteOpenHelper necesita un contexto por lo tanto en la clase SQLiteHelper pedimos como parametro el contexto, ademas, en la clase SQLiteOpenHelper
//nos pide como vamos a nombrar nuestra base de datos por lo que la llamaremos amigos.db, despues no pide un factory (no se que sea) y le ponemos null, despues ponemos la version
//de la base de datos que estamos creando en este caso seria la version 1, despues implementamos los metodos onCreate y onUpgrade*/
class SQLiteHelper(context:Context): SQLiteOpenHelper(context,"amigos.db",null,1) {

    /*Esta función se ejecuturá cuando la base de datos no exista y haya que crear por primera vez o si ha sufrido cambios la base de datos se tendrá que actualizar la version
    //para que se ejecute el segundo metodo que viene siendo el onUpgrade*/

    //En esta primera función hacemos la creación de la tabla amigos
    override fun onCreate(db: SQLiteDatabase?) {
        //En esta variable almacenamos la query o sentencia SQL, en ella indicaremos los campos que este tendrá, su primary key que será autoincrementable
        //y los tipos de datos que almacenará cada campo
        var ordenCreacion = "CREATE TABLE amigos " + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT, email TEXT)"
        //Despues ejecutamos la setencia SQL con el metodo execSQL
        db!!.execSQL(ordenCreacion)
    }

    //En este metodo se ejecutará instrucciones SQL para actualizar la estuctura de la base de datos por lo que si detecta una nueva version hará
    //las instrucciones que tiene este metodo
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //Se guarda en una variable la setencia SQL de borrar la tabla amigos si esta tabla existiera
        val ordenBorrado = "DROP TABLE IF EXISTS amigos"
        //Despues ejecutamos el query de borrado
        db!!.execSQL(ordenBorrado)
        //y volvemos a crear la tabla haciendo la llamada onCreate
        onCreate(db)

        /*Nota: no es recomendable hacer un borrado de la tabla en un entorno de producción por que borrará su contenido por lo que se busca una mejor opción mas
        //coveniente para actualizar la estructura de la base, tal vez una setencia de ALTER TABLE, si es posible ejecutar la setencia de borrado de tablas y volverlas
        //a crear pero en un etorno de pruebas, "Aun no se como manejar la actualización de la estructura de la base de datos en base a la version anterior y la nueva version"*/
    }

    //En esta parte del codigo empezaremos a crear funciones que serán la setencias SQL que nos permitirá realizar las operaciones basicas del CRUD hacia la base de datos

    //Empezaremos primero con la función agregarDatos, esta nos permitirá crear nuevos registros a la base de datos, esta función nos pedirá nos parametros que será
    //El nombre y correo ambos de tipo String
    fun agregarDatos(nombre:String, email: String){
        //Creamos una variable que contendrá el objeto llamado ContentValues() este se encargará de guardar los valores de cada campo de la tabla
        val datos = ContentValues()
        //mapeamos o asignamos los valores obtenidos por medio del metodo con su respectivo campo
        //La interpretación de esta linea de codigo datos.put("nombre", nombre) es el siguiente por medio del ContentValues le guardaremos un dato
        //que es llamado nombre y le asignamos su valor correspondiente
        datos.put("nombre", nombre)
        datos.put("email", email)

        //Despues ponemos la base de datos en modo escritura para que sepa que es lo que va hacer SQLite si va a leer o escribir en la base
        val db = this.writableDatabase
        //Despues realizamos una insercción de datos a la base con el metodo insert y este nos pedirá el nombre de la tabla, despues en el nullColumnHack
        // significa que si los valores de los campos queremos que los establezca con un null, esto es en el caso de que los valores esten vacios, pero
        // como no van a estar vacios pues le colocamos el null y por ultimo los datos que se van insertar en la tabla
        db.insert("amigos",null, datos)
        //finalmente cerramos la conexión de la base de datos
        db.close()
    }

    //Con este metodo vamos a borrar un registro de la base de datos y por lo tanto requeriremos el id de la persona por lo que se pedirá este dato como parametro
    //del metodo y este nos devolvera el resultado en un entero
    fun borrarDato(id: Int): Int{
        //Creamos un variable para meter los id's de las personas que queramos borrar
        val args = arrayOf(id.toString())

        //Ponemos la base de datos en modo de escritura ya que se borrará un registro en la base de datos y esto modificará la información de la tabla
        val db = this.writableDatabase

        //Aqui realizamos el borrado del registro de la información de la persona con el metodo delete especificando la tabla como primer parametro, despues
        //nos pedirá en base a que condición será borrado?, esto será en base al id del usuario indicando de la siguiente forma "_id=?" el _id es el nombre
        //que le pusimos al campo y el ? indica que voy a borrar algo y ese algo viene siendo el tercer parametro que es la variable args, este contiene
        //los id's de las personas que queremos borrar de la base de datos, forzosamente android studio nos pide que sea un arreglo ya sea de strings o de int
        val result= db.delete("amigos","_id = ?", args)

        //Aqui coloque otra forma de realizar el borrado de un registro en la base, sin embargo este metodo no se puede contabilizar cuantos registros fueron borrados
        //y de la forma anterior si
        //db.execSQL("DELETE FROM amigos WHERE _id = ?", args)

        //Despues cerramos la conexión a la base de datos
        db.close()
        //finalmente debolvemos el resultado de la operación
        return result
    }

    //En este ultimo metodo vamos a realizar la actualización de los datos de la persona y para este ejemplo solo tenemos 3 campos los cuales son id, nombre y correo
    //por lo que es necesario pedir esto por medio de los parametros de la función
    fun modificarDatos(id: Int, nombre:String, email: String){
        //Creamos un variable para meter los id's de las personas que queramos modificar
        val args = arrayOf(id.toString())

        //mapeamos las columnas con sus respectivos valores que queramos modificar
        val datos = ContentValues()
        datos.put("nombre", nombre)
        datos.put("email", email)

        //colocamos la base de datos en modo de escritura
        val db = this.writableDatabase

        //Realizamos la actualización de los datos de la persona con el metodo update y los parametros que nos pide en este metodo es similar al del borrado con la unica
        //diferencia es que aqui aparte de inidicar el nombre de la tabla, despues sobre que condición se va a borrar el registro (en este caso sobre el id persona) y
        //al final se le colocan los id's de las personas que van a ser modificados, aparte de esto necesitamos agregar un parametro mas que es la variable datos, este contiene
        // la información sobre las columnas que van hacer modificados y el valor de cada columna
        db.update("amigos",datos,"_id = ?", args)
        //por ultimo cerramos la conexión de la base
        db.close()
    }

}