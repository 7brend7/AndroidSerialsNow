package com.brend.serialsnow.models

class SerialInfo {
    var ID: String? = null
    var YEAR: String? = null
    var TITLE_RU: String? = null
    var TITLE_EN: String? = null
    var GENRE: String? = null
    var COUNTRY: String? = null
    var DESCRIPTION: String? = null
    var IMDB: String? = null
    var KINOPOISK: String? = null
    var STAT: String? = null
    var WATCHED: String? = null
    var LIKE: String? = null
    var DISLIKE: String? = null
    var FAVORITES: String? = null
    var COMMENTS: String? = null
    var SUBSCRIPTIONS: String? = null
    var VOTE: Any? = null
    var UPDATE: String? = null

    val IMAGE: String?
        get() = "https://serials-now.ru/jpg/$ID.360.jpg"
}