package com.example.bemunsoed.util

import com.example.bemunsoed.R
import com.example.bemunsoed.ui.adapter.ProfileOption

object ProfileOptionsManager {

    fun getProfilePhotoOptions(): List<ProfileOption> {
        return listOf(
            ProfileOption("Default", R.drawable.ava_1, "default"),
            ProfileOption("Avatar 1", R.drawable.ava_2, "avatar1"),
            ProfileOption("Avatar 2", R.drawable.ava_3, "avatar2"),
            ProfileOption("Avatar 3", R.drawable.ava_4, "avatar3"),
            ProfileOption("Avatar 4", R.drawable.ava_5, "avatar4"),
        )
    }

    fun getBackgroundOptions(): List<ProfileOption> {
        return listOf(
            ProfileOption("Default", R.drawable.default_profile_bg, "default"),
            ProfileOption("Brave Pink", R.drawable.pink_background, "brave_pink"),
            ProfileOption("Hero Green", R.drawable.green_background, "hero_green"),
            ProfileOption("Orange Theme", R.drawable.orange_background, "orange_theme"),
            ProfileOption("Purple Theme", R.drawable.purple_background, "purple_design")
        )
    }

    fun getProfilePhotoDrawable(photoId: String): Int {
        return when (photoId) {
            "avatar1" -> R.drawable.ava_1
            "avatar2" -> R.drawable.ava_2
            "avatar3" -> R.drawable.ava_3
            "avatar4" -> R.drawable.ava_4
            else -> R.drawable.ava_5
        }
    }

    fun getBackgroundDrawable(backgroundId: String): Int {
        return when (backgroundId) {
            "brave_pink" -> R.drawable.pink_background
            "hero_green" -> R.drawable.green_background
            "orange_theme" -> R.drawable.orange_background
            "purple_design" -> R.drawable.purple_background
            else -> R.drawable.default_profile_bg
        }
    }

    fun getProfilePhotoPosition(photoId: String): Int {
        return when (photoId) {
            "avatar1" -> 1
            "avatar2" -> 2
            "avatar3" -> 3
            "avatar4" -> 4
            else -> 0
        }
    }

    fun getBackgroundPosition(backgroundId: String): Int {
        return when (backgroundId) {
            "brave_pink" -> 1
            "hero_green" -> 2
            "orange_theme" -> 3
            "purple_design" -> 4
            else -> 0
        }
    }
}
