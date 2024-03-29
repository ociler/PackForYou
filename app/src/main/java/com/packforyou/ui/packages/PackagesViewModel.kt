package com.packforyou.ui.packages

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.packforyou.api.ICallbackAPICalls
import com.packforyou.api.ICallbackDirectionsResponse
import com.packforyou.data.directionsDataClases.DirectionsResponse
import com.packforyou.data.models.*
import com.packforyou.data.repositories.IPackagesAndAtlasRepository
import com.packforyou.ui.login.CurrentSession
import com.packforyou.ui.utils.PermutationsIteratively
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


interface IPackagesViewModel {

    fun addPackage(packge: Package)
    fun getAddressFromLocation(geoPoint: GeoPoint, context: Context): String
    fun getLocationFromAddress(address: String?, context: Context): LatLng?

    fun getExamplePackages(): List<Package>

    fun computeDistanceBetweenAllPackages(packages: List<Package>)

    fun computeDistanceBetweenStartLocationAndPackages(
        startLocation: Location,
        endLocation: Location,
        packages: List<Package>
    )

    fun computeDistanceBetweenEndLocationAndPackages(endLocation: Location, packages: List<Package>)

    fun computeOptimizedRouteDirectionsAPI(route: Route)
    fun computeOptimizedRouteDirectionsAPIWithUrgency(
        veryUrgentRoute: Route,
        urgentRoute: Route,
        notUrgentRoute: Route
    )

    fun computeDirectionsResponse(route: Route)


    fun getOptimizedRouteBruteForceTravelTime(
        route: Route,
        travelTimeArray: Array<IntArray>,
        startTravelTimeArray: IntArray,
        endTravelTimeArray: IntArray
    ): Route

    fun getOptimizedRouteNearestNeighbourTravelTime(
        route: Route,
        travelTimeArray: Array<IntArray>,
        startTravelTimeArray: IntArray,
        endTravelTimeArray: IntArray
    ): Route

    fun getRouteTravelTime(
        packages: List<Package>,
        travelTimeArray: Array<IntArray>,
        startTravelTimeArray: IntArray,
        endTravelTimeArray: IntArray
    ): Int

    fun getOptimizedRouteBruteForceDistance(
        route: Route,
        distanceArray: Array<IntArray>,
        startDistanceArray: IntArray,
        endDistanceArray: IntArray
    ): Route

    fun getOptimizedRouteNearestNeighbourDistance(
        route: Route,
        distanceArray: Array<IntArray>,
        startDistanceArray: IntArray,
        endDistanceArray: IntArray
    ): Route

    fun getRouteDistance(
        packages: List<Package>,
        distanceArray: Array<IntArray>,
        startDistanceArray: IntArray,
        endDistanceArray: IntArray
    ): Int

    fun computePermutations(size: Int)
    fun computePermutationsOfArray(array: Array<Byte>)

    fun observeTravelTimeArray(): MutableLiveData<Array<IntArray>>
    fun getStartTravelTimeArray(): IntArray
    fun getEndTravelTimeArray(): IntArray

    fun getDistanceArray(): Array<IntArray>
    fun getStartDistanceArray(): IntArray
    fun getEndDistanceArray(): IntArray

    fun observeDirectionsResponse(): MutableLiveData<DirectionsResponse>
    fun observeOptimizedDirectionsAPIRoute(): MutableLiveData<Route>

    fun setStartTravelTimeArray(startTravelTimeArray: IntArray)
    fun setEndTravelTimeArray(endTravelTimeArray: IntArray)
    fun setDistanceArray(distanceArray: Array<IntArray>)
    fun setStartDistanceArray(startDistanceArray: IntArray)
    fun setEndDistanceArray(endDistanceArray: IntArray)
    fun removePackage(pckge: Package)
    fun getExampleLastLocations(): List<Location>
    fun removePackageFromToDeliverList(pckge: Package)
    fun removeLastLocation(location: Location)
    fun addLastLocation(location: Location)
    fun setLastLocation(location: Location)

    fun getNotUrgentRoute(): Route
    fun getUrgentRoute(): Route
    fun observeOptimizedVeryUrgentRoute(): MutableLiveData<Route>
    fun computeProperAlgorithmAndUpdateCurrentSession(
        algorithm: Algorithm,
        packages: List<Package>,
        comesFromAddPackage: Boolean = false
    )

