package com.example.crud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun CrearEjercicios(view: View) {
        val newintent = Intent(this, CrearEjercicio::class.java)
        startActivity(newintent)
    }
    fun VerEjercicios(view: View) {

    }
}