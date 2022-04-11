package com.packforyou.data.repositories

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.packforyou.data.models.DeliveryMan
import com.packforyou.data.dataSources.FirebaseDatabase

val DELIVERYMAN_REF = "deliveryMen"

interface FirebaseCallback {
    fun onResponse(response: Response)
}

class LoginRepository(
    private val rootRef: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val deliveryManRef: CollectionReference = rootRef.collection(DELIVERYMAN_REF)
) : ViewModel() {

    fun getAllDeliveryMen(): List<DeliveryMan> {
        return FirebaseDatabase.getAllDeliverymen()
    }

    fun getResponseFromFirestoreUsingCallback(callback: FirebaseCallback) {
        deliveryManRef.get().addOnCompleteListener { task ->
            val response = Response()
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

}

data class Response(
    var deliveryMen: List<DeliveryMan>? = null,
    var exception: Exception? = null
)