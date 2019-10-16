package org.macho.beforeandafter.shared.util

import android.content.Context
import android.preference.PreferenceManager

object SharedPreferencesUtil {
    enum class Key(val string: String) {
        CAN_BACKUP_AND_RESTORE("canBackupAndRestore")
    }
    fun getBoolean(context: Context, key: Key): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key.string, false)
    }
    fun setBoolean(context: Context, key: Key, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key.string, value).apply()
    }
}