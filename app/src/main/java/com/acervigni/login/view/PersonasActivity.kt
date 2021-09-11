package com.acervigni.login.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.acervigni.login.DbHelper
import com.acervigni.login.model.Persona
import com.acervigni.login.adapter.PersonaAdapter
import com.acervigni.login.databinding.ActivityPersonasBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PersonasActivity : AppCompatActivity() {
    lateinit var binding : ActivityPersonasBinding
    var personas: ArrayList<Persona>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ESTO OBTIENE DESDE SQLite */
        //val db = DbHelper (this,null)
        //personas = db.obtenerPersonas()
        personas = intent.getSerializableExtra("personas") as ArrayList<Persona>?
        Log.d("ARIEL", "Personas" + personas.toString())

        iniciarPersonasActivity()
    }

    @SuppressLint("WrongConstant")
    private fun iniciarPersonasActivity () {
        title = "Personas"

        if(personas != null) {
            binding.rvPersonas.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL,false)
            binding.rvPersonas.adapter = PersonaAdapter(personas!!)
        } else {
            Toast.makeText(this,"No hay personas para mostrar",Toast.LENGTH_SHORT).show()
        }

        binding.btnPVolver.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}