package com.packforyou.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import com.packforyou.data.models.Package
import com.packforyou.ui.atlas.IAtlasViewModel

object ArgumentsHolder {
        var packagesList = listOf<Package>()
        lateinit var atlasViewModel: IAtlasViewModel
}