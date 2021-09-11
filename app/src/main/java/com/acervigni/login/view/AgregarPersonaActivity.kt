package com.acervigni.login.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.acervigni.login.R
import com.acervigni.login.databinding.ActivityAgregarPersonaBinding

class AgregarPersonaActivity : AppCompatActivity() {
    lateinit var binding : ActivityAgregarPersonaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarPersonaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.aBtnVolver.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}