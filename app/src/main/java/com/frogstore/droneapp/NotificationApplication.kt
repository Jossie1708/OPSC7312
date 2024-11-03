package com.frogstore.droneapp

import NotificationItem
import android.app.Application

class NotificationApplication : Application() {
    val notifications: ArrayList<NotificationItem> = arrayListOf()

    fun addNotification(notification: NotificationItem) {
        notifications.add(notification)
        // Notify observers if necessary, or you can directly call addNotification in SideMenuNavBarActivity
    }
}


