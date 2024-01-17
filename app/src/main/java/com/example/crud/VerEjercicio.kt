package com.example.crud


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
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
    private lateinit var volver: Button
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Ejercicio>
    private lateinit var adaptador: EjercicioAdaptador
    private lateinit var db_ref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_ejercicio)
        volver = findViewById(R.id.volver)

        lista = mutableListOf()
        db_ref = FirebaseDatabase.getInstance().getReference()


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
        recycler.addItemDecoration(DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL))

        volver.setOnClickListener {
            val activity = Intent(applicationContext, MainActivity::class.java)
            startActivity(activity)
            //finish()
        }



    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ejercicios, menu)
        val item = menu?.findItem(R.id.search)
        val searhView = item?.actionView as SearchView


        searhView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adaptador.filter.filter((newText))
                return true
            }
        })

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                adaptador.filter.filter("")
                return true
            }

        })


        return super.onCreateOptionsMenu(menu)
    }




}