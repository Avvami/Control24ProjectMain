package com.example.control24projectmain.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.control24projectmain.Data
import com.example.control24projectmain.FirstResponse
import com.example.control24projectmain.HttpRequestHelper
import com.example.control24projectmain.ObjectRoutesAdapter
import com.example.control24projectmain.R
import com.example.control24projectmain.Routes
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityRouteBinding
import com.example.control24projectmain.isDarkModeEnabled
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.color.MaterialColors
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.gson.Gson
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.log2

class RouteActivity: AppCompatActivity(), DrivingSession.DrivingRouteListener {

    private lateinit var binding: ActivityRouteBinding
    private lateinit var yandexMVRoute: MapView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var dateTopTV: TextView
    private lateinit var dateBottomTV: TextView

    private lateinit var mapObjectsColl: MapObjectCollection
    private lateinit var drivingRouter: DrivingRouter
    private lateinit var drivingSession: DrivingSession

    private lateinit var isPeriodCheckedRB: RadioButton
    private lateinit var calendar: Calendar
    private lateinit var startCalendar: Calendar
    private lateinit var endCalendar: Calendar
    private lateinit var routesRV: RecyclerView
    private lateinit var noDataTV: TextView
    private var routePoints: Data? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the maps
        MapKitFactory.initialize(this)

        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val topMargin = binding.routeBackCL.marginTop
        ViewCompat.setOnApplyWindowInsetsListener(binding.routeBackCL) { view, windowInsets ->
            val statusBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())

            val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.topMargin = statusBarInsets.top + topMargin

