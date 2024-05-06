package ru.control24.tracking.monitoring.domain.objects.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.control24.tracking.R

sealed class CarCategory(
    @StringRes val definitionRes: Int,
    @DrawableRes val iconRes: Int
) {
    data object House: CarCategory(
        definitionRes = R.string.house,
        iconRes = R.drawable.icon_house_fill0
    )

    data object Car: CarCategory(
        definitionRes = R.string.car,
        iconRes = R.drawable.icon_directions_car_fill0
    )

    data object Cargo: CarCategory(
        definitionRes = R.string.cargo,
        iconRes = R.drawable.icon_local_shipping_fill0
    )

    data object Special: CarCategory(
        definitionRes = R.string.special,
        iconRes = R.drawable.icon_directions_car_fill0
    )

    data object VIP: CarCategory(
        definitionRes = R.string.vip,
        iconRes = R.drawable.icon_directions_car_fill0
    )

    data object Passenger: CarCategory(
        definitionRes = R.string.passenger,
        iconRes = R.drawable.icon_directions_car_fill0
    )

    data object Mixer: CarCategory(
        definitionRes = R.string.mixer,
        iconRes = R.drawable.icon_directions_car_fill0
    )


    data object FrontLoader: CarCategory(
        definitionRes = R.string.front_loader,
        iconRes = R.drawable.icon_front_loader_fill0
    )

    data object Van: CarCategory(
        definitionRes = R.string.van,
        iconRes = R.drawable.icon_airport_shuttle_fill0
    )


    data object TruckCrane: CarCategory(
        definitionRes = R.string.truck_crane,
        iconRes = R.drawable.icon_auto_towing_fill0
    )

    data object EKG: CarCategory(
        definitionRes = R.string.ekg,
        iconRes = R.drawable.icon_directions_car_fill0
    )


    data object BelAZ: CarCategory(
        definitionRes = R.string.belaz,
        iconRes = R.drawable.icon_directions_car_fill0
    )

    data object Excavator: CarCategory(
        definitionRes = R.string.excavator,
        iconRes = R.drawable.icon_directions_car_fill0
    )


    data object Loader: CarCategory(
        definitionRes = R.string.loader,
        iconRes = R.drawable.icon_forklift_fill0
    )

    data object Tractor: CarCategory(
        definitionRes = R.string.tractor,
        iconRes = R.drawable.icon_agriculture_fill0
    )

    data object TractorExcavator: CarCategory(
        definitionRes = R.string.tractor_excavator,
        iconRes = R.drawable.icon_front_loader_fill0
    )

    data object FuelTanker: CarCategory(
        definitionRes = R.string.fuel_tanker,
        iconRes = R.drawable.icon_local_shipping_fill0
    )

    data object Tractor2: CarCategory(
        definitionRes = R.string.tractor2,
        iconRes = R.drawable.icon_agriculture_fill0
    )

    data object TractorExcavator2: CarCategory(
        definitionRes = R.string.tractor_excavator2,
        iconRes = R.drawable.icon_front_loader_fill0
    )

    data object Bus: CarCategory(
        definitionRes = R.string.bus,
        iconRes = R.drawable.icon_directions_bus_fill0
    )

    data object Bulldozer: CarCategory(
        definitionRes = R.string.bulldozer,
        iconRes = R.drawable.icon_directions_car_fill0
    )

    companion object {
        fun fromDigit(code: Int): CarCategory {
            return when(code) {
                1 -> House
                2 -> Car
                3 -> Cargo
                4 -> Special
                5 -> VIP
                101 -> Passenger
                102 -> Mixer
                103 -> FrontLoader
                104 -> Van
                105 -> TruckCrane
                106 -> EKG
                107 -> BelAZ
                108 -> Excavator
                109 -> Loader
                110 -> Tractor
                111 -> TractorExcavator
                112 -> FuelTanker
                113 -> Tractor2
                114 -> TractorExcavator2
                115 -> Bus
                116 -> Bulldozer
                else -> Car
            }
        }
    }
}