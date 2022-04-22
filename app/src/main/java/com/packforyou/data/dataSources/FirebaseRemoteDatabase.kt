package com.packforyou.data.dataSources

import android.annotation.SuppressLint
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.models.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

interface IFirebaseRemoteDatabase {
    fun getAllDeliveryMen(): Flow<State<List<DeliveryMan>>>

    fun addDeliveryMan(deliveryMan: DeliveryMan): Flow<State<DocumentReference>>
}

val DELIVERYMAN_REF = "deliveryMen"

class FirebaseRemoteDatabaseImpl(
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val deliveryManCollection: CollectionReference = rootRef.collection(DELIVERYMAN_REF) //igual esto val la pena posar-ho fora del constructor i tindre la classe sense paràmetres
) : IFirebaseRemoteDatabase {
    @SuppressLint("StaticFieldLeak")
    val db = Firebase.firestore

    fun getInstance() = db

    fun createRandomUser() {
        val deliveryMan = DeliveryMan(
            id = "123123123A",
            name = "John Doe",
            mail = "john@si.com",
            password = "123456",
            phone = 654654654
        )
        createDeliveryMan(deliveryMan)
    }

    fun createDeliveryMan(deliveryMan: DeliveryMan) {
        val hashmapDeliveryMan = deliveryManToHashMap(deliveryMan)

        db.collection("deliveryMen")
            .document(hashmapDeliveryMan["name"].toString())
            .set(hashmapDeliveryMan)

    }

    private fun deliveryManToHashMap(deliveryMan: DeliveryMan): HashMap<String, Any?> {
        return hashMapOf(
            "id" to deliveryMan.id,
            "name" to deliveryMan.name,
            "mail" to deliveryMan.mail,
            "password" to deliveryMan.password,
            "phone" to deliveryMan.phone
        )
    }

    override fun getAllDeliveryMen() = flow<State<List<DeliveryMan>>> {
        emit(State.loading())

        val snapshot = deliveryManCollection.get().await()
        val deliveryMen = snapshot.toObjects(DeliveryMan::class.java)

        // Emit success state with data
        emit(State.success(deliveryMen))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    override fun addDeliveryMan(deliveryMan: DeliveryMan) = flow<State<DocumentReference>> {

        // Emit loading state
        emit(State.loading())

        val deliveryManRef = deliveryManCollection.add(deliveryMan).await()

        // Emit success state with post reference
        emit(State.success(deliveryManRef))

    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)
}


data class DeliveryMenResponse(
    var deliveryMen: List<DeliveryMan>? = null,
    var exception: Exception? = null
)

