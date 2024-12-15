package com.example.digitalshelf.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalshelf.models.UserResource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlinx.coroutines.flow.asStateFlow






class PersonalLibraryViewModel : ViewModel() {
    private val _selectedTabs = MutableStateFlow<List<String>>(emptyList())
    val selectedTabs: StateFlow<List<String>> = _selectedTabs

    private val _resources = MutableStateFlow<List<UserResource>>(emptyList())
    val resources: StateFlow<List<UserResource>> = _resources

    private val _uploadStatus = MutableStateFlow<String>("")
    val uploadStatus: StateFlow<String> = _uploadStatus

    private val _uploadProgress = MutableStateFlow<Float>(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress

//    private val _deleteState = MutableLiveData<DeleteState>()
//    val deleteState: LiveData<DeleteState> get() = _deleteState

    private val _deleteState = MutableStateFlow<DeleteState?>(null)
    val deleteState: StateFlow<DeleteState?> get() = _deleteState.asStateFlow()

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        if (userId != null) {
            fetchSelectedTabs()
            fetchResources()
        } else {
            Log.e("PersonalLibraryViewModel", "User is not logged in")
        }
    }

    private fun fetchSelectedTabs() {
        if (userId == null) return

        viewModelScope.launch {
            val tabsRef = firebaseDatabase.reference
                .child("PROFILES") // Adjust to match your database structure
                .child(userId)
                .child("selectedTabs")

            tabsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val tabs = snapshot.children.map { it.getValue(String::class.java) ?: "" }
                        Log.d("PersonalLibraryViewModel", "Fetched selected tabs: $tabs")
                        _selectedTabs.value = tabs
                    } else {
                        Log.d("PersonalLibraryViewModel", "No tabs found for user: $userId")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PersonalLibraryViewModel", "Error fetching selected tabs: ${error.message}")
                }
            })
        }
    }

    private fun fetchResources() {
        if (userId == null) return

        viewModelScope.launch {
            val resourcesRef = firebaseDatabase.reference
                .child("UserResources") // Ensure it matches your database structure
                .child(userId)

            resourcesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val resources = snapshot.children.mapNotNull { it.getValue(UserResource::class.java) }
                        Log.d("PersonalLibraryViewModel", "Fetched resources: $resources")
                        _resources.value = resources
                    } else {
                        Log.d("PersonalLibraryViewModel", "No resources found for user: $userId")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PersonalLibraryViewModel", "Error fetching resources: ${error.message}")
                }
            })
        }
    }

    fun updateSelectedTab(tab: String) {
        if (!_selectedTabs.value.contains(tab)) {
            _selectedTabs.value = _selectedTabs.value + tab
        }
    }

    fun addNewTab(tab: String) {
        if (!_selectedTabs.value.contains(tab)) {
            _selectedTabs.value = _selectedTabs.value + tab
        }
    }

    fun updateUploadStatus(status: String) {
        _uploadStatus.value = status
    }

    fun deleteResource(resource: UserResource) {
        val resourceId = resource.id ?: return

        // Delete from Database
        if (userId != null) {
            val databaseRef = firebaseDatabase.reference
            databaseRef.child("UserResources").child(userId).child(resourceId).removeValue()

            // Delete from Storage
            val resourcePath = resource.storagePath ?: return
            firebaseStorage.reference.child(resourcePath).delete().addOnSuccessListener {
                fetchResources() // Refresh resources
            }.addOnFailureListener {
                Log.e("PersonalLibraryViewModel", "Error deleting resource: ${it.message}")
            }
        } else {
            Log.e("PersonalLibraryViewModel", "User ID is null")
        }
    }

    fun downloadResource(resource: UserResource) {
        val resourcePath = resource.storagePath ?: return

        val storageRef = firebaseStorage.reference.child(resourcePath)
        val localFile = File.createTempFile("resource", ".tmp") // Create a temporary file

        storageRef.getFile(localFile).addOnSuccessListener {
            // Handle successful download
        }.addOnFailureListener {
            Log.e("PersonalLibraryViewModel", "Error downloading resource: ${it.message}")
        }
    }

    fun playResource(resource: UserResource, context: Context) {
        val resourcePath = resource.storagePath ?: return

        val storageRef = firebaseStorage.reference.child(resourcePath)
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            // Use ExoPlayer to play the media
            val player = ExoPlayer.Builder(context).build()
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }.addOnFailureListener {
            Log.e("PersonalLibraryViewModel", "Error playing resource: ${it.message}")
        }
    }

    fun shareResource(resource: UserResource, context: Context) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf" // Adjust MIME type as needed
            putExtra(Intent.EXTRA_STREAM, Uri.parse(resource.storagePath)) // Ensure this is a valid URI
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Resource"))
    }

    fun uploadResource(uri: Uri, selectedTab: String, resourceTitle: String, context: Context) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "user_id_example"
        val storageRef = firebaseStorage.reference.child("resources/${selectedTab}/${uri.lastPathSegment}")

        storageRef.putFile(uri)
            .addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                _uploadProgress.value = progress
            }
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Create UserResource object with the provided title
                    val resource = UserResource(
                        id = taskSnapshot.metadata?.name,
                        title = resourceTitle,  // Use the provided title
                        description = selectedTab,  // Ensure the description is correctly set to the tab name
                        storagePath = downloadUri.toString()  // Use the download URL
                    )
                    // Update Realtime Database
                    FirebaseDatabase.getInstance().reference
                        .child("UserResources")
                        .child(userId)
                        .child(resource.id ?: "unknown")
                        .setValue(resource)
                        .addOnSuccessListener {
                            _uploadStatus.value = "Resource uploaded successfully"
                            _uploadProgress.value = 0f  // Reset progress after success
                            fetchResources()  // Refresh resources after uploading
                        }
                        .addOnFailureListener {
                            _uploadStatus.value = "Error saving resource to database: ${it.message}"
                        }
                }
            }
            .addOnFailureListener {
                _uploadStatus.value = "Error uploading file: ${it.message}"
            }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                // Implement your account deletion logic here
                userId?.let {
                    // Delete user's data from the database
                    val databaseRef = firebaseDatabase.reference
                    databaseRef.child("PROFILES").child(it).removeValue()
                    databaseRef.child("UserResources").child(it).removeValue()

                    // Delete user from authentication
                    FirebaseAuth.getInstance().currentUser?.delete()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _deleteState.value = DeleteState.Success
                        } else {
                            _deleteState.value = DeleteState.Error(task.exception?.message ?: "Unknown error")
                        }
                    }
                }
            } catch (e: Exception) {
                _deleteState.value = DeleteState.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class DeleteState {
        object Success : DeleteState()
        data class Error(val message: String) : DeleteState()
    }
}
