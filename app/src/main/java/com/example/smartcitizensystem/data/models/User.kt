package com.example.smartcitizensystem.data.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val nid: String,
    val address: String,
    val profileImage: String? = null,
    val isBiometricEnabled: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

data class UserProfile(
    val userId: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val nidNumber: String,
    val address: String,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val bloodGroup: String? = null
)

data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val email: String? = null
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)