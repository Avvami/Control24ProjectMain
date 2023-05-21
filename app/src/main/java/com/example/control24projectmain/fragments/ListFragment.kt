package com.example.control24projectmain.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.control24projectmain.CombinedResponse
import com.example.control24projectmain.ObjectsListAdapter
import com.example.control24projectmain.R
import com.example.control24projectmain.SharedViewModel
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.activities.MainActivity
import com.example.control24projectmain.databinding.FragmentListBinding
import com.google.gson.Gson
import io.github.muddz.styleabletoast.StyleableToast

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

            val adapter = ObjectsListAdapter(requireContext(), response.objects, viewLifecycleOwner.lifecycleScope)
            adapter.setOnItemClickListener(object : ObjectsListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, size: Int) {
                    var displayedItemsArray = BooleanArray(size)
                    val gson = Gson()

                    // Check if our item is displayed on the map
                    val savedDisplayedItemsJsonString = UserManager.getDisplayedItems(requireContext())
                    if (savedDisplayedItemsJsonString != null) {
                        val savedDisplayedItemsArray = gson.fromJson(savedDisplayedItemsJsonString, BooleanArray::class.java)
                        displayedItemsArray = savedDisplayedItemsArray
                    }

                    val displayedItemsJsonString = if (!displayedItemsArray[position]) {
                        displayedItemsArray[position] = !displayedItemsArray[position]
                        gson.toJson(displayedItemsArray)
                    } else {
                        gson.toJson(displayedItemsArray)
                    }
                    UserManager.saveDisplayedItems(requireContext(), displayedItemsJsonString)

                    bundle.putInt("POSITION", position)
                    MapFragment().arguments = bundle
                    (activity as MainActivity).replaceFragment(R.id.map_menu)
                }
            })
            adapter.setOnDriverCallClickListener(object : ObjectsListAdapter.OnDriverCallClickListener {
                override fun onDriverCallClick(position: Int) {
                    val driverInfo = UserManager.getDriverInfo(requireContext(), position.toString())
                    val driverPhoneNumber = if (driverInfo.second != "null") {
                        driverInfo.second
                    } else {
                        ""
                    }
                    if (driverPhoneNumber == "") {
                        StyleableToast.makeText(
                            requireContext(),
                            "Номер не указан",
                            Toast.LENGTH_SHORT,
                            R.style.CustomStyleableToast
                        ).show()
                    } else {
                        val phoneNumber = "tel: +7${driverPhoneNumber}"
                        val dialIntent = Intent(Intent.ACTION_DIAL)
                        dialIntent.data = Uri.parse(phoneNumber)
                        startActivity(dialIntent)
                        activity?.overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long)
                    }
                }

            })
            recyclerView.adapter = adapter

            binding.progressBar2.visibility = View.INVISIBLE
        }

        return binding.root
    }
}