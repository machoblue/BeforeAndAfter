package org.macho.beforeandafter.shared.data.record

interface RecordDao {
    fun findAll(): List<Record>

    fun find(date: Long): Record?

    fun register(record: Record)

    fun update(record: Record)

    fun delete(date: Long)

    fun deleteAll()
}