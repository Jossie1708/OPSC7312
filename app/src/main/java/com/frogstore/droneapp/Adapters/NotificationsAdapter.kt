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
import com.frogstore.droneapp.R

class NotificationsAdapter(
    private val context: Context,
    private val notificationList: List<String>,
    private val imageList: List<String> = emptyList(), // Default to empty if not provided
    private val itemClickListener: ((String) -> Unit)? = null // Optional click listener
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notificationList[position]
        holder.itemNotification.text = item
        holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

        // Load the image into the ImageView if available
        if (position < imageList.size) {
            Glide.with(context)
                .load(imageList[position])
                .into(holder.imageViewNotification)
        }

        // Set click listener if provided
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemNotification: TextView = view.findViewById(R.id.txtItem_notification)
        val card: CardView = view.findViewById(R.id.cardNotification)
        val imageViewNotification: ImageView = view.findViewById(R.id.imageViewNotification)
    }
}
