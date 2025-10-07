package com.example.bemunsoed.ui.kuliner

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bemunsoed.data.model.Kuliner
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class KulinerViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _kulinerList = MutableLiveData<List<Kuliner>>()
    val kulinerList: LiveData<List<Kuliner>> = _kulinerList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadKuliner()
    }

    fun loadKuliner() {
        _isLoading.value = true
        Log.d("KulinerViewModel", "Loading kuliner data from Firestore")

        firestore.collection("kuliner")
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    Log.e("KulinerViewModel", "Error loading kuliner", error)
                    _errorMessage.value = "Gagal memuat data kuliner: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val kulinerItems = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Kuliner::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            Log.e("KulinerViewModel", "Error parsing document: ${doc.id}", e)
                            null
                        }
                    }
                    Log.d("KulinerViewModel", "Loaded ${kulinerItems.size} kuliner items")
                    _kulinerList.value = kulinerItems
                } else {
                    Log.w("KulinerViewModel", "Snapshot is null")
                    _kulinerList.value = emptyList()
                }
            }
    }

    fun refreshData() {
        loadKuliner()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
