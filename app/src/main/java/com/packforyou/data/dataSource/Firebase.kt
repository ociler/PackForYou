package com.packforyou.data.dataSource

import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore

object Firebase {
    @SuppressLint("StaticFieldLeak")
    var db = FirebaseFirestore.getInstance()

    fun getInstance() = db

    fun createRandomUser() {
        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815
        )

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)

        println("YES")
    }
}