    fun postponePackage(pckge: Package)
}

//TODO refactor of some methods that maybe should be elsewhere

@HiltViewModel
class PackagesViewModelImpl @Inject constructor(
    private val repository: IPackagesAndAtlasRepository
) : ViewModel(), IPackagesViewModel {

    private var permutationsListRec = ArrayList<Array<Int>>()

    private var permutationsListIt = ArrayList<ArrayList<Byte>>()

    var globalTravelTimeArray = MutableLiveData<Array<IntArray>>()
    lateinit var globalStartTravelTimeArray: IntArray
    lateinit var globalEndTravelTimeArray: IntArray

    lateinit var globalDistanceArray: Array<IntArray>
    lateinit var globalStartDistanceArray: IntArray
    lateinit var globalEndDistanceArray: IntArray

    lateinit var directionsResponse: MutableLiveData<DirectionsResponse>

    var optimizedDirectionsAPIRoute = MutableLiveData<Route>()

    var globalVeryUrgentRoute = MutableLiveData<Route>()
    lateinit var globalUrgentRoute: Route
    lateinit var globalNotUrgentRoute: Route


    private val callbackObject = object : ICallbackAPICalls {
        //Here we need the endLocation because we are going to call "computeDistanceBetweenEndLocationAndPackages"
        override fun onSuccessBetweenStartLocationAndPackages(
            computedStartTravelTimeArray: IntArray,
            computedStartDistanceArray: IntArray,
            endLocation: Location,
            packages: List<Package>
        ) {
            globalStartTravelTimeArray = computedStartTravelTimeArray
            globalStartDistanceArray = computedStartDistanceArray
            computeDistanceBetweenEndLocationAndPackages(endLocation, packages)
        }

        override fun onSuccessBetweenEndLocationAndPackages(
            computedEndTravelTimeArray: IntArray,
            computedEndDistanceArray: IntArray,
            packages: List<Package>
        ) {
            globalEndTravelTimeArray = computedEndTravelTimeArray
            globalEndDistanceArray = computedEndDistanceArray
            computeDistanceBetweenAllPackages(packages)
        }

        override fun onSuccessOptimizedDirectionsAPI(route: Route) {
            optimizedDirectionsAPIRoute.postValue(route)
        }

        override fun onSuccessNotUrgentPackages(
            optimizedNotUrgentRoute: Route,
            callbackObject: ICallbackAPICalls
        ) {
            globalNotUrgentRoute = optimizedNotUrgentRoute

            //UrgentRoute will finish where NotUrgentRoute starts
            globalUrgentRoute.endLocation = optimizedNotUrgentRoute.packages[0].location
            viewModelScope.launch {
                repository.computeOptimizedRouteDirectionsAPI(
                    globalUrgentRoute,
                    callbackObject,
                    Urgency.URGENT
                )
            }
        }

        override fun onSuccessUrgentPackages(
            optimizedUrgentRoute: Route,
            callbackObject: ICallbackAPICalls
        ) {
            globalUrgentRoute = optimizedUrgentRoute

            //VeryUrgentRoute will finish where UrgentRoute starts
            globalVeryUrgentRoute.value!!.endLocation = optimizedUrgentRoute.packages[0].location
            viewModelScope.launch {
                repository.computeOptimizedRouteDirectionsAPI(
                    globalVeryUrgentRoute.value!!,
                    callbackObject,
                    Urgency.VERY_URGENT
                )
            }
        }

        override fun onSuccessVeryUrgentPackages(optimizedVeryUrgentRoute: Route) {
            globalVeryUrgentRoute.postValue(optimizedVeryUrgentRoute)
        }

        override fun onSuccessBetweenPackages(
            computedTravelTimeArray: Array<IntArray>,
            computedDistanceArray: Array<IntArray>
        ) {
            globalDistanceArray = computedDistanceArray
            globalTravelTimeArray.postValue(computedTravelTimeArray)
        }
    }

    private val directionsResponseCallbackObject = object : ICallbackDirectionsResponse {
        override fun onSuccessResponseDirectionsAPI(response: DirectionsResponse) {
            directionsResponse.postValue(response)
        }
    }


    override fun addPackage(packge: Package) {
        val newPackages = CurrentSession.packagesToDeliver.value.plus(packge)
        computeProperAlgorithmAndUpdateCurrentSession(
            CurrentSession.algorithm,
            newPackages,
            comesFromAddPackage = true
        )

        viewModelScope.launch {
            repository.addPackage(packge)
        }

    }

    override fun computeProperAlgorithmAndUpdateCurrentSession(
        algorithm: Algorithm,
        packages: List<Package>,
        comesFromAddPackage: Boolean
    ) {

        val route = CurrentSession.route.value.copy(packages = packages)
        IsLoading.state.value = true

        when (algorithm) {
            Algorithm.DIRECTIONS_API -> {

                computeOptimizedRouteDirectionsAPI(route)
                observeOptimizedDirectionsAPIRoute().observeForever { optimizedRoute ->
                    CurrentSession.route.value = optimizedRoute
                    CurrentSession.packagesToDeliver.value = optimizedRoute.packages

                    IsLoading.state.value = false
                }
            }

            Algorithm.BRUTE_FORCE, Algorithm.NEAREST_NEIGHBOUR -> {
                var isFirstExec = true

                if (comesFromAddPackage) {

                    //I reset the position
                    route.packages.forEachIndexed { index, pckg ->
                        pckg.position = index
                    }
                }

                //I compute the permutations.
                // This is like this bc this was the same for both brute force algorithms
                val listWithPositions = mutableListOf<Byte>()
                route.packages.forEach {
                    listWithPositions.add(it.position.toByte())
                }

                val arrayToPermute = listWithPositions.toTypedArray()

                computePermutationsOfArray(arrayToPermute)

                if (comesFromAddPackage) {
                    //I get the arrays
                    computeDistanceBetweenStartLocationAndPackages(
                        startLocation = route.startLocation,
                        endLocation = route.endLocation,
                        packages = route.packages
                    )

                    //once they are ready, we compute de brute force / Nearest neighbour algorithm
                    observeTravelTimeArray().observeForever { travelTimeArray ->
                        if(isFirstExec) {
                            isFirstExec = false
                        } else {
                            val optimizedRoute = if (algorithm == Algorithm.BRUTE_FORCE) {
                                getOptimizedRouteBruteForceTravelTime(
                                    route = route,
                                    travelTimeArray = travelTimeArray,
                                    startTravelTimeArray = getStartTravelTimeArray(),
                                    endTravelTimeArray = getEndTravelTimeArray()
                                )
                            } else {
                                getOptimizedRouteNearestNeighbourTravelTime(
                                    route = route,
                                    travelTimeArray = travelTimeArray,
                                    startTravelTimeArray = getStartTravelTimeArray(),
                                    endTravelTimeArray = getEndTravelTimeArray()
                                )
                            }
                            //and we update the route
                            CurrentSession.route.value = optimizedRoute
                            CurrentSession.packagesToDeliver.value = optimizedRoute.packages

                            IsLoading.state.value = false
                        }
                    }


                } else { //we already have the arrays

                    val optimizedRoute = if (algorithm == Algorithm.BRUTE_FORCE) {
                        getOptimizedRouteBruteForceTravelTime(
                            route = route,
                            travelTimeArray = observeTravelTimeArray().value!!,
                            startTravelTimeArray = getStartTravelTimeArray(),
                            endTravelTimeArray = getEndTravelTimeArray()
                        )
                    } else {
                        getOptimizedRouteNearestNeighbourTravelTime(
                            route = route,
                            travelTimeArray = observeTravelTimeArray().value!!,
                            startTravelTimeArray = getStartTravelTimeArray(),
                            endTravelTimeArray = getEndTravelTimeArray()
                        )
                    }

                    //and we update the route
                    CurrentSession.route.value = optimizedRoute
                    CurrentSession.packagesToDeliver.value = optimizedRoute.packages

                    IsLoading.state.value = false
                }
            }

            Algorithm.URGENCY -> {

                val veryUrgentPackages = mutableListOf<Package>()
                val urgentPackages = mutableListOf<Package>()
                val notUrgentPackages = mutableListOf<Package>()

                val currentPackages = route.packages

                currentPackages.forEach {
                    when (it.urgency) {
                        Urgency.VERY_URGENT -> {
                            veryUrgentPackages.add(it)
                        }
                        Urgency.URGENT -> {
                            urgentPackages.add(it)
                        }
                        else -> {
                            notUrgentPackages.add(it)
                        }
                    }
                }

                val veryUrgentRoute = route.copy(packages = veryUrgentPackages)
                val urgentRoute = route.copy(packages = urgentPackages)
                val notUrgentRoute = route.copy(packages = notUrgentPackages)


                //this will throw callbacks and, at the end, when the optimizedNotUrgentRoute is ready,
                //we will be able to get the others, as they will be also ready
                computeOptimizedRouteDirectionsAPIWithUrgency(
                    veryUrgentRoute = veryUrgentRoute,
                    urgentRoute = urgentRoute,
                    notUrgentRoute = notUrgentRoute
                )

                observeOptimizedVeryUrgentRoute()
                    .observeForever { optimizedVeryUrgentRoute ->
                        //TODO SOLVE THIS LIVEDATA THING. The observe is triggered twice: One at the beginning
                        //bc optimizedVeryUrgentRoute already has a value and anotherone
                        //when it should be triggered (when this object.value changes).
                        //This means that these lines of code are computed twice
                        val optimizedPackages = mutableListOf<Package>()
                        var totalTime = 0

                        optimizedPackages.addAll(optimizedVeryUrgentRoute.packages)
                        optimizedPackages.addAll(getUrgentRoute().packages)
                        optimizedPackages.addAll(getNotUrgentRoute().packages)


                        totalTime += optimizedVeryUrgentRoute.totalTime!!
                        totalTime += getUrgentRoute().totalTime!!
                        totalTime += getNotUrgentRoute().totalTime!!

                        CurrentSession.route.value =
                            route.copy(packages = optimizedPackages, totalTime = totalTime)
                        CurrentSession.packagesToDeliver.value = optimizedPackages
                        CurrentSession.travelTime.value = totalTime

                        IsLoading.state.value = false
                    }
            }


            else -> {
                val newRoute = CurrentSession.route.value.copy(packages = packages)

                if (comesFromAddPackage) {
                    CurrentSession.route.value = newRoute
                    CurrentSession.packagesToDeliver.value = newRoute.packages
                    CurrentSession.packagesForToday.value =
                        CurrentSession.packagesForToday.value.plus(packages.last())

                } else {
                    CurrentSession.route.value = newRoute
                    CurrentSession.packagesToDeliver.value = packages
                }

                IsLoading.state.value = false
            }
        }
    }

    override fun postponePackage(pckge: Package) {
        removePackageFromToDeliverList(pckge)
        pckge.deliveryDate = pckge.deliveryDate!!.plusDays(1)
    }


    override fun getLocationFromAddress(address: String?, context: Context): LatLng? {
        val coder = Geocoder(context)
        val addressList: List<Address>?
        val p1: LatLng
        try {
            if (address == null) {
                return null
            }

            addressList = coder.getFromLocationName(address, 5)
            val location: Address = addressList!![0]
            p1 = LatLng(
                location.latitude,
                location.longitude
            )

            return p1
         } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getAddressFromLocation(geoPoint: GeoPoint, context: Context): String {
        val addresses: List<Address>
        val geocoder = Geocoder(context, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            geoPoint.latitude,
            geoPoint.longitude,
            1
        ) as List<Address> // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        val address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName // Only if available else return NULL

        return address.replace(", ", ",")
    }


    override fun computeDistanceBetweenAllPackages(packages: List<Package>) {
        //initializeTravelTimeArray(packages.size + 1)
        if (packages.size > 30) {
            println("\nPackages.size > 30. It would be too many calls between packages\n")
            return
        }

        viewModelScope.launch {
            repository.computeDistanceBetweenAllPackages(packages, callbackObject)
        }
    }

    override fun computeDistanceBetweenStartLocationAndPackages(
        startLocation: Location,
        endLocation: Location,
        packages: List<Package>
    ) {
        viewModelScope.launch {
            repository.computeDistanceBetweenStartLocationAndPackages(
                startLocation,
                endLocation,
                packages,
                callbackObject
            )
        }
    }

    override fun computeDistanceBetweenEndLocationAndPackages(
        endLocation: Location,
        packages: List<Package>
    ) {
        viewModelScope.launch {
            repository.computeDistanceBetweenEndLocationAndPackages(
                endLocation,
                packages,
                callbackObject
            )
        }
    }


    override fun computeOptimizedRouteDirectionsAPI(route: Route) {
        viewModelScope.launch {
            repository.computeOptimizedRouteDirectionsAPI(route, callbackObject)
        }
    }

    override fun computeOptimizedRouteDirectionsAPIWithUrgency(
        veryUrgentRoute: Route,
        urgentRoute: Route,
        notUrgentRoute: Route
    ) {
        globalVeryUrgentRoute.value = veryUrgentRoute
        globalUrgentRoute = urgentRoute
        globalNotUrgentRoute = notUrgentRoute

        viewModelScope.launch {
            repository.computeOptimizedRouteDirectionsAPI(
                notUrgentRoute,
                callbackObject,
                urgency = Urgency.NOT_URGENT
            )
        }
    }

    override fun computeDirectionsResponse(route: Route) {
        viewModelScope.launch {
            repository.computeDirectionsAPIResponse(route, directionsResponseCallbackObject)
        }
    }

    override fun getOptimizedRouteBruteForceTravelTime(
        route: Route,
        travelTimeArray: Array<IntArray>,
        startTravelTimeArray: IntArray,
        endTravelTimeArray: IntArray
    ): Route {
        if (route.packages.isNullOrEmpty()) return route.copy(id = -1)

        //So much compute. With the emulator up to 9 packages. Otherwise, OutOfMemory
        //Physical device up to 10 packages. Same problem
        if (route.packages.size > 10) {
            println("With this number of packages, the Brute Force algorithm won't finish")
            return route.copy(id = -1)
        }

        val permutations = permutationsListIt

        var minimumTotalTravelTime = Int.MAX_VALUE
        var bestPermutation = permutations[0]
        var origin: Int
        var destination: Int

        var totalTravelTime: Int
        var legTravelTime: Int

        for (permutation in permutations) { //we have one permutation
            totalTravelTime = startTravelTimeArray[permutation[0].toInt()]

            for (permPosition in 0 until permutation.size - 1) { //we have the position we want to check in our route

                origin = permutation[permPosition].toInt()
                destination = permutation[permPosition + 1].toInt()

                legTravelTime = travelTimeArray[origin][destination]
                totalTravelTime += legTravelTime

            }

            totalTravelTime += endTravelTimeArray[permutation.last().toInt()]

            if (totalTravelTime < minimumTotalTravelTime) {
                minimumTotalTravelTime = totalTravelTime
                bestPermutation = permutation
            }
        }

        val optimizedPackages = arrayListOf<Package>()

        var properPackage = Package()

        //we are taking into account the order of the package
        //maybe I should refactor this
        for (i in bestPermutation) {
            properPackage = getPackageWithPosition(i.toInt(), packages = route.packages)
            optimizedPackages.add(properPackage)
        }

        return route.copy(packages = optimizedPackages, totalTime = minimumTotalTravelTime)

    }

    override fun getOptimizedRouteBruteForceDistance(
        route: Route,
        distanceArray: Array<IntArray>,
        startDistanceArray: IntArray,
        endDistanceArray: IntArray
    ): Route {

        if (route.packages.isNullOrEmpty()) return route.copy(id = -1)

        if (startDistanceArray.size != route.packages.size ||
            endDistanceArray.size != route.packages.size ||
            distanceArray.size != route.packages.size
        ) {
            return route.copy(id = -2)
        }

        //So much compute. With the emulator up to 9 packages. Otherwise, OutOfMemory
        //With physical device up to 10. Same problem.
        if (route.packages.size > 10) {
            println("With this number of packages, the Brute Force algorithm won't finish")
            return route.copy(id = -1)
        }

        val permutations = permutationsListIt

        var minimumTotalDistance = Int.MAX_VALUE
        var bestPermutation = permutations[0]
        var origin: Int
        var destination: Int

        var totalDistance = 0
        var legDistance: Int

        for (permutation in permutations) { //we have one permutation
            totalDistance = startDistanceArray[permutation[0].toInt()]

            for (permPosition in 0 until permutation.size - 1) { //we have the position we want to check in our route
                origin = route.packages[permutation[permPosition].toInt()].position
                destination = route.packages[permutation[permPosition + 1].toInt()].position

                legDistance = distanceArray[origin][destination]
                totalDistance += legDistance

            }

            totalDistance += endDistanceArray[permutation.last().toInt()]

            if (totalDistance < minimumTotalDistance) {
                minimumTotalDistance = totalDistance
                bestPermutation = permutation
            }
        }

        val optimizedPackages = arrayListOf<Package>()
        for (i in bestPermutation) {
            optimizedPackages.add(route.packages!![i.toInt()])
        }

        return route.copy(packages = optimizedPackages, totalDistance = minimumTotalDistance)

    }

    //With the size it creates an array from 0 to array.size - 1.
// As we are using Byte, only up to 127. If we used Int, OutOfMemory with size > 9
    private fun getPermutationsIteratively(size: Int): ArrayList<ArrayList<Byte>> {
        val permutations = arrayListOf<ArrayList<Byte>>()

        if (size > 127) return permutations

        val perm = PermutationsIteratively(Array(size) { it.toByte() })

        permutations.add(perm.GetFirst())
        while (perm.HasNext()) {
            permutations.add(perm.GetNext())
        }

        return permutations
    }

    private fun getPermutationsIterativelyOfArray(array: Array<Byte>): ArrayList<ArrayList<Byte>> {
        val permutations = arrayListOf<ArrayList<Byte>>()
        val perm = PermutationsIteratively(array)

        permutations.add(perm.GetFirst())
        while (perm.HasNext()) {
            permutations.add(perm.GetNext())
        }

        return permutations
    }


    override fun computePermutations(size: Int) {
        permutationsListIt = getPermutationsIteratively(size)
    }

    override fun computePermutationsOfArray(array: Array<Byte>) {
        permutationsListIt = getPermutationsIterativelyOfArray(array)
    }


    //Starts from startLocation, goes to its Nearest neighbour (first package) and from there,
//goes to its Nearest neighbour until the last package. When arrives to the last package,
//goes to endLocation
    override fun getOptimizedRouteNearestNeighbourTravelTime(
        route: Route,
        travelTimeArray: Array<IntArray>,
        startTravelTimeArray: IntArray,
        endTravelTimeArray: IntArray
    ): Route {
        if (route.packages.isNullOrEmpty()) return route.copy(id = -1)

        if (startTravelTimeArray.size != route.packages.size ||
            endTravelTimeArray.size != route.packages.size ||
            travelTimeArray.size != route.packages.size
        ) {
            return route.copy(id = -2)
        }

        val packages = route.packages
        var nearestNeighbour = getPackageWithPosition(0, packages)
        var minimumTime = startTravelTimeArray[0]
        val optimizedList = ArrayList<Package>()
        var totalTravelTime = 0

        val visitedArray = BooleanArray(route.packages.size)

        for (i in 1 until startTravelTimeArray.size) {
            if (startTravelTimeArray[i] < minimumTime) {
                minimumTime = startTravelTimeArray[i]
                nearestNeighbour = getPackageWithPosition(i, packages)
            }
        }

        //We have the first package we have to go from the route
        optimizedList.add(nearestNeighbour)
        totalTravelTime += minimumTime
        visitedArray[nearestNeighbour.position] = true

        var minIndex = 0


        //Now we add all the middle packages
        while (!areAllTrue(visitedArray)) {
            minimumTime = Int.MAX_VALUE
            travelTimeArray[nearestNeighbour.position].forEachIndexed { index, currentTravelTime ->
                if (!visitedArray[index] && currentTravelTime < minimumTime) {
                    minimumTime = currentTravelTime
                    minIndex = index
                }
            }

            nearestNeighbour = getPackageWithPosition(minIndex, packages)

            optimizedList.add(nearestNeighbour)
            totalTravelTime += minimumTime
            visitedArray[minIndex] = true
        }

        //And finally we add the travel time of going from this last package to the endLocation
        totalTravelTime += endTravelTimeArray[nearestNeighbour.position]

        return route.copy(packages = optimizedList, totalTime = totalTravelTime)

    }

    private fun getPackageWithPosition(position: Int, packages: List<Package>): Package {
        for (pckg in packages) {
            if (pckg.position == position) return pckg
        }
        return Package(numPackage = -1)
    }

    override fun getOptimizedRouteNearestNeighbourDistance(
        route: Route,
        distanceArray: Array<IntArray>,
        startDistanceArray: IntArray,
        endDistanceArray: IntArray
    ): Route {
        if (route.packages.isEmpty()) return route.copy(id = -1)

        if (startDistanceArray.size != route.packages.size ||
            endDistanceArray.size != route.packages.size ||
            distanceArray.size != route.packages.size
        ) {
            return route.copy(id = -2)
        }

        val packages = route.packages
        var nearestNeighbour = packages[0]
        var minimumDistance = startDistanceArray[0]
        val optimizedList = ArrayList<Package>()
        var totalDistance = 0

        val visitedArray = BooleanArray(route.packages.size)

        for (i in 1 until startDistanceArray.size) {
            if (startDistanceArray[i] < minimumDistance) {
                minimumDistance = startDistanceArray[i]
                nearestNeighbour = packages[i]
            }
        }

        //We have the first package we have to go from the route
        optimizedList.add(nearestNeighbour)
        totalDistance += minimumDistance
        visitedArray[nearestNeighbour.position] = true

        var minIndex = 0


        //Now we add all the middle packages
        while (!areAllTrue(visitedArray)) {
            minimumDistance = Int.MAX_VALUE
            distanceArray[nearestNeighbour.position].forEachIndexed { index, currentTravelTime ->
                if (!visitedArray[index] && currentTravelTime < minimumDistance) {
                    minimumDistance = currentTravelTime
                    minIndex = index
                }
            }

            nearestNeighbour = packages[minIndex]
            optimizedList.add(nearestNeighbour)
            totalDistance += minimumDistance
            visitedArray[minIndex] = true
        }

        //And finally we add the travel time of going from this last package to the endLocation
        totalDistance += endDistanceArray[nearestNeighbour.position]

        return route.copy(packages = optimizedList, totalDistance = totalDistance)

    }


    private fun areAllTrue(array: BooleanArray): Boolean {
        for (b in array) if (!b) {
            return false
        }
        return true
    }

    private fun getPermutationsRecursively(size: Int): ArrayList<Array<Int>> {
        val array = IntArray(size)
        permutationsListRec.clear()
        computePermutationsRecursive(size, array.toTypedArray(), 'a')
        return permutationsListRec
    }

    private fun computePermutationsRecursive(
        n: Int, elements: Array<Int>, delimiter: Char
    ) {
        if (n == 1) {
            permutationsListRec.add(elements.clone())
        } else {
            for (i in 0 until n - 1) {
                computePermutationsRecursive(n - 1, elements, delimiter)
                if (n % 2 == 0) {
                    swap(elements, i, n - 1)
                } else {
                    swap(elements, 0, n - 1)
                }
            }
            computePermutationsRecursive(n - 1, elements, delimiter)
        }
    }

    private fun swap(input: Array<Int>, a: Int, b: Int) {
        val tmp: Int = input[a]
        input[a] = input[b]
        input[b] = tmp
    }

    override fun getRouteTravelTime(
        packages: List<Package>,
        travelTimeArray: Array<IntArray>,
        startTravelTimeArray: IntArray,
        endTravelTimeArray: IntArray
    ): Int {
        if (packages.isEmpty()) return -1
        var travelTime = 0

        //from startLocation to first package
        travelTime += startTravelTimeArray[packages[0].position]

        //from every package to the next one
        for (i in 0 until packages.size - 1) {
            travelTime += travelTimeArray[packages[i].position][packages[i + 1].position]
        }

        //from last package to endLocation
        travelTime += endTravelTimeArray[packages.last().position]

        return travelTime
    }

    override fun getRouteDistance(
        packages: List<Package>,
        distanceArray: Array<IntArray>,
        startDistanceArray: IntArray,
        endDistanceArray: IntArray
    ): Int {
        if (packages.isEmpty()) return -1
        var distance = 0

        //from startLocation to first package
        distance += startDistanceArray[packages[0].position]

        //from every package to the next one
        for (i in 0 until packages.size - 1) {
            distance += distanceArray[packages[i].position][packages[i + 1].position]
        }

        //from last package to endLocation
        distance += globalEndTravelTimeArray[packages.last().position]

        return distance
    }

    override fun observeTravelTimeArray(): MutableLiveData<Array<IntArray>> {
        return globalTravelTimeArray
    }

    override fun getStartTravelTimeArray(): IntArray {
        return globalStartTravelTimeArray
    }

    override fun getEndTravelTimeArray(): IntArray {
        return globalEndTravelTimeArray
    }

    override fun getDistanceArray(): Array<IntArray> {
        return globalDistanceArray
    }

    override fun getStartDistanceArray(): IntArray {
        return globalStartDistanceArray
    }

    override fun getEndDistanceArray(): IntArray {
        return globalEndDistanceArray
    }

    override fun observeDirectionsResponse(): MutableLiveData<DirectionsResponse> {
        return directionsResponse
    }

    override fun observeOptimizedDirectionsAPIRoute(): MutableLiveData<Route> {
        return optimizedDirectionsAPIRoute
    }


    override fun setStartTravelTimeArray(startTravelTimeArray: IntArray) {
        globalStartTravelTimeArray = startTravelTimeArray
    }

    override fun setEndTravelTimeArray(endTravelTimeArray: IntArray) {
        globalEndTravelTimeArray = endTravelTimeArray
    }

    override fun setDistanceArray(distanceArray: Array<IntArray>) {
        globalDistanceArray = distanceArray
    }

    override fun setStartDistanceArray(startDistanceArray: IntArray) {
        globalStartDistanceArray = startDistanceArray
    }

    override fun setEndDistanceArray(endDistanceArray: IntArray) {
        globalEndDistanceArray = endDistanceArray
    }

    override fun removePackage(pckge: Package) {
        //TODO remove from database
    }

    override fun getExampleLastLocations(): List<Location> {
        return listOf(
            Location(address = "Valencia"),
            Location(address = "El Hierro la mejor isla del mundo entero ", latitude = 2.0),
            Location(address = "Tenerife la segunda mejor isla del mundo entero ", latitude = 3.0)
        )
    }

    override fun removePackageFromToDeliverList(pckge: Package) {
        val newPackages = CurrentSession.packagesToDeliver.value.filter {
            it.numPackage != pckge.numPackage
        }
        computeProperAlgorithmAndUpdateCurrentSession(CurrentSession.algorithm, newPackages)
    }

    override fun removeLastLocation(location: Location) {
        CurrentSession.lastLocationsList.value =
            CurrentSession.lastLocationsList.value.minus(location)
    }

    override fun addLastLocation(location: Location) {
        CurrentSession.lastLocationsList.value =
            CurrentSession.lastLocationsList.value.plus(location)
        //TODO add to the database
    }

    override fun setLastLocation(location: Location) {
        if (CurrentSession.deliveryMan != null) {
            CurrentSession.deliveryMan!!.lastLocation = location
            CurrentSession.route.value.endLocation = location
        }
        //TODO tell this to the database
    }

    override fun getNotUrgentRoute(): Route {
        return globalNotUrgentRoute
    }

    override fun getUrgentRoute(): Route {
        return globalUrgentRoute
    }

    override fun observeOptimizedVeryUrgentRoute(): MutableLiveData<Route> {
        return globalVeryUrgentRoute
    }

    override fun getExamplePackages(): List<Package> {
        return listOf(
            Package(
                location = Location(
                    address = "Avd Universitat 44 Valencia Espanya",
                    latitude = 39.872713,
                    longitude = -0.844302
                ),
                client = Client(name = "Esther Frasquet"),
                urgency = Urgency.URGENT,
                state = PackageState.RELOCATED,
                numPackage = 0
            ),

            Package(
                location = Location(
                    address = "Carrer Arquitecte Arnau 30, Valencia",
                    latitude = 38.804918,
                    longitude = -0.879529
                ),
                client = Client(name = "Esther Frasquet"),
                note = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown.",
                state = PackageState.CONFIRMED,
                numPackage = 1
            ),

            Package(
                location = Location(
                    address = "Avd Universitat 44",
                    latitude = 38.872713,
                    longitude = -0.754302
                ),
                client = Client(name = "Esther Frasquet"),
                state = PackageState.RELOCATED,
                numPackage = 2
            ),

            Package(
                location = Location(
                    address = "Avd Universitat 44",
                    latitude = 37.872713,
                    longitude = -0.784302
                ),
                client = Client(name = "Esther Frasquet"),
                urgency = Urgency.VERY_URGENT,
                state = PackageState.NOT_CONFIRMED,
                numPackage = 3
            ),

            Package(
                location = Location(
                    address = "Avd Universitat 44",
                    latitude = 38.752713,
                    longitude = -0.864302
                ),
                client = Client(name = "Esther Frasquet"),
                urgency = Urgency.VERY_URGENT,
                note = "olei",
                state = PackageState.CONFIRMED,
                numPackage = 4
            )
        )
    }

}