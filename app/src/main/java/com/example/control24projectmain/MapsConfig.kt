package com.example.control24projectmain

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.control24projectmain.databinding.BottomOverlayInfoBinding
import com.example.control24projectmain.databinding.EditDialogViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.traffic.TrafficColor
import com.yandex.mapkit.traffic.TrafficLayer
import com.yandex.mapkit.traffic.TrafficLevel
import com.yandex.mapkit.traffic.TrafficListener
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private var mapProvider: String = yandexMap
private val tapListeners = mutableListOf<MapObjectTapListener>()

class MapsConfig: TrafficListener {

    private lateinit var trafficSignal: ImageView
    private lateinit var levelText: TextView
    private lateinit var levelIcon: ImageView
    private lateinit var zoomInButton: ConstraintLayout
    private lateinit var zoomOutButton: ConstraintLayout

    private var trafficLevel: TrafficLevel? = null
    private enum class TrafficFreshness {Loading, OK, Expired}
    private var trafficFreshness: TrafficFreshness? = null
    private lateinit var traffic: TrafficLayer

    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var iconState = false

    private fun getMapProvider(context: Context) {
        mapProvider = UserManager.getSelectedMap(context).toString()
    }

    fun mapsInitialization(context: Context) {
        // Initialize the maps
        getMapProvider(context)
        when (mapProvider) {
            yandexMap -> MapKitFactory.initialize(context) // Yandex Maps
        }
    }

    fun mapsOnStop(context: Context, yandexMV: MapView) {
        when (mapProvider) {
            yandexMap -> {
                yandexMV.onStop()
                MapKitFactory.getInstance().onStop()
                UserManager.saveTrafficJamState(context, traffic.isTrafficVisible)
            }
        }
    }

    fun mapsOnStart(yandexMV: MapView) {
        when (mapProvider) {
            yandexMap -> {
                MapKitFactory.getInstance().onStart()
                yandexMV.onStart()
            }
        }
    }

