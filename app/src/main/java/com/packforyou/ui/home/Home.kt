package com.packforyou.ui.home

import androidx.compose.runtime.Composable
import com.packforyou.ui.atlas.Atlas
import com.packforyou.ui.atlas.IAtlasViewModel
import com.packforyou.ui.packages.IPackagesViewModel
import com.packforyou.ui.packages.Packages

//This will be the main screen. Exists just to be able to use the packages and the map on the same screen

@Composable
fun Home(
    packagesViewModel: IPackagesViewModel,
    atlasViewModel: IAtlasViewModel
) {
    Packages(packagesViewModel = packagesViewModel)
    //Atlas(atlasViewModel)
}