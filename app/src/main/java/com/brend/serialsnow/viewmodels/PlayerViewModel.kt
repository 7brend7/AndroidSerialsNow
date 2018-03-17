package com.brend.serialsnow.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    var currentStream: String? = null

    var availableFormats = HashMap<String, String>()
}