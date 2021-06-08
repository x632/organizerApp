package com.poema.theorganizerapp.viewModels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.poema.theorganizerapp.repository.Repository

class LoginViewModel() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    val repository = Repository()

    fun checkIfSignedIn(): Boolean {
        val currentUser: FirebaseUser? = auth.currentUser
        return currentUser != null
    }

    fun login2(email:String, password: String){
        if (email == ""  || password == "") {
            repository.message.value = "Authentication failed - you need to fill in both email and password"

        }else{
        repository.login(email, password)
        }
    }


    fun createAccount(email:String, password:String, password2 : String) {
        if (email == ""  || password == "" || password2 == ""){
            repository.message.value ="Authentication failed - none of the fields can be empty"

        }
        else if (password != password2){
            repository.message.value = "Passwords do not match."
        }
        else{
            repository.createAccount(email, password)
        }
    }

    fun getIsSignedIn(): MutableLiveData<Boolean> {
        return repository.isSignedIn
    }

    fun getAccountCreated():MutableLiveData<Boolean>{
        return repository.accountCreated
    }

    fun getMessage():MutableLiveData<String>{
        return repository.message
    }

}