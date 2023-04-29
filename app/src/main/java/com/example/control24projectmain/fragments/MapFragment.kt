package com.example.control24projectmain.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var yandexMV: MapView
    private var isDarkTheme: Boolean = false
    private var systemUiVisibility: Int = 0
    private var statusBarColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            // Restore the system UI visibility value
            systemUiVisibility = savedInstanceState.getInt("systemUiVisibility")
            statusBarColor = savedInstanceState.getInt("statusBarColor")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save the system UI visibility value
        outState.putInt("systemUiVisibility", systemUiVisibility)
        outState.putInt("statusBarColor", statusBarColor)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize the yandex maps
        MapKitFactory.initialize(requireContext())

        binding = FragmentMapBinding.inflate(layoutInflater)

        // Initialize the variable and set default position - Krasnoyarsk
        yandexMV = binding.yandexMV
        yandexMV.map.move(
            CameraPosition(Point(56.010569, 92.852572), 12.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null)

        // Set the status bar color
        val window: Window = requireActivity().window
        statusBarColor = ContextCompat.getColor(requireContext(), R.color.black_10p)
        window.statusBarColor = statusBarColor

        // Set the status bar text color to dark
        isDarkTheme = UserManager.getThemeState(requireContext())
        if (!isDarkTheme) {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = systemUiVisibility
            yandexMV.map.isNightModeEnabled = false
        } else {
            yandexMV.map.isNightModeEnabled = true
        }

        /*// Load configuration
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        // Get reference to OSM MapVie
        val osmMapView = binding.osmMV
        // Set Tile source to MAPNIK
        osmMapView.setTileSource(TileSourceFactory.MAPNIK)

        // Disable map rotation gestures
        osmMapView.setMultiTouchControls(true)

        // Add zoom buttons to the middle right
        val zoomController = osmMapView.zoomController

        val mapController = osmMapView.controller
        mapController.setZoom(13.0)
        val startPoint = GeoPoint(56.010569, 92.852572)
        mapController.setCenter(startPoint)*/


        return binding.root
    }

    override fun onStop() {
        // Stop the mapkit
        yandexMV.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()

        // Set default status bar color
        val window: Window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.transparent)

        // Set the system UI visibility back to the default value when the fragment is detached or exited
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    override fun onStart() {
        super.onStart()
        // Start the mapkit
        MapKitFactory.getInstance().onStart()
        yandexMV.onStart()
    }
}