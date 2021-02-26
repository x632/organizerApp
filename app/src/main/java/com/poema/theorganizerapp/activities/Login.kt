package com.poema.theorganizerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.R

class Login : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailView = findViewById<EditText>(R.id.emailHolder)
        val passwordView = findViewById<EditText>(R.id.passwordHolder)
        val repeatedPasswordView = findViewById<EditText>(R.id.password2Holder)
        val regButton = findViewById<Button>(R.id.regBtn)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        //val forgotPassword = findViewById<TextView>(R.id.forgotPassword)

        regButton.setOnClickListener() {

            val email = emailView.text.toString()
            val password = passwordView.text.toString()
            val repeatedPassword = repeatedPasswordView.text.toString()
            createAccount(email, password, repeatedPassword)
        }

        loginBtn.setOnClickListener(){

            val email = emailView.text.toString()
            val password = passwordView.text.toString()
            login(email, password)
        }
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null) {
            println("!!! current users uid = ${currentUser.uid}")
            println("!!!You are signed in!")
            toNextScreen()

        } else {
            println("!!!You are not signed in!")
        }

    }


    private fun login(email:String, password: String) {
        if (email == ""  || password == ""){
            val mess ="Authentication failed - you need to fill in both email and password"
            showMessage(mess)
        }
        else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                       toNextScreen()
                    } else {
                        showMessage("signInWithEmail:failure - ${task.exception}")
                    }
                }
        }
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Cannot go back here", Toast.LENGTH_SHORT).show()
    }


    private fun createAccount(email:String, password:String, password2 : String) {
        if (email == ""  || password == "" || password2 == ""){
            val mess ="Authentication failed - none of the fields can be empty"
            showMessage(mess)
        }
        else if (password != password2){
            val mess ="Passwords do not match."
            showMessage(mess)
        }
        else{
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        println("createUserWithEmail:success")
                        toNextScreen()

                    } else {
                        val eMess = "${task.exception}"
                        val errMess = eMess.substringAfter(":")
                        val finalMessage = "Authentication failed - $errMess"
                        println(errMess)
                        showMessage(finalMessage)
                    }
                }
        }
    }

    private fun showMessage(message: String){
        Toast.makeText(
            applicationContext, message,
            Toast.LENGTH_LONG).show()
    }

    private fun toNextScreen(){

        val intent = Intent(this, MainView::class.java)
        startActivity(intent)
    }
}