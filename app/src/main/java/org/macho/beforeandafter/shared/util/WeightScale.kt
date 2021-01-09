package org.macho.beforeandafter.shared.util

import android.content.Context
import org.macho.beforeandafter.R

enum class WeightUnitType(val stringResourceId: Int, val multiplier: Double) {
    KG(R.string.weight_unit_kg, 1.0), // kgがベース。データはkgで保持する。
    LB(R.string.weight_unit_lb, 2.20462)
}

class WeightScale(context: Context) {

    val weightUnitType: WeightUnitType
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