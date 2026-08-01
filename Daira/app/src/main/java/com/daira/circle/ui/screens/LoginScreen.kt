package com.daira.circle.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.daira.circle.ui.theme.BgDeep
import com.daira.circle.ui.theme.Peach
import com.daira.circle.ui.theme.TextMuted
import com.daira.circle.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    state: AuthUiState,
    onLogin: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("دائرة", style = androidx.compose.material3.MaterialTheme.typography.titleLarge, color = Color.White)
        Text(
            if (isSignUpMode) "أنشئ حسابك الخاص" else "أهلًا بعودتك",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            modifier = Modifier.padding(top = 6.dp, bottom = 28.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("البريد الإلكتروني") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Peach,
                focusedLabelColor = Peach,
                cursorColor = Peach
            )
        )

        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 6.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة المرور") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = TextMuted
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Peach,
                focusedLabelColor = Peach,
                cursorColor = Peach
            )
        )

        if (state is AuthUiState.Error) {
            Text(
                state.message,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Button(
            onClick = {
                if (isSignUpMode) onSignUp(email.trim(), password) else onLogin(email.trim(), password)
            },
            enabled = state !is AuthUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Peach, contentColor = BgDeep)
        ) {
            if (state is AuthUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(4.dp), color = BgDeep, strokeWidth = 2.dp)
            } else {
                Text(if (isSignUpMode) "إنشاء حساب" else "تسجيل الدخول")
            }
        }

        TextButton(onClick = { isSignUpMode = !isSignUpMode }, modifier = Modifier.padding(top = 8.dp)) {
            Text(
                if (isSignUpMode) "عندك حساب؟ سجّل دخول" else "ما عندك حساب؟ أنشئ واحد",
                color = TextMuted
            )
        }
    }
}
