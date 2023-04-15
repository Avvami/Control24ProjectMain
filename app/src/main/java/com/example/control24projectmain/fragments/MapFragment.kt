package com.example.control24projectmain.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.example.control24projectmain.R
import com.example.control24projectmain.databinding.FragmentMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMapBinding.inflate(layoutInflater)

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
}