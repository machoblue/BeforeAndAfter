package org.macho.beforeandafter.shared.data.restoreimage

import org.macho.beforeandafter.shared.util.AppExecutors
import javax.inject.Inject

class RestoreImageRepositoryImpl @Inject constructor(val restoreImageDao: RestoreImageDao, val appExecutors: AppExecutors): RestoreImageRepository {
    override fun getRestoreImages(onComplete: (List<RestoreImage>) -> Unit) {
        appExecutors.diskIO.execute {
            val restoreImages = restoreImageDao.findAll()
            appExecutors.mainThread.execute {
                onComplete(restoreImages)
            }
        }
    }

    override fun getRestoreImage(imageFileName: String, onComplete: (RestoreImage?) -> Unit) {
        appExecutors.diskIO.execute {
            val restoreImage = restoreImageDao.find(imageFileName)
            appExecutors.mainThread.execute {
                onComplete(restoreImage)
            }
        }
    }

    override fun insertOrUpdate(restoreImage: RestoreImage, onComplete: (() -> Unit)?) {
        appExecutors.diskIO.execute {
            restoreImageDao.insertOrUpdate(restoreImage)
            appExecutors.mainThread.execute {
                onComplete?.invoke()
            }
        }
    }

    override fun deleteAll(onComplete: (() -> Unit)?) {
        appExecutors.diskIO.execute {
            restoreImageDao.deleteAll()
            appExecutors.mainThread.execute {
                onComplete?.invoke()
            }
        }
    }
}