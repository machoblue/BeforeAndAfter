package org.macho.beforeandafter.record

data class RecordItem(
    var date: Long,
    var yearText: String,
    var dateText: String,
    var timeText: String,
    var weight: Float,
    var weightDiff: Float?,
    var rate: Float,
    var rateDiff: Float?,
    var frontImagePath: String?,
    var sideImagePath: String?,
    var memo: String?
) {
}