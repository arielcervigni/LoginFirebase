package com.acervigni.login.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acervigni.login.model.Persona
import com.acervigni.login.R

class PersonaAdapter (val personas: ArrayList<Persona>) :
    RecyclerView.Adapter<PersonaAdapter.ViewHolder>() {

    class ViewHolder(view: View) :RecyclerView.ViewHolder(view) {

        var id: TextView
        var nombre: TextView
        var edad: TextView

        init {
            id = view.findViewById(R.id.tv_p_id)
            nombre = view.findViewById(R.id.tv_p_nombre)
            edad = view.findViewById(R.id.tv_p_edad)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.peronas_layout,parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.id.text = "Id: " + personas[position].id.toString()
        holder.nombre.text =  "Nombre: " + personas[position].nombre
        holder.edad.text = "Edad: " + personas[position].edad
    }

    override fun getItemCount(): Int {
        return personas.size
    }
}