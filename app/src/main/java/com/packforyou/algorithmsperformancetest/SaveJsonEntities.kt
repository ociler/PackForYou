package com.packforyou.algorithmsperformancetest

import com.google.gson.Gson
import com.packforyou.data.models.Package
import com.packforyou.ui.packages.IPackagesViewModel

class SaveJsonEntities(
    private val packagesViewModel: IPackagesViewModel
) {
    fun saveArraysInJson() {
        println("TRAVEL TIME ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.observeTravelTimeArray().value))
        println("--------------------\n\n")

        println("DISTANCE ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getDistanceArray()))
        println("--------------------\n\n")


        println("START TRAVEL TIME ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getStartTravelTimeArray()))
        println("--------------------\n\n")

        println("START DISTANCE ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getStartDistanceArray()))
        println("--------------------\n\n")

        println("END TRAVEL TIME ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getEndTravelTimeArray()))
        println("--------------------\n\n")

        println("END DISTANCE ARRAY")
        println("--------------------")
        println(Gson().toJson(packagesViewModel.getEndDistanceArray()))
        println("--------------------\n\n")
    }

    fun savePackagesInJson(packages: List<Package>) {
        println(Gson().toJson(packages))
    }
}
