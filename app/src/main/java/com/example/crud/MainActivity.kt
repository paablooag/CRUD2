package com.example.crud

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.checkerframework.checker.units.qual.C
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private lateinit var ver: Button
    private lateinit var androidId: String
    private lateinit var generador: AtomicInteger
    private lateinit var db_ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ver = findViewById(R.id.botonVerEjercicio)

        ver.setOnClickListener {
            val activity = Intent(applicationContext, VerEjercicio::class.java)
            startActivity(activity)
        }


        crearCanalNotificaciones()
        androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        db_ref = FirebaseDatabase.getInstance().reference
        generador = AtomicInteger(0)

        //Controlador de notificaciones
        db_ref.child("ejercicios").child("series")
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_ejercicio = snapshot.getValue(Ejercicio::class.java)
                    if (!pojo_ejercicio!!.notificacion_usuario.equals(androidId) && pojo_ejercicio.estado_noti!!.equals(
                            Estado.CREADO
                        )
                    ) {
                        db_ref.child("ejercicios").child("series").child(pojo_ejercicio.id!!)
                            .child("estado_noti").setValue(Estado.NOTIFICADO)
                        generarNotificacion(
                            generador.incrementAndGet(), pojo_ejercicio,
                            "Se ha creado un nuevo ejercicio, ${pojo_ejercicio.nombre}",
                            "Nuevos datos en la aplicacion",
                            VerEjercicio::class.java
                        )

                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val pojo_ejercicio = snapshot.getValue(Ejercicio::class.java)
                    if (!pojo_ejercicio!!.notificacion_usuario.equals(androidId) && pojo_ejercicio.estado_noti!!.equals(
                            Estado.MODIFICADO
                        )
                    ) {
                        db_ref.child("ejercicios").child("series").child(pojo_ejercicio.id!!)
                            .child("estado_noti").setValue(Estado.NOTIFICADO)
                        generarNotificacion(
                            generador.incrementAndGet(), pojo_ejercicio,
                            "Se ha modificado un ejercicio, ${pojo_ejercicio.nombre}",
                            "Nuevos datos en la aplicacion",
                            EditarEjercicio::class.java
                        )
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val pojo_ejercicio = snapshot.getValue(Ejercicio::class.java)
                    if (!pojo_ejercicio!!.notificacion_usuario.equals(androidId)) {
                        db_ref.child("ejercicios").child("series").child(pojo_ejercicio.id!!)
                            .child("estado_noti").setValue(Estado.NOTIFICADO)
                        generarNotificacion(
                            generador.incrementAndGet(), pojo_ejercicio,
                            "Se ha eliminado un ejercicio, ${pojo_ejercicio.nombre}",
                            "Nuevos datos en la aplicacion",
                            VerEjercicio::class.java
                        )

                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }


    private fun generarNotificacion(
        id_noti: Int,
        pojo: Parcelable,
        contenido: String,
        titulo: String,
        destino: Class<*>
    ) {
        val id = "Canal de prueba"
        val intent = Intent(applicationContext, destino)
        intent.putExtra("ejercicio", pojo)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificacion = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.baseline_message_24)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setSubText("sistema de informacion")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            )
                notify(id_noti, notificacion)
        }

    }

    private fun crearCanalNotificaciones() {
        val nombre = "canal_basico"
        val id = "Canal de prueba"
        val descripcion = "Notificacion basica"
        val importancia = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(id, nombre, importancia).apply {
            this.description = descripcion
        }

        val nm: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(channel)
    }


}