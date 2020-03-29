package org.macho.beforeandafter.shared.data.restoreimage


data class RestoreImage(val imageFileName: String, val driveFileId: String, var status: Status = Status.TODO) {

    enum class Status(val rawValue: Int)  {
        TODO(0),
        PROCESSING(1),
        COMPLETE(2)
    }
}