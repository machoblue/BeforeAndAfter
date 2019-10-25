package org.macho.beforeandafter.shared.util

import android.content.Context
import android.preference.PreferenceManager

object SharedPreferencesUtil {
    enum class Key(val string: String) {
        CAN_BACKUP_AND_RESTORE("canBackupAndRestore"),
        LATEST_WEIGHT("latestWeight"),
        LATEST_RATE("latestRate")
    }
    fun getBoolean(context: Context, key: Key): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key.string, false)
    }
    fun setBoolean(context: Context, key: Key, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key.string, value).apply()
    }

    fun getFloat(context: Context, key: Key): Float {
        return PreferenceManager.getDefaultSharedPreferences(context).getFloat(key.string, 0.0f)
    }

    fun setFloat(context: Context, key: Key, value: Float) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat(key.string, value).apply()
    }
}