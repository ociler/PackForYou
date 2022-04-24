package com.packforyou.data.dataSources

import android.annotation.SuppressLint
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.models.Package
import com.packforyou.data.models.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

interface IFirebaseRemoteDatabase {
    fun getAllDeliveryMen(): Flow<State<List<DeliveryMan>>>

    fun addDeliveryMan(deliveryMan: DeliveryMan): Flow<State<DocumentReference>>
    fun addPackage(packge: Package): Flow<State<DocumentReference>>
    fun getDeliveryManPackages(deliveryManId: String): Flow<State<List<Package>>>
}

val DELIVERYMEN_REF = "deliveryMen"
val PACKAGES_REF = "packages"

class FirebaseRemoteDatabaseImpl(
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance() //igual esto val la pena posar-ho fora del constructor i tindre la classe sense paràmetres
) : IFirebaseRemoteDatabase {
    @SuppressLint("StaticFieldLeak")
    val db = Firebase.firestore

    private val deliveryMenCollection: CollectionReference = rootRef.collection(DELIVERYMEN_REF)
    private val packagesCollection: CollectionReference = rootRef.collection(PACKAGES_REF)

    fun getInstance() = db

    override fun getAllDeliveryMen(): Flow<State<List<DeliveryMan>>> {
        return flow {
            emit(State.loading())

            val snapshot = deliveryMenCollection.get().await()
            val deliveryMen = snapshot.toObjects(DeliveryMan::class.java)

            // Emit success state with data
            emit(State.success(deliveryMen))

        }.catch {
            // If exception is thrown, emit failed state along with message.
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }


    override fun addDeliveryMan(deliveryMan: DeliveryMan): Flow<State<DocumentReference>> {
        return flow {
            emit(State.loading())

            val deliveryManRef = deliveryMenCollection.document("seh")
            deliveryManRef.set(deliveryMan)
            
            emit(State.success(deliveryManRef))

        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }


    override fun getDeliveryManPackages(deliveryManId: String): Flow<State<List<Package>>> {
        return flow {
            emit(State.loading())

            val snapshot = packagesCollection.whereEqualTo("deliveryMan", deliveryManId).get().await()
            val packages = snapshot.toObjects(Package::class.java).toList()

            // Emit success state with data
            if (packages.isNotEmpty()) {
                emit(State.success(packages))
            }

        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    override fun addPackage(packge: Package): Flow<State<DocumentReference>> {
        return flow {
            emit(State.loading())

            val packageRef = packagesCollection.document(packge.numPackage.toString())
            packageRef.set(packge).await()

            emit(State.success(packageRef))

        }.catch {
            emit(State.failed(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }



}


data class DeliveryMenResponse(
    var deliveryMen: List<DeliveryMan>? = null,
    var exception: Exception? = null
)

