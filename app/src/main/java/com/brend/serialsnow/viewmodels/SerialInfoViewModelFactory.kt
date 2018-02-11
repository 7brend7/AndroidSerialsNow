package com.brend.serialsnow.viewmodels

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.brend.serialsnow.utils.Utility

class SerialInfoViewModelFactory(var application: Application, var context: Context, var id: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val serialInfoViewModel = SerialInfoViewModel(application, id)

        Utility.connectionCheck(context, {
            serialInfoViewModel.loadSerialInfo()
        })

        return serialInfoViewModel as T
    }
}