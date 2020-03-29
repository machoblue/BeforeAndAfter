package org.macho.beforeandafter.shared.data.restoreimage

interface RestoreImageRepository {
    fun getRestoreImages(onComplete: (List<RestoreImage>) -> Unit)
    fun getRestoreImage(restoreImageName: String, onComplete: (RestoreImage?) -> Unit)
    fun insertOrUpdate(record: RestoreImage, onComplete: (() -> Unit)? = null)
    fun deleteAll(onComplete: (() -> Unit)? = null)
}