package com.example.control24projectmain.fragments

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.TilesOverlay

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var yandexMV: MapView
    private lateinit var osmMV: org.osmdroid.views.MapView
    private var isDarkTheme: Boolean = false
    private var systemUiVisibility: Int = 0
    private var statusBarColor: Int = 0
    private val defaultMap = "YANDEX"
    private var mapProvider: String = defaultMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mapProvider = UserManager.getSelectedMap(requireContext()).toString()
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

        // Initialize the maps
        if (mapProvider == defaultMap) {
            // Yandex Maps
            MapKitFactory.initialize(requireContext())
        } else {
            // Open Street Maps
            Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        }

        binding = FragmentMapBinding.inflate(layoutInflater)

        // Set margin for constraintMapL when user has a virtual navigation control
        ViewCompat.setOnApplyWindowInsetsListener(binding.constraintMapL) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.leftMargin = insets.left
            layoutParams?.bottomMargin = insets.bottom
            layoutParams?.rightMargin = insets.right

            WindowInsetsCompat.CONSUMED
        }

        yandexMV = binding.yandexMV
        osmMV = binding.osmMV

        // Initialize the variable and set default position - Krasnoyarsk
        if (mapProvider == defaultMap) {
            osmMV.visibility = View.GONE
            yandexMV.visibility = View.VISIBLE
            yandexConfig()
        } else {
            yandexMV.visibility = View.GONE
            osmMV.visibility = View.VISIBLE
            osmConfig()
        }

        // Set the status bar color
        val window: Window = requireActivity().window
        statusBarColor = ContextCompat.getColor(requireContext(), R.color.black_10p)
        window.statusBarColor = statusBarColor

        // Set the status bar text color to dark
        isDarkTheme = UserManager.getThemeState(requireContext())
        if (!isDarkTheme) {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = systemUiVisibility

            if (mapProvider == defaultMap) {
                yandexMV.map.isNightModeEnabled = false
            } else {
                osmMV.overlayManager.tilesOverlay.setColorFilter(null)
            }
        } else {
            if (mapProvider == defaultMap) {
                yandexMV.map.isNightModeEnabled = true
            } else {
                osmMV.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
            }
        }

        return binding.root
    }

    override fun onStop() {
        // Stop the maps
        if (mapProvider == defaultMap) {
            yandexMV.onStop()
            MapKitFactory.getInstance().onStop()
        } else {
            osmMV.onPause()
        }
        super.onStop()

        // Set default status bar color
        val window: Window = requireActivity().window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.transparent)

        // Set the system UI visibility back to the default value when the fragment is detached or exited
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    override fun onStart() {
        super.onStart()
        // Start the maps
        if (mapProvider == defaultMap) {
            MapKitFactory.getInstance().onStart()
            yandexMV.onStart()
        } else {
            osmMV.onResume()
        }
    }

    private fun yandexConfig() {
        yandexMV.map.move(
            CameraPosition(Point(56.010569, 92.852572), 12.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null)
        //yandexMV.map
    }

    private fun osmConfig() {
        osmMV.setTileSource(TileSourceFactory.MAPNIK)
        osmMV.controller.setZoom(14.0)
        osmMV.controller.setCenter(GeoPoint(56.010569, 92.852572))
        osmMV.setMultiTouchControls(true)
    }
}