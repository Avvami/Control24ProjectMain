package com.example.control24projectmain

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class ObjectsListAdapter(
    private val context : Context,
    private val item: List<CombinedResponseObject>
    ) : RecyclerView.Adapter<ObjectsListAdapter.ObjectsListViewHolder>() {

    private var expandedStateArray = BooleanArray(item.size)

    class ObjectsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carNameTV: TextView = itemView.findViewById(R.id.carNameTemplateTV)
        val carSpeedIV: ImageView = itemView.findViewById(R.id.dashboardIconIV)
        val carSpeedTV: TextView = itemView.findViewById(R.id.speedTemplateTV)
        val carLocationTV: TextView = itemView.findViewById(R.id.locationTemplateTV)
        val carLastTimeUpdateTV: TextView = itemView.findViewById(R.id.timeTemplateTV)
        val ownerTV: TextView = itemView.findViewById(R.id.ownerTemplateTV)
        val carTypeTV: TextView = itemView.findViewById(R.id.typeTemplateTV)
        val carCategoryTV: TextView = itemView.findViewById(R.id.categoryTemplateTV)
        val carNumberTV: TextView = itemView.findViewById(R.id.autoNumTemplateTV)
        val downArrow: CheckBox = itemView.findViewById(R.id.downArrow)
        val expandableCL: ConstraintLayout = itemView.findViewById(R.id.expandableCL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectsListViewHolder {
        val isDetailedView = UserManager.getObjectsListView(context)
        val view = if (!isDetailedView) {
            LayoutInflater.from(context).inflate(R.layout.objects_list_brief, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.objects_list_detailed, parent, false)
        }
        return ObjectsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectsListViewHolder, position: Int) {
        val currentItem = item[position]
        holder.carNameTV.text = currentItem.name

        // Change icon if speed = 0
        if (currentItem.speed == 0) {
            holder.carSpeedIV.setImageResource(R.drawable.icon_parking)
        } else {
            holder.carSpeedIV.setImageResource(R.drawable.icon_dashboard)
        }
        val speed = context.resources.getString(R.string.speed_template, currentItem.speed)
        holder.carSpeedTV.text = speed

        // Geocode convert to an address
        val latitude = currentItem.lat
        val longitude = currentItem.lon
        val geocoder = Geocoder(context, Locale("ru"))
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val address = addresses?.get(0)

        holder.carLocationTV.text = "${address?.getAddressLine(0)}"
        holder.carLastTimeUpdateTV.text = currentItem.gmt
        holder.ownerTV.text = currentItem.client
        holder.carTypeTV.text = currentItem.avto_model

        // Convert category number to its equivalent word
        val numberDefinition = NumberDefinitions.list
        val category = numberDefinition.find { it.number == currentItem.category }

        holder.carCategoryTV.text = category?.definition
        holder.carNumberTV.text = currentItem.avto_no

        // Here we check if our list item is on expanded mode
        val savedExpandedStateJsonString = UserManager.getExpandedListItem(context)
        val gson = Gson()
        if (savedExpandedStateJsonString != null) {
            val savedExpandedStateArray = gson.fromJson(savedExpandedStateJsonString, BooleanArray::class.java)
            expandedStateArray = savedExpandedStateArray
        }
        holder.downArrow.isChecked = expandedStateArray[position]
        holder.expandableCL.visibility = if (expandedStateArray[position]) View.VISIBLE else View.GONE

        /*holder.itemView.setOnClickListener {
            // Define the animation
            val anim = AnimationUtils.loadAnimation(context, R.anim.fade_in_out)
            currentItem.is_expanded = !currentItem.is_expanded
            notifyItemChanged(position, isExpanded)
            holder.expandableCL.startAnimation(anim)
            holder.downArrow.isChecked = !holder.downArrow.isChecked
        }*/

        holder.downArrow.setOnClickListener {
            // Define the animation
            val anim = AnimationUtils.loadAnimation(context, R.anim.fade_in_out)
            expandedStateArray[position] = !expandedStateArray[position]
            notifyItemChanged(position, expandedStateArray[position])
            holder.expandableCL.startAnimation(anim)

            // Save the expandable state
            val expandedStateJsonString = gson.toJson(expandedStateArray)
            UserManager.saveExpandedListItem(context, expandedStateJsonString)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }
}