    fun startMapsConfig(
        context: Context,
        yandexMV: MapView,
        osmMapBoxMV: com.mapbox.maps.MapView,
        trafficSignalIV: ImageView,
        levelIV: ImageView,
        levelTV: TextView,
        zoomCL: ConstraintLayout,
        zoomInCL: ConstraintLayout,
        zoomOutCL: ConstraintLayout
    ) {
        trafficSignal = trafficSignalIV
        zoomInButton = zoomInCL
        zoomOutButton = zoomOutCL
        levelIcon = levelIV
        levelText = levelTV

        when (mapProvider) {
            yandexMap -> {
                osmMapBoxMV.visibility = View.GONE
                yandexMV.visibility = View.VISIBLE
                if (UserManager.getTrafficControlsState(context)) {
                    levelIcon.visibility = View.VISIBLE
                    levelText.visibility = View.VISIBLE
                } else {
                    levelIcon.visibility = View.GONE
                    levelText.visibility = View.GONE
                }
                yandexMapStartConfig(context, yandexMV)
            }
            osmMap -> {
                yandexMV.visibility = View.GONE
                osmMapBoxMV.visibility = View.VISIBLE
                levelIcon.visibility = View.GONE
                levelText.visibility = View.GONE
                osmMapStartConfig(context, osmMapBoxMV)
            }
        }

        if (UserManager.getZoomControlsState(context)) {
            zoomCL.visibility = View.VISIBLE
        } else {
            zoomCL.visibility = View.GONE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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

        traffic = MapKitFactory.getInstance().createTrafficLayer(yandexMV.mapWindow)
        traffic.addTrafficListener(this)

        if (UserManager.getTrafficJamState(context) && UserManager.getTrafficControlsState(context)) {
            trafficSignal.visibility = View.GONE
            traffic.isTrafficVisible = true
            updateLevel()
        }

        levelIcon.setOnClickListener {
            traffic.isTrafficVisible = !traffic.isTrafficVisible
            if (traffic.isTrafficVisible) {
                trafficSignal.visibility = View.GONE
            } else {
                trafficSignal.visibility = View.VISIBLE
            }
            updateLevel()
        }

        zoomInButton.setOnClickListener {
            val cameraPosition = yandexMV.map.cameraPosition
            yandexMV.map.move(
                CameraPosition(Point(cameraPosition.target.latitude, cameraPosition.target.longitude), cameraPosition.zoom + 1, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, .2f),
                null
            )
        }

        val handler = Handler(Looper.getMainLooper())
        val postDelay: Long = 200
        val runnableZoomIn = object : Runnable {
            override fun run() {
                val cameraPosition = yandexMV.map.cameraPosition
                yandexMV.map.move(
                    CameraPosition(Point(cameraPosition.target.latitude, cameraPosition.target.longitude), cameraPosition.zoom + 1, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, .2f),
                    null
                )

                handler.postDelayed(this, postDelay)
            }
        }
        val runnableZoomOut = object : Runnable {
            override fun run() {
                val cameraPosition = yandexMV.map.cameraPosition
                yandexMV.map.move(
                    CameraPosition(Point(cameraPosition.target.latitude, cameraPosition.target.longitude), cameraPosition.zoom - 1, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, .2f),
                    null
                )

                handler.postDelayed(this, postDelay)
            }
        }

        zoomInButton.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Start the continuous zooming
                    handler.postDelayed(runnableZoomIn, postDelay)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Stop the continuous zooming
                    handler.removeCallbacks(runnableZoomIn)
                    view.performClick()
                    true
                }
                else -> false
            }
        }

        zoomOutButton.setOnClickListener {
            val cameraPosition = yandexMV.map.cameraPosition
            yandexMV.map.move(
                CameraPosition(Point(cameraPosition.target.latitude, cameraPosition.target.longitude), cameraPosition.zoom - 1, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, .2f),
                null
            )
        }

        zoomOutButton.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Start the continuous zooming
                    handler.postDelayed(runnableZoomOut, postDelay)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Stop the continuous zooming
                    handler.removeCallbacks(runnableZoomOut)
                    view.performClick()
                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("InflateParams")
    fun yandexMapLiveConfig(
        context: Context,
        objectsList: List<CombinedResponseObject>,
        array: BooleanArray,
        yandexMV: MapView,
        objectId: Int,
        lifecycleScope: CoroutineScope
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
                    openBottomOverlayInfo(context, objectsList[index], lifecycleScope)
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
            openBottomOverlayInfo(context, objectsList[objectId], lifecycleScope)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun osmMapStartConfig(context: Context, osmMapBoxMV: com.mapbox.maps.MapView) {
        val isDarkMode = isDarkModeEnabled(context)

        osmMapBoxMV.getMapboxMap().loadStyleUri(when (UserManager.getMapType(context)) {
            SCHEME -> {
                if (isDarkMode) {
                    Style.TRAFFIC_NIGHT
                } else {
                    Style.TRAFFIC_DAY
                }
            }
            SATELLITE -> Style.SATELLITE
            HYBRID -> Style.SATELLITE_STREETS
            else -> {
                if (isDarkMode) {
                    Style.TRAFFIC_NIGHT
                } else {
                    Style.TRAFFIC_DAY
                }
            }
        })

        // Get the saved camera position values from UserManager
        val latitude = UserManager.getOsmCameraPosition(context, "osm_lat", 56.010569.toString())!!.toDouble()
        val longitude = UserManager.getOsmCameraPosition(context, "osm_lon", 92.852572.toString())!!.toDouble()
        val zoom = UserManager.getOsmCameraPosition(context, "osm_zoom", 11.0.toString())!!.toDouble()

        val cameraPosition = CameraOptions.Builder()
            .center(com.mapbox.geojson.Point.fromLngLat(longitude, latitude))
            .zoom(zoom)
            .build()
        osmMapBoxMV.getMapboxMap().setCamera(cameraPosition)
        osmMapBoxMV.gestures.rotateEnabled = false
        osmMapBoxMV.compass.enabled = false
        osmMapBoxMV.scalebar.enabled = false
        osmMapBoxMV.logo.updateSettings {
            position = Gravity.BOTTOM or Gravity.END
            marginRight = 80f
        }
        osmMapBoxMV.attribution.updateSettings {
            position = Gravity.BOTTOM or Gravity.END
        }

        val handler = Handler(Looper.getMainLooper())
        val postDelay: Long = 200
        val runnableZoomIn = object : Runnable {
            override fun run() {
                val cameraState = osmMapBoxMV.getMapboxMap().cameraState
                val cameraOptions = CameraOptions.Builder()
                    .zoom(cameraState.zoom + 1)
                    .build()
                val animationOptions = MapAnimationOptions.mapAnimationOptions {
                    duration(500)
                }
                osmMapBoxMV.getMapboxMap().easeTo(cameraOptions, animationOptions)

                handler.postDelayed(this, postDelay)
            }
        }
        val runnableZoomOut = object : Runnable {
            override fun run() {
                val cameraState = osmMapBoxMV.getMapboxMap().cameraState
                val cameraOptions = CameraOptions.Builder()
                    .zoom(cameraState.zoom - 1)
                    .build()
                val animationOptions = MapAnimationOptions.mapAnimationOptions {
                    duration(500)
                }
                osmMapBoxMV.getMapboxMap().easeTo(cameraOptions, animationOptions)

                handler.postDelayed(this, postDelay)
            }
        }

        zoomInButton.setOnClickListener {
            val cameraState = osmMapBoxMV.getMapboxMap().cameraState
            val cameraOptions = CameraOptions.Builder()
                .zoom(cameraState.zoom + 1)
                .build()
            val animationOptions = MapAnimationOptions.mapAnimationOptions {
                duration(500)
            }
            osmMapBoxMV.getMapboxMap().easeTo(cameraOptions, animationOptions)
        }

        zoomInButton.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Start the continuous zooming
                    handler.postDelayed(runnableZoomIn, postDelay)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Stop the continuous zooming
                    handler.removeCallbacks(runnableZoomIn)
                    view.performClick()
                    true
                }
                else -> false
            }
        }

        zoomOutButton.setOnClickListener {
            val cameraState = osmMapBoxMV.getMapboxMap().cameraState
            val cameraOptions = CameraOptions.Builder()
                .zoom(cameraState.zoom - 1)
                .build()
            val animationOptions = MapAnimationOptions.mapAnimationOptions {
                duration(500)
            }
            osmMapBoxMV.getMapboxMap().easeTo(cameraOptions, animationOptions)
        }

        zoomOutButton.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Start the continuous zooming
                    handler.postDelayed(runnableZoomOut, postDelay)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Stop the continuous zooming
                    handler.removeCallbacks(runnableZoomOut)
                    view.performClick()
                    true
                }
                else -> false
            }
        }
    }

    // Configures the live settings for an OSM mapbox, including adding markers
    @SuppressLint("InflateParams")
    fun osmMapLiveConfig(
        context: Context,
        objectsList: List<CombinedResponseObject>,
        array: BooleanArray,
        osmMapBoxMV: com.mapbox.maps.MapView,
        objectId: Int,
        lifecycleScope: CoroutineScope
    ) {
        // Inflate the custom layout for the placemark icon
        val inflater = LayoutInflater.from(context)
        val customIconView = inflater.inflate(R.layout.placemark, null)

        // Find the ImageView and TextView within the custom layout
        val iconRotateImage = customIconView.findViewById<ImageView>(R.id.rotateImage)
        val textView = customIconView.findViewById<TextView>(R.id.text_view)

        // Create an instance of the Annotation API
        val annotationApi = osmMapBoxMV.annotations
        annotationApi.cleanup()

        for ((index, obj) in objectsList.withIndex()) {
            if (index < array.size && array[index]) {
                //  Get the PointAnnotationManager
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()

                // Set name and rotation angle
                textView.text = obj.name
                iconRotateImage.rotation = obj.heading.toFloat()

                // Create a Bitmap from the custom layout and set icon to a placemark
                val bitmap = createBitmapFromView(customIconView)

                // Set options for point annotation
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    // Define a geographic coordinate
                    .withPoint(com.mapbox.geojson.Point.fromLngLat(obj.lon, obj.lat))
                    // Specify the bitmap
                    .withIconImage(bitmap)
                    // Set the anchor and offset
                    .withIconAnchor(IconAnchor.RIGHT)
                    .withIconOffset(listOf(20.0, 0.0))

                // Add the resulting pointAnnotation to the map.
                pointAnnotationManager.create(pointAnnotationOptions)

                // Set a point annotation click listener
                pointAnnotationManager.addClickListener {pointAnnotation ->
                    // Animate the to the point's position
                    val cameraOptions = CameraOptions.Builder()
                        .center(pointAnnotation.point)
                        .zoom(16.0)
                        .build()
                    val animationOptions = MapAnimationOptions.mapAnimationOptions {
                        duration(2000)
                    }
                    osmMapBoxMV.getMapboxMap().flyTo(cameraOptions, animationOptions)

                    // Open the bottom overlay
                    openBottomOverlayInfo(context, objectsList[index],lifecycleScope)

                    true
                }
            }
        }

        // If came from list fragment
        if (objectId != -1) {
            // If an objectId is specified, animate the map view to the corresponding object's position
            val latitude = objectsList[objectId].lat
            val longitude = objectsList[objectId].lon

            val cameraOptions = CameraOptions.Builder()
                .center(com.mapbox.geojson.Point.fromLngLat(longitude, latitude))
                .zoom(16.0)
                .build()
            val animationOptions = MapAnimationOptions.mapAnimationOptions {
                duration(1500)
            }
            osmMapBoxMV.getMapboxMap().flyTo(cameraOptions, animationOptions)

            // Open the bottom overlay
            openBottomOverlayInfo(context, objectsList[objectId], lifecycleScope)
        }
    }

    // Open bottom overlay with object information
    private fun openBottomOverlayInfo(context: Context, objectsList: CombinedResponseObject, lifecycleScope: CoroutineScope) {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogStyle)
        val binding = BottomOverlayInfoBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(binding.root)

        binding.carNameTemplateTV.text = objectsList.name
        val latitude = objectsList.lat
        val longitude = objectsList.lon
        val app = context.applicationContext as AppLevelClass

        lifecycleScope.launch {
            binding.progressBar4.visibility = View.VISIBLE
            try {
                val address = app.coroutineGeocode(latitude, longitude)
                withContext(Dispatchers.Main) {
                    binding.locationTemplateTV.text = address
                }
            } catch (e: Exception) {
                binding.locationTemplateTV.text = "Ошибка геокодирования"
                Log.i("GEOCODER ERROR", "Failed to get address", e)
            }
            binding.locationTemplateTV.visibility = View.VISIBLE
            binding.progressBar4.visibility = View.INVISIBLE
        }

        binding.speedTemplateTV.text = context.resources.getString(R.string.speed_template, objectsList.speed)
        binding.timeTemplateTV.text = app.convertTime(objectsList.gmt)
        binding.ownerTemplateTV.text = objectsList.client
        binding.typeTemplateTV.text = objectsList.avto_model

        // Convert category number to its equivalent word
        val numberDefinition = NumberDefinitions.list
        val category = numberDefinition.find { it.number == objectsList.category }

        binding.categoryTemplateTV.text = category?.definition
        binding.autoNumTemplateTV.text = objectsList.avto_no

        val driverInfo = UserManager.getDriverInfo(context, objectsList.id.toString())
        binding.driverTemplateTV.text = (if (driverInfo.first != "null") {
            driverInfo.first
        } else {
            context.resources.getString(R.string.driver_template)
        }).toString()

        binding.driverCL.setOnClickListener {
            openEditDialog(context, objectsList.id, binding.driverTemplateTV)
        }

        bottomSheetDialog.show()
    }

    private fun openEditDialog(context: Context, itemId: Int, driverTemplateTV: TextView) {
        val binding = EditDialogViewBinding.inflate(LayoutInflater.from(context))
        val editDialog = AlertDialog.Builder(context)
        editDialog.setView(binding.root)
        val dialog = editDialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val driverInfo = UserManager.getDriverInfo(context, itemId.toString())
        binding.driverNameET.setText(if (driverInfo.first != "null") {
            driverInfo.first
        } else {
            ""
        })

        binding.driverPhoneET.setText(if (driverInfo.second != "null") {
            driverInfo.second
        } else {
            ""
        })

        binding.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        binding.saveBtn.setOnClickListener {
            val driverNameET = binding.driverNameET.text.toString().ifEmpty { "null" }
            val driverPhoneET = binding.driverPhoneET.text.toString().ifEmpty { "null" }
            UserManager.saveDriverInfo(context, itemId.toString(), driverNameET, driverPhoneET)

            driverTemplateTV.text = (if (driverNameET != "null") {
                driverNameET
            } else {
                context.resources.getString(R.string.driver_template)
            }).toString()
            //listener.onDialogClose()
            dialog.dismiss()
        }

        dialog.show()
    }

    // Open bottom overlay with object information
    /*private fun openBottomOverlayLayers(
        context: Context,
        osmMapBoxMV: com.mapbox.maps.MapView,
        zoomCL: ConstraintLayout
    ) {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogStyle)
        val binding = BottomOverlayLayersBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(binding.root)

        val yandexMapsProvider = binding.yandexMapsMRadioButton
        val osmMapsProvider = binding.osmMapsMRadioButton

        var checkedMapProviderRB = when (UserManager.getSelectedMap(context)) {
            yandexMap -> yandexMapsProvider
            osmMap -> osmMapsProvider
            else -> yandexMapsProvider
        }
        checkedMapProviderRB.isChecked = true
        checkedMapProviderRB.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextCheckedColor, Color.BLACK))
        checkedMapProviderRB.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)

        binding.mapProviderRG.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                yandexMapsProvider.id -> {
                    checkedMapProviderRB.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapProviderRB.typeface = ResourcesCompat.getFont(context, R.font.roboto)
                    yandexMapsProvider.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    yandexMapsProvider.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
                    checkedMapProviderRB = yandexMapsProvider
                    UserManager.saveSelectedMap(context, yandexMap)

                    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()

                    val newFragment = MapFragment()
                    fragmentTransaction.replace(R.id.frameLayout, newFragment)

                    fragmentTransaction.commit()
                }
                osmMapsProvider.id -> {
                    checkedMapProviderRB.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapProviderRB.typeface = ResourcesCompat.getFont(context, R.font.roboto)
                    osmMapsProvider.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    osmMapsProvider.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
                    checkedMapProviderRB = osmMapsProvider
                    UserManager.saveSelectedMap(context, osmMap)

                    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()

                    val newFragment = MapFragment()
                    fragmentTransaction.replace(R.id.frameLayout, newFragment)

                    fragmentTransaction.commit()
                }
            }
        }

        binding.yandex.setOnClickListener {
            UserManager.saveSelectedMap(context, yandexMap)
            val mapViewModel = ViewModelProvider((context as AppCompatActivity))[MapViewModel::class.java]
            mapViewModel.selectMap(context, yandexMap)
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            val newFragment = MapFragment()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)

            fragmentTransaction.commit()
        }

        binding.osm.setOnClickListener {
            UserManager.saveSelectedMap(context, osmMap)
            val mapViewModel = ViewModelProvider((context as AppCompatActivity))[MapViewModel::class.java]
            mapViewModel.selectMap(context, osmMap)
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            val newFragment = MapFragment()
            fragmentTransaction.replace(R.id.frameLayout, newFragment)

            fragmentTransaction.commit()
        }

        binding.mapTypeCL.visibility = when (UserManager.getSelectedMap(context)) {
            yandexMap -> View.GONE
            osmMap -> View.VISIBLE
            else -> View.GONE
        }

        val schemeRB = binding.schemeMRadioButton
        val satelliteRB = binding.satelliteMRadioButton
        val hybridRD = binding.hybridMRadioButton

        var checkedMapTypeRB = when (UserManager.getMapType(context)) {
            SCHEME -> schemeRB
            SATELLITE -> satelliteRB
            HYBRID -> hybridRD
            else -> schemeRB
        }

        checkedMapTypeRB.isChecked = true
        checkedMapTypeRB.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextCheckedColor, Color.BLACK))
        checkedMapTypeRB.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)

        binding.layersRG.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                schemeRB.id -> {
                    checkedMapTypeRB.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapTypeRB.typeface = ResourcesCompat.getFont(context, R.font.roboto)
                    schemeRB.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    schemeRB.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
                    checkedMapTypeRB = schemeRB
                    UserManager.saveMapType(context, SCHEME)
                    if (isDarkModeEnabled(context)) {
                        osmMapBoxMV.getMapboxMap().loadStyleUri(Style.TRAFFIC_NIGHT)
                    } else {
                        osmMapBoxMV.getMapboxMap().loadStyleUri(Style.TRAFFIC_DAY)
                    }
                }
                satelliteRB.id -> {
                    checkedMapTypeRB.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapTypeRB.typeface = ResourcesCompat.getFont(context, R.font.roboto)
                    satelliteRB.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    satelliteRB.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
                    checkedMapTypeRB = satelliteRB
                    UserManager.saveMapType(context, SATELLITE)
                    osmMapBoxMV.getMapboxMap().loadStyleUri(Style.SATELLITE)
                }
                hybridRD.id -> {
                    checkedMapTypeRB.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapTypeRB.typeface = ResourcesCompat.getFont(context, R.font.roboto)
                    hybridRD.setTextColor(MaterialColors.getColor(context, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    hybridRD.typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
                    checkedMapTypeRB = hybridRD
                    UserManager.saveMapType(context, HYBRID)
                    osmMapBoxMV.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS)
                }
            }
        }

        binding.zoomButtonsMCheckBox.isChecked = UserManager.getZoomControlsState(context)

        binding.zoomButtonsMCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                UserManager.saveZoomControlsState(context, true)
                zoomCL.visibility = View.VISIBLE
            } else {
                UserManager.saveZoomControlsState(context, false)
                zoomCL.visibility = View.GONE
            }
        }

        bottomSheetDialog.show()
    }*/

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

    private fun updateLevel() {
        val iconId: Int
        var level = ""
        if (!traffic.isTrafficVisible) {
            iconId = R.drawable.icon_traffic_grey
        } else if (trafficFreshness == TrafficFreshness.Loading) {
            startLoadingAnimation()
            return  // Stop further execution in this call
        } else if (trafficFreshness == TrafficFreshness.Expired) {
            iconId = R.drawable.icon_traffic_grey
        } else if (trafficLevel == null) { // State is fresh but region has no data
            iconId = R.drawable.icon_traffic_blue
        } else {
            iconId = when (trafficLevel!!.color) {
                TrafficColor.RED -> R.drawable.icon_traffic_red
                TrafficColor.YELLOW -> R.drawable.icon_traffic_yellow
                TrafficColor.GREEN -> R.drawable.icon_traffic_green
                else -> R.drawable.icon_traffic_grey
            }
            level = trafficLevel!!.level.toString()
        }
        levelIcon.setImageResource(iconId)
        levelText.text = level
    }

    private fun startLoadingAnimation() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                if (trafficFreshness != TrafficFreshness.Loading) {
                    handler?.removeCallbacks(this) // Stop the loop when trafficFreshness is no longer Loading
                    updateLevel() // Call updateLevel again to set the correct icon
                    return
                }

                if (iconState) {
                    levelIcon.setImageResource(R.drawable.icon_traffic_grey)
                } else {
                    levelIcon.setImageResource(R.drawable.icon_traffic_yellow)
                }

                iconState = !iconState // Flip the icon state
                handler?.postDelayed(this, 1000) // Call this runnable again in 1 second
            }
        }
        handler?.post(runnable as Runnable)
    }

    override fun onTrafficChanged(trafficLevel: TrafficLevel?) {
        this.trafficLevel = trafficLevel
        this.trafficFreshness = TrafficFreshness.OK
        updateLevel()
    }

    override fun onTrafficLoading() {
        this.trafficLevel = null
        this.trafficFreshness = TrafficFreshness.Loading
        updateLevel()
    }

    override fun onTrafficExpired() {
        this.trafficLevel = null
        this.trafficFreshness = TrafficFreshness.Loading
        updateLevel()
    }
}