package com.packforyou.navigation

import com.packforyou.data.models.Package
import javax.inject.Singleton

@Singleton
object ArgumentsHolder {
    var packagesList = listOf<Package>()
}