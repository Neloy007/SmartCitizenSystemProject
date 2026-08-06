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
    // ✅ New: verification status flags. Both default to false — email verification and
    // face-scan verification are not implemented yet (UI shows them as "Coming Soon"),
    // but the fields exist now so the completion calculation and future features have
    // something to read/write without another schema migration later.
    val isEmailVerified: Boolean = false,
    val isFaceVerified: Boolean = false,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Profile completion, expressed as 0f 1f.
 *
 * Checks four things a "fully verified" citizen profile needs:
 *  - a profile photo
 *  - a saved address
 *  - email verified
 *  - face-scan verified
 *
 * The last two are intentionally not user-completable yet (no UI wired to flip them),
 * so today's realistic max is 0.5f (2 of 4) until email + face verification ship.
 * That's by design — the bar should reflect true verification state, not just what the
 * user can currently do.
 */
fun User.profileCompletion(): Float {
    val checks = listOf(
        !profileImage.isNullOrBlank(),
        address.isNotBlank(),
        isEmailVerified,
        isFaceVerified
    )
    return checks.count { it } / checks.size.toFloat()
}

fun User.completedItemsCount(): Int = listOf(
    !profileImage.isNullOrBlank(),
    address.isNotBlank(),
    isEmailVerified,
    isFaceVerified
).count { it }

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