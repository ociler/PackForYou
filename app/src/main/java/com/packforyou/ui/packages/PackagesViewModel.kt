package com.packforyou.ui.packages

import androidx.lifecycle.ViewModel
import com.packforyou.data.repositories.PackagesRepository
import javax.inject.Inject

class PackagesViewModel @Inject constructor(
    private val repository: PackagesRepository = PackagesRepository()
) : ViewModel() {

}