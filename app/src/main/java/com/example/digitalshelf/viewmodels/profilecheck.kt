package com.example.digitalshelf.viewmodels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileCheckViewModel : ViewModel() {
    private val _profileStatus = MutableStateFlow<String>("loading")
    val profileStatus: StateFlow<String> = _profileStatus

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        checkUserProfile()
    }

    private fun checkUserProfile() {
        if (userId == null) {
            _profileStatus.value = "not_setup"
            return
        }

        viewModelScope.launch {
            val profileRef = firebaseDatabase.reference
                .child("PROFILES")
                .child(userId)

            profileRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists() && snapshot.child("fullName").value != null && snapshot.child("age").value != null) {
                    _profileStatus.value = "setup"
                } else {
                    _profileStatus.value = "not_setup"
                }
            }.addOnFailureListener {
                _profileStatus.value = "not_setup"
            }
        }
    }
}
