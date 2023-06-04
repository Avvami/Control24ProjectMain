package com.example.control24projectmain

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ObjectRoutesAdapter(
    private val context : Context,
    private val item: List<Data>,
    private val lifecycleScope: CoroutineScope
    ) : RecyclerView.Adapter<ObjectRoutesAdapter.ObjectsListViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var selectedItemPosition = -1
    private var previousSelectedItem: View? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: List<Data>, isFocused: Boolean)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ObjectsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val counterTV: TextView = itemView.findViewById(R.id.routeCounterTV)
        val routePeriodTV: TextView = itemView.findViewById(R.id.routePeriodTV)
        val routeStartLocationTV: TextView = itemView.findViewById(R.id.routeStartLocationTV)
        val routeEndLocationTV: TextView = itemView.findViewById(R.id.routeEndLocationTV)
        val averageSpeedTV: TextView = itemView.findViewById(R.id.averageSpeedTemplateTV)
        val overallTimeTV: TextView = itemView.findViewById(R.id.overallTimeTemplateTV)
        val mileageTemplateTV: TextView = itemView.findViewById(R.id.mileageTemplateTV)
        val routeStartLocationPB: ProgressBar = itemView.findViewById(R.id.routeStartLocationPB)
        val routeEndLocationPB: ProgressBar = itemView.findViewById(R.id.routeEndLocationPB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectsListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.object_routes, parent, false)
        return ObjectsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectsListViewHolder, position: Int) {
        val currentItem = item[position]
        val currentPosition = position + 1

        holder.counterTV.text = currentPosition.toString()

        val app = context.applicationContext as AppLevelClass
        val startTime = app.convertTime(currentItem.points.first().gmt)
        val endTime = app.convertTime(currentItem.points.last().gmt)
        holder.routePeriodTV.text = context.resources.getString(
            R.string.range_template,
            startTime,
            endTime
        )

        var averageSpeed = 0
        for (point in currentItem.points) {
            averageSpeed += point.speed
        }
        holder.averageSpeedTV.text = context.resources.getString(R.string.speed_template, (averageSpeed / currentItem.points.size))
        holder.overallTimeTV.text = app.getTimeInterval(startTime, endTime)
        val mileage = String.format("%.2f", calculateMileage(currentItem.points))
        holder.mileageTemplateTV.text = context.resources.getString(R.string.mileage_template, mileage)

        lifecycleScope.launch {
            holder.routeStartLocationPB.visibility = View.VISIBLE
            holder.routeEndLocationPB.visibility = View.VISIBLE
            try {
                val startLocation = app.coroutineGeocode(currentItem.points.first().lat, currentItem.points.first().lon)
                val endLocation = app.coroutineGeocode(currentItem.points.last().lat, currentItem.points.last().lon)
                withContext(Dispatchers.Main) {
                    holder.routeStartLocationTV.text = startLocation
                    holder.routeEndLocationTV.text = endLocation
                }
            } catch (e: Exception) {
                holder.routeStartLocationTV.text = "Ошибка геокодирования"
                holder.routeEndLocationTV.text = "Ошибка геокодирования"
                Log.i("GEOCODER ERROR", "Failed to get address", e)
            }
            holder.routeStartLocationPB.visibility = View.INVISIBLE
            holder.routeEndLocationPB.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            selectedItemPosition = if (selectedItemPosition == position) {
                -1
            } else {
                position
            }
            val isFocused = selectedItemPosition == position

            // Update the background of the previously selected item
            if (previousSelectedItem != null) {
                previousSelectedItem?.setBackgroundResource(R.drawable.card_objects_route_unselected_color)
            }

            if (isFocused) {
                holder.itemView.setBackgroundResource(R.drawable.card_objects_route_selected_color)
            } else {
                holder.itemView.setBackgroundResource(R.drawable.card_objects_route_unselected_color)
            }

            // Store the reference to the current item view as the previously selected item view
            previousSelectedItem = holder.itemView

            listener?.onItemClick(selectedItemPosition, item, isFocused)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    private fun calculateMileage(points: List<Point>): Float {
        var totalDistance = 0f

        for (i in 0 until points.size - 1) {
            val startLocation = Location("start").apply {
                latitude = points[i].lat
                longitude = points[i].lon
            }

            val endLocation = Location("end").apply {
                latitude = points[i + 1].lat
                longitude = points[i + 1].lon
            }

            totalDistance += startLocation.distanceTo(endLocation) / 1000  // Convert to km
        }

        return totalDistance
    }
}