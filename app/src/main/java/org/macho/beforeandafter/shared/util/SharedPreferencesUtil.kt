package org.macho.beforeandafter.shared.util

import android.content.Context
import android.preference.PreferenceManager

object SharedPreferencesUtil {
    enum class Key(val string: String) {
        LATEST_WEIGHT("latestWeight"),
        LATEST_RATE("latestRate"),
        LATEST_WATCH_REWARDED_AD("latest_watch_rewarded_ad"),
        TIME_OF_LATEST_RECORD("time_of_latest_record"),
        PIN("pin"),
        GRAPH_SELECTION("graph_selection"),

        LAST_SURVEY_DIALOG_TIME("last_survey_dialog_time"),

        ALARM_ENABLED("alarm_enabled"),
        ALARM_HOUR_OF_DAY("alarm_hour_of_day"),
        ALARM_MINUTE("alarm_minute"),

        NEVER_DISPLAY_ALARM_SETTING_DIALOG("never_display_alarm_setting_dialog"),

        HIDE_SHADOW_PHOTO("hide_shadow_photo"),

        GOAL_WEIGHT("GOAL_WEIGHT"),

        HIDE_WEIGHT_SUMMARY("hide_weight_summary"),
        HIDE_WEIGHT_PROGRESS("hide_weight_progress")
    }

    fun getFloat(context: Context, key: Key): Float {
        return PreferenceManager.getDefaultSharedPreferences(context).getFloat(key.string, 0.0f)
    }

    fun setFloat(context: Context, key: Key, value: Float) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putFloat(key.string, value).apply()
    }

    fun getLong(context: Context, key: Key): Long {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key.string, 0)
    }

    fun setLong(context: Context, key: Key, value: Long) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key.string, value).apply()
    }

    fun setLongImmediately(context: Context, key: Key, value: Long) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(key.string, value).commit()
    }

    fun getBoolean(context: Context, key: Key): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key.string, false)
    }

    fun setBoolean(context: Context, key: Key, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key.string, value).apply()
    }

    fun getString(context: Context, key: Key): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key.string, "")!!
    }

    fun setString(context: Context, key: Key, value: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key.string, value).apply()
    }

    fun getInt(context: Context, key: Key): Int {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key.string, 0)
    }

    fun setInt(context: Context, key: Key, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key.string, value).apply()
    }

}

