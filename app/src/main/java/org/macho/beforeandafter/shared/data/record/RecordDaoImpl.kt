package org.macho.beforeandafter.shared.data.record

import io.realm.Realm

class RecordDaoImpl: RecordDao {
    override fun findAll(): List<Record> {
        var records: MutableList<Record>  = mutableListOf()
        Realm.getDefaultInstance().use {
            val results = it.where(RecordDto::class.java)
                    .findAll()
                    .sort("date")

            for (result in results) {
                records.add(Record(
                        result.date,
                        result.weight,
                        result.rate,
                        result.frontImagePath,
                        result.sideImagePath,
                        result.otherImagePath1,
                        result.otherImagePath2,
                        result.otherImagePath3,
                        result.memo))
            }
        }
        return records
    }

    override fun find(date: Long): Record? {
        Realm.getDefaultInstance().use {
            val result = it.where(RecordDto::class.java)
                    .equalTo("date", date)
                    .findAll()
                    .firstOrNull() ?: return null

            return Record(result.date,
                    result.weight,
                    result.rate,
                    result.frontImagePath,
                    result.sideImagePath,
                    result.otherImagePath1,
                    result.otherImagePath2,
                    result.otherImagePath3,
                    result.memo)
        }
    }

    override fun createOrUpdate(record: Record) {
        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                realm.copyToRealmOrUpdate(RecordDto(
                        record.date,
                        record.weight,
                        record.rate,
                        record.frontImagePath,
                        record.sideImagePath,
                        record.otherImagePath1,
                        record.otherImagePath2,
                        record.otherImagePath3,
                        record.memo))
            }
        }
    }

    override fun delete(date: Long) {
        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                val recordDto = realm.where(RecordDto::class.java).equalTo("date", date).findAll().firstOrNull()
                recordDto?.deleteFromRealm()
            }
        }
    }

    override fun deleteAll() {
        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                val results = realm.where(RecordDto::class.java).findAll()
                for (result in results) {
                    result.deleteFromRealm()
                }
            }
        }
    }
}