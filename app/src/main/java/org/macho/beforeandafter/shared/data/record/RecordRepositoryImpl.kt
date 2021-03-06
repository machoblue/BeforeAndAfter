package org.macho.beforeandafter.shared.data.record

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

    override fun getRecord(date: Long, onComplete: (Record?) -> Unit) {
        appExecutors.diskIO.execute {
            val record = recordDao.find(date)
            appExecutors.mainThread.execute {
                onComplete(record)
            }
        }
    }

    override fun register(record: Record, onComplete: (() -> Unit)?) {
        appExecutors.diskIO.execute {
            recordDao.createOrUpdate(record)
            appExecutors.mainThread.execute {
                if (onComplete == null) {
                    return@execute
                }
                onComplete()
            }
        }
    }

    override fun update(record: Record, onComplete: (() -> Unit)?) {
        appExecutors.diskIO.execute {
            recordDao.createOrUpdate(record)
            appExecutors.mainThread.execute {
                if (onComplete == null) {
                    return@execute
                }
                onComplete()
            }
        }
    }

    override fun delete(date: Long, onComplete: (() -> Unit)?) {
        appExecutors.diskIO.execute {
            recordDao.delete(date)
            appExecutors.mainThread.execute {
                if (onComplete == null) {
                    return@execute
                }
                onComplete()
            }
        }
    }

}