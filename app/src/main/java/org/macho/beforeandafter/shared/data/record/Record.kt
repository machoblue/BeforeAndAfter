package org.macho.beforeandafter.shared.data.record

import java.util.*

data class Record(var date: Long = Date().time,
             var weight: Float = 0f,
             var rate: Float = 0f,
             var frontImagePath: String? = null,
             var sideImagePath: String? = null,
             var memo: String = "") {

}