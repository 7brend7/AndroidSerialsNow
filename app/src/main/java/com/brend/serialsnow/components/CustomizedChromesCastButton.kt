package com.brend.serialsnow.components

import android.content.Context
import android.support.v7.app.MediaRouteButton
import android.util.AttributeSet

class CustomizedChromesCastButton(context: Context?, attrs: AttributeSet?) : MediaRouteButton(context, attrs) {

    private lateinit var listener: () -> Unit

    override fun performClick(): Boolean {
        listener()
        return false
    }

    fun performClickOrigin(): Boolean {
        return super.performClick()
    }

    fun setClickListener(listenerParam: () -> Unit) {
        listener = listenerParam
    }
}