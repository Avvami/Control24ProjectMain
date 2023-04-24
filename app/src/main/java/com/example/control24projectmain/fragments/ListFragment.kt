package com.example.control24projectmain.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.control24projectmain.ObjectData
import com.example.control24projectmain.ObjectsListAdapter
import com.example.control24projectmain.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListBinding.inflate(layoutInflater)

        recyclerView = binding.objectsListRV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)

        val objectDataList = listOf(
            ObjectData(1, "OBJECT_1", "Category A", "Client A", "Auto Num 1", "Auto Model 1", false),
            ObjectData(2, "OBJECT_2", "Category B", "Client B", "Auto Num 2", "Auto Model 2", false),
            ObjectData(3, "OBJECT_3", "Category C", "Client C", "Auto Num 3", "Auto Model 3", false),
            ObjectData(3, "OBJECT_4", "Category D", "Client D", "Auto Num 4", "Auto Model 4", false)
        )
        val adapter = ObjectsListAdapter(requireContext(), objectDataList)
        recyclerView.adapter = adapter

        /*val keyString = arguments?.getString("KEY")
        val objectsList = arguments?.getParcelableArrayList<ObjectData>("OBJECTS")
        binding.textView.text = keyString
        binding.textView4.text = objectsList.toString()*/

        return binding.root
    }
}