package com.example.control24projectmain.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        val yandexMapsProvider = binding.yandexMapsMRadioButton
        val osmMapsProvider = binding.osmMapsMRadioButton

        var checkedMapProviderRB = when (UserManager.getSelectedMap(this)) {
            yandexMap -> yandexMapsProvider
            osmMap -> osmMapsProvider
            else -> yandexMapsProvider
        }
        checkedMapProviderRB.isChecked = true
        checkedMapProviderRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))
        checkedMapProviderRB.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.mapProviderRG.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                yandexMapsProvider.id -> {
                    checkedMapProviderRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapProviderRB.typeface = ResourcesCompat.getFont(this, R.font.roboto)
                    yandexMapsProvider.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    yandexMapsProvider.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
                    checkedMapProviderRB = yandexMapsProvider
                    UserManager.saveSelectedMap(this, yandexMap)
                    binding.mapTypeCL.visibility = View.GONE
                    binding.trafficControlsMCheckBox.visibility = View.VISIBLE
                }
                osmMapsProvider.id -> {
                    checkedMapProviderRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapProviderRB.typeface = ResourcesCompat.getFont(this, R.font.roboto)
                    osmMapsProvider.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    osmMapsProvider.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
                    checkedMapProviderRB = osmMapsProvider
                    UserManager.saveSelectedMap(this, osmMap)
                    binding.mapTypeCL.visibility = View.VISIBLE
                    binding.trafficControlsMCheckBox.visibility = View.GONE
                }
            }
        }

        val schemeRB = binding.schemeMRadioButton
        val satelliteRB = binding.satelliteMRadioButton
        val hybridRD = binding.hybridMRadioButton

        var checkedMapTypeRB = when (UserManager.getMapType(this)) {
            SCHEME -> schemeRB
            SATELLITE -> satelliteRB
            HYBRID -> hybridRD
            else -> schemeRB
        }

        checkedMapTypeRB.isChecked = true
        checkedMapTypeRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))
        checkedMapTypeRB.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)

        binding.layersRG.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                schemeRB.id -> {
                    checkedMapTypeRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapTypeRB.typeface = ResourcesCompat.getFont(this, R.font.roboto)
                    schemeRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    schemeRB.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
                    checkedMapTypeRB = schemeRB
                    UserManager.saveMapType(this, SCHEME)
                }
                satelliteRB.id -> {
                    checkedMapTypeRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapTypeRB.typeface = ResourcesCompat.getFont(this, R.font.roboto)
                    satelliteRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    satelliteRB.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
                    checkedMapTypeRB = satelliteRB
                    UserManager.saveMapType(this, SATELLITE)
                }
                hybridRD.id -> {
                    checkedMapTypeRB.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextUncheckedColor, Color.BLACK))
                    checkedMapTypeRB.typeface = ResourcesCompat.getFont(this, R.font.roboto)
                    hybridRD.setTextColor(MaterialColors.getColor(this, R.attr.radioButtonTextCheckedColor, Color.BLACK))
                    hybridRD.typeface = ResourcesCompat.getFont(this, R.font.roboto_medium)
                    checkedMapTypeRB = hybridRD
                    UserManager.saveMapType(this, HYBRID)
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