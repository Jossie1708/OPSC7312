package com.frogstore.droneapp

import android.app.Application

class NotificationApplication : Application() {
    // Initialize the notifications list as an empty ArrayList
    val notifications = ArrayList<NotificationItem>()

    fun addNotification(notification: NotificationItem) {
        notifications.add(notification)
        // Notify the activity (or another component) to update the UI if needed
        // Use a way to reference the activity to call update methods
    }
}
