package com.example.webdogapiV2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {

    //creamos una sola petición, que será la lista de imágenes, pero podemos tener muchas más funciones con respuestas distintas
    @GET
    fun getCharacterByName(@Url url:String): Call<DogsResponse>
}