package org.macho.beforeandafter.record

import android.graphics.Bitmap
import android.util.LruCache

class ImageCache {
    private var lruCache: LruCache<String, Bitmap>

    init {
        val maxMemory = Runtime.getRuntime().maxMemory() / 1024
        val cacheSize = (maxMemory / 8).toInt()

        lruCache = object: LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String?, value: Bitmap?): Int {
                if (value == null) {
                    return 0
                }
                return value.allocationByteCount / 1024
            }
        }
    }


    fun put(key: String, image: Bitmap) {
        lruCache.put(key, image)
    }

    fun get(key: String): Bitmap? {
        return lruCache.get(key)
    }

    fun clear() {
        lruCache.evictAll()
    }
}