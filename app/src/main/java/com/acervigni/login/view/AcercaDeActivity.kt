package com.acervigni.login.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.acervigni.login.R
import com.acervigni.login.databinding.ActivityAcercaDeBinding
import com.squareup.picasso.Picasso

class AcercaDeActivity : AppCompatActivity() {
    lateinit var binding : ActivityAcercaDeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcercaDeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val img = getDrawable(R.drawable.ac)
        img?.setTint(getColor(R.color.azulFB))

        binding.ivAcImagen.setImageDrawable(img)

        binding.tvAcInfo.text = "Aplicación creada por Ariel Cervigni \n arielcervigni@gmail.com " +
                "\n Mar del Plata, Argentina"

        Picasso.with(this).load("https://i.ibb.co/0YrX7FV/acazul.png")

        binding.btnAcVolver.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}


