package dev.sebastianleon.firebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dev.sebastianleon.firebase.model.UserModel

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etFullName: EditText = findViewById(R.id.etFullName)
        val etCountry: EditText = findViewById(R.id.etCountry)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPsswd: EditText = findViewById(R.id.etPsswd)
        val btnSaveSignup: Button = findViewById(R.id.btnSaveSignup)
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        btnSaveSignup.setOnClickListener {
            val fullName = etFullName.text.toString()
            val country = etCountry.text.toString()
            val email = etEmail.text.toString()
            val password = etPsswd.text.toString()

            //Generar el usuario en FirebaseAuth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        val user: FirebaseUser? = auth.currentUser
                        val uid = user?.uid
                        val userModel: UserModel = UserModel(fullName, country, email, uid)

                        db.collection("users")
                            .add(userModel)
                            .addOnCompleteListener{
                                Snackbar
                                    .make(findViewById(android.R.id.content),
                                        "Registro exitoso",
                                        Snackbar.LENGTH_SHORT).show()
                                finish()
                            }.addOnFailureListener{error ->
                                Snackbar
                                    .make(findViewById(android.R.id.content),
                                        "Ocurrió un error al registrar el usuario: ${error.message}",
                                        Snackbar.LENGTH_SHORT).show()
                            }
                    }
                }.addOnFailureListener{error ->
                    Snackbar
                        .make(findViewById(android.R.id.content),
                        "Ocurrió un error al registrar el usuario: ${error.message}",
                        Snackbar.LENGTH_SHORT).show()
                    }
        }
    }
}