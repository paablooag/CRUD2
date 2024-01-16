package com.example.crud

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.gms.fido.fido2.api.common.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class Utilidades {
    companion object {

        fun existeEjercicio(ejercicios: List<Ejercicio>, nombre: String): Boolean {
            return ejercicios.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }

        fun obtenerListaEjercicios(db_ref: DatabaseReference): MutableList<Ejercicio> {
            var lista = mutableListOf<Ejercicio>()

            db_ref.child("ejercicios")
                .child("series")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { hijo: DataSnapshot ->
                            val pojo_ejercicio = hijo.getValue(Ejercicio::class.java)
                            lista.add(pojo_ejercicio!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })
            return lista
        }
        fun escribirEjercicio(db_ref: DatabaseReference, id: String, nombre: String, series: Int, repeticiones: Int, url_firebase: String) =
            db_ref.child("ejercicios").child("series").child(id).setValue(
                Ejercicio(
                    id,
                    nombre,
                    series,
                    repeticiones,
                    url_firebase
                ))

        suspend fun guardarImagen(sto_ref: StorageReference, id: String, imagen: Uri): String {
            lateinit var url_imagen_firebase: Uri

            url_imagen_firebase = sto_ref.child("ejercicios").child("series").child("imagenes").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return url_imagen_firebase.toString()
        }
        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto:String){
            activity.runOnUiThread{
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        fun animacion_carga(contexto: Context): CircularProgressDrawable{
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()
            return animacion
        }

        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): com.bumptech.glide.request.RequestOptions {
            val options = com.bumptech.glide.request.RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.imagen_generica)
                .error(R.drawable.baseline_error_24)
            return options
        }
    }
}
