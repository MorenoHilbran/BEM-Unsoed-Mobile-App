package com.example.bemunsoed.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class InfoKos(
    val id: String = "",
    val nama: String = "",
    val gambar: String = "",
    val harga: String = "",
    val tipe: String = "", // "Pria" atau "Wanita"
    val alamat: String = "",
    val isActive: Boolean = true,
    @ServerTimestamp
    val createdAt: Timestamp? = null
)
