package com.example.crud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class VerEjercicio : AppCompatActivity() {
    private lateinit var volver:Button
    private lateinit var recycle : RecyclerView
    private lateinit var lista:MutableList<Ejercicio>
    private lateinit var adapter: EjercicioAdaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_ejercicio)
    }
}