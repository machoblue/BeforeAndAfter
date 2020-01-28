package org.macho.beforeandafter.graph

data class DataSet(val type: DataType, val dataList: List<Data>)

data class Data(val time: Long, val value: Float)