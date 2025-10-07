package com.example.bemunsoed.ui.infokos

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bemunsoed.data.model.InfoKos
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class InfoKosViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _infoKosList = MutableLiveData<List<InfoKos>>()
    val infoKosList: LiveData<List<InfoKos>> = _infoKosList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadInfoKos()
    }

    fun loadInfoKos() {
        _isLoading.value = true
        Log.d("InfoKosViewModel", "Loading info kos data from Firestore")

        firestore.collection("infokos")
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false

                if (error != null) {
                    Log.e("InfoKosViewModel", "Error loading info kos", error)
                    _errorMessage.value = "Gagal memuat data info kos: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val infoKosItems = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(InfoKos::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            Log.e("InfoKosViewModel", "Error parsing document: ${doc.id}", e)
                            null
                        }
                    }
                    Log.d("InfoKosViewModel", "Loaded ${infoKosItems.size} info kos items")
                    _infoKosList.value = infoKosItems
                } else {
                    Log.w("InfoKosViewModel", "Snapshot is null")
                    _infoKosList.value = emptyList()
                }
            }
    }

    fun refreshData() {
        loadInfoKos()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
