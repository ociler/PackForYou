package com.packforyou.data.models

import android.graphics.Color

data class Pointer (
    val id: Int = 0,
    var color: Int = Color.BLACK,
    var state: PointerState = PointerState.NOT_CONFIRMED,
    var location: Location? = null
    )