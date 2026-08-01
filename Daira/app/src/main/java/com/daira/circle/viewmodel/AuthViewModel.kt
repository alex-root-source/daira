package com.daira.circle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthUiState {
    data object LoggedOut : AuthUiState()
    data object Loading : AuthUiState()
    data class LoggedIn(val email: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<AuthUiState>(
        auth.currentUser?.let { AuthUiState.LoggedIn(it.email ?: "") } ?: AuthUiState.LoggedOut
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signUp(email: String, password: String) {
        if (!isInputValid(email, password)) return
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                _uiState.value = AuthUiState.LoggedIn(email)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(friendlyError(e))
            }
        }
    }

    fun login(email: String, password: String) {
        if (!isInputValid(email, password)) return
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _uiState.value = AuthUiState.LoggedIn(email)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(friendlyError(e))
            }
        }
    }

    fun logout() {
        auth.signOut()
        _uiState.value = AuthUiState.LoggedOut
    }

    fun resetToLoggedOut() {
        // يُستخدم للرجوع لشاشة الدخول بعد ظهور خطأ، بدون تسجيل خروج فعلي
        _uiState.value = AuthUiState.LoggedOut
    }

    private fun isInputValid(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("الرجاء تعبئة البريد الإلكتروني وكلمة المرور")
            return false
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("كلمة المرور لازم تكون ٦ أحرف على الأقل")
            return false
        }
        return true
    }

    private fun friendlyError(e: Exception): String {
        val msg = e.message ?: return "صار خطأ غير متوقع، حاول مرة ثانية"
        return when {
            msg.contains("badly formatted", ignoreCase = true) -> "صيغة البريد الإلكتروني غير صحيحة"
            msg.contains("already in use", ignoreCase = true) -> "هذا البريد مسجّل مسبقًا — جرّب تسجيل الدخول بدل إنشاء حساب"
            msg.contains("password is invalid", ignoreCase = true) -> "كلمة المرور غير صحيحة"
            msg.contains("no user record", ignoreCase = true) -> "ما فيه حساب بهذا البريد — جرّب إنشاء حساب جديد"
            msg.contains("network", ignoreCase = true) -> "تأكد من اتصالك بالإنترنت وحاول مرة ثانية"
            else -> msg
        }
    }
}
