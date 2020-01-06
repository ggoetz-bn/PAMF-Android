package de.abg.pamf.ui.centergravity

import android.content.Context
import android.content.SharedPreferences

object CogData {

    /**
     * In dieser Klasse stehen nur Daten, die vom Nutzer eingestellt werden.
     * Die Daten, die vom Mikrocontroller gemessen werden stehen in TODO
     */


    private const val NAME = "COG_DATA"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    // Namen der Daten und Default-Werte
    private val TYPE = Pair("TYPE", 1)
    private val DISTANCE_1_2 = Pair("DISTANCE_1_2", 0)
    private val DISTANCE_FRONT = Pair("DISTANCE_FRONT", 0)
    private val DISTANCE_TARGET = Pair("DISTANCE_TARGET", 0)

    private val SCALE_1 = Pair("SCALE_1", 1000)
    private val SCALE_2 = Pair("SCALE_2", 1000)
    private val SCALE_3 = Pair("SCALE_3", 1000)

    fun init(context : Context){
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    var type : Int
        get() = preferences.getInt(TYPE.first, TYPE.second)
        set(value) = preferences.edit().putInt(TYPE.first, value).apply()

    var distance_1_2 : Int
        get() = preferences.getInt(DISTANCE_1_2.first, DISTANCE_1_2.second)
        set(value) = preferences.edit().putInt(DISTANCE_1_2.first, value).apply()

    var distance_front : Int
        get() = preferences.getInt(DISTANCE_FRONT.first, DISTANCE_FRONT.second)
        set(value) = preferences.edit().putInt(DISTANCE_FRONT.first, value).apply()

    var distance_target : Int
        get() = preferences.getInt(DISTANCE_TARGET.first, DISTANCE_TARGET.second)
        set(value) = preferences.edit().putInt(DISTANCE_TARGET.first, value).apply()

    var scale_1 : Int
        get() = preferences.getInt(SCALE_1.first, SCALE_1.second)
        set(value) = preferences.edit().putInt(SCALE_1.first, value).apply()

    var scale_2 : Int
        get() = preferences.getInt(SCALE_2.first, SCALE_2.second)
        set(value) = preferences.edit().putInt(SCALE_2.first, value).apply()

    var scale_3 : Int
        get() = preferences.getInt(SCALE_3.first, SCALE_3.second)
        set(value) = preferences.edit().putInt(SCALE_3.first, value).apply()
}