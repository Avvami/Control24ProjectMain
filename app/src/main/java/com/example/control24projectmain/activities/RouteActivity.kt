package com.example.control24projectmain.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsetsController
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import androidx.lifecycle.lifecycleScope
import com.example.control24projectmain.Data
import com.example.control24projectmain.FirstResponse
import com.example.control24projectmain.HttpRequestHelper
import com.example.control24projectmain.MapsConfig
import com.example.control24projectmain.R
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityRouteBinding
import com.example.control24projectmain.isDarkModeEnabled
import com.example.control24projectmain.yandexMap
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.ArrayList

class RouteActivity: AppCompatActivity(), DrivingSession.DrivingRouteListener {

    private lateinit var binding: ActivityRouteBinding
    private lateinit var yandexMVRoute: MapView
    private var mapProvider: String = yandexMap
    private val mapsConfig = MapsConfig()
    private lateinit var routePoints: Data
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var mapObjectsColl: MapObjectCollection
    private lateinit var drivingRouter: DrivingRouter
    private lateinit var drivingSession: DrivingSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the maps
        mapsConfig.mapsInitialization(this)

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

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (UserManager.getZoomControlsState(this)) {
            binding.zoomCL.visibility = View.VISIBLE
        } else {
            binding.zoomCL.visibility = View.GONE
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

        val carId = intent.getIntExtra("carId", -1)
        val carName = intent.getStringExtra("carName")
        carNameTV.text = carName

        if (carId == -1) {
            StyleableToast.makeText(
                this@RouteActivity,
                "Неизвестная ошибка",
                Toast.LENGTH_SHORT,
                R.style.CustomStyleableToast
            ).show()
        } else {
            lifecycleScope.launch {
                try {
                    val loginCredentials = UserManager.getLoginCredentials(this@RouteActivity)
                    val httpResponseFirst = HttpRequestHelper.makeHttpRequest(
                        "http://91.193.225.170:8012/login2&${loginCredentials?.first}&${loginCredentials?.second}"
                    )
                    val gson = Gson()
                    val firstResponseData = gson.fromJson(httpResponseFirst, FirstResponse::class.java)
                    val httpResponseSecond = HttpRequestHelper.makeHttpRequest(
                        "http://91.193.225.170:8012/route2&${firstResponseData.key}&$carId&2023/05/23%2017:00:00&2023/05/24%2017:00:00"
                    )
                    routePoints = gson.fromJson(httpResponseSecond, Data::class.java)
                    //binding.text.text = httpResponseSecond
                } catch (e: Exception) {
                    StyleableToast.makeText(
                        this@RouteActivity,
                        e.toString(),
                        Toast.LENGTH_LONG,
                        R.style.CustomStyleableToast
                    ).show()
                } finally {
                    // Some code

                    submitRequest()
                }
            }
        }

        mapProvider = UserManager.getSelectedMap(this).toString()

        binding.routeBackCL.setOnClickListener {
            onBackPressed()
        }
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
        mapsConfig.mapsOnStart(yandexMVRoute)

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

    private fun submitRequest() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()
        val requestPoints = ArrayList<RequestPoint>()
        for (point in routePoints.points) {
            val requestPoint = RequestPoint(Point(point.lat, point.lon), RequestPointType.WAYPOINT, null)
            requestPoints.add(requestPoint)
        }
        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this)
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
        Log.i("HDFJSDHFK", errorMessage)
    }

    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.scale_in, R.anim.slide_out_left)
    }
}