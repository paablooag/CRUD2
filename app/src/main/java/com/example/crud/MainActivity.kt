package com.example.crud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import org.checkerframework.checker.units.qual.C

class MainActivity : AppCompatActivity() {

    private lateinit var crear: Button
    private lateinit var ver: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        crear = findViewById(R.id.botonCrearEjercicio)
        ver = findViewById(R.id.botonVerEjercicio)

        ver.setOnClickListener {
            val activity = Intent(applicationContext, VerEjercicio::class.java)
            startActivity(activity)
        }

        crear.setOnClickListener {
            val activity = Intent(applicationContext, CrearEjercicio::class.java)
            startActivity(activity)
        }
    }

}