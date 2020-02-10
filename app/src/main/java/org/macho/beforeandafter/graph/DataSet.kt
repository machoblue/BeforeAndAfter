package org.macho.beforeandafter.graph

data class DataSet(val type: DataType, val dataList: List<Data>, val color: Int)

data class Data(val time: Long, val value: Float)

enum class DataType(val index: Int) {
    LEFT(0), RIGHT(1)
}

data class Legend(val name: String, val color: Int)