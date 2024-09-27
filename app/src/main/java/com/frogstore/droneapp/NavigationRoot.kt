package com.frogstore.droneapp

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

// The `LoginRoute` object represents the starting navigation destination for the login screen.
// Since it’s marked with `@Serializable`, it can be serialized (e.g., saved or passed around in a navigation route).
@Serializable
data object LoginRoute

// `LoggedInRoute` is a data class representing the route when a user is successfully logged in.
// It carries the `username` as a parameter to be displayed on the logged-in screen.
@Serializable
data class LoggedInRoute(val username: String)

// The root of the composable navigation system.
@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    // The `navController` is a stateful object used to control the navigation flow.
    // It keeps track of the back stack and handles navigation between different screens.
    val navController = rememberNavController()

    // `NavHost` defines the navigation graph for the app.
    // The `startDestination` specifies the first screen users will see, which is `LoginRoute`.
    NavHost(
        navController = navController,
        startDestination = LoginRoute // Sets the login screen as the first screen in the navigation graph.
    ) {
        // The `composable` function creates a new navigation route for `LoginRoute`.
        // Inside this block, the logic for rendering the login screen is implemented.
        composable<LoginRoute> {
            // `viewModel()` is a helper function from Compose to retrieve or create the `LoginViewModel`.
            // It follows the lifecycle of the composable, and stores the UI logic (like user actions and state).
            val viewModel = viewModel<LoginViewModel>()

            // `LoginScreen` is the composable that renders the actual UI for login.
            // It takes in the state from the `LoginViewModel` and passes the `onAction` handler for user input.
            LoginScreen(
                state = viewModel.state,       // Passes the current state of the login (e.g., loading, success, etc.)
                onAction = viewModel::onAction // Passes the function that handles login actions like button clicks.
            )
        }

        // Defines a composable navigation route for `LoggedInRoute`.
        // This route will be triggered when the user successfully logs in, passing the `username` as part of the route data.
        composable<LoggedInRoute> {
            // `it.toRoute<LoggedInRoute>()` parses the navigation arguments into the `LoggedInRoute` data class.
            val username = it.toRoute<LoggedInRoute>().username

            // A `Box` composable is used to create a full-screen container to center-align the content.
            Box(
                modifier = Modifier
                    .fillMaxSize(),             // Fills the entire available space of the parent.
                contentAlignment = Alignment.Center // Centers the content horizontally and vertically.
            ) {
                // Displays a welcome message with the logged-in user's username.
                Text(text = "Hello $username!")
            }
        }
    }
}