            WindowInsetsCompat.CONSUMED
        }

        val bottomSheet = findViewById<ConstraintLayout>(R.id.persistentBottomSheet)
        val carNameTV = findViewById<TextView>(R.id.carNameTemplateTV1)
        val infoTypeRG = findViewById<RadioGroup>(R.id.infoTypeRG)
        val periodInfoRB = findViewById<MaterialRadioButton>(R.id.periodInfoMRadioButton)
        val dayInfoRB = findViewById<MaterialRadioButton>(R.id.dayInfoMRadioButton)
        dateTopTV = findViewById(R.id.dateTopTV)
        dateBottomTV = findViewById(R.id.dateBottomTV)
        val leftArrowCL = findViewById<ConstraintLayout>(R.id.leftArrowCL)
        val rightArrowCL = findViewById<ConstraintLayout>(R.id.rightArrowCL)
        val dateChangeCL = findViewById<ConstraintLayout>(R.id.dateChangeCL)
        val dateCL = findViewById<ConstraintLayout>(R.id.dateCL)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar3)
        routesRV = findViewById(R.id.routesRV)
        noDataTV = findViewById(R.id.noDataTV)

        if (UserManager.getZoomControlsState(this)) {
            binding.zoomCL.visibility = View.VISIBLE
        } else {
            binding.zoomCL.visibility = View.GONE
        }

        val handler = Handler(Looper.getMainLooper())
        val postDelay: Long = 200
        val runnableZoomIn = object : Runnable {
            override fun run() {
                val cameraPosition = yandexMVRoute.map.cameraPosition
                yandexMVRoute.map.move(
                    CameraPosition(Point(cameraPosition.target.latitude, cameraPosition.target.longitude), cameraPosition.zoom + 1, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, .2f),
                    null
                )

                handler.postDelayed(this, postDelay)
            }
        }
        val runnableZoomOut = object : Runnable {
            override fun run() {
                val cameraPosition = yandexMVRoute.map.cameraPosition
                yandexMVRoute.map.move(
                    CameraPosition(Point(cameraPosition.target.latitude, cameraPosition.target.longitude), cameraPosition.zoom - 1, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, .2f),
                    null
                )

                handler.postDelayed(this, postDelay)
            }
        }

        binding.zoomInCL.setOnClickListener {
            val cameraPosition = yandexMVRoute.map.cameraPosition
            yandexMVRoute.map.move(
                CameraPosition(Point(cameraPosition.target.latitude, cameraPosition.target.longitude), cameraPosition.zoom + 1, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, .2f),
                null
            )
        }

        binding.zoomInCL.setOnTouchListener { view, event ->
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

        binding.zoomOutCL.setOnClickListener {
            val cameraPosition = yandexMVRoute.map.cameraPosition
            yandexMVRoute.map.move(
                CameraPosition(Point(cameraPosition.target.latitude, cameraPosition.target.longitude), cameraPosition.zoom - 1, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, .2f),
                null
            )
        }

        binding.zoomOutCL.setOnTouchListener { view, event ->
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

        yandexMVRoute = binding.yandexMVRoute

        val isDarkMode = isDarkModeEnabled(this)
        yandexMVRoute.map.isNightModeEnabled = isDarkMode

        yandexMVRoute.map.move(
            CameraPosition(Point(56.010569, 92.852572), 12.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null)
        yandexMVRoute.map.isRotateGesturesEnabled = false

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjectsColl = yandexMVRoute.map.mapObjects.addCollection()

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.halfExpandedRatio = 0.31f

        val carId = intent.getIntExtra("carId", -1)
        val carName = intent.getStringExtra("carName")
        carNameTV.text = carName

        calendar = Calendar.getInstance()
        startCalendar = Calendar.getInstance()
        endCalendar = Calendar.getInstance()

        if (savedInstanceState != null) {
            // Restore the saved state
            calendar.timeInMillis = savedInstanceState.getLong("calendarTime")
            startCalendar.timeInMillis = savedInstanceState.getLong("startCalendarTime")
            endCalendar.timeInMillis = savedInstanceState.getLong("endCalendarTime")
            if (savedInstanceState.getBoolean("isPeriodChecked")) {
                isPeriodCheckedRB = periodInfoRB
                isPeriodCheckedRB.isChecked = true
            } else {
                isPeriodCheckedRB = dayInfoRB
                isPeriodCheckedRB.isChecked = true
            }
            if (!isPeriodCheckedRB.isChecked) {
                dateTopTV.text = savedInstanceState.getString("dateTop").toString()
                dateBottomTV.text = savedInstanceState.getString("dateBottom").toString()
            } else {
                dateTopTV.text = resources.getString(R.string.pick_range)
                dateBottomTV.text = savedInstanceState.getString("dateBottom").toString()
                leftArrowCL.visibility = View.GONE
                rightArrowCL.visibility = View.GONE
            }
        } else {
            dateTopTV.text = formattedDate(calendar)
            dateBottomTV.text = formattedDayOfWeek(calendar)
            isPeriodCheckedRB = dayInfoRB
            isPeriodCheckedRB.isChecked = true
        }

        isPeriodCheckedRB.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
        isPeriodCheckedRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))

        infoTypeRG.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                periodInfoRB.id -> {
                    isPeriodCheckedRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    isPeriodCheckedRB.typeface = ResourcesCompat.getFont(this, R.font.roboto)
                    periodInfoRB.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
                    periodInfoRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    isPeriodCheckedRB = periodInfoRB

                    dateChangeCL.isClickable = true
                    dateChangeCL.background = ContextCompat.getDrawable(this, R.drawable.btn_calendars_ripple_color)
                    dateCL.isClickable = false
                    dateCL.background = null
                    dateTopTV.text = resources.getString(R.string.pick_range)
                    if (startCalendar.get(Calendar.DATE) != Calendar.getInstance().get(Calendar.DATE)) {
                        dateBottomTV.text = resources.getString(R.string.range_template, formattedDate(startCalendar), formattedDate(endCalendar))
                    } else {
                        dateBottomTV.text = resources.getString(R.string.range)
                    }
                    leftArrowCL.visibility = View.GONE
                    rightArrowCL.visibility = View.GONE

                    routesRV.adapter = null
                    noDataTV.visibility = View.GONE

                    mapObjectsColl.clear()
                    if (startCalendar.get(Calendar.DATE) != Calendar.getInstance().get(Calendar.DATE)) {
                        val (calendar1, calendar2) = getCalendarsRange(startCalendar, endCalendar)
                        requestToDatabase(carId, formattedDateToDatabase(calendar1), formattedDateToDatabase(calendar2), progressBar)
                    }
                }
                dayInfoRB.id -> {
                    isPeriodCheckedRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    isPeriodCheckedRB.typeface = ResourcesCompat.getFont(this, R.font.roboto)
                    dayInfoRB.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
                    dayInfoRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    isPeriodCheckedRB = dayInfoRB

                    dateChangeCL.background = null
                    dateChangeCL.isClickable = false
                    dateCL.isClickable = true
                    dateCL.background = ContextCompat.getDrawable(this, R.drawable.btn_calendars_ripple_color)
                    dateTopTV.text = formattedDate(calendar)
                    dateBottomTV.text = formattedDayOfWeek(calendar)
                    leftArrowCL.visibility = View.VISIBLE
                    rightArrowCL.visibility = View.VISIBLE

                    routesRV.adapter = null
                    noDataTV.visibility = View.GONE

                    mapObjectsColl.clear()
                    val (calendar1, calendar2) = getCalendars(calendar)
                    requestToDatabase(carId, formattedDateToDatabase(calendar1), formattedDateToDatabase(calendar2), progressBar)
                }
            }
        }

        leftArrowCL.setOnClickListener {
            routesRV.adapter = null

            calendar.add(Calendar.DATE, -1)
            dateTopTV.text = formattedDate(calendar)
            dateBottomTV.text = formattedDayOfWeek(calendar)

            val (calendar1, calendar2) = getCalendars(calendar)
            requestToDatabase(carId, formattedDateToDatabase(calendar1), formattedDateToDatabase(calendar2), progressBar)
        }

        rightArrowCL.setOnClickListener {
            val today = Calendar.getInstance()
            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                return@setOnClickListener
            } else {
                routesRV.adapter = null
            }
            calendar.add(Calendar.DATE, 1)
            dateTopTV.text = formattedDate(calendar)
            dateBottomTV.text = formattedDayOfWeek(calendar)

            val (calendar1, calendar2) = getCalendars(calendar)
            requestToDatabase(carId, formattedDateToDatabase(calendar1), formattedDateToDatabase(calendar2), progressBar)
        }

        dateCL.setOnClickListener {
            openTimePicker(carId, calendar, progressBar)
        }

        dateChangeCL.setOnClickListener {
            openPeriodTimePicker(carId, startCalendar, endCalendar, progressBar)
        }

        if (carId == -1) {
            StyleableToast.makeText(
                this@RouteActivity,
                "Неизвестная ошибка",
                Toast.LENGTH_LONG,
                R.style.CustomStyleableToast
            ).show()
        } else {
            val (calendar1, calendar2) = getCalendars(calendar)
            requestToDatabase(carId, formattedDateToDatabase(calendar1), formattedDateToDatabase(calendar2), progressBar)
        }

        binding.routeBackCL.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("dateTop", dateTopTV.text.toString())
        outState.putString("dateBottom", dateBottomTV.text.toString())
        outState.putBoolean("isPeriodChecked", isPeriodCheckedRB.isChecked)
        outState.putLong("calendarTime", calendar.timeInMillis)
        outState.putLong("startCalendarTime", startCalendar.timeInMillis)
        outState.putLong("endCalendarTime", endCalendar.timeInMillis)
    }

    override fun onStop() {
        yandexMVRoute.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()

        // Set default status bar color
        val window: Window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)

        // Set the system UI visibility back to the default value when the fragment is detached or exited
        if (!isDarkModeEnabled(this)) {
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
        MapKitFactory.getInstance().onStart()
        yandexMVRoute.onStart()

        // Set the background for status bar
        val window: Window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.black_10p)

        // Set the system UI visibility to the dark
        if (!isDarkModeEnabled(this)) {
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

    private fun getCalendars(calendar: Calendar): Pair<Calendar, Calendar> {
        val calendar1 = calendar.clone() as Calendar
        calendar1.set(Calendar.HOUR_OF_DAY, 0)
        calendar1.set(Calendar.MINUTE, 0)
        calendar1.set(Calendar.SECOND, 0)

        val calendar2: Calendar
        if (calendar.get(Calendar.DATE) != Calendar.getInstance().get(Calendar.DATE)) {
            calendar2 = calendar1.clone() as Calendar
            calendar2.set(Calendar.HOUR_OF_DAY, 24)
        } else {
            calendar2 = calendar.clone() as Calendar
        }

        return Pair(calendar1, calendar2)
    }

    private fun getCalendarsRange(startCalendar: Calendar, endCalendar: Calendar): Pair<Calendar, Calendar> {
        val calendar1 = startCalendar.clone() as Calendar
        calendar1.set(Calendar.HOUR_OF_DAY, 0)
        calendar1.set(Calendar.MINUTE, 0)
        calendar1.set(Calendar.SECOND, 0)

        val calendar2: Calendar
        if (endCalendar.get(Calendar.DATE) != Calendar.getInstance().get(Calendar.DATE)) {
            calendar2 = endCalendar.clone() as Calendar
            calendar2.set(Calendar.HOUR_OF_DAY, 24)
        } else {
            calendar2 = Calendar.getInstance()
        }

        return Pair(calendar1, calendar2)
    }

    private fun requestToDatabase(carId: Int, startDate: String, endDate: String, progressBar: ProgressBar) {
        noDataTV.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val loginCredentials = UserManager.getLoginCredentials(this@RouteActivity)
                val httpResponseFirst = HttpRequestHelper.makeHttpRequest(
                    "http://91.193.225.170:8012/login2&${loginCredentials?.first}&${loginCredentials?.second}"
                )
                val gson = Gson()
                val firstResponseData = gson.fromJson(httpResponseFirst, FirstResponse::class.java)

                val httpResponseSecond = HttpRequestHelper.makeHttpRequest(
                    "http://91.193.225.170:8012/route2&${firstResponseData.key}&$carId&$startDate&$endDate"
                )
                Log.i("HDFJSDHFK", "http://91.193.225.170:8012/route2&${firstResponseData.key}&$carId&$startDate&$endDate")
                routePoints = gson.fromJson(httpResponseSecond, Data::class.java)
            } catch (e: HttpRequestHelper.BadRequestException) {
                StyleableToast.makeText(
                    this@RouteActivity,
                    "Bad request: ${e.message}",
                    Toast.LENGTH_LONG,
                    R.style.CustomStyleableToast
                ).show()
            } catch (e: HttpRequestHelper.TimeoutException) {
                StyleableToast.makeText(
                    this@RouteActivity,
                    "Ошибка: Превышено время ожидания ответа от сервера",
                    Toast.LENGTH_LONG,
                    R.style.CustomStyleableToast
                ).show()
            } catch (e: ConnectException) {
                StyleableToast.makeText(
                    this@RouteActivity,
                    "Ошибка подключения",
                    Toast.LENGTH_LONG,
                    R.style.CustomStyleableToast
                ).show()
            } catch (e: Exception) {
                StyleableToast.makeText(
                    this@RouteActivity,
                    e.toString(),
                    Toast.LENGTH_LONG,
                    R.style.CustomStyleableToast
                ).show()
            } finally {
                if (routePoints != null && routePoints!!.points.size > 1) {
                    submitRequest(routePoints!!)
                    progressBar.visibility = View.GONE
                    noDataTV.visibility = View.GONE

                    val routes = fillRoutes(routePoints!!)

                    // Filter the data to get only Data objects with more than 1 Point
                    val filteredData = routes.route.filter { data ->
                        data.points.size > 1
                    }

                    // Set the layout manager for the RecyclerView
                    routesRV.layoutManager = LinearLayoutManager(this@RouteActivity)
                    routesRV.setHasFixedSize(true)

                    val adapter = ObjectRoutesAdapter(this@RouteActivity, filteredData, lifecycleScope)
                    adapter.setOnItemClickListener(object : ObjectRoutesAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int, item: List<Data>, isFocused: Boolean) {
                            if (isFocused) {
                                val currentItem = item[position]
                                submitRequest(currentItem)
                            } else {
                                submitRequest(routePoints!!)
                            }
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }
                    })

                    routesRV.adapter = adapter
                } else {
                    routesRV.adapter = null
                    progressBar.visibility = View.GONE
                    noDataTV.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun fillRoutes(data: Data): Routes {
        val routesList = mutableListOf<Data>()
        var currentPoints = mutableListOf<com.example.control24projectmain.Point>()

        for (point in data.points) {
            if (currentPoints.isEmpty() || (point.speed != 0)) {
                currentPoints.add(point)
            } else {
                routesList.add(Data(currentPoints.toList()))
                currentPoints = mutableListOf(point)
            }
        }

        if (currentPoints.isNotEmpty()) {
            routesList.add(Data(currentPoints.toList()))
        }

        return Routes(routesList)
    }

    private fun submitRequest(routePoints: Data) {
        val drivingOptions = DrivingOptions().apply {
            avoidPoorConditions = false
            avoidUnpaved = false
            avoidTolls = false
            routesCount = 1
        }
        val vehicleOptions = VehicleOptions()
        val requestPoints = ArrayList<RequestPoint>()
        mapObjectsColl.clear()

        for (point in routePoints.points) {
            val requestPoint = RequestPoint(Point(point.lat, point.lon), RequestPointType.WAYPOINT, null)
            requestPoints.add(requestPoint)
        }
        Log.i("HDFJSDHFK", requestPoints.toString())

        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)

        val firstPoint = routePoints.points.first()
        firstPoint.let {
            val placemark = mapObjectsColl.addPlacemark(Point(it.lat, it.lon))
            val drawable = ContextCompat.getDrawable(this, R.drawable.icon_route_start)
            val bitmap = createBitmapFromDrawable(drawable!!)
            placemark.setIcon(ImageProvider.fromBitmap(bitmap), IconStyle().setAnchor(PointF(.5f, .8f)))
        }

        val lastPoint = routePoints.points.last()
        lastPoint.let {
            val placemark = mapObjectsColl.addPlacemark(Point(it.lat, it.lon))
            val drawable = ContextCompat.getDrawable(this, R.drawable.icon_route_end)
            val bitmap = createBitmapFromDrawable(drawable!!)
            placemark.setIcon(ImageProvider.fromBitmap(bitmap), IconStyle().setAnchor(PointF(.5f, .8f)))
        }

        val routeCenter = Point(
            (firstPoint.lat + lastPoint.lat) / 2,
            (firstPoint.lon + lastPoint.lon) / 2
        )

        /*val screenWidth = yandexMVRoute.width
        val screenHeight = yandexMVRoute.height

        val padding = 250

        val latDiff = abs(firstPoint.lat - lastPoint.lat)
        val lonDiff = abs(firstPoint.lon - lastPoint.lon)

        val zoomLevel = calculateZoomLevel(screenWidth - (2 * padding), screenHeight - (2 * padding), latDiff, lonDiff)*/

        yandexMVRoute.map.move(
            CameraPosition(routeCenter, 12f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    /*private fun calculateZoomLevel(screenWidth: Int, screenHeight: Int, latDiff: Double, lonDiff: Double): Float {
        val zoomScale = minOf(screenWidth.toDouble() / lonDiff, screenHeight.toDouble() / latDiff)
        val zoomLevel = (log2(zoomScale) - 1).toFloat()
        return maxOf(0f, zoomLevel)
    }*/

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        if (routes.isNotEmpty()) {
            val route = routes[0]
            val polylineMapObject = mapObjectsColl.addPolyline(route.geometry)
            polylineMapObject.setStrokeColor(MaterialColors.getColor(this@RouteActivity, R.attr.polylineColor, Color.BLUE))
        }
    }

    override fun onDrivingRoutesError(error: Error) {
        var errorMessage = "Unknown error"
        if (error is RemoteError) {
            errorMessage = "Remote server error"
        } else if (error is NetworkError) {
            errorMessage = "Network error"
        }
        Log.i("DRIVING ROUTE ERROR", errorMessage)
    }

    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.scale_in, R.anim.slide_out_left)
    }

    private fun formattedDateToDatabase(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("yyyy/M/d%20HH:mm:ss", Locale("ru", "RU"))
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return dateFormat.format(calendar.time)
    }

    private fun formattedDate(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("d MMMM, yyyy", Locale("ru", "RU"))

        val formatSymbols = dateFormat.dateFormatSymbols
        formatSymbols.months = formatSymbols.months.map { it.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(
                Locale.getDefault()
            ) else char.toString()
        } }.toTypedArray()
        dateFormat.dateFormatSymbols = formatSymbols

        return dateFormat.format(calendar.time)
    }

    private fun formattedDayOfWeek(calendar: Calendar): String {
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("ru", "RU"))
        return dayOfWeekFormat.format(calendar.time)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    private fun openTimePicker(carId: Int, calendar: Calendar, progressBar: ProgressBar) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Выберите дату")
            .setSelection(calendar.timeInMillis)
            .setTheme(R.style.CustomMaterialDatePickerStyle)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.before(today))
                    .build()
            )
            .build()
        datePicker.show(supportFragmentManager, "DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            val selectedCalendar = calendar.apply {
                time = selectedDate
            }
            dateTopTV.text = formattedDate(selectedCalendar)
            dateBottomTV.text = formattedDayOfWeek(selectedCalendar)

            val (calendar1, calendar2) = getCalendars(selectedCalendar)
            requestToDatabase(carId, formattedDateToDatabase(calendar1), formattedDateToDatabase(calendar2), progressBar)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openPeriodTimePicker(carId: Int, startCalendar: Calendar, endCalendar: Calendar, progressBar: ProgressBar) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Выберите период")
            .setTheme(R.style.CustomMaterialDatePickerStyle)
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.before(today))
                    .build()
            )
            .build()
        datePicker.show(supportFragmentManager, "DATE_RANGE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = Date(selection.first)
            val endDate = Date(selection.second)

            val selectedStartCalendar = startCalendar.apply {
                time = startDate
            }

            val selectedEndCalendar = endCalendar.apply {
                time = endDate
            }

            dateBottomTV.text = "${formattedDate(selectedStartCalendar)} - ${formattedDate(selectedEndCalendar)}"
            val (calendar1, calendar2) = getCalendarsRange(selectedStartCalendar, selectedEndCalendar)
            requestToDatabase(carId, formattedDateToDatabase(calendar1), formattedDateToDatabase(calendar2), progressBar)
        }
    }

    private fun createBitmapFromDrawable(drawable: Drawable): Bitmap {
        // Get the intrinsic dimensions of the drawable
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        // Create a bitmap with the intrinsic dimensions and ARGB_8888 configuration
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Set the bounds of the drawable to match the bitmap dimensions
        drawable.setBounds(0, 0, width, height)

        // Draw the drawable onto the canvas
        drawable.draw(canvas)

        // Return the generated bitmap
        return bitmap
    }
}