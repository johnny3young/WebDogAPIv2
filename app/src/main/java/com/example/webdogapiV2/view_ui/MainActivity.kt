package com.example.webdogapiV2.view_ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.inputmethod.InputMethodManager
import com.example.webdogapiV2.APIService
import com.example.webdogapiV2.DogsAdapter
import com.example.webdogapiV2.DogsResponse
import com.example.webdogapiV2.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jetbrains.anko.yesButton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Para que el SearchView implementado en el Recycler primero hay que implementemos y hacer que nuestra activity
//implemente los métodos necesarios. android.support.v7.widget.SearchView.OnQueryTextListener
//Añadimos en la función onCreate() su listener e implementamos sus métodos onQueryTextChange y OnQueryTextSubmit
class MainActivity : AppCompatActivity(), android.support.v7.widget.SearchView.OnQueryTextListener {

    // iniciaremos el recyclerView con dicha lista.
    lateinit var imagesPuppies:List<String>
    lateinit var dogsAdapter: DogsAdapter

    //OnQueryTextSubmit, que se lanzará una vez el usuario pulse en la acción de que ha terminado de escribir,
    //por ello nos devuelve una String, que será el texto introducido
    override fun onQueryTextSubmit(p0: String): Boolean {
        searchByName(p0.toLowerCase())
        return true
    }

    //onQueryTextChange, nos avisará cada vez que el usuario añada un carácter, pero este no lo utilizaremos porque
    //solo buscaremos la raza una vez el usuario termine de escribir
    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Añadimos el listener
        searchBreed.setOnQueryTextListener(this)
    }

    //Creamos un método que nos devuelve un objeto Retrofit, que será el encargado de la petición.
    //Para poder crearlo llamaremos al Builder() de la librería retrofit, en la cual hay que pasarle
    //el baseUrl, que es la url donde haremos la petición, pero solo la base, es decir
    //https://dog.ceo/api/breed/ (importante el / final o nos dará error).
    //antes del build, añadimos la factoría que convierte el Json en nuestra clase DogsResponse, que es la línea addConverterFactory.
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dog.ceo/api/breed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    //Debemos entender que una petición se hace de manera asíncrona es decir, nosotros empezamos la petición y
    // como no sabemos lo que puede tardar, una vez tenga los datos nos avisará. Este proceso podría bloquear
    // la aplicación, por que usamos los hilos. Los hilos nos permiten crear subprocesos dentro de la app sin bloquear
    //el hilo principal, que es el que gestiona la interfaz. Por ello debemos crear un hilo que gestione la llamada
    //llamaremos a la función doAsync{} todolo que hagamos adentro se gestionará en otro hilo. Todoesto es gracias a ANKO
    //crear una variable llamada call que se encargará de llamar a la función getRetrofit() que creamos previamente,
    //seguido de la interfaz que contiene la llamada que queremos y terminamos pasando la query (que será la raza del
    //perro que hemos puesto en el buscador) y llamando posteriormente a la función execute().
    //El contenido de esa variable será la respuesta de nuestra API, pero Retrofit nos devolverá un objeto genérico con
    //más contenido del que buscamos, por lo que creamos una variable puppies que llamará a la variable anterior call
    //seguido de .body() que permite extraer solo la información de nuestro interés. Seguidamente como es un objeto
    // genérico le debemos decir que es del tipo DogsResponse.
    // lo siguiente será modificar la vista para añadir la nueva información, pero como he dicho anteriormente,
    //la parte visual se trabaja en el hilo principal por lo que llamaremos dentro del doAsync{} a la función uiThread{}
    // que nos permite ejecutar parte del código en el hilo principal.
    //comprobaremos si el estado es success, lo que significa que el API nos ha devuelto los valores correctos y si es así
    //iniciaremos la vista o mostraremos un diálogo de error y para terminar, independientemente del resultado, ocultaremos el teclado.
    private fun searchByName(query: String) {
        doAsync {
            val call = getRetrofit().create(APIService::class.java).getCharacterByName("$query/images").execute()
            val puppies = call.body() as DogsResponse
            uiThread {
                if(puppies.status == "success") {
                    initCharacter(puppies)
                }else{
                    showErrorDialog()
                }
                hideKeyboard()
            }
        }
    }
    private fun showErrorDialog() {
        alert("Ha ocurrido un error, inténtelo de nuevo.") {
            yesButton { }
        }.show()
    }

    private fun hideKeyboard(){
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(viewRoot.windowToken, 0)
    }

    //una vez la petición nos responda podremos realizar dos opciones, la primera cargar los elementos obtenidosen el recyclerView.
    //Primero nos aseguramos que el estado sea success por si cometemos el error de llamar al método desde otro lado,
    //si es así, asignaremos el resultado de la respuesta en una array genérica que hemos creado en la parte superior de la clase.
    //lateinit var imagesPuppies:List<String>
    private fun initCharacter(puppies: DogsResponse) {
        if(puppies.status == "success"){
            imagesPuppies = puppies.images
        }
        dogsAdapter = DogsAdapter(imagesPuppies)
        rvDogs.setHasFixedSize(true)
        rvDogs.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rvDogs.adapter = dogsAdapter
    }


}
