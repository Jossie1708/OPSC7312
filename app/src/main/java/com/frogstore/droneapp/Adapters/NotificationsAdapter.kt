package com.frogstore.droneapp.Adapters

import NotificationItem
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frogstore.droneapp.R

class NotificationsAdapter(
    private val context: Context,
    private var notificationList: List<NotificationItem>,
    private val itemClickListener: ((NotificationItem) -> Unit)? = null
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notificationList[position]
        holder.itemNotificationTitle.text = item.title // Set title
        holder.itemNotificationBody.text = item.body   // Set body

        // Set click listener
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    fun updateNotifications(newNotifications: List<NotificationItem>) {
        notificationList = newNotifications // Use the correct variable name
        notifyDataSetChanged() // Notify the adapter about data changes
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNotificationTitle: TextView = view.findViewById(R.id.txtItem_notification_title)
        val itemNotificationBody: TextView = view.findViewById(R.id.txtItem_notification_body)
    }
}


