package com.example.webdogapiV2

import com.google.gson.annotations.SerializedName

//Creamos un data class DogsResponse y tendrá dos campos, los mismos que nos devolvía la respuesta de la petición.
data class DogsResponse (@SerializedName("status") var status:String, @SerializedName("message") var images: List<String>)