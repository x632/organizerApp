package com.poema.theorganizerapp.data.local

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.poema.theorganizerapp.viewModels.MainViewViewModel

class ViewModelFactory(val context: Context) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewViewModel(context) as T
        }
    }