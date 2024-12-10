package com.example.digitalshelf.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalshelf.models.Resource
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PersonalLibraryViewModel : ViewModel() {

    // Firebase References
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("UserResources")
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
}
