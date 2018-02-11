package com.brend.serialsnow.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.brend.serialsnow.models.Category
import com.brend.serialsnow.repositories.OnSerialsReadyCallback
import com.brend.serialsnow.repositories.SerialsRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var serialsRepository = SerialsRepository(getApplication())

    var categories = MutableLiveData<ArrayList<Category>>()

    var isLoading = ObservableField<Boolean>(false)

    fun loadSerials() {
        isLoading.set(true)

        serialsRepository.getSerials(object : OnSerialsReadyCallback {
            override fun onDataReady(data: ArrayList<Category>) {
                isLoading.set(false)
                categories.value = data
            }
        })
    }
}