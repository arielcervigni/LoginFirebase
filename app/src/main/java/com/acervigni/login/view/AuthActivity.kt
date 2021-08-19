package com.acervigni.login.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.acervigni.login.R
import com.acervigni.login.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    lateinit var binding : ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        generarAcciones()
        session()
    }

    private fun generarAcciones (){

        title = "Ingreso de sesión"
        binding.btnLRegistrar.setOnClickListener {

            if(!binding.etLUsername.text.isNullOrEmpty() && !binding.etLPassword.text.isNullOrEmpty())
            {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(binding.etLUsername.text.toString(),
                        binding.etLPassword.toString()).addOnCompleteListener {

                            if(it.isSuccessful)
                            {
                                irAlMain(it.result?.user?.email!!, ProviderType.BASIC)
                            } else {

                                Log.d("E",it.exception?.message.toString())
                                if(it.exception?.message.toString() == ("The email address is already in use by another account."))
                                    mostrarAlerta("El email ingresado ya se encuentra registrado.")
                                else
                                    mostrarAlerta("Se ha producido un error creando al usuario")
                            }

                    }

            } else {
                when {
                    binding.etLUsername.text.isNullOrEmpty() && binding.etLPassword.text.isNullOrEmpty() -> mostrarAlerta("Por favor ingrese un usuario y una contraseña.")
                    binding.etLUsername.text.isNullOrEmpty() -> mostrarAlerta("Por favor ingrese un usuario.")
                    binding.etLPassword.text.isNullOrEmpty() -> mostrarAlerta("Por favor ingrese una contraseña.")
                    else -> mostrarAlerta("Se ha producido un error creando al usuario")
                }
            }
        }

        binding.btnLIniciar.setOnClickListener {
            if(!binding.etLUsername.text.isNullOrEmpty() && !binding.etLPassword.text.isNullOrEmpty())
            {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(binding.etLUsername.text.toString(),
                        binding.etLPassword.toString()).addOnCompleteListener {

                        if(it.isSuccessful)
                        {
                            irAlMain(it.result?.user?.email!!, ProviderType.BASIC)

                        } else {
                            when {
                                it.exception?.message.toString() == "The password is invalid or the user does not have a password." -> mostrarAlerta("La contraseña es incorrecta.")
                                it.exception?.message.toString() == "There is no user record corresponding to this identifier. The user may have been deleted." -> mostrarAlerta("El email ingresado no se encuentra registrado")
                                else -> mostrarAlerta("Se ha producido un error autenticando al usuario")
                            }
                        }

                    }

            } else {
                if(binding.etLUsername.text.isNullOrEmpty() && binding.etLPassword.text.isNullOrEmpty())
                    mostrarAlerta("Por favor ingrese un usuario y una contraseña.")
                else if(binding.etLUsername.text.isNullOrEmpty())
                    mostrarAlerta("Por favor ingrese un usuario.")
                else if (binding.etLPassword.text.isNullOrEmpty())
                    mostrarAlerta("Por favor ingrese una contraseña.")
            }
        }

        binding.btnLGoogle.setOnClickListener {
            val googleConf: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this,googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent,GOOGLE_SIGN_IN)
        }
    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs), Context.MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val provider = prefs.getString("email",null)
        if(email != null && provider != null)
            irAlMain(email, ProviderType.valueOf(provider))
    }

    private fun mostrarAlerta (message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun irAlMain (email: String, provider: ProviderType){
        val i = Intent(this, MainActivity::class.java)
        i.putExtra("email",email)
        i.putExtra("provider",provider.toString())
        startActivity(i)
        binding.etLPassword.setText("")
        binding.etLUsername.setText("")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null ) {
                    val credential: AuthCredential =
                        GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance()
                        .signInWithCredential(credential).addOnCompleteListener {

                            if (it.isSuccessful) {
                                irAlMain(account.email ?: "", ProviderType.GOOGLE)
                            } else {
                                mostrarAlerta("Se ha producido un error autenticando al usuario")
                            }


                        }
                }
            } catch (e: ApiException) {
                mostrarAlerta("Se ha producido un error autenticando al usuario")
            }
        }



    }


}