package com.example.control24projectmain

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class ObjectsListAdapter(private val context : Context, private val item: List<ObjectData>) : RecyclerView.Adapter<ObjectsListAdapter.ObjectsListViewHolder>() {

    class ObjectsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val canNameTV: TextView = itemView.findViewById(R.id.carNameTemplateTV)
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
        holder.canNameTV.text = currentItem.name

        val isExpanded: Boolean = currentItem.is_expanded
        holder.expandableCL.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            // Define the animation
            val anim = AnimationUtils.loadAnimation(context, R.anim.fade_in_out)
            currentItem.is_expanded = !currentItem.is_expanded
            notifyItemChanged(position, isExpanded)
            holder.expandableCL.startAnimation(anim)
            holder.downArrow.isChecked = !holder.downArrow.isChecked
        }

        holder.downArrow.setOnClickListener {
            // Define the animation
            val anim = AnimationUtils.loadAnimation(context, R.anim.fade_in_out)
            /*if (holder.expandableCL.visibility == View.GONE) {
                holder.expandableCL.visibility = View.VISIBLE
            } else {
                holder.expandableCL.visibility = View.GONE
            }*/
            /*holder.expandableCL.visibility = if (holder.expandableCL.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
            holder.expandableCL.startAnimation(anim)*/
            currentItem.is_expanded = !currentItem.is_expanded
            notifyItemChanged(position, isExpanded)
            holder.expandableCL.startAnimation(anim)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }
}