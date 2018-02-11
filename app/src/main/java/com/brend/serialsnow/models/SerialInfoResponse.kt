package com.brend.serialsnow.models

import com.google.gson.annotations.SerializedName

class SerialInfoResponse {

    @SerializedName("0")
    var serialInfo: SerialInfo? = null

    @SerializedName("1")
    var comments: List<Comment>? = null

    @SerializedName("2")
    var translation: Map<String, Translation>? = null

    @SerializedName("3")
    var ratios: Ratio? = null

    @SerializedName("7")
    var translationUpdates: List<TranslationUpdate>? = null
}