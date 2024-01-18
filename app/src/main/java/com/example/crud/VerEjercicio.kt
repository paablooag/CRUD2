package com.example.crud


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class VerEjercicio : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Ejercicio>
    private lateinit var adaptador: EjercicioAdaptador
    private lateinit var db_ref: DatabaseReference
    private lateinit var filter: ImageView
    private lateinit var anadir: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_ejercicio)
        filter = findViewById(R.id.filter)
        anadir = findViewById(R.id.anadir)
        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()
        var searchView = findViewById<SearchView>(R.id.searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter.filter(newText)
                return true
            }
        })

        anadir.setOnClickListener {
            val activity = Intent(applicationContext, CrearEjercicio::class.java)
            startActivity(activity)
        }
        db_ref.child("ejercicios")
            .child("series")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach { hijo: DataSnapshot?
                        ->
                        val pojo_ejercicio = hijo?.getValue(Ejercicio::class.java)
                        lista.add(pojo_ejercicio!!)

                    }
                    recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }


            })

        adaptador = EjercicioAdaptador(lista)
        recycler = findViewById(R.id.lista_ejercicios)
        recycler.adapter = adaptador
        recycler.layoutManager = LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)
        recycler.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )

        filter.setOnClickListener() {
            lista.sortBy { it.rating }
            //carga la lista ordenada
            recycler.adapter?.notifyDataSetChanged()
            adaptador = EjercicioAdaptador(lista)
            recycler.adapter = adaptador
            lista.reverse()

        }


    }

    fun retroceder(view: View) {
        val activity = Intent(applicationContext, MainActivity::class.java)
        startActivity(activity)
    }
}


