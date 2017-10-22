package org.macho.beforeandafter.record;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by yuukimatsushima on 2017/10/21.
 */

public class ImageCache {
    private LruCache<String, Bitmap> lruCache;

    public ImageCache() {
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
        int cacheSize = (int) (maxMemory / 8);

        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap value) {
                return value.getAllocationByteCount() / 1024;
            }
        };
    }

    public void put(String key, Bitmap image) {
        lruCache.put(key, image);
    }

    public Bitmap get(String key) {
        return lruCache.get(key);
    }

    public void clear() {
        lruCache.evictAll();
    }
}
