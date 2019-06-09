package org.macho.beforeandafter.shared.data

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*
import javax.inject.Singleton

@Singleton
class RecordDaoRealm: RecordDao {
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
                    result.memo)
        }
    }

    override fun register(record: Record) {
        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                var registered = realm.createObject(RecordDto::class.java, record.date)
                registered.weight = record.weight
                registered.rate = record.rate
                registered.frontImagePath = record.frontImagePath
                registered.sideImagePath = record.sideImagePath
                registered.memo = record.memo
            }
        }
    }

    override fun update(record: Record) {
        Realm.getDefaultInstance().use {
            it.executeTransaction { realm ->
                realm.copyToRealmOrUpdate(RecordDto(
                        record.date,
                        record.weight,
                        record.rate,
                        record.frontImagePath,
                        record.sideImagePath,
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

open class RecordDto(): RealmObject() {
    @PrimaryKey
    var date = Date().time
    var weight = 0f
    var rate = 0f
    var frontImagePath: String? = null
    var sideImagePath : String? = null
    var memo = ""

    constructor(date: Long, weight: Float = 0f, rate: Float = 0f, frontImagePath: String? = null, sideImagePath: String? = null, memo: String = ""): this() {
        this.date = date
        this.weight = weight
        this.rate = rate
        this.frontImagePath = frontImagePath
        this.sideImagePath = sideImagePath
        this.memo = memo
    }
}