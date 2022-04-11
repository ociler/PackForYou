package com.packforyou.data.dataSources

import android.annotation.SuppressLint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.packforyou.data.models.DeliveryMan

object FirebaseDatabase {
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

    fun getAllDeliverymen(): List<DeliveryMan> {
        val deliveryManList = arrayListOf<DeliveryMan>()

        /*
        db.collection("deliveryMen")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(
                    value: QuerySnapshot,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {
                        Log.e("Firestore error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            deliveryManList.add(dc.document.toObject((DeliveryMan::class.java)))
                        }
                    }
                }
            })

         */

        return deliveryManList
    }
}

