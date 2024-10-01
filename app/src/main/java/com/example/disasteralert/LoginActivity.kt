package com.example.disasteralert

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signUpTextView = findViewById<TextView>(R.id.tvSignUp)

        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Show loading dialog
                val loadingDialog = ProgressDialog(this)
                loadingDialog.setMessage("Logging in...")
                loadingDialog.setCancelable(false)
                loadingDialog.show()

                // Perform Firebase authentication
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        loadingDialog.dismiss() // Dismiss loading after login attempt
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.let {
                                checkUserRole(it.uid)
                            }
                        } else {
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserRole(uid: String) {
        // Show loading dialog while checking user role
        val loadingDialog = ProgressDialog(this)
        loadingDialog.setMessage("Checking user role...")
        loadingDialog.setCancelable(false)
        loadingDialog.show()

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                loadingDialog.dismiss() // Dismiss loading after role check
                val role = document.getString("role")
                if (role != null) {
                    when (role) {
                        "admin" -> {
                            val intent = Intent(this, MediaListActivity::class.java)
                            startActivity(intent)
                        }
                        "user" -> {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else -> {
                            Toast.makeText(this, "Unknown role: $role", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                loadingDialog.dismiss() // Dismiss loading on failure
                Toast.makeText(this, "Failed to retrieve user role", Toast.LENGTH_SHORT).show()
            }
    }
}
