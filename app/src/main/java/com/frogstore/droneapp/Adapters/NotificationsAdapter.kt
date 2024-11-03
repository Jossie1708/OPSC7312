package com.frogstore.droneapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.frogstore.droneapp.NotificationItem
import com.frogstore.droneapp.R

class NotificationsAdapter(
    private val context: Context,
    private var notificationList: List<NotificationItem>, // Change to NotificationItem
    private val itemClickListener: ((NotificationItem) -> Unit)? = null // Update click listener type
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notificationList[position]
        holder.itemNotification.text = item.title // Use title for display
        holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

        // Load the image into the ImageView if available
        item.imageUrl?.let { url ->
            Glide.with(context)
                .load(url)
                .into(holder.imageViewNotification)
        } ?: run {
            holder.imageViewNotification.visibility = View.GONE // Hide if no image
        }

        // Set click listener if provided
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    // Method to update notifications
    fun updateNotifications(newNotifications: List<NotificationItem>) {
        notificationList = newNotifications
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNotification: TextView = view.findViewById(R.id.txtItem_notification)
        val card: CardView = view.findViewById(R.id.cardNotification)
        val imageViewNotification: ImageView = view.findViewById(R.id.imageViewNotification)
    }
}
