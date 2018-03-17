package com.brend.serialsnow.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Favorites(context: Context) {

    private var favorites = HashMap<String, SerialInfo>()

    private val gson = Gson()

    private var favoritePref: SharedPreferences

    companion object {
        private const val FAVORITE_PREFERENCE = "FAVORITE_PREFERENCE"
    }

    private fun apply() {
        val json = gson.toJson(favorites)

        with (favoritePref.edit()) {
            putString(FAVORITE_PREFERENCE, json)
            commit()
        }
    }

    fun get(id: String): SerialInfo? {
        return favorites[id]
    }

    fun get(): HashMap<String, SerialInfo> {
        return favorites
    }

    fun remove(list: ArrayList<String>) {
        list.forEach {
            remove(it)
        }
    }

    fun remove(id: String) {
        favorites.remove(id)
        apply()
    }

    fun set(serialInfo: SerialInfo) {
        favorites[serialInfo.ID as String] = serialInfo
        apply()
    }

    init {
        favoritePref = context.getSharedPreferences(FAVORITE_PREFERENCE, Context.MODE_PRIVATE)
        val favoriteStr = favoritePref.getString(FAVORITE_PREFERENCE, null)
        val type = object : TypeToken<HashMap<String, SerialInfo>>() {}.type
        favorites = if (favoriteStr != null) gson.fromJson(favoriteStr, type) else HashMap()
    }
}