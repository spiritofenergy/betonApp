package com.example.betonadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private lateinit var signUpButton: TextView

    private lateinit var nameTextEdit: EditText
    private lateinit var passwordTextEdit: EditText
    private lateinit var logIn: Button

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth

        database = Firebase.firestore

        signUpButton = findViewById(R.id.SignUpButton)

        nameTextEdit = findViewById(R.id.nameTextEdit)
        passwordTextEdit = findViewById(R.id.passwordEditText)
        logIn = findViewById(R.id.logIn)

        logIn.setOnClickListener {
            logInAction(
                nameTextEdit.text.toString().trim(),
                passwordTextEdit.text.toString().trim()
            )
        }

        signUpButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun logInAction(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("login", "signInWithEmail:success")
                    val user = auth.currentUser
                    var root: String? = null
                    database.collection("users")
                        .whereEqualTo("uid", user?.uid)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                root = document.data["root"].toString()

                                if (root == "admin") {
                                    startActivity(
                                        Intent(
                                            this,
                                            HomeActivity::class.java
                                        )
                                    )
                                } else {
                                    Firebase.auth.signOut()
                                    Toast.makeText(
                                        baseContext,
                                        "Access is denied!",
                                        Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w("home", "Error getting documents: ", exception)
                        }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("login", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }

            }
    }
}