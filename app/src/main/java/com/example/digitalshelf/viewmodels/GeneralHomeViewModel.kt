package com.example.digitalshelf.viewmodels



import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalshelf.models.Resource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class GeneralHomeViewModel : ViewModel() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _selectedTab = MutableStateFlow("Books")
    val selectedTab: StateFlow<String> = _selectedTab

    private val _resources = MutableStateFlow<List<Resource>>(emptyList())
    val resources: StateFlow<List<Resource>> = _resources

    fun updateSelectedTab(tab: String) {
        _selectedTab.value = tab
        fetchResources(tab) // Fetch resources when the tab changes
    }

    // Function to fetch resources from Firebase based on the tab
    fun fetchResources(tab: String) {
        viewModelScope.launch {
            database.child("resources").child(tab).get().addOnSuccessListener { snapshot ->
                val newResources = snapshot.children.mapNotNull { child ->
                    val name = child.child("name").getValue(String::class.java)
                    val description = child.child("description").getValue(String::class.java)
                    val fileType = child.child("fileType").getValue(String::class.java)
                    val fileUrl = child.child("fileUrl").getValue(String::class.java)

                    if (name != null && description != null && fileType != null && fileUrl != null) {
                        Resource(name, description, fileType, fileUrl)
                    } else null
                }
                _resources.value = newResources
            }.addOnFailureListener {
                _resources.value = emptyList() // Handle errors gracefully
            }
        }
    }

}




