package com.example.smartcitizensystem.ui.presentation.main.facescan


import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * A single frame's worth of face-pose info, distilled down to what the liveness
 * state machine (FaceVerificationViewModel) needs.
 */
data class FaceScanFrame(
    val faceDetected: Boolean,
    val yawDegrees: Float = 0f,          // negative = turned toward the camera's left, positive = right
    val eyesOpenProbability: Float = 0f,  // 0f 1f, average of both eyes
    val faceWidthRatio: Float = 0f        // face bounding box width / image width — rough "is it too close/far" check
)

/**
 * CameraX ImageAnalysis.Analyzer backed by ML Kit's on-device face detector.
 * Nothing here leaves the device — ML Kit's bundled face detector runs locally.
 */
class FaceAnalyzer(
    private val onResult: (FaceScanFrame) -> Unit
) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        detector.process(inputImage)
            .addOnSuccessListener { faces: List<Face> ->
                // If more than one face is in frame, use the largest — assume it's the
                // person actively completing the scan.
                val face = faces.maxByOrNull { it.boundingBox.width() }
                if (face != null) {
                    val leftEye = face.leftEyeOpenProbability ?: 1f
                    val rightEye = face.rightEyeOpenProbability ?: 1f
                    onResult(
                        FaceScanFrame(
                            faceDetected = true,
                            yawDegrees = face.headEulerAngleY,
                            eyesOpenProbability = (leftEye + rightEye) / 2f,
                            faceWidthRatio = face.boundingBox.width().toFloat() / inputImage.width.toFloat()
                        )
                    )
                } else {
                    onResult(FaceScanFrame(faceDetected = false))
                }
            }
            .addOnFailureListener {
                onResult(FaceScanFrame(faceDetected = false))
            }
            .addOnCompleteListener {
                imageProxy.close() // must always close, or the camera pipeline stalls
            }
    }
}