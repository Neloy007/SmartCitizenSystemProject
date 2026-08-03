# Fix ViewModel instantiation issue

The app crashes because `LoginViewModel` and `SignupViewModel` are annotated with `@HiltViewModel` but are being instantiated using the default `viewModel()` delegate instead of `hiltViewModel()`. This causes a `NoSuchMethodException` as Hilt-managed ViewModels typically lack a parameterless constructor.

## Proposed Changes

### [Component Name] UI Presentation

#### [MODIFY] [LoginScreen.kt](file:///Users/neloy/AndroidStudioProjects/SmartCitizenSystem/app/src/main/java/com/example/smartcitizensystem/ui/presentation/auth/login/LoginScreen.kt)
- Replace `viewModel()` with `hiltViewModel()`.
- Add import `androidx.hilt.navigation.compose.hiltViewModel`.

#### [MODIFY] [SignupScreen.kt](file:///Users/neloy/AndroidStudioProjects/SmartCitizenSystem/app/src/main/java/com/example/smartcitizensystem/ui/presentation/auth/signup/SignupScreen.kt)
- Replace `viewModel()` with `hiltViewModel()`.
- Add import `androidx.hilt.navigation.compose.hiltViewModel`.

#### [MODIFY] [HomeViewModel.kt](file:///Users/neloy/AndroidStudioProjects/SmartCitizenSystem/app/src/main/java/com/example/smartcitizensystem/ui/presentation/main/home/HomeViewModel.kt)
- Add `@HiltViewModel` annotation.
- Add `@Inject constructor()`.

#### [MODIFY] [HomeScreen.kt](file:///Users/neloy/AndroidStudioProjects/SmartCitizenSystem/app/src/main/java/com/example/smartcitizensystem/ui/presentation/main/home/HomeScreen.kt)
- Replace `viewModel()` with `hiltViewModel()`.
- Add import `androidx.hilt.navigation.compose.hiltViewModel`.

## Verification Plan

### Automated Tests
- Build the project to ensure Hilt code generation works and no compilation errors.
- Run the app and navigate to Login, Signup, and Home screens to verify they no longer crash.
