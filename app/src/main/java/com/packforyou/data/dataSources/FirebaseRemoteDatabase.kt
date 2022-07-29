package com.packforyou.data.dataSources

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.packforyou.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

interface IFirebaseRemoteDatabase {
    fun getAllDeliveryMen(): Flow<CallbackState<List<DeliveryMan>>>
    fun getDeliveryManPackages(deliveryManId: String): Flow<CallbackState<List<Package>>>
    fun getDeliveryMan(uid: String): Flow<CallbackState<DeliveryMan>>

    fun addDeliveryMan(deliveryMan: DeliveryMan): Flow<CallbackState<DocumentReference>>
    fun addPackage(packge: Package): Flow<CallbackState<DocumentReference>>
    fun addLocation(location: Location): Flow<CallbackState<DocumentReference>>
    fun addClient(client: Client): Flow<CallbackState<DocumentReference>>
    fun getFirebaseAuthConnection(): FirebaseAuth
}

const val DELIVERYMEN_REF = "deliveryMen"
const val PACKAGES_REF = "packages"
const val LOCATION_REF = "locations"
const val CLIENT_REF = "clients"

class FirebaseRemoteDatabaseImpl(
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance() //igual esto val la pena posar-ho fora del constructor i tindre la classe sense paràmetres
) : IFirebaseRemoteDatabase {
    @SuppressLint("StaticFieldLeak")
    val db = Firebase.firestore

    private val deliveryMenCollection: CollectionReference = rootRef.collection(DELIVERYMEN_REF)
    private val packagesCollection: CollectionReference = rootRef.collection(PACKAGES_REF)
    private val locationCollection: CollectionReference = rootRef.collection(LOCATION_REF)
    private val clientCollection: CollectionReference = rootRef.collection(CLIENT_REF)

    override fun getAllDeliveryMen(): Flow<CallbackState<List<DeliveryMan>>> {
        return flow {
            emit(CallbackState.loading())

            val snapshot = deliveryMenCollection.get().await()
            val deliveryMen = snapshot.toObjects(DeliveryMan::class.java)

            // Emit success state with data
            emit(CallbackState.success(deliveryMen))

        }.catch {
            // If exception is thrown, emit failed state along with message.
            emit(CallbackState.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }


    override fun addDeliveryMan(deliveryMan: DeliveryMan): Flow<CallbackState<DocumentReference>> {
        return flow {
            emit(CallbackState.loading())

            val deliveryManRef = deliveryMenCollection.document(deliveryMan.id)
            deliveryManRef.set(deliveryMan)

            emit(CallbackState.success(deliveryManRef))

        }.catch {
            emit(CallbackState.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }


    override fun getDeliveryManPackages(deliveryManId: String): Flow<CallbackState<List<Package>>> {
        return flow {
            emit(CallbackState.loading())

            val snapshot =
                packagesCollection.whereEqualTo("deliveryMan", deliveryManId).get().await()
            val packages = snapshot.toObjects(Package::class.java).toList()

            // Emit success state with data
            if (packages.isNotEmpty()) {
                emit(CallbackState.success(packages))
            }

        }.catch {
            emit(CallbackState.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    override fun addPackage(packge: Package): Flow<CallbackState<DocumentReference>> {
        return flow {
            emit(CallbackState.loading())

            val packageRef = packagesCollection.document(packge.numPackage.toString())
            packageRef.set(packge).await()

            emit(CallbackState.success(packageRef))

        }.catch {
            emit(CallbackState.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    override fun addLocation(location: Location): Flow<CallbackState<DocumentReference>> {
        return flow {
            emit(CallbackState.loading())

            val locationRef = locationCollection.document(location.address)
            locationRef.set(location).await()

            emit(CallbackState.success(locationRef))

        }.catch {
            emit(CallbackState.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    override fun addClient(client: Client): Flow<CallbackState<DocumentReference>> {
        return flow {
            emit(CallbackState.loading())

            val clientRef = clientCollection.document(client.id)
            clientRef.set(client).await()

            emit(CallbackState.success(clientRef))

        }.catch {
            emit(CallbackState.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    override fun getFirebaseAuthConnection(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    override fun getDeliveryMan(uid: String): Flow<CallbackState<DeliveryMan>> {
        return flow {
            emit(CallbackState.loading())

            val snapshot = deliveryMenCollection.document(uid).get().await()
            val deliveryMan = snapshot.toObject(DeliveryMan::class.java)

            // Emit success state with data
            if(deliveryMan != null)
            emit(CallbackState.success(deliveryMan))

        }.catch {
            emit(CallbackState.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }


}


data class DeliveryMenResponse(
    var deliveryMen: List<DeliveryMan>? = null,
    var exception: Exception? = null
)

