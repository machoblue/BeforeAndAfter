package org.macho.beforeandafter.shared.data

import org.macho.beforeandafter.record.Record
import org.macho.beforeandafter.shared.util.AppExecutors
import javax.inject.Inject

class RecordRepositoryImpl @Inject constructor(val recordDao: RecordDao, val appExecutors: AppExecutors): RecordRepository {
    override fun getRecords(onComplete: (List<Record>) -> Unit) {
        appExecutors.diskIO.execute {
            val records = recordDao.findAll()
            appExecutors.mainThread.execute {
                onComplete(records)
            }
        }
    }
}