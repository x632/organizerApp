package com.poema.theorganizerapp.viewModels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginViewModel() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private var message = MutableLiveData<String>()
    private var isSignedIn = MutableLiveData<Boolean>()
    private var accountCreated = MutableLiveData<Boolean>()


    fun checkIfSignedIn():Boolean{
        val currentUser: FirebaseUser? = auth.currentUser
        return currentUser != null
    }

    fun login(email:String, password: String) {
        if (email == ""  || password == ""){
            message.value = "Authentication failed - you need to fill in both email and password"
            //showMessage(mess)
        }
        else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        isSignedIn.value=true
                    } else {
                        message.value = ("signInWithEmail:failure - ${task.exception}")
                    }
                }
        }
    }

    fun createAccount(email:String, password:String, password2 : String) {
        if (email == ""  || password == "" || password2 == ""){
            message.value ="Authentication failed - none of the fields can be empty"
            //showMessage(mess)
        }
        else if (password != password2){
            message.value = "Passwords do not match."
            //showMessage(mess)
        }
        else{
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        println("createUserWithEmail:success")
                        accountCreated.value = true
                        //toNextScreen()

                    } else {
                        val eMess = "${task.exception}"
                        val errMess = eMess.substringAfter(":")
                        message.value = "Authentication failed - $errMess"
                        println(errMess)
                        //showMessage(finalMessage)
                    }
                }
        }
    }

    fun getIsSignedIn(): MutableLiveData<Boolean> {
        return isSignedIn
    }

    fun getAccountCreated():MutableLiveData<Boolean>{
        return accountCreated
    }

    fun getMessage():MutableLiveData<String>{
        return message
    }

}