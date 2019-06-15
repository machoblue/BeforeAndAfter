package org.macho.beforeandafter.data

import org.macho.beforeandafter.record.Record

interface RecordRepository {
    fun getRecords(with: (List<Record>) -> Unit)
}