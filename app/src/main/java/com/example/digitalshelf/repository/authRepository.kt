package com.example.digitalshelf.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase



class AuthRepository {

    fun loginUser(
        email: String,
        password: String,
        onLoginSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onError("Both fields are required.")
            return
        }

        // Use Firebase Authentication to log in
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // After successful login, retrieve the user
                    val user = FirebaseAuth.getInstance().currentUser

                    if (user != null) {
                        // Reference the correct database URL
                        val database = Firebase.database("https://digitalshelf-f1703-default-rtdb.firebaseio.com/").reference
                        val usersRef = database.child("users")

                        usersRef.orderByChild("email").equalTo(email).get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    // Assuming you have a role stored for each user
                                    val userRole = snapshot.children.firstOrNull()?.child("role")?.value.toString()
                                    onLoginSuccess(userRole) // Pass the role to navigate to the appropriate screen
                                } else {
                                    onError("User not found in database.")
                                }
                            }
                            .addOnFailureListener { exception ->
                                onError("Database error: ${exception.localizedMessage}")
                            }
                    } else {
                        onError("User not found in Firebase Authentication.")
                    }
                } else {
                    // If login fails via Firebase Auth
                    onError("Login failed: ${task.exception?.localizedMessage}")
                }
            }
    }
}
