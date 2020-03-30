package org.macho.beforeandafter.shared.data.restoreimage

import io.realm.Realm

class RestoreImageDaoImpl: RestoreImageDao {
    override fun findAll(): List<RestoreImage> {
        val restoreImages = mutableListOf<RestoreImage>()
        Realm.getDefaultInstance().use {
            for (restoreImageDto in it.where(RestoreImageDto::class.java).findAll()) {
                restoreImages.add(restoreImageDto.restoreImage)
            }
        }
        return restoreImages
    }

    override fun find(imageFileName: String): RestoreImage? {
        Realm.getDefaultInstance().use {
            return it.where(RestoreImageDto::class.java)
                    .equalTo("imageFileName", imageFileName)
                    .findFirst()?.restoreImage
        }
    }

    override fun insertOrUpdate(restoreImage: RestoreImage) {
        Realm.getDefaultInstance().executeTransaction {
            it.copyToRealmOrUpdate(RestoreImageDto(restoreImage))
        }
    }

    override fun delete(imageFileName: String) {
        Realm.getDefaultInstance().executeTransaction {
            it.where(RestoreImageDto::class.java)
                    .equalTo("imageFileName", imageFileName)
                    .findFirst()?.deleteFromRealm()
        }
    }

    override fun deleteAll() {
        Realm.getDefaultInstance().executeTransaction {
            it.where(RestoreImageDto::class.java).findAll().forEach { dto ->
                dto.deleteFromRealm()
            }
        }
    }
}