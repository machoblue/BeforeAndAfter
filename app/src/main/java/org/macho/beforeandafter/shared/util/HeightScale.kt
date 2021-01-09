package org.macho.beforeandafter.shared.util

import android.content.Context
import org.macho.beforeandafter.R

enum class HeightUnitType(val stringResourceId: Int, val multiplier: Double) {
    CM(R.string.height_unit_cm, 1.0),
    IN(R.string.height_unit_in, 0.393701)
}

class HeightScale(val context: Context) {
    val heightUnitType: HeightUnitType
    val heightUnitText: String

    init {
        val heightUnitIndex = SharedPreferencesUtil.getInt(context, SharedPreferencesUtil.Key.HEIGHT_UNIT)
        heightUnitType =  HeightUnitType.values().getOrElse(heightUnitIndex) { _ -> HeightUnitType.CM }
        heightUnitText = context.getString(heightUnitType.stringResourceId)
    }

    fun convertFromCm(heightInCm: Float): Float {
        val convertedValue = heightInCm * heightUnitType.multiplier
        return convertedValue.toFloat()
    }

    fun convertToCm(height: Float): Float {
        val convertedValue = height / heightUnitType.multiplier
        return convertedValue.toFloat()
    }
}