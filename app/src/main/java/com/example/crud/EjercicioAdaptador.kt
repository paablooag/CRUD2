package com.example.crud

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class EjercicioAdaptador(private val lista_ejercicio: MutableList<Ejercicio>):
    RecyclerView.Adapter<EjercicioAdaptador.EjercicioViewHolder>(), Filterable {


    private lateinit var contexto: Context
    private var lista_filtrada = lista_ejercicio
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):EjercicioAdaptador.EjercicioViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.layout_ejercicios,parent,false)
        contexto = parent.context
        return EjercicioViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder:EjercicioAdaptador.EjercicioViewHolder, position: Int) {
        val item_actual = lista_filtrada[position]
        holder.ejercicio.text = item_actual.nombre
        holder.series.text = item_actual.series.toString()
        holder.repeticiones.text = item_actual.repeticiones.toString()
        holder.rating.rating = item_actual.rating.toString().toFloat()

        val URL:String? = when(item_actual.imagen){
            ""-> null
            else -> item_actual.imagen
        }

        Glide.with(contexto)
            .load(URL)
            .apply(Utilidades.opcionesGlide(contexto))
            .transition(Utilidades.transicion)
            .into(holder.imagen)

        holder.editar.setOnClickListener {
            val newintent = Intent(contexto,EditarEjercicio::class.java)
            println("hasta aqui he llegado")
            newintent.putExtra("ejercicios", item_actual)
            println("hasta aqui tambien")
            contexto.startActivity(newintent)
        }

        holder.eliminar.setOnClickListener {
            val  db_ref = FirebaseDatabase.getInstance().getReference()
            val sto_ref = FirebaseStorage.getInstance().getReference()
            lista_filtrada.remove(item_actual)
            sto_ref.child("ejercicios").child("series")
                .child("imagenes").child(item_actual.id!!).delete()
            db_ref.child("ejercicios").child("series")
                .child(item_actual.id!!).removeValue()

            Toast.makeText(contexto,"Ejercicio borrado", Toast.LENGTH_SHORT).show()
        }



    }
    class EjercicioViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.item_imagen)
        val ejercicio: TextView = itemView.findViewById(R.id.item_ejercicio)
        val series: TextView = itemView.findViewById(R.id.item_series1)
        val repeticiones: TextView = itemView.findViewById(R.id.item_repeticiones1)
        val editar: ImageView = itemView.findViewById(R.id.item_editar)
        val eliminar: ImageView = itemView.findViewById(R.id.item_borrar)
        val rating: RatingBar = itemView.findViewById(R.id.item_rating)

    }
    override fun getItemCount(): Int = lista_filtrada.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val busqueda = p0.toString().lowercase()
                if (busqueda.isEmpty()) {
                    lista_filtrada = lista_ejercicio
                } else {
                    lista_filtrada = (lista_ejercicio.filter {
                        it.nombre.toString().lowercase().contains(busqueda)
                    }) as MutableList<Ejercicio>
                }

                val filterResults = FilterResults()
                filterResults.values = lista_filtrada
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }




}