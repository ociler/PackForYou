package com.packforyou.data.dataSources

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.packforyou.data.DeliveryMan
import org.w3c.dom.Document
import java.util.*

object FirebaseDatabase {
    @SuppressLint("StaticFieldLeak")
    val db = Firebase.firestore

    fun getInstance() = db

    fun createRandomUser() {
        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815
        )

        val user2 = hashMapOf(
            "first" to "Alan",
            "middle" to "Mathison",
            "last" to "Turing",
            "born" to 1912
        )

        // Add a new document with a generated ID
        db.collection("users")
            .add(user2)

    }

    fun createUser(user: DeliveryMan) {
        db.collection("deliveryMen")
            .add(user)
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

