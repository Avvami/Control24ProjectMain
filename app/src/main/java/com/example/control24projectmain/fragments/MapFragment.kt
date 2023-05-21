package com.example.control24projectmain.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.control24projectmain.CombinedResponse
import com.example.control24projectmain.MapsConfig
import com.example.control24projectmain.R
import com.example.control24projectmain.SharedViewModel
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.FragmentMapBinding
import com.example.control24projectmain.isDarkModeEnabled
import com.google.gson.Gson
import com.yandex.mapkit.mapview.MapView

private const val yandexMap = "YANDEX"
private const val osmMap = "OSM"

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var yandexMV: MapView
    private lateinit var osmMV: org.osmdroid.views.MapView
    private var mapProvider: String = yandexMap
    private val mapsConfig = MapsConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapProvider = UserManager.getSelectedMap(requireContext()).toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize the maps
        mapsConfig.mapsInitialization(requireContext())

        binding = FragmentMapBinding.inflate(layoutInflater)

        // Set margin for constraintMapL when user has a virtual navigation control
        ViewCompat.setOnApplyWindowInsetsListener(binding.constraintMapL) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.leftMargin = insets.left
            layoutParams?.bottomMargin = insets.bottom
            layoutParams?.rightMargin = insets.right

            windowInsets
        }

        val topMargin = binding.trafficIV.marginTop
        ViewCompat.setOnApplyWindowInsetsListener(binding.trafficIV) { view, windowInsets ->
            val statusBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())

            val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.topMargin = statusBarInsets.top + topMargin

            WindowInsetsCompat.CONSUMED
        }

        yandexMV = binding.yandexMV
        osmMV = binding.osmMV
        val levelIcon = binding.trafficIV
        val levelText = binding.trafficTV
        val zoomInCL = binding.zoomInCL
        val zoomOutCL = binding.zoomOutCL

        // Initialize the variable and set default position - Krasnoyarsk
        mapsConfig.startMapsConfig(requireContext(), yandexMV, osmMV, levelIcon, levelText, zoomInCL, zoomOutCL)

        sharedViewModel.bundleLiveData.observe(viewLifecycleOwner) { bundle ->
            // Update the UI with the new data
            val response = bundle.getSerializable("OBJECTS_DATA") as CombinedResponse
            val objectsList = response.objects
            val gson = Gson()
            var array = BooleanArray(objectsList.size)
            val savedArrayString = UserManager.getDisplayedItems(requireContext())
            if (savedArrayString != null) {
                val savedArray = gson.fromJson(savedArrayString, BooleanArray::class.java)
                array = savedArray
            }
            val objectId = bundle.getInt("POSITION", -1)

            when (mapProvider) {
                yandexMap -> mapsConfig.yandexMapLiveConfig(requireContext(), objectsList, array, yandexMV, objectId, viewLifecycleOwner.lifecycleScope)
                osmMap -> mapsConfig.osmMapLiveConfig(requireContext(), objectsList, array, osmMV, objectId, viewLifecycleOwner.lifecycleScope)
            }
            bundle.putInt("POSITION", -1)
        }

        return binding.root
    }

    override fun onStop() {
        // Save maps camera position
        when (mapProvider) {
            yandexMap -> UserManager.saveYandexCameraPosition(requireContext(), yandexMV)
            osmMap -> UserManager.saveOsmCameraPosition(requireContext(), osmMV)
        }

        mapsConfig.mapsOnStop(requireContext(), yandexMV, osmMV)
        super.onStop()

        // Set default status bar color
        val window: Window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.transparent)

        // Set the system UI visibility back to the default value when the fragment is detached or exited
        if (!isDarkModeEnabled(requireContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Start the maps
        mapsConfig.mapsOnStart(yandexMV, osmMV)

        // Set the background for status bar
        val window: Window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black_10p)

        // Set the system UI visibility to the dark
        if (!isDarkModeEnabled(requireContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}