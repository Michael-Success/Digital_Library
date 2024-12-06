package com.example.digitalshelf.viewmodels



import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class GeneralHomeViewModel : ViewModel() {

    // Firebase database reference
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // StateFlow for the selected tab
    private val _selectedTab = MutableStateFlow("Books")
    val selectedTab: StateFlow<String> = _selectedTab

    // StateFlow for the resources list
    private val _resources = MutableStateFlow<List<String>>(emptyList())
    val resources: StateFlow<List<String>> = _resources

    // Function to update the selected tab
    fun updateSelectedTab(tab: String) {
        _selectedTab.value = tab
        fetchResources(tab) // Automatically fetch resources when the tab changes
    }

    // Function to fetch resources from Firebase
    fun fetchResources(tab: String) {
        viewModelScope.launch {
            database.child("resources").child(tab).get().addOnSuccessListener { snapshot ->
                val newResources = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                _resources.value = newResources
            }.addOnFailureListener {
                _resources.value = listOf("Error fetching resources")
            }
        }
    }
}
