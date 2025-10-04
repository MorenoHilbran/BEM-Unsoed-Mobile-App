package com.example.bemunsoed.util

import com.example.bemunsoed.R
import com.example.bemunsoed.ui.adapter.ProfileOption

object ProfileOptionsManager {

    fun getProfilePhotoOptions(): List<ProfileOption> {
        return listOf(
            ProfileOption("Default", R.drawable.ic_profile_placeholder, "default"),
            ProfileOption("Avatar 1", R.drawable.ic_person, "avatar1"),
            ProfileOption("Avatar 2", R.drawable.ic_profile, "avatar2"),
            ProfileOption("Avatar 3", R.drawable.profil, "avatar3"),
            ProfileOption("Avatar 4", R.drawable.ic_comic, "avatar4"),
            ProfileOption("Avatar 5", R.drawable.ic_forum, "avatar5")
        )
    }

    fun getBackgroundOptions(): List<ProfileOption> {
        return listOf(
            ProfileOption("Default", R.drawable.default_profile_bg, "default"),
            ProfileOption("Blue Gradient", R.drawable.profile_background, "blue_gradient"),
            ProfileOption("Green Pattern", R.drawable.banner_background, "green_pattern"),
            ProfileOption("Orange Theme", R.drawable.button_primary, "orange_theme"),
            ProfileOption("Purple Design", R.drawable.badge_background, "purple_design")
        )
    }

    fun getProfilePhotoDrawable(photoId: String): Int {
        return when (photoId) {
            "avatar1" -> R.drawable.ic_person
            "avatar2" -> R.drawable.ic_profile
            "avatar3" -> R.drawable.profil
            "avatar4" -> R.drawable.ic_comic
            "avatar5" -> R.drawable.ic_forum
            else -> R.drawable.ic_profile_placeholder
        }
    }

    fun getBackgroundDrawable(backgroundId: String): Int {
        return when (backgroundId) {
            "blue_gradient" -> R.drawable.profile_background
            "green_pattern" -> R.drawable.banner_background
            "orange_theme" -> R.drawable.button_primary
            "purple_design" -> R.drawable.badge_background
            else -> R.drawable.default_profile_bg
        }
    }

    fun getProfilePhotoPosition(photoId: String): Int {
        return when (photoId) {
            "avatar1" -> 1
            "avatar2" -> 2
            "avatar3" -> 3
            "avatar4" -> 4
            "avatar5" -> 5
            else -> 0
        }
    }

    fun getBackgroundPosition(backgroundId: String): Int {
        return when (backgroundId) {
            "blue_gradient" -> 1
            "green_pattern" -> 2
            "orange_theme" -> 3
            "purple_design" -> 4
            else -> 0
        }
    }
}
