package com.example.bemunsoed.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Kuliner(
    val id: String = "",
    val nama: String = "",
    val gambar: String = "",
    val harga: String = "",
    val alamat: String = "",
    val kontak: String = "",
    val isActive: Boolean = true,
    @ServerTimestamp
    val createdAt: Timestamp? = null
)
