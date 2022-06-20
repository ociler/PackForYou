package com.packforyou

import com.google.gson.Gson
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import com.packforyou.data.repositories.IPackagesAndAtlasRepository
import com.packforyou.data.repositories.PackagesAndAtlasRepositoryImpl
import com.packforyou.ui.packages.IPackagesViewModel
import com.packforyou.ui.packages.PackagesViewModelImpl
import io.mockk.InternalPlatformDsl.toArray
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.InputStreamReader

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(MockitoJUnitRunner::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlgorithmsPreconditionsUnitTest {

    @Mock
    private lateinit var packagesViewModel: IPackagesViewModel
    private var packagesAndAtlasRepository = mock(IPackagesAndAtlasRepository::class.java)

    private var travelTimeArray = arrayOf(intArrayOf(1, 2), intArrayOf(3, 4))
    private var startTravelTimeArray = intArrayOf(1, 2)
    private var endTravelTimeArray = intArrayOf(1, 2)

    private var distanceArray = arrayOf(intArrayOf(1, 2), intArrayOf(3, 4))
    private var startDistanceArray = intArrayOf(1, 2)
    private var endDistanceArray = intArrayOf(1, 2)


    @Before
    fun setUp() {
        packagesViewModel = PackagesViewModelImpl(packagesAndAtlasRepository)
    }


    @Test
    fun zero_packages_on_brute_force_travel_time() {
        val route = Route(packages = listOf())

        val routeId = packagesViewModel.getOptimizedRouteBruteForceTravelTime(
            route = route,
            travelTimeArray = travelTimeArray,
            startTravelTimeArray = startTravelTimeArray,
            endTravelTimeArray = endTravelTimeArray
        ).id

        assertEquals(routeId, -1)
    }

    @Test
    fun zero_packages_on_brute_force_distance() {
        val route = Route(packages = listOf())

        val routeId = packagesViewModel.getOptimizedRouteBruteForceDistance(
            route = route,
            distanceArray = distanceArray,
            startDistanceArray = startDistanceArray,
            endDistanceArray = endDistanceArray
        ).id

        assertEquals(routeId, -1)
    }


    @Test
    fun different_size_arrays_and_packages_brute_force_travel_time() {
        val route = Route(packages = listOf(Package()))

        val routeId = packagesViewModel.getOptimizedRouteBruteForceTravelTime(
            route = route,
            travelTimeArray = travelTimeArray,
            startTravelTimeArray = startTravelTimeArray,
            endTravelTimeArray = endTravelTimeArray
        ).id

        assertEquals(routeId, -2)
    }

    @Test
    fun different_size_arrays_and_packages_brute_force_distance() {
        val route = Route(packages = listOf(Package()))

        val routeId = packagesViewModel.getOptimizedRouteBruteForceDistance(
            route = route,
            distanceArray = distanceArray,
            startDistanceArray = startDistanceArray,
            endDistanceArray = endDistanceArray
        ).id

        assertEquals(routeId, -2)
    }


    @Test
    fun zero_packages_on_closest_neighbour_travel_time() {
        val route = Route(packages = listOf())

        val routeId = packagesViewModel.getOptimizedRouteClosestNeighbourTravelTime(
            route = route,
            travelTimeArray = travelTimeArray,
            startTravelTimeArray = startTravelTimeArray,
            endTravelTimeArray = endTravelTimeArray
        ).id

        assertEquals(routeId, -1)
    }

    @Test
    fun zero_packages_on_closest_neighbour_distance() {
        val route = Route(packages = listOf())

        val routeId = packagesViewModel.getOptimizedRouteClosestNeighbourDistance(
            route = route,
            distanceArray = distanceArray,
            startDistanceArray = startDistanceArray,
            endDistanceArray = endDistanceArray
        ).id

        assertEquals(routeId, -1)
    }


    @Test
    fun different_size_arrays_and_packages_closest_neighbour_travel_time() {
        val route = Route(packages = listOf(Package()))

        val routeId = packagesViewModel.getOptimizedRouteClosestNeighbourTravelTime(
            route = route,
            travelTimeArray = travelTimeArray,
            startTravelTimeArray = startTravelTimeArray,
            endTravelTimeArray = endTravelTimeArray
        ).id

        assertEquals(routeId, -2)
    }

    @Test
    fun different_size_arrays_and_packages_closest_neighbour_distance() {
        val route = Route(packages = listOf(Package()))

        val routeId = packagesViewModel.getOptimizedRouteClosestNeighbourDistance(
            route = route,
            distanceArray = distanceArray,
            startDistanceArray = startDistanceArray,
            endDistanceArray = endDistanceArray
        ).id

        assertEquals(routeId, -2)
    }
}