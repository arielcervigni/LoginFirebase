package com.acervigni.login.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.acervigni.login.DbHelper
import com.acervigni.login.model.Persona
import com.acervigni.login.R
import com.acervigni.login.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var email : String? = intent.getStringExtra("email")
        var provider: String? = intent.getStringExtra("provider")

        if(email.isNullOrEmpty() || provider.isNullOrEmpty()){
            prefs = getSharedPreferences(getString(R.string.prefs),Context.MODE_PRIVATE)
            email = prefs.getString("email","")
            provider = prefs.getString("provider","")
        }
        iniciarMain(email?: "" ,provider?: "")

        // Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider", provider.toString())
        prefs.apply()

    }

    private fun iniciarMain(email: String, provider:String) {
        title = "Home"
        binding.hEmail.text = email
        binding.hProveedor.text = provider


        binding.hCerrarSesion.setOnClickListener {
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.clear()
            editor.apply()
            FirebaseAuth.getInstance().signOut()
            irALogin()
        }

        binding.hGuardarPersona.setOnClickListener {

            if(!binding.etHNombre.text.isNullOrEmpty() && !binding.etHEdad.text.isNullOrEmpty()){

                val persona = Persona (0,binding.etHNombre.text.toString(),binding.etHEdad.text.toString().toInt())

                val db = DbHelper(this,null)
                if(db.savePersona(persona)) {
                    binding.tvHRta.text = "Persona Guardada: " + binding.etHNombre.text.toString() + ", " +binding.etHEdad.text.toString()
                    binding.tvHRta.visibility = View.VISIBLE
                    binding.etHEdad.setText("")
                    binding.etHNombre.setText("")

                }
            } else
                Toast.makeText(this,"Ingrese nombre y edad",Toast.LENGTH_SHORT).show()
        }

        binding.hVerPersonas.setOnClickListener {
            irAListaPersonas()
        }

    }

    private fun irALogin() {
        startActivity(Intent(this,AuthActivity::class.java))
        finish()
    }

    private fun irAListaPersonas(){
        startActivity(Intent(this, PersonasActivity::class.java))
        finish()
    }
}