package com.example.control24projectmain.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
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
import com.example.control24projectmain.Data
import com.example.control24projectmain.FirstResponse
import com.example.control24projectmain.HttpRequestHelper
import com.example.control24projectmain.R
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
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

class RouteActivity: AppCompatActivity(), DrivingSession.DrivingRouteListener {

    private lateinit var binding: ActivityRouteBinding
    private lateinit var yandexMVRoute: MapView
    private lateinit var routePoints: Data
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
                    dateCL.isClickable = false
                    dateTopTV.text = resources.getString(R.string.pick_range)
                    if (startCalendar.get(Calendar.DATE) != Calendar.getInstance().get(Calendar.DATE)) {
                        dateBottomTV.text = resources.getString(R.string.range_template, formattedDate(startCalendar), formattedDate(endCalendar))
                    } else {
                        dateBottomTV.text = resources.getString(R.string.range)
                    }
                    leftArrowCL.visibility = View.GONE
                    rightArrowCL.visibility = View.GONE

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

                    dateChangeCL.isClickable = false
                    dateCL.isClickable = true
                    dateTopTV.text = formattedDate(calendar)
                    dateBottomTV.text = formattedDayOfWeek(calendar)
                    leftArrowCL.visibility = View.VISIBLE
                    rightArrowCL.visibility = View.VISIBLE

                    mapObjectsColl.clear()
                    val (calendar1, calendar2) = getCalendars(calendar)
                    requestToDatabase(carId, formattedDateToDatabase(calendar1), formattedDateToDatabase(calendar2), progressBar)
                }
            }
        }

        leftArrowCL.setOnClickListener {
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
                Toast.LENGTH_SHORT,
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
                Log.i("HDFJSDHFK", httpResponseSecond)
                routePoints = gson.fromJson(httpResponseSecond, Data::class.java)
            } catch (e: Exception) {
                StyleableToast.makeText(
                    this@RouteActivity,
                    e.toString(),
                    Toast.LENGTH_LONG,
                    R.style.CustomStyleableToast
                ).show()
            } finally {
                if (routePoints.points.isNotEmpty()) {
                    submitRequest()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun submitRequest() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        val requestPoints = ArrayList<RequestPoint>()
        mapObjectsColl.clear()

        for (point in routePoints.points) {
            val requestPoint = RequestPoint(Point(point.lat, point.lon), RequestPointType.WAYPOINT, null)
            requestPoints.add(requestPoint)
        }

        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)

        val firstPoint = routePoints.points.first()
        firstPoint.let {
            val placemark = mapObjectsColl.addPlacemark(Point(it.lat, it.lon))
            placemark.setIcon(ImageProvider.fromResource(this, R.drawable.icon))
        }

        val lastPoint = routePoints.points.last()
        lastPoint.let {
            val placemark = mapObjectsColl.addPlacemark(Point(it.lat, it.lon))
            placemark.setIcon(ImageProvider.fromResource(this, R.drawable.icon))
        }

        val routeCenter = Point(
            (firstPoint.lat + lastPoint.lat) / 2,
            (firstPoint.lon + lastPoint.lon) / 2
        )

        yandexMVRoute.map.move(
            CameraPosition(routeCenter, 12.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        for (route in routes) {
            mapObjectsColl.addPolyline(route.geometry)
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
}