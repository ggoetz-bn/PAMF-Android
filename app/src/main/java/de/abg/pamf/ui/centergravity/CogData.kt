package de.abg.pamf.ui.centergravity

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.abg.pamf.remote.BluetoothCommunicator
import de.abg.pamf.remote.BluetoothMessage
import java.util.regex.Pattern
import kotlin.math.round

object CogData {

    /**
     * In dieser Klasse stehen nur Daten, die vom Nutzer eingestellt werden.
     * Die Daten, die vom Mikrocontroller gemessen werden stehen in TODO
     */


    private const val NAME = "COG_DATA"
    private const val TAG = "COG_DATA"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    // Namen der Einstellungen und Default-Werte
    private val TYPE = Pair("TYPE", 1)
    private val DISTANCE_1_2 = Pair("DISTANCE_1_2", 0)
    private val DISTANCE_FRONT = Pair("DISTANCE_FRONT", 0)
    private val DISTANCE_TARGET = Pair("DISTANCE_TARGET", 0)

    private val SCALE_1 = Pair("SCALE_1", 1000)
    private val SCALE_2 = Pair("SCALE_2", 1000)
    private val SCALE_3 = Pair("SCALE_3", 1000)



    fun init(context : Context){
        preferences = context.getSharedPreferences(NAME, MODE)

        weight_sum.addSource(weight_1){
            weight_sum.value = weight_1.value!! + weight_2.value!! + weight_3.value!!
        }
        weight_sum.addSource(weight_2){
            weight_sum.value = weight_1.value!! + weight_2.value!! + weight_3.value!!
        }
        weight_sum.addSource(weight_3){
            weight_sum.value = weight_1.value!! + weight_2.value!! + weight_3.value!!
        }

        center_of_gravity.addSource(weight_1){
            if(weight_1.value == 0 || weight_2.value == 0 || weight_3.value == 0 || distance_1_2 == 0)
                center_of_gravity.value = 0
            else
                center_of_gravity.value = ((weight_1.value!! * distance_1_2  ) / (weight_1.value!! + weight_2.value!! + weight_3.value!!)) - distance_front
        }
        center_of_gravity.addSource(weight_2){
            if(weight_1.value == 0 || weight_2.value == 0 || weight_3.value == 0 || distance_1_2 == 0)
                center_of_gravity.value = 0
            else
                center_of_gravity.value = ((weight_1.value!! * distance_1_2  ) / (weight_1.value!! + weight_2.value!! + weight_3.value!!)) - distance_front
        }
        center_of_gravity.addSource(weight_3){
            if(weight_1.value == 0 || weight_2.value == 0 || weight_3.value == 0 || distance_1_2 == 0)
                center_of_gravity.value = 0
            else
                center_of_gravity.value = ((weight_1.value!! * distance_1_2  ) / (weight_1.value!! + weight_2.value!! + weight_3.value!!)) - distance_front
        }

        center_of_gravity_diff.addSource(center_of_gravity){
            center_of_gravity_diff.value = center_of_gravity.value!! - distance_target
        }
/*
        BluetoothCommunicator.m_receiveMap.put(
            "CG#0#START#OK",
            fun() {
                Log.d(TAG, "Starte CG ")
            })
        BluetoothCommunicator.m_receiveMap.put(
            "CG#0#ZERO#OK", fun() {

            })
        BluetoothCommunicator.m_receiveMap.put(
            "CG#[123]#SET#(1|5|10)#OK", fun() {
                Log.d(TAG, "Waage gesetzt ")
            })*/
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
        set(value) {
            preferences.edit().putInt(SCALE_1.first, value).apply()
            // Änderung an Mikrocontroller senden
            BluetoothMessage(
                true,
                "CG#1#SET#" + (value / 1000),
                Pair("CG#1#SET#" + (value / 1000)+ "#OK", fun(response) : Boolean {
                    Log.e(TAG, "CG Set 1 ist beantwortet: " + response)
                    return false
                })
            )
        }

    var scale_2 : Int
        get() = preferences.getInt(SCALE_2.first, SCALE_2.second)
        set(value) {
            preferences.edit().putInt(SCALE_2.first, value).apply()
            // Änderung an Mikrocontroller senden
            BluetoothMessage(
                true,
                "CG#2#SET#" + (value / 1000),
                Pair("CG#2#SET#" + (value / 1000)+ "#OK", fun(response) : Boolean  {
                    Log.e(TAG, "CG Set 2 ist beantwortet: " + response)
                    return false
                })
            )
        }

    var scale_3 : Int
        get() = preferences.getInt(SCALE_3.first, SCALE_3.second)
        set(value) {
            preferences.edit().putInt(SCALE_3.first, value).apply()
            // Änderung an Mikrocontroller senden
            BluetoothMessage(
                true,
                "CG#3#SET#" + (value / 1000),
                Pair("CG#3#SET#" + (value / 1000)+ "#OK", fun(response) : Boolean  {
                    Log.e(TAG, "CG Set 3 ist beantwortet: " + response)
                    return false
                })
            )
        }



    // Ab hier sind die Daten, die über Bluetooth empfangen werden

    private var isRequestingWeights = false

    fun requestWeights(){
        if(isRequestingWeights)
            return
        isRequestingWeights = true
        lateinit var message : BluetoothMessage
        message = BluetoothMessage(
            false,
            "CG#0#START",
            Pair("CG#0#START#OK", fun(response) : Boolean  {
                Log.e(TAG, "Beginne mit der Messung der Gewichte")
                return true
            }),
            Pair("CG#1#([-]?\\d+.\\d)#2#([-]?\\d+.\\d)#3#([-]?\\d+.\\d)", fun(response) : Boolean  {

//                CG#1#-0.1#2#0.0#3#-0.1

                val match = Regex("CG#1#([-]?\\d+.\\d)#2#([-]?\\d+.\\d)#3#([-]?\\d+.\\d)").find(response)!!
                val (a,b,c) = match.destructured

                weight_1.postValue(round(a.toFloat()).toInt())
                weight_2.postValue(round(b.toFloat()).toInt())
                weight_3.postValue(round(c.toFloat()).toInt())


//                Log.e(TAG, "Gewichte wurden empfangen: " + response)
                return true
            }),
            timeout = 2000,
            timeoutFunction = fun() {
                Log.e(TAG, "Tmeout Gewichte")
                message!!.onDefaultTimeout()
                //Abbruch, weil zwischendurch eine andere Nachricht gesendet wurde?
                if(isRequestingWeights){
                    isRequestingWeights = false
                    requestWeights()
                }
            }
        )
    }

    fun stopRequestingWeights(){
        isRequestingWeights = false
        BluetoothMessage(
            false,
            "CG#0#END",
            Pair("CG#0#END#OK", fun(response) : Boolean  {
                Log.e(TAG, "Beende mit der Messung der Gewichte")
                return false
            })
        )
    }


    var weight_1 = MutableLiveData<Int>().apply {
        value = 0
    }
    var weight_2 = MutableLiveData<Int>().apply {
        value = 0
    }
    var weight_3 = MutableLiveData<Int>().apply {
        value = 0
    }

    var weight_sum = MediatorLiveData<Int>()


    var center_of_gravity = MediatorLiveData<Int>()
    var center_of_gravity_diff = MediatorLiveData<Int>()
}

