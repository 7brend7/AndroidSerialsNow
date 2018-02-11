package com.brend.serialsnow.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import com.brend.serialsnow.models.SerialInfoResponse
import com.brend.serialsnow.repositories.OnSerialInfoReadyCallback
import com.brend.serialsnow.repositories.SerialInfoRepository


class SerialInfoViewModel(application: Application, private var id : String) : AndroidViewModel(application) {

    private var serialInfoRepository = SerialInfoRepository(getApplication())

    var serialInfoResponse = MutableLiveData<SerialInfoResponse?>()

    var isLoading = ObservableField<Boolean>(false)

    var translation = MutableLiveData<String>()

    var season = MutableLiveData<String>()
    var seasonsKey = ObservableField<Int>(null)
    var seasonHash : HashMap<String, String>? = null

    var episode = MutableLiveData<String>()
    var episodesKey = ObservableField<Int>()
    var episodesHash : HashMap<String, String>? = null

    var translationToken = ObservableField<String>()

    fun loadSerialInfo() {
        isLoading.set(true)
        serialInfoRepository.getSerialInfo(id, object : OnSerialInfoReadyCallback {
            override fun onDataReady(data: SerialInfoResponse) {
                isLoading.set(false)
                serialInfoResponse.value = data
            }
        })
    }
}