package com.packforyou

import com.google.gson.Gson
import com.packforyou.data.models.Location
import com.packforyou.data.models.Package
import com.packforyou.data.models.Route
import com.packforyou.data.repositories.IPackagesAndAtlasRepository
import com.packforyou.ui.packages.IPackagesViewModel
import com.packforyou.ui.packages.PackagesViewModelImpl
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import org.junit.runner.RunWith
import org.mockito.Mock
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
class PackagesUnitTest {

    @Mock
    private lateinit var packagesViewModel: IPackagesViewModel
    private var packagesAndAtlasRepository = mock(IPackagesAndAtlasRepository::class.java)

    private lateinit var travelTimeArray: Array<IntArray>
    private lateinit var startTravelTimeArray: IntArray
    private lateinit var endTravelTimeArray: IntArray

    private lateinit var distanceArray: Array<IntArray>
    private lateinit var startDistanceArray: IntArray
    private lateinit var endDistanceArray: IntArray

    private lateinit var startLocation: Location
    private lateinit var endLocation: Location


    private lateinit var packages: Array<Package>
    private lateinit var expectedPackages: Array<Package>


    @Before
    fun setUp() {
        packagesViewModel = PackagesViewModelImpl(packagesAndAtlasRepository)

        var reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("test_start_location.json"))
        startLocation = Gson().fromJson(reader, Location::class.java)

        reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("test_end_location.json"))
        endLocation = Gson().fromJson(reader, Location::class.java)

        /***ARRAYS***/
        reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("travel_time_array_test.json"))
        travelTimeArray = Gson().fromJson(reader, Array<IntArray>::class.java)

        reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("distance_array_test.json"))
        distanceArray = Gson().fromJson(reader, Array<IntArray>::class.java)

        reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("start_travel_time_array_test.json"))
        startTravelTimeArray = Gson().fromJson(reader, IntArray::class.java)

        reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("start_distance_array_test.json"))
        startDistanceArray = Gson().fromJson(reader, IntArray::class.java)

        reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("end_travel_time_array_test.json"))
        endTravelTimeArray = Gson().fromJson(reader, IntArray::class.java)

        reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("end_distance_array_test.json"))
        endDistanceArray = Gson().fromJson(reader, IntArray::class.java)


        reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("test_packages.json"))
        packages = Gson().fromJson(reader, Array<Package>::class.java)

        reader =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream("expected_packages.json"))
        expectedPackages = Gson().fromJson(reader, Array<Package>::class.java)

        packagesViewModel.computePermutations(packages.size)
    }


    @Test
    fun brute_force_travel_time_works_properly() {
        val route = Route(packages = packages.toList())
        val optimizedRoute = packagesViewModel.getOptimizedRouteBruteForceTravelTime(
            route = route,
            startTravelTimeArray = startTravelTimeArray,
            travelTimeArray = travelTimeArray,
            endTravelTimeArray = endTravelTimeArray
        )

        val packageOrder = arrayListOf<Int>()
        optimizedRoute.packages.forEach {
            packageOrder.add(it.numPackage)
        }

        val expectedOrder = arrayListOf<Int>()
        expectedPackages.forEach {
            expectedOrder.add(it.numPackage)
        }

        assertArrayEquals(packageOrder.toArray(), expectedOrder.toArray())
    }

    @Test
    fun brute_force_distance_works_properly() {
        val route = Route(packages = packages.toList())
        val optimizedRoute = packagesViewModel.getOptimizedRouteBruteForceDistance(
            route = route,
            startDistanceArray = startDistanceArray,
            distanceArray = distanceArray,
            endDistanceArray = endDistanceArray
        )

        val packageOrder = arrayListOf<Int>()
        optimizedRoute.packages.forEach {
            packageOrder.add(it.numPackage)
        }

        val expectedOrder = arrayListOf<Int>()
        expectedPackages.forEach {
            expectedOrder.add(it.numPackage)
        }

        assertArrayEquals(packageOrder.toArray(), expectedOrder.toArray())
    }

    @Test
    fun closest_neighbour_travel_time_works_properly() {
        val route = Route(packages = packages.toList())
        val optimizedRoute = packagesViewModel.getOptimizedRouteClosestNeighbourTravelTime(
            route = route,
            startTravelTimeArray = startTravelTimeArray,
            travelTimeArray = travelTimeArray,
            endTravelTimeArray = endTravelTimeArray
        )

        val packageOrder = arrayListOf<Int>()
        optimizedRoute.packages.forEach {
            packageOrder.add(it.numPackage)
        }

        val expectedOrder = arrayListOf<Int>()
        expectedPackages.forEach {
            expectedOrder.add(it.numPackage)
        }

        assertArrayEquals(packageOrder.toArray(), expectedOrder.toArray())
    }

    @Test
    fun closest_neighbour_distance_works_properly() {
        val route = Route(packages = packages.toList())
        val optimizedRoute = packagesViewModel.getOptimizedRouteClosestNeighbourDistance(
            route = route,
            startDistanceArray = startDistanceArray,
            distanceArray = distanceArray,
            endDistanceArray = endDistanceArray
        )

        val packageOrder = arrayListOf<Int>()
        optimizedRoute.packages.forEach {
            packageOrder.add(it.numPackage)
        }

        val expectedOrder = arrayListOf<Int>()
        expectedPackages.forEach {
            expectedOrder.add(it.numPackage)
        }

        assertArrayEquals(packageOrder.toArray(), expectedOrder.toArray())
    }


}