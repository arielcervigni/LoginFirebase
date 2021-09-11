package com.acervigni.login.view

import android.Manifest.permission
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.acervigni.login.DbHelper
import com.acervigni.login.model.Persona
import com.acervigni.login.R
import com.acervigni.login.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.File
import android.graphics.BitmapFactory

import android.graphics.Bitmap

import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.content.DialogInterface

import android.content.pm.PackageManager
import android.provider.Settings

import androidx.annotation.NonNull
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE

import android.os.Build

import android.annotation.TargetApi

import com.google.android.material.snackbar.Snackbar








enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK
}

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var prefs: SharedPreferences
    lateinit var personas : ArrayList<Persona>

    private val APP_DIRECTORY = "MyPictureApp/"
    private val MEDIA_DIRECTORY = "Login/Picture"

    private val MY_PERMISSIONS = 100
    private val PHOTO_CODE = 200
    private val SELECT_PICTURE = 300
    private var path : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var email : String? = intent.getStringExtra("email")
        var provider: String? = intent.getStringExtra("provider")
        personas = ArrayList()

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


            /* Esto guarda en SQLite */
            if(!binding.etHNombre.text.isNullOrEmpty() && !binding.etHEdad.text.isNullOrEmpty()){


                val persona = Persona ("",binding.etHNombre.text.toString(),binding.etHEdad.text.toString().toInt())

                /* ESTO ES PARA GUARDAR EN FIRESTORE */
                val dbFs = Firebase.firestore

                dbFs.collection("personas")
                    .add(persona)
                    .addOnSuccessListener { documentReference ->
                        binding.tvHRta.visibility = View.VISIBLE
                        binding.tvHRta.text = "Persona Guardada: " + binding.etHNombre.text.toString() + ", " +binding.etHEdad.text.toString()
                    }
                    .addOnFailureListener { e ->
                        binding.tvHRta.visibility = View.VISIBLE
                        binding.tvHRta.text = "Error al guardar la persona. " + e.message
                    }

                /* ESTO ES PARA GUARDAR EN SQLite
                val db = DbHelper(this,null)
                if(db.savePersona(persona)) {
                    binding.tvHRta.text = "Persona Guardada: " + binding.etHNombre.text.toString() + ", " +binding.etHEdad.text.toString()
                    binding.tvHRta.visibility = View.VISIBLE
                    binding.etHEdad.setText("")
                    binding.etHNombre.setText("")

                } else {
                    binding.tvHRta.text = "Error al guardar la persona. "
                }*/
            } else
                Toast.makeText(this,"Ingrese nombre y edad",Toast.LENGTH_SHORT).show()



        }

        binding.hVerPersonas.setOnClickListener {
            irAListaPersonas()
        }


        binding.btnCamara.setOnClickListener {
            if(mayRequestStoragePermission())
                openCamera()
        }

        binding.btnGaleria.setOnClickListener {
            if(mayRequestStoragePermission())
            {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(intent, "Selecciona app de imagen"),
                    SELECT_PICTURE
                )
            }

        }

    }

    private fun irALogin() {
        startActivity(Intent(this,AuthActivity::class.java))
        finish()
    }

    private fun irAListaPersonas(){
        binding.progressBar.visibility = View.VISIBLE
        /* ESTO OBTIENE DESDE FIRESTORE */
        val dbFs = Firebase.firestore
        dbFs.collection("personas")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val map = document.data as HashMap<*, *>
                    val p = Persona (document.id, map["nombre"].toString(),map["edad"].toString().toInt())
                    personas.add(p)
                }

                if(personas.isNotEmpty()) {
                    val i = Intent(this, PersonasActivity::class.java)
                    i.putExtra("personas",personas)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(this,"No hay personas para mostrar.",Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"Error al obtener las personas " + exception.message,Toast.LENGTH_SHORT).show()
            }



    }


    private fun mayRequestStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED
        )
            return true

        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) ||
            shouldShowRequestPermissionRationale(CAMERA)) {

                Toast.makeText(this, "Los permisos son necesarios para poder usar la aplicación",
                Toast.LENGTH_SHORT).show()

        } else {
            requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA), MY_PERMISSIONS)
        }
        return false
    }

    private fun openCamera() {

        val file = File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY)

        var isDirectoryCreated : Boolean = file.exists()
        Log.d("Ariel","drCreated:" + isDirectoryCreated)

        if (!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs()

        Log.d("Ariel","drCreated:" + isDirectoryCreated)

        if (isDirectoryCreated) {
            Log.d("ARIEL", "isDirectoryCreated")
            val timestamp = System.currentTimeMillis() / 1000
            val imageName = "$timestamp.jpg"

            path = (Environment.getExternalStorageDirectory()
                .toString() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName)

            val newFile = File(path)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile))
            startActivityForResult(intent, PHOTO_CODE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("file_path", path)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        path = savedInstanceState.getString("file_path").toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PHOTO_CODE -> {
                    MediaScannerConnection.scanFile(
                        this, arrayOf(path), null
                    ) { path, uri ->
                        Log.i("ExternalStorage", "Scanned $path:")
                        Log.i("ExternalStorage", "-> Uri = $uri")
                    }
                    val bitmap = BitmapFactory.decodeFile(path)
                    binding.imImagen.setImageBitmap(bitmap)
                }
                SELECT_PICTURE -> {
                    val path = data?.data
                    binding.imImagen.setImageURI(path)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Permisos aceptados", Toast.LENGTH_SHORT).show()
            }
        } else {
            showExplanation()
        }
    }

    private fun showExplanation() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Permisos denegados")
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos")
        builder.setPositiveButton("Aceptar",
            DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            })
        builder.setNegativeButton("Cancelar",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                finish()
            })
        builder.show()
    }



}