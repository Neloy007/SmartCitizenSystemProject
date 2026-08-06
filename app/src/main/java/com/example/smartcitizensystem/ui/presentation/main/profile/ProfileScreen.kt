package com.example.smartcitizensystem.ui.presentation.main.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.smartcitizensystem.data.models.User
import com.example.smartcitizensystem.data.models.completedItemsCount
import com.example.smartcitizensystem.data.models.profileCompletion
import com.example.smartcitizensystem.ui.navigation.Screen

private val AccentColor = Color(0xFF7D7DFF)
private val AccentSoft = Color(0xFFF7F7FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    navController: NavHostController? = null
) {
    val user by viewModel.user
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val isSavingAddress by viewModel.isSavingAddress
    val isUploadingPhoto by viewModel.isUploadingPhoto

    var showAddressDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateProfileImageUri(it.toString()) }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    if (showAddressDialog) {
        AddressEditDialog(
            initialValue = user?.address.orEmpty(),
            isSaving = isSavingAddress,
            onDismiss = { showAddressDialog = false },
            onSave = { newAddress ->
                viewModel.updateAddress(newAddress)
                showAddressDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshProfile() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // ✅ Persistent completion bar — always visible under the top bar, regardless
            // of loading/error/loaded state below, so it never feels like it "pops in".
            ProfileCompletionBar(completion = user?.profileCompletion() ?: 0f)

            Box(modifier = Modifier.weight(1f)) {
                when {
                    isLoading && user == null -> LoadingState()
                    error != null && user == null -> ErrorState(
                        message = error ?: "Failed to load profile",
                        onRetry = { viewModel.refreshProfile() }
                    )
                    user != null -> ProfileContent(
                        user = user!!,
                        isUploadingPhoto = isUploadingPhoto,
                        onChangePhotoClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onAddAddressClick = { showAddressDialog = true },
                        onFaceScanClick = {
                            navController?.navigate(Screen.FaceScan.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = AccentColor)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Loading profile...", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = Color.Red
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message, color = Color.Red, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
            ) {
                Text("Retry")
            }
        }
    }
}

/** Slim, persistent progress bar shown beneath the top bar. */
@Composable
private fun ProfileCompletionBar(completion: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = completion,
        animationSpec = tween(durationMillis = 500),
        label = "profileCompletion"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AccentSoft)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile Completion",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AccentColor
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = AccentColor,
            trackColor = Color.White
        )
    }
}

@Composable
private fun ProfileContent(
    user: User,
    isUploadingPhoto: Boolean,
    onChangePhotoClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onFaceScanClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ProfileHeader(
            user = user,
            isUploadingPhoto = isUploadingPhoto,
            onChangePhotoClick = onChangePhotoClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel("Complete Your Profile")
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AccentSoft),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ChecklistRow(
                    icon = Icons.Default.CameraAlt,
                    title = "Profile Photo",
                    isComplete = !user.profileImage.isNullOrBlank(),
                    onClick = onChangePhotoClick
                )
                Divider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                ChecklistRow(
                    icon = Icons.Default.LocationOn,
                    title = "Home Address",
                    isComplete = user.address.isNotBlank(),
                    onClick = onAddAddressClick
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel("Verification")
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AccentSoft),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                ComingSoonRow(
                    icon = Icons.Default.MarkEmailRead,
                    title = "Email Verification",
                    subtitle = "Confirm your email address"
                )
                Divider(color = Color.LightGray.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                ComingSoonRow(
                    icon = Icons.Default.Face,
                    title = "Face Scan Verification",
                    subtitle = "Match your face to your NID photo",
                    onClick = onFaceScanClick
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel("Personal Information")
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AccentSoft),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(icon = Icons.Default.Phone, label = "Phone Number", value = user.phone.ifBlank { "Not set" })
                Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 4.dp))
                InfoRow(icon = Icons.Default.Email, label = "Email Address", value = user.email.ifBlank { "Not set" })
                if (user.nid.isNotBlank()) {
                    Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 4.dp))
                    InfoRow(icon = Icons.Default.Badge, label = "NID Number", value = user.nid)
                }
                if (user.address.isNotBlank()) {
                    Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 4.dp))
                    InfoRow(icon = Icons.Default.LocationOn, label = "Address", value = user.address)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Handled by MainScreen's drawer logout */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444)),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Logout, contentDescription = "Logout", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileHeader(
    user: User,
    isUploadingPhoto: Boolean,
    onChangePhotoClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(CircleShape)
                .background(AccentSoft)
                .clickable(enabled = !isUploadingPhoto) { onChangePhotoClick() },
            contentAlignment = Alignment.Center
        ) {
            when {
                isUploadingPhoto -> CircularProgressIndicator(color = AccentColor, modifier = Modifier.size(28.dp))
                !user.profileImage.isNullOrBlank() -> AsyncImage(
                    model = user.profileImage,
                    contentDescription = "Profile Photo",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                else -> Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Photo",
                    modifier = Modifier.size(48.dp),
                    tint = AccentColor
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(AccentColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change Photo",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap to change photo",
            fontSize = 12.sp,
            color = AccentColor,
            modifier = Modifier.clickable(enabled = !isUploadingPhoto) { onChangePhotoClick() }
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = user.name.ifBlank { "Citizen User" },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))
        VerificationBadge(completedItems = user.completedItemsCount())
    }
}

@Composable
private fun VerificationBadge(completedItems: Int) {
    val isFullyVerified = completedItems == 4
    val backgroundColor = if (isFullyVerified) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val contentColor = if (isFullyVerified) Color(0xFF2E7D32) else Color(0xFFEF6C00)
    val icon = if (isFullyVerified) Icons.Default.Verified else Icons.Default.HourglassEmpty
    val label = if (isFullyVerified) "Verified" else "$completedItems of 4 completed"

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = contentColor)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = contentColor)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
}

@Composable
private fun ChecklistRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isComplete: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = AccentColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        if (isComplete) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Complete", tint = Color(0xFF4CAF50))
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Add", fontSize = 13.sp, color = AccentColor, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = AccentColor)
            }
        }
    }
}

@Composable
private fun ComingSoonRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(if (onClick != null) AccentColor else Color(0xFFEEEEEE))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (onClick != null) "Scan Now" else "Coming Soon",
                fontSize = 11.sp,
                color = if (onClick != null) Color.White else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp), tint = AccentColor)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
}

@Composable
private fun AddressEditDialog(
    initialValue: String,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = { if (!isSaving) onDismiss() },
        title = { Text("Home Address") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Address") },
                minLines = 2,
                maxLines = 4,
                enabled = !isSaving,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    cursorColor = AccentColor
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(text) },
                enabled = !isSaving && text.isNotBlank()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = AccentColor)
                } else {
                    Text("Save", color = AccentColor, fontWeight = FontWeight.Medium)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSaving) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}