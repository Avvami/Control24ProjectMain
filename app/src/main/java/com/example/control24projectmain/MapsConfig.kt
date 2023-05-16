package com.example.control24projectmain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.control24projectmain.databinding.BottomOverlayBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay

private const val yandexMap = "YANDEX"
private const val osmMap = "OSM"
private var followedObjectIndex = -1
private var mapProvider: String = yandexMap
private val tapListeners = mutableListOf<MapObjectTapListener>()

class MapsConfig {
    private fun getMapProvider(context: Context) {
        mapProvider = UserManager.getSelectedMap(context).toString()
    }

    fun mapsInitialization(context: Context) {
        // Initialize the maps
        getMapProvider(context)
        when (mapProvider) {
            yandexMap -> MapKitFactory.initialize(context) // Yandex Maps
            osmMap -> Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context)) // Open Street Maps
        }
    }

    fun mapsOnStop(yandexMV: MapView, osmMV: org.osmdroid.views.MapView) {
        when (mapProvider) {
            yandexMap -> {
                yandexMV.onStop()
                MapKitFactory.getInstance().onStop()
            }
            osmMap -> osmMV.onPause()
        }
    }

    fun mapsOnStart(yandexMV: MapView, osmMV: org.osmdroid.views.MapView) {
        when (mapProvider) {
            yandexMap -> {
                MapKitFactory.getInstance().onStart()
                yandexMV.onStart()
            }
            osmMap -> osmMV.onResume()
        }
    }

    fun startMapsConfig(context: Context, yandexMV: MapView, osmMV: org.osmdroid.views.MapView) {
        when (mapProvider) {
            yandexMap -> {
                osmMV.visibility = View.GONE
                yandexMV.visibility = View.VISIBLE
                yandexMapStartConfig(context, yandexMV)
            }
            osmMap -> {
                yandexMV.visibility = View.GONE
                osmMV.visibility = View.VISIBLE
                osmMapStartConfig(context, osmMV)
            }
        }
    }

    private fun yandexMapStartConfig(context: Context, yandexMV: MapView) {
        val isDarkMode = isDarkModeEnabled(context)
        yandexMV.map.isNightModeEnabled = isDarkMode
        val latitude = UserManager.getYandexCameraPosition(context, "yandex_lat", 56.010569.toString())!!.toDouble()
        val longitude = UserManager.getYandexCameraPosition(context, "yandex_lon", 92.852572.toString())!!.toDouble()
        val zoom = UserManager.getYandexCameraPosition(context, "yandex_zoom", 12.0f.toString())!!.toFloat()

        yandexMV.map.move(
            CameraPosition(Point(latitude, longitude), zoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null)
        yandexMV.map.isRotateGesturesEnabled = false
    }

    fun yandexMapLiveConfig(
        context: Context,
        objectsList: List<CombinedResponseObject>,
        array: BooleanArray,
        yandexMV: MapView,
        objectId: Int
    ) {
        // Inflate the custom layout for the placemark icon
        val inflater = LayoutInflater.from(context)
        val customIconView = inflater.inflate(R.layout.placemark, null)

        // Find the ImageView and TextView within the custom layout
        val iconRotateImage = customIconView.findViewById<ImageView>(R.id.rotateImage)
        val textView = customIconView.findViewById<TextView>(R.id.text_view)

        val mapObjects = yandexMV.map.mapObjects
        mapObjects.clear()
        tapListeners.clear()

        for ((index, obj) in objectsList.withIndex()) {
            if (index < array.size && array[index]) {
                val point = Point(obj.lat, obj.lon)
                val placemark = mapObjects.addPlacemark(point)

                textView.text = obj.name
                iconRotateImage.rotation = obj.heading.toFloat()
                val bitmap = createBitmapFromView(customIconView)
                placemark.setIcon(ImageProvider.fromBitmap(bitmap), IconStyle().setAnchor(PointF(0.85f, 0.5f)))

                val tapListener = MapObjectTapListener { _, _ ->
                    yandexMV.map.move(
                        CameraPosition(point, 17.0f, 0.0f, 0.0f),
                        Animation(Animation.Type.SMOOTH, 1f),
                        null
                    )
                    openBottomOverlay(context, objectsList[index])
                    true
                }
                placemark.addTapListener(tapListener)

                // Store a strong reference to the listener.
                tapListeners.add(tapListener)
            } else {
                // Do nothing
            }
        }

        if (objectId != -1) {
            val point = Point(objectsList[objectId].lat, objectsList[objectId].lon)

            yandexMV.map.move(
                CameraPosition(point, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
            openBottomOverlay(context, objectsList[objectId])
        }
    }

    private fun osmMapStartConfig(context: Context, osmMV: org.osmdroid.views.MapView) {
        val isDarkMode = isDarkModeEnabled(context)
        if (isDarkMode) {
            // If dark mode is enabled, set the color filter of the tiles overlay to invert colors
            osmMV.overlayManager.tilesOverlay.setColorFilter(TilesOverlay.INVERT_COLORS)
        } else {
            // If dark mode is not enabled, remove any color filter from the tiles overlay
            osmMV.overlayManager.tilesOverlay.setColorFilter(null)
        }

        // Get the saved camera position values from UserManager
        val latitude = UserManager.getOsmCameraPosition(context, "osm_lat", 56.010569.toString())!!.toDouble()
        val longitude = UserManager.getOsmCameraPosition(context, "osm_lon", 92.852572.toString())!!.toDouble()
        val zoom = UserManager.getOsmCameraPosition(context, "osm_zoom", 14.0.toString())!!.toDouble()

        osmMV.setTileSource(TileSourceFactory.MAPNIK)
        osmMV.controller.setZoom(zoom)
        osmMV.controller.setCenter(GeoPoint(latitude, longitude))
        osmMV.setMultiTouchControls(true)
    }

    // Configures the live settings for an OSM map view, including adding markers
    fun osmMapLiveConfig(
        context: Context,
        objectsList: List<CombinedResponseObject>,
        array: BooleanArray,
        osmMV: org.osmdroid.views.MapView,
        objectId: Int
    ) {
        // Clear the overlays before adding new markers
        osmMV.overlays.clear()

        // Inflate the custom layout for the placemark icon
        val inflater = LayoutInflater.from(context)
        val customIconView = inflater.inflate(R.layout.placemark, null)

        // Find the ImageView and TextView within the custom layout
        val iconRotateImage = customIconView.findViewById<ImageView>(R.id.rotateImage)
        val textView = customIconView.findViewById<TextView>(R.id.text_view)

        for ((index, obj) in objectsList.withIndex()) {
            if (index < array.size && array[index]) {
                // Create a GeoPoint object from the latitude and longitude values of the object
                val point = GeoPoint(obj.lat, obj.lon)

                // Create a new marker for the placemark
                val placemark = Marker(osmMV)
                placemark.position = point

                // Set name and rotation angle
                textView.text = obj.name
                iconRotateImage.rotation = obj.heading.toFloat()

                // Create a Drawable from the custom layout and set icon to a placemark
                val drawable = createDrawableFromLayout(context, customIconView)
                placemark.icon = drawable

                // Set the anchor point of the placemark
                placemark.setAnchor(0.85f, Marker.ANCHOR_CENTER)

                // Set a marker click listener
                placemark.setOnMarkerClickListener { marker, mapView ->
                    // Animate the map view to the marker's position
                    mapView.controller.animateTo(marker.position, 18.0, 1500)

                    // Open the bottom overlay
                    openBottomOverlay(context, objectsList[index])
                    true
                }
                // Add the placemark overlay to the map view
                osmMV.overlays.add(placemark)
            }
        }
        // Refresh the map view
        osmMV.invalidate()

        if (objectId != -1) {
            // If an objectId is specified, animate the map view to the corresponding object's position
            val point = GeoPoint(objectsList[objectId].lat, objectsList[objectId].lon)
            osmMV.controller.animateTo(point, 18.0, 1500)

            // Open the bottom overlay
            openBottomOverlay(context, objectsList[objectId])
        }
    }


    private fun openBottomOverlay(context: Context, objectsList: CombinedResponseObject) {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogStyle)
        val binding = BottomOverlayBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(binding.root)

        binding.carNameTemplateTV.text = objectsList.name
        val latitude = objectsList.lat
        val longitude = objectsList.lon
        binding.speedTemplateTV.text = context.resources.getString(R.string.speed_template, objectsList.speed)
        val app = context.applicationContext as AppLevelClass
        binding.timeTemplateTV.text = app.convertTime(objectsList.gmt)
        binding.ownerTemplateTV.text = objectsList.client
        binding.typeTemplateTV.text = objectsList.avto_model

        // Convert category number to its equivalent word
        val numberDefinition = NumberDefinitions.list
        val category = numberDefinition.find { it.number == objectsList.category }

        binding.categoryTemplateTV.text = category?.definition
        binding.autoNumTemplateTV.text = objectsList.avto_no

        bottomSheetDialog.show()
    }

    // Creates a drawable object from a given layout view
    private fun createDrawableFromLayout(context: Context, view: View): Drawable {
        // Measure the view to determine its dimensions
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        // Create a bitmap with the measured dimensions and ARGB_8888 configuration
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw the view onto the bitmap
        view.draw(canvas)

        // Create a BitmapDrawable from the generated bitmap
        return BitmapDrawable(context.resources, bitmap)
    }

    // Create a bitmap from a given view
    private fun createBitmapFromView(view: View): Bitmap {
        // Measure the view to determine its dimensions and set the layout of the view within the measured dimensions
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        // Create a bitmap with the measured dimensions and ARGB_8888 configuration
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw the view onto the canvas
        view.draw(canvas)

        // Return the generated bitmap
        return bitmap
    }
}