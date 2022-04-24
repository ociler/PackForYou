package com.packforyou.ui.packages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.packforyou.data.repositories.IPackagesRepository
import com.packforyou.data.models.Package
import kotlinx.coroutines.launch
import javax.inject.Inject

interface IPackagesViewModel{
    fun addPackage(packge: Package)
}
class PackagesViewModelImpl @Inject constructor(
    private val repository: IPackagesRepository
) : ViewModel(), IPackagesViewModel {

    override fun addPackage(packge: Package){
        viewModelScope.launch {
            repository.addPackage(packge)
        }
    }

}