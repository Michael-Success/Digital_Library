package com.example.digitalshelf.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.digitalshelf.models.Resource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
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
            "fileUrl" to resource.fileUrl,
            "previewUrl" to resource.previewUrl,
            "drawableResIds" to resource.drawableResIds // Update to use drawableResIds
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

    fun uploadFileToStorageWithPreview(
        context: Context,
        fileUri: Uri,
        onSuccess: (String, String?) -> Unit, // Returns file URL and optional preview image URL
        onFailure: (String) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference.child("resources/${UUID.randomUUID()}")

        // Step 1: Upload the main file (PDF)
        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                // Get the file URL after upload
                storageRef.downloadUrl.addOnSuccessListener { fileUrl ->
                    // Step 2: Generate preview if the file is a PDF
                    val contentResolver = context.contentResolver
                    val parcelFileDescriptor =
                        contentResolver.openFileDescriptor(fileUri, "r")
                    if (parcelFileDescriptor != null) {
                        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
                        val page = pdfRenderer.openPage(0) // First page

                        // Render the first page to a Bitmap
                        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.close()
                        pdfRenderer.close()

                        // Save the Bitmap as a file
                        val previewFile = File(context.cacheDir, "preview_${UUID.randomUUID()}.png")
                        FileOutputStream(previewFile).use { out ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                        }

                        // Upload the preview file to Firebase Storage
                        val previewRef = FirebaseStorage.getInstance().reference.child("resources/previews/${UUID.randomUUID()}.png")
                        previewRef.putFile(Uri.fromFile(previewFile))
                            .addOnSuccessListener {
                                // Get the preview URL
                                previewRef.downloadUrl.addOnSuccessListener { previewUrl ->
                                    onSuccess(fileUrl.toString(), previewUrl.toString()) // Return both URLs
                                }
                            }
                            .addOnFailureListener { exception ->
                                onFailure("Failed to upload preview: ${exception.message}")
                            }
                    } else {
                        // No preview generated, proceed with file URL only
                        onSuccess(fileUrl.toString(), null)
                    }
                }.addOnFailureListener {
                    onFailure("Failed to get file URL: ${it.message}")
                }
            }
            .addOnFailureListener {
                onFailure("Failed to upload file: ${it.message}")
            }
    }
}
