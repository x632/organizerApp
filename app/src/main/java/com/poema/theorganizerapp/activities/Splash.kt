package com.poema.theorganizerapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.poema.theorganizerapp.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*
import kotlin.concurrent.schedule

class Splash : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timer("SettingUp", false).schedule(1500) {
            check()
        }
    }

    private fun check(){
        val signedIn = checkIfSignedIn()
        if(signedIn){
            toMainScreen()
        }
        else{
            toLoginScreen()
        }
    }

    private fun toMainScreen() {
            val intent = Intent(this, MainView::class.java)
            startActivity(intent)
    }

    private fun toLoginScreen() {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
    }

    fun checkIfSignedIn():Boolean{
        val currentUser: FirebaseUser? = auth.currentUser
        return currentUser != null
    }
}