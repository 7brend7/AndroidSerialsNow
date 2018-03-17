package com.brend.serialsnow.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.brend.serialsnow.models.Favorites
import com.brend.serialsnow.models.SerialInfo

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading = ObservableField<Boolean>(true)

    var editMode = ObservableBoolean(false)

    var favorites: ArrayList<SerialInfo> = ArrayList()

    var selectedFavorites = ObservableArrayList<String>()

    fun loadFavorites(context: Context) {
        isLoading.set(true)

        favorites = ArrayList(Favorites(context).get().values)

        isLoading.set(false)
    }
}