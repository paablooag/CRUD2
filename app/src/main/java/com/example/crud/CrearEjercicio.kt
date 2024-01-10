package com.example.crud

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class CrearEjercicio : AppCompatActivity(), CoroutineScope {

    private lateinit var nombre:EditText
    private lateinit var repeticiones:EditText
    private lateinit var series:EditText
    private lateinit var imagen:ImageView
    private lateinit var crear:Button
    private lateinit var volver:Button
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private var url_maquina: Uri? = null
    private lateinit var lista_ejercicios: MutableList<Ejercicio>


    private lateinit var job: Job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_ejercicio)
        val this_activity = this

        job = Job()
        nombre = findViewById(R.id.nombre)
        series = findViewById(R.id.series)
        repeticiones = findViewById(R.id.repeticiones)
        imagen = findViewById(R.id.imagen)
        crear = findViewById(R.id.crear)
        volver = findViewById(R.id.volver)

        db_ref = FirebaseDatabase.getInstance().reference
        st_ref = FirebaseStorage.getInstance().reference
        lista_ejercicios = Utilidades.obtenerListaEjercicios(db_ref)

        crear.setOnClickListener{
            if(nombre.text.toString().trim().isNullOrEmpty() ||
                series.text.toString().trim().isNullOrEmpty() ||
                    repeticiones.toString().trim().isNullOrEmpty() ||
                        url_maquina == null) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el formulario", Toast.LENGTH_SHORT
                ).show()
            }else if (Utilidades.existeEjercicio(lista_ejercicios, nombre.text.toString().trim())){
            Toast.makeText(applicationContext, "Ese ejercicio ya existe", Toast.LENGTH_SHORT).show()
        }else{
                var nuevo_ejercicio:Ejercicio?=null
                val id_ejercicio = db_ref.child("ejercicios").child("series").push().key

                st_ref.child("ejercicios").child("series").child("imagenes").child(id_ejercicio!!).putFile(url_maquina!!).addOnSuccessListener {
                    uploadTask->
                    st_ref.child("ejercicios").child("series")
                    .child("imagenes").child(id_ejercicio).downloadUrl.addOnSuccessListener {
                    uri: Uri->

                   nuevo_ejercicio = Ejercicio(id_ejercicio,nombre.text.toString().toUpperCase(),repeticiones.text.toString().toInt(), series.text.toString().toInt(),
                         uri.toString())

                    db_ref.child("ejercicios").child("series").child(id_ejercicio).setValue(nuevo_ejercicio)

                    Toast.makeText(applicationContext,"Ejercicio creado", Toast.LENGTH_SHORT).show()

                }}


            var id_generado:String?= db_ref.child("ejercicios").child("series").push().key

            launch {
                val url_imagen_firebase=
                    Utilidades.guardarImagen(st_ref, id_generado!!,url_maquina!!)

                Utilidades.escribirEjercicio(
                    db_ref, id_generado!!,
                    nombre.toString().trim().toUpperCase(),
                    repeticiones.toString().trim().toInt(),
                    series.toString().trim().toInt(),
                    url_imagen_firebase
                )
                Utilidades.tostadaCorrutina(
                    this_activity,
                    applicationContext,
                    "Ejercicio creado"
                )
                val activity = Intent(applicationContext, CrearEjercicio::class.java)
                startActivity(activity)
            }

            }
        }
        volver.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)
        }

        imagen.setOnClickListener {

            accesoGaleria.launch("image/*")
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }


    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    {uri: Uri ->
        if(uri!=null){
            url_maquina = uri
            imagen.setImageURI(uri)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    fun retroceder(view: View) {
        val newintent=Intent(this, MainActivity::class.java)
        startActivity(newintent)
    }
}
