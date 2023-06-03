package com.example.control24projectmain

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    interface OnItemClickListener {
        fun onItemClick(position: Int, size: Int)
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
        val routeStartTV: TextView = itemView.findViewById(R.id.routeStartTV)
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

        Log.i("HDFJSDHFK", context.resources.getString(
            R.string.range_template,
            startTime,
            endTime
        ))

        var averageSpeed = 0
        for (point in currentItem.points) {
            averageSpeed += point.speed
        }
        holder.averageSpeedTV.text = context.resources.getString(R.string.speed_template, (averageSpeed / currentItem.points.size))

        holder.overallTimeTV.text = app.getTimeInterval(startTime, endTime)

        /*if (position < currentItem.points.size - 1) {
            val totalDistance = calculateTotalDistance(currentItem.points.subList(0, position + 2))
            holder.routeStartTV.text = "Total distance: $totalDistance km"
        } else {
            val totalDistance = calculateTotalDistance(currentItem.points)
            holder.routeStartTV.text = "Final total distance: $totalDistance km"
        }*/

        lifecycleScope.launch {
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
        }

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position, item.size)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    private fun calculateTotalDistance(points: List<Point>): Float {
        var totalDistance = 0f
        for (i in 1 until points.size) {
            val p1 = points[i - 1]
            val p2 = points[i]
            totalDistance += getDistance(p1.lat, p1.lon, p2.lat, p2.lon)
        }
        return totalDistance
    }

    private fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] // distance in meters
    }
}