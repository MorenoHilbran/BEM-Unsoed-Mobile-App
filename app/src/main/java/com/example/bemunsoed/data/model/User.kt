package com.example.bemunsoed.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val fakultas: String = "",
    val jurusan: String = "",
    val angkatan: String = "",
    val avatarUrl: String? = null, // Keep for backward compatibility
    val backgroundUrl: String? = null, // Keep for backward compatibility
    val profilePhotoId: String = "default", // New field for predefined profile photos
    val backgroundId: String = "default", // New field for predefined backgrounds
    val bio: String? = null
)
