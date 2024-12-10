package com.example.digitalshelf.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalshelf.models.Resource
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.example.digitalshelf.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth

class PersonalLibraryViewModel : ViewModel() {

    // Firebase References
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("UserResources")
    private val userDatabaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val storageReference = FirebaseStorage.getInstance().reference

    // State for selected tab
    private val _selectedTab = MutableStateFlow("Photos")
    val selectedTab: StateFlow<String> = _selectedTab

    // State for user resources
    private val _resources = MutableStateFlow<List<Resource>>(emptyList())
    val resources: StateFlow<List<Resource>> = _resources

    init {
        loadResources()
    }

    // Update selected tab
    fun updateSelectedTab(tab: String) {
        _selectedTab.value = tab
        loadResources()
    }

    // Load resources for the selected tab
    private fun loadResources() {
        val tab = _selectedTab.value
        databaseReference.child(tab).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val resourceList = mutableListOf<Resource>()
                for (data in snapshot.children) {
                    val resource = data.getValue(Resource::class.java)
                    resource?.let { resourceList.add(it) }
                }
                _resources.value = resourceList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error (could show a message to the user, log it, etc.)
                // Example: Log.e("PersonalLibraryViewModel", "Error loading resources: ${error.message}")
            }
        })
    }

    // Start the upload process for the current tab
    fun startUpload(selectedTab: String) {
        viewModelScope.launch {
            // TODO: Implement file picker, upload to Firebase Storage, and save metadata in Firebase Database
            // This is where you would use Firebase Storage and Database references to manage file uploads
        }
    }

    // Placeholder for compression logic
    fun compressFile(fileUri: String) {
        viewModelScope.launch {
            // Implement audio/video compression here
            // Update Firebase storage and metadata
            // This would involve working with Firebase Storage to compress the file and update the resource metadata in Firebase Realtime Database
        }
    }

    fun deleteUserAccount(context: Context, navController: NavHostController) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid ?: return

        // Delete user data from Firebase Realtime Database
        userDatabaseReference.child(uid).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Delete user from Firebase Authentication
                    user.delete().addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Navigate to the Login Screen after account deletion
                            Toast.makeText(context, "Account deleted successfully.", Toast.LENGTH_SHORT).show()
                            navController.navigate(ROUTE_LOGIN) {
//                                popUpTo(ROUTE_LOGIN) { inclusive = true }
                            }
//                            Toast.makeText(context, "Account deleted successfully.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to delete account from Authentication.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to delete user data.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
