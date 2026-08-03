# Fix ViewModel instantiation crash

The application crashes with a `NoSuchMethodException` when trying to instantiate `LoginViewModel` and `SignupViewModel`. This is because these ViewModels have constructor parameters and are being created with the default `viewModel()` function, which requires a zero-argument constructor. Since the project uses Hilt, these should be created using `hiltViewModel()`.

## Proposed Changes

### [Component Name] UI Presentation - Auth

#### [MODIFY] [LoginScreen.kt](file:///Users/neloy/AndroidStudioProjects/SmartCitizenSystem/app/src/main/java/com/example/smartcitizensystem/ui/presentation/auth/login/LoginScreen.kt)
- Import `hiltViewModel` from `androidx.hilt.navigation.compose`.
- Change the default value of the `viewModel` parameter from `viewModel()` to `hiltViewModel()`.

#### [MODIFY] [SignupScreen.kt](file:///Users/neloy/AndroidStudioProjects/SmartCitizenSystem/app/src/main/java/com/example/smartcitizensystem/ui/presentation/auth/signup/SignupScreen.kt)
- Import `hiltViewModel` from `androidx.hilt.navigation.compose`.
- Change the default value of the `viewModel` parameter from `viewModel()` to `hiltViewModel()`.

## Verification Plan

### Manual Verification
- Deploy the application to a device/emulator.
- Navigate to the Login screen.
- Verify the app no longer crashes upon entering the Login screen.
- Navigate to the Signup screen.
- Verify the app no longer crashes upon entering the Signup screen.
