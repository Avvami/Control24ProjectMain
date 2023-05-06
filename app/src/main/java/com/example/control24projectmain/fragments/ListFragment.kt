package com.example.control24projectmain.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.control24projectmain.CombinedResponse
import com.example.control24projectmain.FirstResponse
import com.example.control24projectmain.ObjectsListAdapter
import com.example.control24projectmain.SharedViewModel
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var recyclerView: RecyclerView
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentListBinding.inflate(layoutInflater)

        // Observe the ViewModel and update the UI when new data is received
        sharedViewModel.bundleLiveData.observe(viewLifecycleOwner) { bundle ->
            // Update the UI with the new data
            val response = bundle.getSerializable("OBJECTS_DATA") as CombinedResponse

            recyclerView = binding.objectsListRV
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            /*Log.i("HSDKJFHJDKFDKJ", UserManager.getDisplayedItems(requireContext()).toString())
            Log.i("HSDKJFHJDKFDKJ", response.objects.toString())*/
            val adapter = ObjectsListAdapter(requireContext(), response.objects)
            recyclerView.adapter = adapter
        }

        return binding.root
    }
}