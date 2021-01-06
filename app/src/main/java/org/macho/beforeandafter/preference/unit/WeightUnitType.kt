package org.macho.beforeandafter.preference.unit

import org.macho.beforeandafter.R

enum class WeightUnitType(val stringResourceId: Int, val multiplier: Double) {
    KG(R.string.weight_unit_kg, 1.0), // kgがベース。データはkgで保持する。
    LB(R.string.weight_unit_lb, 0.45359237)
}