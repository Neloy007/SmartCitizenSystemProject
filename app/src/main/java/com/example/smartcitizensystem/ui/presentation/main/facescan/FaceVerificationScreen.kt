package com.example.smartcitizensystem.ui.presentation.main.facescan

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import java.util.concurrent.Executors

private val AccentGreen = Color(0xFF4CAF50)
private val AccentColor = Color(0xFF7D7DFF)

@Composable
fun FaceVerificationScreen(
    navController: NavHostController,
    viewModel: FaceVerificationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (hasCameraPermission) {
            CameraContent(viewModel = viewModel, onClose = { navController.popBackStack() })
        } else {
            PermissionRationale(
                onGrantClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                onClose = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun PermissionRationale(onGrantClick: () -> Unit, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.CameraAlt,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Camera access is needed for face verification",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onGrantClick,
            colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
        ) {
            Text("Grant Camera Access")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onClose) { Text("Cancel", color = Color.Gray) }
    }
}

@Composable
private fun CameraContent(viewModel: FaceVerificationViewModel, onClose: () -> Unit) {
    val uiState by viewModel.uiState
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { cameraExecutor.shutdown() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor, FaceAnalyzer { frame ->
                                viewModel.onFrame(frame)
                            })
                        }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            preview,
                            analysis
                        )
                    } catch (e: Exception) {
                        Log.e("FaceVerification", "Camera bind failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }
        )

        // Subtle scrim so overlay text/icons stay legible over any background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.15f))
        )

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }

        Text(
            text = uiState.instruction,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 56.dp, start = 32.dp, end = 32.dp)
        )

        val frameColor = if (uiState.isFaceWellPositioned || uiState.step != FaceScanStep.CENTER) {
            AccentGreen
        } else {
            Color.White
        }
        Canvas(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 260.dp, height = 340.dp)
        ) {
            drawRoundRect(
                color = frameColor,
                style = Stroke(width = 6f),
                cornerRadius = CornerRadius(48f, 48f)
            )
        }

        // Turn-direction arrow, shown only during the two turn steps — mirrors the
        // reference design's directional cue under the face.
        if (uiState.step == FaceScanStep.TURN_RIGHT || uiState.step == FaceScanStep.TURN_LEFT) {
            Icon(
                imageVector = if (uiState.step == FaceScanStep.TURN_RIGHT) {
                    Icons.Default.ArrowForward
                } else {
                    Icons.Default.ArrowBack
                },
                contentDescription = null,
                tint = AccentColor,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 380.dp)
                    .size(32.dp)
            )
        }

        AnimatedVisibility(
            visible = uiState.step == FaceScanStep.CENTER,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                ChecklistIcon(icon = Icons.Default.WbSunny, label = "Good lighting", isOk = uiState.goodLighting)
                ChecklistIcon(icon = Icons.Default.Face, label = "Face visible", isOk = uiState.faceVisible)
                ChecklistIcon(icon = Icons.Default.RemoveRedEye, label = "Eyes visible", isOk = uiState.eyesVisible)
            }
        }

        if (uiState.step == FaceScanStep.VERIFYING) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        if (uiState.step == FaceScanStep.FAILED) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(uiState.instruction, color = Color(0xFFFF6B6B))
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.retry() },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                ) {
                    Text("Try Again")
                }
            }
        }

        if (uiState.step == FaceScanStep.SUCCESS) {
            SuccessOverlay(onDone = onClose)
        }
    }
}

@Composable
private fun ChecklistIcon(icon: ImageVector, label: String, isOk: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isOk) AccentGreen else Color.White,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
private fun SuccessOverlay(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1400)
        onDone()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text("Face Verified!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}