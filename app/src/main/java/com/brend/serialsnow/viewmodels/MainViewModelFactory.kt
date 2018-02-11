package com.brend.serialsnow.viewmodels

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.brend.serialsnow.utils.Utility

class MainViewModelFactory(var application: Application, var context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val mainViewModel = MainViewModel(application)

        Utility.connectionCheck(context, {
            mainViewModel.loadSerials()
        })

        return mainViewModel as T
    }
}