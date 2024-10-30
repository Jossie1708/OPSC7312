package com.frogstore.droneapp

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// The `LoginScreen` function is a composable function that renders the UI for login or registration.
@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val accountManager = remember {
        AccountManager(context as ComponentActivity)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(
            16.dp,
            Alignment.CenterVertically
        )
    ) {
        TextField(
            value = state.username,
            onValueChange = { newUsername ->
                onAction(LoginAction.OnUsernameChange(newUsername))
            },
            label = { Text(text = "Username") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = state.password,
            onValueChange = { newPassword ->
                onAction(LoginAction.OnpasswordChange(newPassword))
            },
            label = { Text(text = "Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Row {
            Text(text = "Register")

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = state.isRegister,
                onCheckedChange = {
                    onAction(LoginAction.OnToggleIsRegister)
                }
            )
        }

        Button(
            onClick = {
                scope.launch {
                    if (state.isRegister) {
                        val result = accountManager.signUp(
                            username = state.username,
                            password = state.password
                        )
                        onAction(LoginAction.OnSignUp(result))
                    }
                }
            }
        ) {
            Text(text = if (state.isRegister) "Register" else "Login")
        }
    }
}
