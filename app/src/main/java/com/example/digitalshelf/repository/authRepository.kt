package com.example.digitalshelf.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


data class User(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val role: String = "",
    val id: String = ""
)

class AuthRepository {

    // Function to log in the user
    fun loginUser(
        email: String,
        password: String,
        onLoginSuccess: (role: String, userId: String) -> Unit, // Now returns both role and userId
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
                                    // Assuming you have a role and userId stored for each user
                                    val userData = snapshot.children.firstOrNull()
                                    val userRole = userData?.child("role")?.value.toString()
                                    val userId = userData?.key ?: ""
                                    onLoginSuccess(userRole, userId) // Pass both role and userId
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

    // Function to get the current user's details
    fun getCurrentUser(): User {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val database = Firebase.database("https://digitalshelf-f1703-default-rtdb.firebaseio.com/").reference
        var currentUser: User? = null

        user?.let {
            database.child("users").child(it.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUser = snapshot.getValue(User::class.java) ?: User()
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Database error: ${error.message}")
                }
            })
        }

        return currentUser ?: User() // Return an empty User object if no user is found
    }

    // Function to update user profile
    fun updateUserProfile(name: String, email: String, currentPassword: String, newPassword: String) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        user?.let {
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(it.uid)

            // Update only if the newPassword is provided
            if (newPassword.isNotEmpty()) {
                it.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("Password updated successfully")
                    } else {
                        println("Password update failed: ${task.exception?.localizedMessage}")
                    }
                }
            }

            val updatedData = mapOf("name" to name, "email" to email)
            userRef.updateChildren(updatedData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Profile updated successfully")
                } else {
                    println("Profile update failed: ${task.exception?.localizedMessage}")
                }
            }
        }
    }

    // Function to send user feedback
    fun sendFeedback(feedback: String) {
        val feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks").push()
        feedbackRef.setValue(feedback).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Feedback submitted successfully")
            } else {
                println("Feedback submission failed: ${task.exception?.localizedMessage}")
            }
        }
    }
}
