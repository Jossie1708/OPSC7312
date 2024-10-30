package com.frogstore.droneapp.UserDetails

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
    state: LoginState, // The current state of the login screen, passed from the ViewModel.
    onAction: (LoginAction) -> Unit // A callback to handle user actions (e.g., when the user enters data or clicks a button).
) {
    // `rememberCoroutineScope` is used to launch coroutines in a composable function.
    val scope = rememberCoroutineScope()

    // `LocalContext` provides access to the current context, here the activity is used.
    val context = LocalContext.current

    // `AccountManager` is initialized as a remembered object to avoid re-creating it during recomposition.
    val accountManager = remember {
        AccountManager(context as ComponentActivity) // Casting context to `ComponentActivity`.
    }

    // `Column` is a vertical layout container that arranges its children vertically.
    Column(
        modifier = Modifier
            .fillMaxSize() // Fill the entire available size of the screen.
            .padding(16.dp), // Add padding around the content.
        verticalArrangement = Arrangement.spacedBy(
            16.dp, // Space out the elements by 16dp vertically.
            Alignment.CenterVertically // Align all the items to the center vertically.
        )
    ) {
        // `TextField` for the username input.
        TextField(
            value = state.username, // The current username from `LoginState`.
            onValueChange = { newUsername ->
                // Call `onAction` with the new username when the user changes it.
                onAction(LoginAction.OnUsernameChange(newUsername))
            },
            label = { Text(text = "Username") }, // Label for the input field.
            modifier = Modifier.fillMaxWidth() // Make the text field take the full width of the screen.
        )

        // `TextField` for the password input.
        TextField(
            value = state.password, // The current password from `LoginState`.
            onValueChange = { newPassword ->
                // Call `onAction` with the new password when the user changes it.
                onAction(LoginAction.OnpasswordChange(newPassword))
            },
            label = { Text(text = "Password") }, // Label for the input field.
            modifier = Modifier.fillMaxWidth() // Make the text field take the full width of the screen.
        )

        // `Row` arranges elements horizontally. Here it's used for the "Register" text and toggle switch.
        Row {
            // Text label for the registration switch.
            Text(text = "Register")

            // Spacer adds some horizontal space between the "Register" text and the switch.
            Spacer(modifier = Modifier.width(8.dp))

            // `Switch` allows the user to toggle between login and registration mode.
            Switch(
                checked = state.isRegister, // The current state of the switch (true for registration, false for login).
                onCheckedChange = {
                    // When the switch is toggled, `onAction` is called to toggle the registration mode.
                    onAction(LoginAction.OnToggleIsRegister)
                }
            )
        }

        // `Button` handles the action of either logging in or registering the user.
        Button(
            onClick = {
                // Launching a coroutine to perform the sign-up process asynchronously.
                scope.launch {
                    if (state.isRegister) {
                        // If the switch is set to "Register", call the `signUp` method from `AccountManager`.
                        val result = accountManager.signUp(
                            username = state.username, // Pass the entered username.
                            password = state.password,
                            email = state.email// Pass the entered password.
                        )
                        // After signing up, trigger the `OnSignUp` action with the result.
                        onAction(LoginAction.OnSignUp(result))
                    }
                    // You could also handle the login logic here if `isRegister` is false.
                }
            }
        ) {
            // The button text changes based on whether the user is in login or registration mode.
            Text(text = if (state.isRegister) "Register" else "Login")
        }
    }
}
