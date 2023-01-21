package com.packforyou.algorithmsperformancetest

import android.content.res.Resources
import com.google.gson.Gson
import com.packforyou.R
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.ui.packages.IPackagesViewModel
import java.io.InputStreamReader

class RestoreJsonEntities(
    private val mode: String,
    private val numPckgMode: String,
    private val resources: Resources,
    private val packagesViewModel: IPackagesViewModel
) {
    fun restoreStartLocation(): Location {
        var startLocation = Location()
        when (mode) {
            "City" -> {
                val reader =
                    InputStreamReader(resources.openRawResource(R.raw.city_start_location))
                startLocation = Gson().fromJson(reader, Location::class.java)
            }

            "Town" -> {
                val reader =
                    InputStreamReader(resources.openRawResource(R.raw.town_start_location))
                return Gson().fromJson(reader, Location::class.java)
            }

            else -> {
                println("Sorry, no valid mode given")
            }
        }
        return startLocation
    }

    fun restoreLastLocation(): Location {
        var endLocation = Location()
        when (mode) {

            "City" -> {
                val reader =
                    InputStreamReader(resources.openRawResource(R.raw.city_end_location))
                endLocation = Gson().fromJson(reader, Location::class.java)
            }

            "Town" -> {
                val reader =
                    InputStreamReader(resources.openRawResource(R.raw.town_end_location))
                return Gson().fromJson(reader, Location::class.java)
            }

            else -> {
                println("Sorry, no valid mode given")
            }
        }
        return endLocation
    }

    fun restoreArraysFromJson() {
        val gson = Gson()
        var reader: InputStreamReader

        when (mode + numPckgMode) {
            "Town5" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town5))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town5))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town5))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town5))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town5))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town5))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town6" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town6))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town6))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town6))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town6))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town6))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town6))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town7" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town7))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town7))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town7))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town7))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town7))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town7))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town8" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town8))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town8))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town8))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town8))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town8))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town8))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town9" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town9))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town9))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town9))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town9))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town9))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town9))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town10" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town10))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town10))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town10))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town10))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town10))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town10))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town15" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town15))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town15))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town15))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town15))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town15))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town15))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town20" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town20))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town20))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town20))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town20))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town20))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town20))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "Town25" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_town25))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_town25))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_town25))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_town25))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_town25))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_town25))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }


            "City5" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city5))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city5))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city5))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city5))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city5))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city5))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City6" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city6))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city6))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city6))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city6))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city6))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city6))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City7" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city7))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city7))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city7))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city7))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city7))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city7))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City8" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city8))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city8))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city8))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city8))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city8))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city8))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City9" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city9))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city9))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city9))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city9))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city9))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city9))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City10" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city10))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city10))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city10))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city10))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city10))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city10))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City15" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city15))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city15))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city15))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city15))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city15))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city15))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }


            "City20" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city20))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city20))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city20))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city20))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city20))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city20))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            "City25" -> {
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.distance_array_city25))
                packagesViewModel.setDistanceArray(
                    gson.fromJson(
                        reader,
                        Array<IntArray>::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_distance_array_city25))
                packagesViewModel.setStartDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_distance_array_city25))
                packagesViewModel.setEndDistanceArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )


                reader =
                    InputStreamReader(resources.openRawResource(R.raw.start_travel_time_array_city25))
                packagesViewModel.setStartTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                reader =
                    InputStreamReader(resources.openRawResource(R.raw.end_travel_time_array_city25))
                packagesViewModel.setEndTravelTimeArray(
                    gson.fromJson(
                        reader,
                        IntArray::class.java
                    )
                )

                //We are calling this the last one because this way the .observe will run when everything is ready
                reader =
                    InputStreamReader(resources.openRawResource(R.raw.travel_time_array_city25))
                packagesViewModel.observeTravelTimeArray()
                    .postValue(gson.fromJson(reader, Array<IntArray>::class.java))
            }

            else -> println("Sorry, not valid mode given")
        }
    }

    fun restorePackagesFromJson(): Array<Package> {
        var packages = arrayOf<Package>()
        val gson = Gson()
        val reader: InputStreamReader

        when (mode + numPckgMode) {

            "Town5" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages5))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town6" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages6))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town7" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages7))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town8" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages8))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town9" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages9))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town10" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages10))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town15" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages15))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town20" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages20))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "Town25" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.town_packages25))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }


            "City5" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages5))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }


            "City6" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages6))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }


            "City7" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages7))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }


            "City8" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages8))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City9" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages9))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City10" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages10))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City15" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages15))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City20" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages20))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            "City25" -> {
                reader = InputStreamReader(resources.openRawResource(R.raw.city_packages25))
                packages = gson.fromJson(reader, Array<Package>::class.java)
            }

            else -> {
                println("Sorry, no valid mode given")
            }
        }
        return packages
    }
}
