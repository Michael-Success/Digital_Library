package com.example.digitalshelf.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.example.digitalshelf.models.Resource
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID



class AdminViewModel : ViewModel() {

    private val storage = FirebaseStorage.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    // To track the upload progress
    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress

    // To track the upload status message
    private val _uploadStatus = MutableStateFlow("")
    val uploadStatus: StateFlow<String> = _uploadStatus

    fun uploadResourceMetadata(resource: Resource) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("resources")
        val resourceMap = mapOf(
            "name" to resource.name,
            "description" to resource.description,
            "fileType" to resource.fileType,
            "fileUrl" to resource.fileUrl
        )

        databaseRef.push().setValue(resourceMap)
            .addOnSuccessListener {
                _uploadStatus.value = "Resource metadata uploaded successfully!"
            }
            .addOnFailureListener {
                _uploadStatus.value = "Failed to upload metadata."
            }
    }


    fun uploadResource(resource: Resource, fileUri: Uri) {
        // Step 1: Upload file to Firebase Storage
        val storageRef = storage.reference.child("resources/${resource.name}")
        val uploadTask = storageRef.putFile(fileUri)

        // Track upload progress
        uploadTask.addOnProgressListener { snapshot ->
            val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
            _uploadProgress.value = progress
        }

        // Handle success when the file is uploaded
        uploadTask.addOnSuccessListener {
            // Get the file URL after successful upload
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                // Step 2: Upload resource metadata to Firebase Realtime Database
                val resourceMap = mapOf(
                    "name" to resource.name,
                    "description" to resource.description,
                    "fileType" to resource.fileType,
                    "fileUrl" to downloadUrl.toString()
                )

                // Save metadata to Realtime Database (new resource entry)
                val databaseRef = FirebaseDatabase.getInstance().reference
                val resourceRef = databaseRef.child("resources").push() // Unique ID generated
                resourceRef.setValue(resourceMap)
                    .addOnSuccessListener {
                        _uploadStatus.value = "Upload successfully!" // Success message
                        _uploadProgress.value = 0f // Reset progress after success
                    }
                    .addOnFailureListener {
                        _uploadStatus.value = "Failed to save resource metadata." // Failure message for database
                    }
            }.addOnFailureListener {
                _uploadStatus.value = "Failed to get file URL." // Failure message for download URL retrieval
            }
        }.addOnFailureListener {
            _uploadStatus.value = "Upload failed. Please try again." // Failure message for file upload
        }
    }



    fun uploadFileToStorage(fileUri: Uri, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("resources/${UUID.randomUUID()}")

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                // Get the file URL after upload
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString()) // Return the file URL
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Failed to upload file: ${exception.message}")
            }
    }

}


