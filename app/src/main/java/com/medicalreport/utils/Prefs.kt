package com.medicalreport.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.medicalreport.base.MainApplication


class Prefs {
    private val AUTH_TOKEN = "_AUTH_TOKEN"
    private val DEVICE_TOKEN = "DEVICE_TOKEN"
    private val USER_EMAIL = "USER_EMAIL"
    private val USER_NAME = "USER_NAME"
    private val USER_SPECIALIZED = "USER_SPECIALIZED"
    private val NUMBER = "NUMBER"
    private val REMEMBER = " REMEMBER"
    private val FIRSTRUN = " FIRSTRUN"
    private val DOCTORID = " DOCTORID"
    private val PATIENTID = " PATIENTID"
    private val HOSPITALNAME = " HOSPITALNAME"
    private val HOSPITALADDRESS = " HOSPITALADDRESS"
    private val HOSPITALPHONE = " HOSPITALPHONE"

    private val PREFS_NAME = "CameraPrefs"
    private val KEY_RESOLUTION = "camera_resolution"
    private val KEY_BRIGHTNESS = "camera_brightness"
    private val KEY_CONTRAST = "camera_contrast"
    private val KEY_WHITE_BALANCE = "camera_white_balance"
    private val KEY_RECORD_FORMAT = "camera_record_format"


    private var sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(MainApplication.get().applicationContext)

    init {
        instance = this
    }

    val gson = Gson()

    companion object {
        private var instance: Prefs? = null
        fun init(): Prefs {
            if (instance == null) {
                instance = Prefs()
            }
            return instance!!
        }
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    var deviceToken: String
        get() {
            return sharedPreferences.getString(DEVICE_TOKEN, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(DEVICE_TOKEN, value).apply()
        }

    var doctorId: Int
        get() {
            return sharedPreferences.getInt(DOCTORID, 0) ?: 0
        }
        set(value) {
            sharedPreferences.edit().putInt(DOCTORID, value).apply()
        }
    var patientId: Int
        get() {
            return sharedPreferences.getInt(PATIENTID, 0) ?: 0
        }
        set(value) {
            sharedPreferences.edit().putInt(PATIENTID, value).apply()
        }

    var email: String
        get() {
            return sharedPreferences.getString(USER_EMAIL, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(USER_EMAIL, value).apply()
        }

    var name: String
        get() {
            return sharedPreferences.getString(USER_NAME, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(USER_NAME, value).apply()
        }

    var specializedIn: String
        get() {
            return sharedPreferences.getString(USER_SPECIALIZED, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(USER_SPECIALIZED, value).apply()
        }

    var number: String
        get() {
            return sharedPreferences.getString(NUMBER, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(NUMBER, value).apply()
        }


    var currentToken: String
        get() {
            return sharedPreferences.getString(AUTH_TOKEN, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(AUTH_TOKEN, value).apply()
        }
    var remeber: Boolean
        get() {
            return sharedPreferences.getBoolean(REMEMBER, false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean(REMEMBER, value).apply()
        }

    var firstRun: Boolean
        get() {
            return sharedPreferences.getBoolean(FIRSTRUN, false)
        }
        set(value) {
            sharedPreferences.edit().putBoolean(FIRSTRUN, value).apply()
        }

    var hospitalName: String
        get() {
            return sharedPreferences.getString(HOSPITALNAME, "") ?: ""

        }
        set(value) {
            sharedPreferences.edit().putString(HOSPITALNAME, value).apply()
        }

    var hospitalAddress: String
        get() {
            return sharedPreferences.getString(HOSPITALADDRESS, "") ?: ""
        }
        set(value) {
            sharedPreferences.edit().putString(HOSPITALADDRESS, value).apply()

        }
    var hospitalPhoneNumber: String
        get() {
            return sharedPreferences.getString(HOSPITALPHONE, "") ?: ""

        }
        set(value) {
            sharedPreferences.edit().putString(HOSPITALPHONE, value).apply()

        }
    var keyResolution: String
        get() {
            return sharedPreferences.getString(KEY_RESOLUTION, "") ?: ""

        }
        set(value) {
            sharedPreferences.edit().putString(KEY_RESOLUTION, value).apply()

        }
    var keyBrightness: Int
        get() {
            return sharedPreferences.getInt(KEY_BRIGHTNESS, 0)

        }
        set(value) {
            sharedPreferences.edit().putInt(KEY_BRIGHTNESS, value).apply()

        }
    var keyContrast: Int
        get() {
            return sharedPreferences.getInt(KEY_CONTRAST, 0)

        }
        set(value) {
            sharedPreferences.edit().putInt(KEY_CONTRAST, value).apply()

        }

    var keyWhiteBalance: String
        get() {
            return sharedPreferences.getString(KEY_WHITE_BALANCE,"")?:""
        }
        set(value) {
            sharedPreferences.edit().putString(KEY_WHITE_BALANCE, value).apply()
        }

}


