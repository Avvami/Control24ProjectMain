package com.example.control24projectmain

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.control24projectmain.activities.MainActivity
import com.example.control24projectmain.activities.RouteActivity
import com.example.control24projectmain.databinding.EditDialogViewBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ObjectsListAdapter(
    private val context : Context,
    private val item: List<CombinedResponseObject>,
    private val lifecycleScope: CoroutineScope
    ) : RecyclerView.Adapter<ObjectsListAdapter.ObjectsListViewHolder>() {

    private var expandedStateArray = BooleanArray(item.size)
    private var displayedItemsArray = BooleanArray(item.size)
    private var listener: OnItemClickListener? = null
    private var callListener: OnDriverCallClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int, size: Int)
    }

    interface OnDriverCallClickListener {
        fun onDriverCallClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setOnDriverCallClickListener(listener: OnDriverCallClickListener) {
        this.callListener = listener
    }

    class ObjectsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar4)
        val carNameTV: TextView = itemView.findViewById(R.id.carNameTemplateTV)
        val carSpeedIV: ImageView = itemView.findViewById(R.id.dashboardIconIV)
        val carSpeedTV: TextView = itemView.findViewById(R.id.speedTemplateTV)
        val carLocationTV: TextView = itemView.findViewById(R.id.locationTemplateTV)
        val carLastTimeUpdateTV: TextView = itemView.findViewById(R.id.timeTemplateTV)
        val ownerTV: TextView = itemView.findViewById(R.id.ownerTemplateTV)
        val carTypeTV: TextView = itemView.findViewById(R.id.typeTemplateTV)
        val carCategoryTV: TextView = itemView.findViewById(R.id.categoryTemplateTV)
        val carNumberTV: TextView = itemView.findViewById(R.id.autoNumTemplateTV)
        val displaySwitch: SwitchMaterial = itemView.findViewById(R.id.mapDisplayMSwitch)
        val downArrow: CheckBox = itemView.findViewById(R.id.downArrow)
        val expandableCL: ConstraintLayout = itemView.findViewById(R.id.expandableCL)
        val driverTV: TextView = itemView.findViewById(R.id.driverTemplateTV)
        val driverCallButton: MaterialButton = itemView.findViewById(R.id.makeCallButton)
        val driverInfo: ConstraintLayout = itemView.findViewById(R.id.driverCL)
        val routeButton: MaterialButton = itemView.findViewById(R.id.routeMButton)
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
        val app = context.applicationContext as AppLevelClass

        lifecycleScope.launch {
            holder.progressBar.visibility = View.VISIBLE
            try {
                val address = app.coroutineGeocode(latitude, longitude)
                withContext(Dispatchers.Main) {
                    holder.carLocationTV.text = address
                }
            } catch (e: Exception) {
                holder.carLocationTV.text = "Ошибка геокодирования"
                Log.i("GEOCODER ERROR", "Failed to get address", e)
            }
            holder.carLocationTV.visibility = View.VISIBLE
            holder.progressBar.visibility = View.INVISIBLE
        }

        holder.carLastTimeUpdateTV.text = app.convertTime(currentItem.gmt)
        holder.ownerTV.text = currentItem.client
        holder.carTypeTV.text = currentItem.avto_model

        // Convert category number to its equivalent word
        val numberDefinition = NumberDefinitions.list
        val category = numberDefinition.find { it.number == currentItem.category }

        holder.carCategoryTV.text = category?.definition
        holder.carNumberTV.text = currentItem.avto_no

        val gson = Gson()

        // Check if our item is displayed on the map
        val savedDisplayedItemsJsonString = UserManager.getDisplayedItems(context)
        if (savedDisplayedItemsJsonString != null) {
            val savedDisplayedItemsArray = gson.fromJson(savedDisplayedItemsJsonString, BooleanArray::class.java)
            displayedItemsArray = savedDisplayedItemsArray
        }
        holder.displaySwitch.isChecked = displayedItemsArray[position]

        // Here we check if our list item is on expanded mode
        val savedExpandedStateJsonString = UserManager.getExpandedListItem(context)
        if (savedExpandedStateJsonString != null) {
            val savedExpandedStateArray = gson.fromJson(savedExpandedStateJsonString, BooleanArray::class.java)
            expandedStateArray = savedExpandedStateArray
        }
        holder.downArrow.isChecked = expandedStateArray[position]
        holder.expandableCL.visibility = if (expandedStateArray[position]) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            listener?.onItemClick(position, item.size)
        }

        holder.displaySwitch.setOnCheckedChangeListener { _, _ ->
            // Save displaying item
            displayedItemsArray[position] = !displayedItemsArray[position]
            val displayedItemsJsonString = gson.toJson(displayedItemsArray)
            UserManager.saveDisplayedItems(context, displayedItemsJsonString)
        }

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

        val driverInfo = UserManager.getDriverInfo(context, position.toString())
        holder.driverTV.text = (if (driverInfo.first != "null") {
            driverInfo.first
        } else {
            context.resources.getString(R.string.driver_template)
        }).toString()

        holder.driverInfo.setOnClickListener {
            openEditDialog(context, position, holder.driverTV)
        }

        holder.driverCallButton.setOnClickListener {
            callListener?.onDriverCallClick(position)
        }

        holder.routeButton.setOnClickListener {
            val activity = (context as MainActivity)
            val intent = Intent(activity, RouteActivity::class.java)
            intent.putExtra("carName", currentItem.name)
            intent.putExtra("carId", currentItem.id)
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.scale_out)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    private fun openEditDialog(context: Context, itemId: Int, driverTemplateTV: TextView) {
        val binding = EditDialogViewBinding.inflate(LayoutInflater.from(context))
        val editDialog = AlertDialog.Builder(context)
        editDialog.setView(binding.root)
        val dialog = editDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val driverInfo = UserManager.getDriverInfo(context, itemId.toString())
        binding.driverNameET.setText(if (driverInfo.first != "null") {
            driverInfo.first
        } else {
            ""
        })

        binding.driverPhoneET.setText(if (driverInfo.second != "null") {
            driverInfo.second
        } else {
            ""
        })

        binding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        binding.saveBtn.setOnClickListener {
            val driverNameET = binding.driverNameET.text.toString().ifEmpty { "null" }
            val driverPhoneET = binding.driverPhoneET.text.toString().ifEmpty { "null" }
            UserManager.saveDriverInfo(context, itemId.toString(), driverNameET, driverPhoneET)

            driverTemplateTV.text = (if (driverNameET != "null") {
                driverNameET
            } else {
                context.resources.getString(R.string.driver_template)
            }).toString()
            dialog.dismiss()
        }

        dialog.show()
    }
}