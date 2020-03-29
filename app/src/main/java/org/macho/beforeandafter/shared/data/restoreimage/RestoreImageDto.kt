package org.macho.beforeandafter.shared.data.restoreimage

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RestoreImageDto(): RealmObject() {
    @PrimaryKey
    var imageFileName = ""
    var driveFileId = ""
    var status = 0

    constructor(imageFileName: String, driveFileId: String, status: Int = 0): this() {
        this.imageFileName = imageFileName
        this.driveFileId = driveFileId
        this.status = 0
    }

    constructor(restoreImage: RestoreImage): this() {
        this.imageFileName = restoreImage.imageFileName
        this.driveFileId = restoreImage.driveFileId
        this.status = restoreImage.status.rawValue
    }

    val restoreImage: RestoreImage
        get() = RestoreImage(imageFileName, driveFileId, RestoreImage.Status.values().first { status == it.rawValue })
}