package com.packforyou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.packforyou.algorithmsperformancetest.AlgorithmPerformanceTest
import com.packforyou.ui.navigation.SetupNavGraph
import com.packforyou.ui.atlas.AtlasViewModelImpl
import com.packforyou.ui.atlas.IAtlasViewModel
import com.packforyou.ui.login.ILoginViewModel
import com.packforyou.ui.login.LoginViewModelImpl
import com.packforyou.ui.packages.*
import com.packforyou.ui.theme.PackForYouTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var loginViewModel: ILoginViewModel
    private lateinit var packagesViewModel: IPackagesViewModel
    private lateinit var atlasViewModel: IAtlasViewModel


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this)[LoginViewModelImpl::class.java]

        packagesViewModel =
            ViewModelProvider(this)[PackagesViewModelImpl::class.java]

        atlasViewModel =
            ViewModelProvider(this)[AtlasViewModelImpl::class.java]


        val mode = "Town" //with this, we will control which situation we want to test
        val numPckgMode = "8"
        val usingLocalFiles = true
        val performingAlgorithmsTests = true
        val callingDirectionsAPI = false


        setContent {
            PackForYouTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    viewModelOwner = this,
                    lifecycleOwner = this
                )
            }
        }

        if (performingAlgorithmsTests) {
            val algorithmTests = AlgorithmPerformanceTest(
                usingLocalFiles = usingLocalFiles,
                callingDirectionsAPI = callingDirectionsAPI,
                mode = mode,
                numPckgMode = numPckgMode,
                resources = resources,
                packagesViewModel = packagesViewModel,
                owner = this
            )
            algorithmTests.performTests()
        }
    }
}