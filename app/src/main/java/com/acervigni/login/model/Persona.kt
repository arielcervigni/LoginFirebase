package com.acervigni.login.model

import java.io.Serializable

data class Persona(val id: String = "", val nombre: String, val edad: Int) : Serializable
