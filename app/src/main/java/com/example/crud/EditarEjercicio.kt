package com.example.crud

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EditarEjercicio : AppCompatActivity(), CoroutineScope {

    private lateinit var ejercicio : EditText
    private lateinit var series : EditText
    private lateinit var repeticiones : EditText
    private lateinit var imagen: ImageView
    private lateinit var modificar: ImageView
    private lateinit var volver: ImageView
    private lateinit var ratingBar: RatingBar
    private var url_imagen: Uri? = null
    private lateinit var db_ref: DatabaseReference
    private lateinit var st_ref: StorageReference
    private  lateinit var  pojo_ejercicio:Ejercicio
    private lateinit var lista_ejercicios: MutableList<Ejercicio>


    private lateinit var job: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_ejercicio)


        val this_activity = this
        job = Job()

        pojo_ejercicio = intent.getParcelableExtra<Ejercicio>("ejercicios")!!


        ejercicio = findViewById(R.id.ejercicio)
        series = findViewById(R.id.series)
        repeticiones = findViewById(R.id.repeticiones)
        imagen = findViewById(R.id.imagen)
        modificar = findViewById(R.id.modificar)
        volver = findViewById(R.id.volver)
        ejercicio.setText(pojo_ejercicio.nombre)
        ratingBar = findViewById(R.id.rating)
        series.setText(pojo_ejercicio.series.toString())
        repeticiones.setText(pojo_ejercicio.repeticiones.toString())



        Glide.with(applicationContext)
            .load(pojo_ejercicio.imagen)
            .apply(Utilidades.opcionesGlide(applicationContext))
            .transition(Utilidades.transicion)
            .into(imagen)

        db_ref = FirebaseDatabase.getInstance().getReference()
        st_ref = FirebaseStorage.getInstance().getReference()

        lista_ejercicios = Utilidades.obtenerListaEjercicios(db_ref)

        modificar.setOnClickListener {

            if (ejercicio.text.toString().trim().isEmpty() ||
                series.text.toString().trim().isEmpty() ||
                repeticiones.text.toString().trim().isEmpty()||
                ratingBar.rating == 0.0f
            ) {
                Toast.makeText(
                    applicationContext, "Faltan datos en el formulario", Toast.LENGTH_SHORT
                ).show()

            }
            else if (Utilidades.existeEjercicio(
                    lista_ejercicios,
                    ejercicio.text.toString().trim()
                ) && ejercicio.text.toString().trim() != pojo_ejercicio.nombre
            ) {
                Toast.makeText(
                    applicationContext,
                    "Ese ejercicio ya existe",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else {
                val androidId =
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                var url_imagen_firebase = String()
                launch {
                    if(url_imagen == null){
                        url_imagen_firebase = pojo_ejercicio.imagen!!
                    }else{
                        url_imagen_firebase =
                        Utilidades.guardarImagen(st_ref, pojo_ejercicio.id!!, url_imagen!!)
                    }

                    Utilidades.escribirEjercicio(
                        db_ref, pojo_ejercicio.id!!,
                        ejercicio.text.toString().trim(),
                        series.text.toString().trim().toInt(),
                        repeticiones.text.toString().trim().toInt(),
                        url_imagen_firebase,
                        pojo_ejercicio.fecha!!,
                        ratingBar.rating,
                        Estado.MODIFICADO,
                        androidId
                    )
                    Utilidades.tostadaCorrutina(
                        this_activity,
                        applicationContext,
                        "Ejercicio modificado"
                    )
                    val activity = Intent(applicationContext, MainActivity::class.java)
                    startActivity(activity)
                }
            }
        }

        volver.setOnClickListener {
            val activity = Intent(applicationContext, VerEjercicio::class.java)
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
            url_imagen = uri
            imagen.setImageURI(uri)
        }


    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

}
