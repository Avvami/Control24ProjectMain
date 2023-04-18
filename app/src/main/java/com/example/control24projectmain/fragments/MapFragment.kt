package com.example.control24projectmain.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMapBinding.inflate(layoutInflater)

        // Set the status bar color
        val window: Window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.black_10p)

        // Set the status bar text color to dark
        val isDark = UserManager.getSharedPreferencesData(requireContext())
        if (!isDark.first) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        // Load configuration
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
        mapController.setCenter(startPoint)


        return binding.root
    }

    // Restore status bar color and text color when exit Fragment
    override fun onDestroyView() {
        super.onDestroyView()

        // Set default status bar color
        val window: Window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.transparent)

        // Restore the status bar text color to white
        val isDark = UserManager.getSharedPreferencesData(requireContext())
        if (!isDark.first) {
            window.decorView.systemUiVisibility = 0
        }
    }
}