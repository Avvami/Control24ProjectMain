package com.example.control24projectmain.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.control24projectmain.HYBRID
import com.example.control24projectmain.R
import com.example.control24projectmain.SATELLITE
import com.example.control24projectmain.SCHEME
import com.example.control24projectmain.UserManager
import com.example.control24projectmain.databinding.ActivityMapProviderBinding
import com.example.control24projectmain.osmMap
import com.example.control24projectmain.yandexMap
import com.google.android.material.color.MaterialColors

class MapProviderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapProviderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapProviderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (UserManager.getSelectedMap(this)) {
            yandexMap -> {
                binding.mapTypeCL.visibility = View.GONE
                binding.trafficControlsMCheckBox.visibility = View.VISIBLE
            }
            osmMap -> {
                binding.mapTypeCL.visibility = View.VISIBLE
                binding.trafficControlsMCheckBox.visibility = View.GONE
            }
            else -> {
                binding.mapTypeCL.visibility = View.GONE
                binding.trafficControlsMCheckBox.visibility = View.VISIBLE
            }
        }

        val yandexMapsProvider = binding.yandexMapsMToggle
        val osmMapsProvider = binding.osmMapsMToggle

        var checkedMapProviderRB = when (UserManager.getSelectedMap(this)) {
            yandexMap -> yandexMapsProvider
            osmMap -> osmMapsProvider
            else -> yandexMapsProvider
        }
        binding.mapProviderTG.check(checkedMapProviderRB.id)
        checkedMapProviderRB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0)

        binding.mapProviderTG.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    yandexMapsProvider.id -> {
                        checkedMapProviderRB.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        checkedMapProviderRB = yandexMapsProvider
                        checkedMapProviderRB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0)
                        UserManager.saveSelectedMap(this, yandexMap)
                        binding.mapTypeCL.visibility = View.GONE
                        binding.trafficControlsMCheckBox.visibility = View.VISIBLE
                    }
                    osmMapsProvider.id -> {
                        checkedMapProviderRB.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        checkedMapProviderRB = osmMapsProvider
                        checkedMapProviderRB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0)
                        UserManager.saveSelectedMap(this, osmMap)
                        binding.mapTypeCL.visibility = View.VISIBLE
                        binding.trafficControlsMCheckBox.visibility = View.GONE
                    }
                }
            }
        }

        val schemeRB = binding.schemeMButton
        val satelliteRB = binding.satelliteMButton
        val hybridRD = binding.hybridMButton

        var checkedMapTypeRB = when (UserManager.getMapType(this)) {
            SCHEME -> schemeRB
            SATELLITE -> satelliteRB
            HYBRID -> hybridRD
            else -> schemeRB
        }
        binding.layersTG.check(checkedMapTypeRB.id)
        checkedMapTypeRB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0)

        binding.layersTG.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    schemeRB.id -> {
                        checkedMapTypeRB.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        checkedMapTypeRB = schemeRB
                        checkedMapTypeRB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0)
                        UserManager.saveMapType(this, SCHEME)
                    }
                    satelliteRB.id -> {
                        checkedMapTypeRB.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        checkedMapTypeRB = satelliteRB
                        checkedMapTypeRB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0)
                        UserManager.saveMapType(this, SATELLITE)
                    }
                    hybridRD.id -> {
                        checkedMapTypeRB.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        checkedMapTypeRB = hybridRD
                        checkedMapTypeRB.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_check, 0, 0, 0)
                        UserManager.saveMapType(this, HYBRID)
                    }
                }
            }
        }

        binding.zoomButtonsMCheckBox.isChecked = UserManager.getZoomControlsState(this)

        binding.zoomButtonsMCheckBox.setOnCheckedChangeListener { _, isChecked ->
            UserManager.saveZoomControlsState(this, isChecked)
        }

        binding.trafficControlsMCheckBox.isChecked = UserManager.getTrafficControlsState(this)

        binding.trafficControlsMCheckBox.setOnCheckedChangeListener { _, isChecked ->
            UserManager.saveTrafficControlsState(this, isChecked)
        }

        // Go back when click on textView
        binding.mapProviderBackCL.setOnClickListener {
            onBackPressed()
        }
    }

    @Deprecated("This method is deprecated. Use the new onBackPressedDispatcher instead.")
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.scale_in, R.anim.slide_out_left)
    }
}