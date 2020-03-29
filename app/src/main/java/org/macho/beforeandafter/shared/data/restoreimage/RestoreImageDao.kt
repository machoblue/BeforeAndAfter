package org.macho.beforeandafter.shared.data.restoreimage

interface RestoreImageDao {

    fun findAll(): List<RestoreImage>

    fun find(imageFileName: String): RestoreImage?

    fun insertOrUpdate(restoreImage: RestoreImage)

    fun delete(imageFileName: String)

    fun deleteAll()
}