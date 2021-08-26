package com.poema.theorganizerapp.ui.viewmodels


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.poema.theorganizerapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val mainRepository : MainRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()


    fun checkIfSignedIn(): Boolean {
        val currentUser: FirebaseUser? = auth.currentUser
        return currentUser != null
    }

    fun login2(email:String, password: String){
        if (email == ""  || password == "") {
            mainRepository.message.value = "Authentication failed - you need to fill in both email and password"

        }else{
        mainRepository.login(email, password)
        }
    }


    fun createAccount(email:String, password:String, password2 : String) {
        if (email == ""  || password == "" || password2 == ""){
            mainRepository.message.value ="Authentication failed - none of the fields can be empty"

        }
        else if (password != password2){
            mainRepository.message.value = "Passwords do not match."
        }
        else{
            mainRepository.createAccount(email, password)
        }
    }

    fun getIsSignedIn(): MutableLiveData<Boolean> {
        return mainRepository.isSignedIn
    }

    fun getAccountCreated():MutableLiveData<Boolean>{
        return mainRepository.accountCreated
    }

    fun getMessage():MutableLiveData<String>{
        return mainRepository.message
    }

}