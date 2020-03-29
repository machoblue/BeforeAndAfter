package org.macho.beforeandafter.shared.data.restoreimage

import io.realm.Realm

class RestoreImageDaoImpl: RestoreImageDao {
    val realm = Realm.getDefaultInstance()
    override fun findAll(): List<RestoreImage> {
        val restoreImages = mutableListOf<RestoreImage>()
        realm.use {
            for (restoreImageDto in it.where(RestoreImageDto::class.java).findAll()) {
                restoreImages.add(restoreImageDto.restoreImage)
            }
        }
        return restoreImages
    }

    override fun find(imageFileName: String): RestoreImage? {
        realm.use {
            return it.where(RestoreImageDto::class.java)
                    .equalTo("imageFileName", imageFileName)
                    .findFirst()?.restoreImage
        }
    }

    override fun insertOrUpdate(restoreImage: RestoreImage) {
        realm.copyToRealmOrUpdate(RestoreImageDto(restoreImage))
    }

    override fun delete(imageFileName: String) {
        realm.executeTransaction {
            it.where(RestoreImageDto::class.java)
                    .equalTo("imageFileName", imageFileName)
                    .findFirst()?.deleteFromRealm()
        }
    }

    override fun deleteAll() {
        realm.use {
            realm.executeTransaction {
                realm.where(RestoreImageDto::class.java).findAll().forEach {
                    it.deleteFromRealm()
                }
            }
        }
    }
}