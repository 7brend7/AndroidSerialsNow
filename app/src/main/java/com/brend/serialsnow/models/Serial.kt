package com.brend.serialsnow.models

class Serial {
    var ID: Int? = null
    var IMDB: Float? = null
    var KINOPOISK: Float? = null
    var TITLE_RU: String? = null
    var YEAR: String? = null
    var IMAGE: String? = null

    fun prepare() {
        IMAGE = "https://serials-now.ru/jpg/$ID.360.jpg"

    }
}