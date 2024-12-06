package com.example.digitalshelf.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.example.digitalshelf.models.Resource
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AdminViewModel : ViewModel() {
    private val _uploadStatus = MutableStateFlow<String>("")
    val uploadStatus: StateFlow<String> get() = _uploadStatus

    fun uploadResource(
        resource: Resource,
        fileUri: Uri
    ) {
        viewModelScope.launch {
            val storageReference = FirebaseStorage.getInstance().reference
            val databaseReference = FirebaseDatabase.getInstance().reference

            // Create a reference for the file in Firebase Storage
            val fileRef: StorageReference = storageReference.child("Media/${resource.fileType}/${System.currentTimeMillis()}")

            // Upload the file to Firebase Storage
            fileRef.putFile(fileUri)
                .addOnSuccessListener { taskSnapshot ->
                    // After the file is uploaded, get the download URL
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        // Save resource metadata to Firebase Realtime Database
                        val resourceMetadata = mapOf(
                            "name" to resource.name,
                            "description" to resource.description,
                            "fileType" to resource.fileType,
                            "url" to uri.toString()
                        )

                        databaseReference.child("resources").push()
                            .setValue(resourceMetadata)
                            .addOnSuccessListener {
                                _uploadStatus.value = "Resource uploaded and metadata saved successfully!"
                            }
                            .addOnFailureListener { exception ->
                                _uploadStatus.value = "Failed to save metadata: ${exception.message}"
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    _uploadStatus.value = "File upload failed: ${exception.message}"
                }
        }
    }
}
