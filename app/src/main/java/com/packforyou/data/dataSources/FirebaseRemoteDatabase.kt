package com.packforyou.data.dataSources

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.packforyou.data.models.DeliveryMan
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface IFirebaseRemoteDatabase {}

val DELIVERYMAN_REF = "deliveryMen"

class FirebaseRemoteDatabaseImpl(
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val deliveryManRef: CollectionReference = rootRef.collection(DELIVERYMAN_REF) //igual esto val la pena posar-ho fora del constructor i tindre la classe sense paràmetres
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
/*
    fun getAllDeliverymen(): List<DeliveryMan> {
        var deliveryManList = listOf<DeliveryMan>()

        getDeliveryMenResponse(object : FirebaseCallback {
            override fun onResponse(response: DeliveryMenResponse) {
                println("Hola")
                deliveryManList = response.deliveryMen!!
                println(deliveryManList)
                println("adios")
            }
        })

        return deliveryManList
    }

 */

    /*
    private fun getDeliveryMenResponse(callback: FirebaseCallback) {
        deliveryManRef.get().addOnCompleteListener { task ->
            val response = DeliveryMenResponse()
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.deliveryMen = result.documents.mapNotNull { snapShot ->
                        snapShot.toObject(DeliveryMan::class.java)
                    }
                }
            } else {
                response.exception = task.exception
            }
            callback.onResponse(response)
        }
    }


     */
    fun getAllDeliveryMen(): List<DeliveryMan> {
        val deliveryMen = arrayListOf<DeliveryMan>()

        db.collection(DELIVERYMAN_REF)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val currentDeliveryMan = document.toObject<DeliveryMan>()
                    deliveryMen.add(currentDeliveryMan)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        return deliveryMen
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getFirstDeliveryMan(): Flow<DeliveryMan?> = callbackFlow {

        // Reference to use in Firestore
        var deliveryManCollection: CollectionReference? = null
        try {
            deliveryManCollection = FirebaseFirestore.getInstance()
                .collection(DELIVERYMAN_REF)
        } catch (e: Throwable) {
            // If Firebase cannot be initialized, close the stream of data
            // flow consumers will stop collecting and the coroutine will resume
            close(e)
        }

        // Registers callback to firestore, which will be called on new events
        val subscription = deliveryManCollection?.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) { return@addSnapshotListener }
            // Sends events to the flow! Consumers will get the new events

                trySend(snapshot.documents.get(0).toObject<DeliveryMan>())
        }

        // The callback inside awaitClose will be executed when the flow is
        // either closed or cancelled.
        // In this case, remove the callback from Firestore
        awaitClose { subscription?.remove() }
    }

}


data class DeliveryMenResponse(
    var deliveryMen: List<DeliveryMan>? = null,
    var exception: Exception? = null
)

