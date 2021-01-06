package org.macho.beforeandafter.shared.util

import android.content.Context
import org.macho.beforeandafter.preference.unit.WeightUnitType

class WeightScale(context: Context) {

    private val weightUnitType: WeightUnitType
    val weightUnitText: String

    init {
        val weightUnitIndex = SharedPreferencesUtil.getInt(context, SharedPreferencesUtil.Key.WEIGHT_UNIT)
        weightUnitType =  WeightUnitType.values().getOrElse(weightUnitIndex) { _ -> WeightUnitType.KG }
        weightUnitText = context.getString(weightUnitType.stringResourceId)
    }

    fun convertFromKg(weightInKg: Float): Float {
        val convertedValue = weightInKg * weightUnitType.multiplier
        return convertedValue.toFloat()
    }

    fun convertToKg(weight: Float): Float {
        val convertedValue = weight / weightUnitType.multiplier
        return convertedValue.toFloat()
    }
}