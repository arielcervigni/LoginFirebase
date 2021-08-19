package com.acervigni.login

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.acervigni.login.model.Persona
import java.lang.Exception

class DbHelper(
    context: Context, factory: SQLiteDatabase.CursorFactory?
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {

        private val DATABASE_NAME = "login.db"
        private val DATABASE_VERSION = 1

        val TABLE_NAME = "personas"
        val COLUMN_ID = "id"
        val COLUMN_NOMBRE = "nombre"
        val COLUMN_EDAD = "edad"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        var createTable =
            ("CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NOMBRE + " TEXT, " + COLUMN_EDAD + " INTEGER )")

        db?.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun savePersona(persona: Persona): Boolean {

        try {
            val db = this.writableDatabase

            val values = ContentValues()

            values.put("nombre", persona.nombre)
            values.put("edad", persona.edad)

            db.insertOrThrow(TABLE_NAME, null, values)
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error al guardar usuario" + e.message)
            return false
        }
    }

    fun crearPersona(cursor: Cursor): Persona {
        val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
        val nombre = cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE))
        val edad = cursor.getInt(cursor.getColumnIndex(COLUMN_EDAD))

        return Persona(id, nombre, edad)
    }

    fun obtenerPersonas(): ArrayList<Persona>? {
        val personas: ArrayList<Persona> = ArrayList()

        try {

            val db = this.readableDatabase
            val query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID

            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val p: Persona = crearPersona(cursor)
                    personas.add(p)

                } while (cursor.moveToNext())
            }

            return personas
        } catch (e: Exception) {
            Log.e("ERROR", "Al obtener los usuarios")
        }
        return personas
    }

    fun borrarTablaPersonas(): Boolean {

        try {
            val db = this.writableDatabase
            db.delete(TABLE_NAME, null, null)
            return true
        } catch (e: Exception) {
            Log.e("ERROR", "Error al borrar la tabla Usuarios " + e.message)
            return false
        }
    }
}