package org.macho.beforeandafter.gallery

import java.io.Serializable
import java.util.*

class GalleryPhoto(val fileName: String, val dateTime: Date, val weight: Float?, val rate: Float?): Serializable {
}