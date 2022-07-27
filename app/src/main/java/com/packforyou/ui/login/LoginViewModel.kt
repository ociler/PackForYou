package com.packforyou.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.packforyou.data.models.*
import com.packforyou.data.repositories.IUsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ILoginViewModel {
    fun getAllDeliveryMen()
    fun observeDeliveryMen(): LiveData<List<DeliveryMan>>
    fun observeDeliveryMan(): LiveData<DeliveryMan>
    fun addDeliveryMan(deliveryMan: DeliveryMan)
    fun addClient(client: Client)
    fun logOut()
    fun logIn(mail: String, password: String, callbackObject: ILoginCallback)
    fun getExampleDeliveryMan(): DeliveryMan
}


@HiltViewModel
class LoginViewModelImpl @Inject constructor(
    private val repository: IUsersRepository
) : ILoginViewModel, ViewModel() {

    private var mutableDeliveryMen = MutableLiveData<List<DeliveryMan>>()
    private var mutableDeliveryMan = MutableLiveData<DeliveryMan>()

    override fun getAllDeliveryMen() {
        viewModelScope.launch {
            mutableDeliveryMen.postValue(repository.getAllDeliveryMen())
            println("getAllViewModel $mutableDeliveryMen")
        }
    }

    override fun observeDeliveryMen(): LiveData<List<DeliveryMan>> {
        return mutableDeliveryMen
    }

    override fun observeDeliveryMan(): LiveData<DeliveryMan> {
        return mutableDeliveryMan
    }


    override fun addDeliveryMan(deliveryMan: DeliveryMan) {
        viewModelScope.launch {
            repository.addDeliveryMan(deliveryMan)
        }
    }

    override fun addClient(client: Client) {
        viewModelScope.launch {
            repository.addClient(client)
        }
    }

    override fun logOut() {
        CurrentSession.userUID = ""
        CurrentSession.packagesToDeliver = mutableStateOf(listOf())
        CurrentSession.packagesForToday = mutableStateOf(listOf())
        CurrentSession.lastLocationsList.value = listOf()
        CurrentSession.deliveryMan = null
    }

    override fun logIn(mail: String, password: String, callbackObject: ILoginCallback) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(mail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Successfully signed in
                    val firebaseUser = auth.currentUser
                    viewModelScope.launch {
                        mutableDeliveryMan.postValue(repository.getDeliveryMan(firebaseUser!!.uid))
                    }


                    //observeForever bc you don't need an owner
                    mutableDeliveryMan.observeForever { deliveryMan ->
                        CurrentSession.packagesForToday = if (deliveryMan.route != null) {
                            mutableStateOf(deliveryMan.route!!.packages)
                        } else {
                            mutableStateOf(listOf())
                        }

                        CurrentSession.userUID = firebaseUser!!.uid
                        CurrentSession.packagesToDeliver =
                            mutableStateOf(CurrentSession.packagesForToday.value)
                        CurrentSession.lastLocationsList =
                            mutableStateOf(deliveryMan.lastLocationList)
                        CurrentSession.deliveryMan = deliveryMan

                        callbackObject.onLoginSuccess()
                    }


                } else {
                    callbackObject.onLoginFailure()
                }

            }
    }

    override fun getExampleDeliveryMan(): DeliveryMan {

        val deliveryMan = DeliveryMan(
            id = "12345678A",
            name = "Rick Astley",
            phone = 654987654,
            mail = "example@packforyou.com",
            password = "example",
            currentLocation = Location(
                address = "Carrer Arquitecte Arnau 30, 46020, València",
                latitude = 39.485826,
                longitude = -0.35638,
                city = "Valencia",
                zipCode = 46020
            ),
            lastLocation = Location(
                address = "Carrer Arquitecte Arnau 30, 46020, València",
                latitude = 39.485826,
                longitude = -0.35638,
                city = "Valencia",
                zipCode = 46020
            ),
            lastLocationList = listOf(
                Location(
                    address = "Carrer Arquitecte Arnau 30, 46020, València",
                    latitude = 39.485826,
                    longitude = -0.35638,
                    city = "Valencia",
                    zipCode = 46020
                ),
                Location(
                    address = "Carrer Emili Baró 35",
                    latitude = 39.488724,
                    longitude = -0.360403,
                    city = "Valencia",
                    zipCode = 46020
                )
            )
        )

        deliveryMan.packages = listOf(
            Package(
                isDelivered = false,
                location = Location(
                    address = "Carrer del Reverend José Maria Pinazo,58,46020 València",
                    city = "Valencia",
                    latitude = 39.488277,
                    longitude = -0.364695,
                    zipCode = 46020
                ),
                note = "Don't worry about my dog's barks. He doesn't bite!!",
                numPackage = 5,
                urgency = Urgency.URGENT,
                client = Client(
                    name = "Raquel Gutiérrez"
                ),
                state = PackageState.NOT_CONFIRMED,

                ),
            Package(
                isDelivered = false,
                location = Location(
                    address = "Carrer de l'Historiador Chabret,3,46019 València",
                    city = "",
                    latitude = 39.492425,
                    longitude = -0.368503,
                    zipCode = 46020
                ),
                note = "",
                numPackage = 2,
                urgency = Urgency.NOT_URGENT,
                client = Client(
                    name = "Steffi Vollmut"
                ),
                state = PackageState.CONFIRMED
            ),

            Package(
                isDelivered = false,
                location = Location(
                    address =
                    "Carrer d'Agustín Lara, 8, 46019 València",
                    city = "",
                    latitude = 39.493444,
                    longitude = -0.370982,
                    zipCode = 46020
                ),
                note = "If I'm not at home, deliver the package to my neighbour. Thank you!",
                numPackage = 9,
                urgency = Urgency.NOT_URGENT,
                client = Client(
                    name = "Neza Divjak"
                ),
                state = PackageState.NEW_LOCATION
            ),

            Package(
                isDelivered = false,
                location = Location(
                    address =
                    "Carrer del Poeta Asins, 5, 46020 València",
                    city = "",
                    latitude = 39.487333,
                    longitude = -0.360446,
                    zipCode = 46020
                ),
                note = "",
                numPackage = 1,
                urgency = Urgency.VERY_URGENT,
                client = Client(
                    name = "Diana Darriba"
                ),
                state = PackageState.NOT_CONFIRMED
            ),
            Package(
                isDelivered = false,
                location = Location(
                    address = "Carrer de la Rambla, 32, 46020 València",
                    city = "",
                    latitude = 39.487419,
                    longitude = -0.357920,
                    zipCode = 46020
                ),
                note = "Thank you for your job :D",
                numPackage = 7,
                urgency = Urgency.NOT_URGENT,
                client = Client(
                    name = "Andrés Arroyo"
                ),
                state = PackageState.NEW_LOCATION
            ),
            Package(
                isDelivered = false,
                location = Location(
                    address = "Carrer d'Emili Baró,84,46020 València",
                    city = "",
                    latitude = 39.490828,
                    longitude = -0.359287,
                    zipCode = 46020
                ),
                note = "If the package doesn't fit on the mailbox, just drop it under it. Thanks!!",
                numPackage = 6,
                urgency = Urgency.URGENT,
                client = Client(
                    name = "Meeri Jürgenson"
                ),
                state = PackageState.MESSAGE_NOT_SENT
            ),
            Package(
                isDelivered = false,
                location = Location(
                    address = "Carrer de l'Arquitecte Arnau,55,46020 València",
                    city = "",
                    latitude = 39.485826,
                    longitude = -0.35638,
                    zipCode = 46020
                ),
                note = "",
                numPackage = 0,
                urgency = Urgency.NOT_URGENT,
                client = Client(
                    name = "Vid Rotvejn"
                ),
                state = PackageState.CONFIRMED
            ),
            Package(
                isDelivered = false,
                location = Location(
                    address = "Carrer d'Emili Baró,67,46020 València",
                    city = "",
                    latitude = 39.488724,
                    longitude = -0.360403,
                    zipCode = 46020
                ),
                note = "My door is the blue one.",
                numPackage = 4,
                urgency = Urgency.URGENT,
                client = Client(
                    name = "Klara Kornelia"
                ),
                state = PackageState.MESSAGE_NOT_SENT
            ),
            Package(
                isDelivered = false,
                location = Location(
                    address = "Carrer de l'Arquitecte Tolsà,1319,46019 València",
                    city = "",
                    latitude = 39.491978,
                    longitude = -0.365413,
                    zipCode = 46020
                ),
                note = "",
                numPackage = 8,
                urgency = Urgency.NOT_URGENT,
                client = Client(
                    name = "Jan Urankar"
                ),
                state = PackageState.NOT_CONFIRMED
            ),
            Package(
                isDelivered = false,
                location = Location(
                    address = "C. del Músic Hipòlit Martínez,2,46020 València",
                    city = "",
                    latitude = 39.484642,
                    longitude = -0.356584,
                    zipCode = 46020
                ),
                note = "",
                numPackage = 3,
                urgency = Urgency.VERY_URGENT,
                client = Client(
                    name = "Pedro García"
                ),
                state = PackageState.CONFIRMED
            )
        )

        deliveryMan.route = Route(
            id = 0,
            startLocation = deliveryMan.currentLocation,
            endLocation = deliveryMan.lastLocation,
            packages = listOf(
                Package(
                    isDelivered = false,
                    location = Location(
                        address = "Carrer del Reverend José Maria Pinazo,58,46020 València",
                        city = "Valencia",
                        latitude = 39.488277,
                        longitude = -0.364695,
                        zipCode = 46020
                    ),
                    note = "Don't worry about my dog's barks. He doesn't bite!!",
                    numPackage = 5,
                    urgency = Urgency.URGENT,
                    client = Client(
                        name = "Raquel Gutiérrez"
                    ),
                    state = PackageState.NOT_CONFIRMED,

                    ),
                Package(
                    isDelivered = false,
                    location = Location(
                        address = "Carrer de l'Historiador Chabret,3,46019 València",
                        city = "",
                        latitude = 39.492425,
                        longitude = -0.368503,
                        zipCode = 46020
                    ),
                    note = "",
                    numPackage = 2,
                    urgency = Urgency.NOT_URGENT,
                    client = Client(
                        name = "Steffi Vollmut"
                    ),
                    state = PackageState.CONFIRMED
                ),

                Package(
                    isDelivered = false,
                    location = Location(
                        address =
                        "Carrer d'Agustín Lara, 8, 46019 València",
                        city = "",
                        latitude = 39.493444,
                        longitude = -0.370982,
                        zipCode = 46020
                    ),
                    note = "If I'm not at home, deliver the package to my neighbour. Thank you!",
                    numPackage = 9,
                    urgency = Urgency.NOT_URGENT,
                    client = Client(
                        name = "Neza Divjak"
                    ),
                    state = PackageState.NEW_LOCATION
                ),

                Package(
                    isDelivered = false,
                    location = Location(
                        address =
                        "Carrer del Poeta Asins, 5, 46020 València",
                        city = "",
                        latitude = 39.487333,
                        longitude = -0.360446,
                        zipCode = 46020
                    ),
                    note = "",
                    numPackage = 1,
                    urgency = Urgency.VERY_URGENT,
                    client = Client(
                        name = "Diana Darriba"
                    ),
                    state = PackageState.NOT_CONFIRMED
                ),
                Package(
                    isDelivered = false,
                    location = Location(
                        address = "Carrer de la Rambla, 32, 46020 València",
                        city = "",
                        latitude = 39.487419,
                        longitude = -0.357920,
                        zipCode = 46020
                    ),
                    note = "Thank you for your job :D",
                    numPackage = 7,
                    urgency = Urgency.NOT_URGENT,
                    client = Client(
                        name = "Andrés Arroyo"
                    ),
                    state = PackageState.NEW_LOCATION
                ),
                Package(
                    isDelivered = false,
                    location = Location(
                        address = "Carrer d'Emili Baró,84,46020 València",
                        city = "",
                        latitude = 39.490828,
                        longitude = -0.359287,
                        zipCode = 46020
                    ),
                    note = "If the package doesn't fit on the mailbox, just drop it under it. Thanks!!",
                    numPackage = 6,
                    urgency = Urgency.URGENT,
                    client = Client(
                        name = "Meeri Jürgenson"
                    ),
                    state = PackageState.MESSAGE_NOT_SENT
                )
            ),
            totalTime = 2688,
            totalDistance = 13931
        )

        return deliveryMan
    }


}
