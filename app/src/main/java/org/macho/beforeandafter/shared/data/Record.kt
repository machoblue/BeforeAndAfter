package org.macho.beforeandafter.shared.data

import java.util.*

class Record {
    var date = Date().time
    var weight = 0f
    var rate = 0f
    var frontImagePath: String? = null
    var sideImagePath : String? = null
    var memo = ""

    constructor(date: Long = Date().time, weight: Float = 0f, rate: Float = 0f, frontImagePath: String? = null, sideImagePath: String? = null, memo: String = "") {
        this.date = date
        this.weight = weight
        this.rate = rate
        this.frontImagePath = frontImagePath
        this.sideImagePath = sideImagePath
        this.memo = memo
    }
}