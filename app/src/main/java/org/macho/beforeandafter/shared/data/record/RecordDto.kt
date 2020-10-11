package org.macho.beforeandafter.shared.data.record

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class RecordDto(): RealmObject() {
    @PrimaryKey
    var date = Date().time
    var weight = 0f
    var rate = 0f
    var frontImagePath: String? = null
    var sideImagePath : String? = null
    var otherImagePath1: String? = null
    var otherImagePath2: String? = null
    var otherImagePath3: String? = null
    var memo = ""

    constructor(
            date: Long,
            weight: Float = 0f,
            rate: Float = 0f,
            frontImagePath: String? = null,
            sideImagePath: String? = null,
            otherImagePath1: String? = null,
            otherImagePath2: String? = null,
            otherImagePath3: String? = null,
            memo: String = ""
    ): this() {
        this.date = date
        this.weight = weight
        this.rate = rate
        this.frontImagePath = frontImagePath
        this.sideImagePath = sideImagePath
        this.otherImagePath1 = otherImagePath1
        this.otherImagePath2 = otherImagePath2
        this.otherImagePath3 = otherImagePath3
        this.memo = memo
    }
}