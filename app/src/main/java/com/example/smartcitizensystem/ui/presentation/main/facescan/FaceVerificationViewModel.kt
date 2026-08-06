package com.example.smartcitizensystem.ui.presentation.main.facescan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcitizensystem.data.repository.FirebaseAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

enum class FaceScanStep {
    CENTER, TURN_RIGHT, TURN_LEFT, VERIFYING, SUCCESS, FAILED
}

data class FaceScanUiState(
    val step: FaceScanStep = FaceScanStep.CENTER,
    val instruction: String = "Position your face in the frame",
    val isFaceWellPositioned: Boolean = false,
    val goodLighting: Boolean = false,
    val faceVisible: Boolean = false,
    val eyesVisible: Boolean = false
)

private const val CENTER_THRESHOLD_DEGREES = 8f
private const val TURN_THRESHOLD_DEGREES = 22f
private const val HOLD_STILL_MILLIS = 700L

@HiltViewModel
class FaceVerificationViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(FaceScanUiState())
    val uiState: State<FaceScanUiState> = _uiState

    private var holdJob: Job? = null
    private var isHolding = false

    /** Called on (roughly) every camera frame via FaceAnalyzer. */
    fun onFrame(frame: FaceScanFrame) {
        val current = _uiState.value
        if (current.step == FaceScanStep.VERIFYING ||
            current.step == FaceScanStep.SUCCESS ||
            current.step == FaceScanStep.FAILED
        ) return

        val faceVisible = frame.faceDetected && frame.faceWidthRatio in 0.18f..0.85f
        val eyesVisible = frame.eyesOpenProbability > 0.4f
        // ML Kit's detector tends to fail outright in very low light, so "a face was
        // found at all" doubles as a rough lighting-quality signal without extra work.
        val goodLighting = frame.faceDetected

        _uiState.value = current.copy(
            faceVisible = faceVisible,
            eyesVisible = eyesVisible,
            goodLighting = goodLighting
        )

        if (!faceVisible) {
            cancelHold()
            if (current.step == FaceScanStep.CENTER) {
                _uiState.value = _uiState.value.copy(
                    instruction = "Position your face in the frame",
                    isFaceWellPositioned = false
                )
            }
            return
        }

        when (current.step) {
            FaceScanStep.CENTER -> {
                val centered = abs(frame.yawDegrees) < CENTER_THRESHOLD_DEGREES
                _uiState.value = _uiState.value.copy(
                    isFaceWellPositioned = centered,
                    instruction = if (centered) "Perfect, hold still" else "Position your face in the frame"
                )
                if (centered) {
                    startHold { advanceTo(FaceScanStep.TURN_RIGHT, "Turn your head to the right") }
                } else {
                    cancelHold()
                }
            }
            FaceScanStep.TURN_RIGHT -> {
                if (frame.yawDegrees > TURN_THRESHOLD_DEGREES) {
                    advanceTo(FaceScanStep.TURN_LEFT, "Turn your head to the left")
                }
            }
            FaceScanStep.TURN_LEFT -> {
                if (frame.yawDegrees < -TURN_THRESHOLD_DEGREES) {
                    advanceTo(FaceScanStep.VERIFYING, "Verifying...")
                    completeVerification()
                }
            }
            else -> Unit
        }
    }

    private fun startHold(onHeld: () -> Unit) {
        if (isHolding) return
        isHolding = true
        holdJob = viewModelScope.launch {
            delay(HOLD_STILL_MILLIS)
            isHolding = false
            onHeld()
        }
    }

    private fun cancelHold() {
        holdJob?.cancel()
        isHolding = false
    }

    private fun advanceTo(step: FaceScanStep, instruction: String) {
        cancelHold()
        _uiState.value = _uiState.value.copy(step = step, instruction = instruction)
    }

    /**
     * TODO (production readiness): this only proves a live, moving face completed the
     * on-screen gesture challenge. It does NOT match the face against the citizen's NID
     * photo, and has no defense against a played-back video or a photo with cut-out eyes.
     * Before relying on this for real identity verification, either:
     *   (a) send frames/a short clip to a certified liveness+matching provider, or
     *   (b) do server-side face matching against the stored NID photo using a proper
     *       biometric matching service.
     * Right now it just flips `isFaceVerified` to true locally — good enough to unblock
     * the UI/UX flow you asked for, not good enough for production KYC.
     */
    private fun completeVerification() {
        viewModelScope.launch {
            delay(900) // brief pause so "Verifying..." doesn't flash by instantly
            val result = authRepository.updateUserProfileFields(mapOf("isFaceVerified" to true))
            _uiState.value = _uiState.value.copy(
                step = if (result.isSuccess) FaceScanStep.SUCCESS else FaceScanStep.FAILED,
                instruction = if (result.isSuccess) "Verified!" else "Something went wrong. Please try again."
            )
        }
    }

    fun retry() {
        cancelHold()
        _uiState.value = FaceScanUiState()
    }
}