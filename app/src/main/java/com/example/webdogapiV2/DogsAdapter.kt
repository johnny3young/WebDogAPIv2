package com.example.webdogapiV2

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsAdapter (val images: List<String>) : RecyclerView.Adapter<DogsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_dog,p0,false))
    }

    override fun getItemCount(): Int {
        return  images.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val item = images[p1]
        p0.bind(item)
    }

    //En la función bind() del ViewHolder, hemos cogido la imagen de la vista y hemos llamado a una
    // función de extensión que hemos creado llamada fromUrl(). Esta extensión la crearemos en un fichero nuevo
    //(yo lo suelo llamar extensions ej ImageExtensions, DateExtensions).
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view){
        fun bind(image: String) {
            itemView.ivDog.fromUrl(image)
        }

        //La extensión aprovecha la librería Picasso para convertir la url de la imagen en un elemento visual.
        fun ImageView.fromUrl(url:String){
            Picasso.get().load(url).into(this)
        }

    }


}