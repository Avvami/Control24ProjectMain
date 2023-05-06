package com.example.control24projectmain.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.control24projectmain.CombinedResponse
import com.example.control24projectmain.R
import com.example.control24projectmain.SharedViewModel
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.FragmentMapBinding
import com.google.gson.Gson
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Cluster
import com.yandex.mapkit.map.ClusterListener
import com.yandex.mapkit.map.ClusterTapListener
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import io.github.muddz.styleabletoast.StyleableToast
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.TilesOverlay
import kotlin.math.abs
import kotlin.math.sqrt

class MapFragment : Fragment(), ClusterListener, ClusterTapListener {

    private lateinit var binding: FragmentMapBinding
    private val sharedViewModel by activityViewModels<SharedViewModel>()
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
        isDarkTheme = /*UserManager.getThemeState(requireContext())*/true
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

    class TextImageProvider(private val context: Context, private val clusterSize: Int) : ImageProvider() {
        companion object {
            private const val FONT_SIZE = 16f
            private const val MARGIN_SIZE = 8
            private const val STROKE_SIZE = 2
        }

        override fun getId(): String {
            return "cluster_$clusterSize"
        }

        override fun getImage(): Bitmap? {
            val metrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metrics)

            var attrs = intArrayOf(R.attr.clusterTextColor, R.attr.clusterBorderColor, R.attr.clusterBackgroundColor)
            var ta = context.obtainStyledAttributes(attrs)
            val clusterTextColor = ta.getColor(0, 0)
            ta.recycle()

            val textPaint = Paint()
            textPaint.color = clusterTextColor
            textPaint.textSize = FONT_SIZE * metrics.density
            textPaint.textAlign = Paint.Align.CENTER
            textPaint.style = Paint.Style.FILL
            textPaint.isAntiAlias = true

            val widthF = textPaint.measureText(clusterSize.toString())
            val textMetrics = textPaint.fontMetrics
            val heightF = abs(textMetrics.bottom) + abs(textMetrics.top)
            val textRadius = sqrt(widthF * widthF + heightF * heightF) / 2
            val internalRadius = textRadius + MARGIN_SIZE * metrics.density
            val externalRadius = internalRadius + STROKE_SIZE * metrics.density

            val width = (2 * externalRadius + 0.5).toInt()

            val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            attrs = intArrayOf(R.attr.clusterBorderColor)
            ta = context.obtainStyledAttributes(attrs)
            val clusterBorderColor = ta.getColor(0, 0)
            ta.recycle()

            val backgroundPaint = Paint()
            backgroundPaint.isAntiAlias = true
            backgroundPaint.color = clusterBorderColor
            canvas.drawCircle(width / 2.toFloat(), width / 2.toFloat(),
                externalRadius, backgroundPaint)

            attrs = intArrayOf(R.attr.clusterBackgroundColor)
            ta = context.obtainStyledAttributes(attrs)
            val clusterBackgroundColor = ta.getColor(0, 0)
            ta.recycle()

            backgroundPaint.color = clusterBackgroundColor
            canvas.drawCircle(width / 2.toFloat(), width / 2.toFloat(),
                internalRadius, backgroundPaint)

            canvas.drawText(
                clusterSize.toString(),
                width / 2.toFloat(),
                width / 2 - (textMetrics.ascent + textMetrics.descent) / 2,
                textPaint
            )

            return bitmap
        }
    }

    private fun yandexConfig() {
        yandexMV.map.move(
            CameraPosition(Point(56.010569, 92.852572), 12.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null)
        yandexMV.map.isRotateGesturesEnabled = false
        val clusterCollection = yandexMV.map.mapObjects.addClusterizedPlacemarkCollection(this)
        val bitmap = requireContext().getBitmapFromVectorDrawable(R.drawable.icon_placemarker)
        val imageProvider = ImageProvider.fromBitmap(bitmap)

        sharedViewModel.bundleLiveData.observe(viewLifecycleOwner) { bundle ->
            // Update the UI with the new data
            val response = bundle.getSerializable("OBJECTS_DATA") as CombinedResponse
            val objectsList = response.objects
            val arrayString = UserManager.getDisplayedItems(requireContext())
            val gson = Gson()
            val array = gson.fromJson(arrayString, BooleanArray::class.java)

            // ВЫЛЕТ ЕСЛИ НИ ОДИН НЕ ОТОБРАЖАЕТСЯ
            val placemarks = mutableListOf<Point>()
            for ((index, obj) in objectsList.withIndex()) {
                if (array[index]) {
                    //val mapObjects = yandexMV.map.mapObjects
                    val point = Point(obj.lat, obj.lon)
                    //Log.i("HSDKJFHJDKFDKJ", "${obj.lat}, ${obj.lon}")
                    //val placemark = mapObjects.addPlacemark(point)
                    //placeMark.setIcon(imageProvider, IconStyle())
                    //placeMark.setIcon(imageProvider, IconStyle())
                    //placemark.setText(obj.name)
                    placemarks.add(point)
                } else {
                    /*do bothing*/
                }
            }
            clusterCollection.clear()
            clusterCollection.addPlacemarks(placemarks, imageProvider, IconStyle())
            clusterCollection.clusterPlacemarks(60.0, 15)
        }
    }

    private fun osmConfig() {
        osmMV.setTileSource(TileSourceFactory.MAPNIK)
        osmMV.controller.setZoom(14.0)
        osmMV.controller.setCenter(GeoPoint(56.010569, 92.852572))
        osmMV.setMultiTouchControls(true)
    }

    override fun onClusterAdded(cluster: Cluster) {
        cluster.appearance.setIcon(TextImageProvider(requireContext(), cluster.size))
        cluster.addClusterTapListener(this)
    }

    override fun onClusterTap(cluster: Cluster): Boolean {
        val clusterPosition = cluster.appearance.geometry
        val currentCameraPosition = yandexMV.map.cameraPosition
        var currentZoomLevel = currentCameraPosition.zoom
        /*while (cluster.placemarks.isNotEmpty()) {
            val cameraPosition = CameraPosition(clusterPosition, currentZoomLevel, 0.0f, 0.0f)
            yandexMV.map.move(cameraPosition, Animation(Animation.Type.SMOOTH, 1f), null)
            Log.i("DHFJKSHJFDS", cluster.toString())
            currentZoomLevel += 1.0f
        }*/
        StyleableToast.makeText(
            requireContext(),
            "OK",
            Toast.LENGTH_SHORT,
            R.style.CustomStyleableToast
        ).show()
        return true
    }

    private fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888) ?: return null
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}