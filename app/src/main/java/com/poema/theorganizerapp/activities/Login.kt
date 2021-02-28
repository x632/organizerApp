package com.poema.theorganizerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.poema.theorganizerapp.R
import com.poema.theorganizerapp.viewModels.LoginViewModel
import com.poema.theorganizerapp.viewModels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {


    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this@Login).get(LoginViewModel::class.java)

        //val forgotPassword = findViewById<TextView>(R.id.forgotPassword)

        setIsSignedInObserver()
        setAccountCreatedObserver()
        setMessageObserver()

        regBtn.setOnClickListener() {

            val email = emailHolder.text.toString()
            val password = passwordHolder.text.toString()
            val repeatedPassword = password2Holder.text.toString()
            viewModel.createAccount(email, password, repeatedPassword)
        }

        loginBtn.setOnClickListener(){

            val email = emailHolder.text.toString()
            val password = passwordHolder.text.toString()
            viewModel.login(email, password)
        }

        val signedIn : Boolean = viewModel.checkIfSignedIn()
        if(signedIn)toNextScreen()
    }

    private fun setAccountCreatedObserver() {
        viewModel.getAccountCreated().observe(this@Login, { t ->
            if(t){
                toNextScreen()
            }
        } )
    }
    private fun setIsSignedInObserver() {
        viewModel.getIsSignedIn().observe(this@Login, {t->
            if (t){
                toNextScreen()
            }
        })
    }
    private fun setMessageObserver(){
        viewModel.getMessage().observe(this@Login, { message ->
            showMessage(message)
        })
    }


    override fun onBackPressed() {
    showMessage("You can not go back here.")
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