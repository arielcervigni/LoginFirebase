package com.acervigni.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.acervigni.login.databinding.ActivityAuthBinding
import com.acervigni.login.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email : String? = intent.getStringExtra("email")
        val provider: String? = intent.getStringExtra("provider")
        iniciarMain(email?: "" ,provider?: "")
    }

    private fun iniciarMain(email: String, provider:String) {
        title = "Home"
        binding.hEmail.text = email
        binding.hProveedor.text = provider

        binding.hCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }
}