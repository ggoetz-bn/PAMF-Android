package de.abg.pamf.remote

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.regex.Pattern
import kotlin.collections.HashMap
import kotlin.collections.Map.Entry

object BluetoothCommunicator {

    const val REQUEST_ENABLE_BT : Int = 1
    const val NAME = "PAMF_APP"
    const val MICROCONTROLLER_BT_NAME = "Air"
    var MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb") //.randomUUID()

    const val TAG = "PAMF BluetoothConnectio"


    lateinit var m_activity : Activity
    lateinit var m_bluetoothAdapter: BluetoothAdapter

    private val m_sendQueue = ConcurrentLinkedQueue<BluetoothMessage>()
    private val m_sentList = LinkedList<BluetoothMessage>()
    private var m_isConnected = false
    private var blocker : BluetoothMessage? = null
    private var m_responseString : String = ""
//    val m_receiveMap = HashMap<String, (() -> Unit)>()


    fun init(activity: Activity){
        m_activity = activity


        // Bluetooth prüfen
        val ba : BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
//        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (ba != null) {
            m_bluetoothAdapter = ba
            if (!m_bluetoothAdapter.isEnabled()) {
/*                Toast.makeText(m_activity, "Bitte Bluetooth aktivieren", Toast.LENGTH_LONG)
                    .show()*/
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activity.startActivityForResult(enableBtIntent, BluetoothCommunicator.REQUEST_ENABLE_BT)
            }
            else {
                // TODO Listener setzen der auf Bluetooth Aktivierung wartet und bei aktivem Bluetooth die Funktion connect() aufruft
                connect()
            }
        } else {
            // Das Gerät unterstützt kein Bluetooth
            Toast.makeText(m_activity, "Ihr Gerät unterstützt kein Bluetooth", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun connect(){
        if(m_isConnected)
            return
        val pairedDevices: Set<BluetoothDevice>? = m_bluetoothAdapter.bondedDevices

        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address

            MY_UUID = device.uuids[0].uuid
            // Alle UUids ausgeben
            device.uuids.forEach {
                Log.d(TAG, "UUID: " + it)
            }
            Log.d(TAG, "connected: " + deviceName + " addr: " + deviceHardwareAddress)

            // TODO Was tun, wenn der Microcontroller nicht "Bonded" ist?
            if(deviceName == MICROCONTROLLER_BT_NAME) {
                m_isConnected = true
                val ct = BtcClientThread(device, MY_UUID)
                createAndStartThread(ct)
            }
        }
    }

    fun sendMessage(message : String){
        // Erzeugt eine einfache BluetoothMessage. Diese ruft von sich aus
        val btm = BluetoothMessage(false, message)
    }

    fun sendMessage(message : BluetoothMessage){
        m_sendQueue.add(message)

    }


    private fun createAndStartThread(t: BtcClientThread): Thread? {
        val workerThread: Thread = object : Thread() {
            var keepRunning = true
            override fun run() {
                try {
                    Looper.prepare()
                    t.start()
                    t.join()
                    val socket: BluetoothSocket? = t.socket
                    if (socket != null) {
                        // Output Stream vorbereiten
                        var _os: OutputStream? = null
                        try {
                            _os = socket.outputStream
                        } catch (e: IOException) {
                            Log.e(TAG, null, e)
                        }
                        val os: OutputStream = _os!!

                        //  Test Nachricht
//                        send(os, "1" + "\r")

                        val inputS = socket.inputStream
                        while (keepRunning) {
                            // Senden
                            // Nur senden, wenn es keinen Blocker gibt
                            if(blocker == null) {
                                val sendMsg = m_sendQueue.poll()
                                if (sendMsg != null) {
                                    send(os, sendMsg)
                                }
                            }

                            // Empfangen
                            val txt: String? = receive(inputS)
                            if (txt != null && txt.trim() != "") {
                                Log.e(TAG, "Empfangen: " + txt.trim())
                                m_responseString += txt.trim()
                                // Teilen
                                val split_str = m_responseString.split(";")
                                // Prüfen ob das letzte Teil leer ist (das beduetet, die Nachricht endet mit einem Semikolon)
                                if(split_str[split_str.size-1] == ""){
                                    split_str.forEach{
                                        if(it != ""){
//                                            Log.e(TAG, "Parse all: " + it)
                                            parseMessage(it.trim())
                                        }
                                    }
                                    m_responseString = ""
                                } else {
                                    // Alle bis auf das letzte parsen
                                    split_str.subList(0, split_str.size-1).forEach{
//                                        Log.e(TAG, "Parse only: " + it)
                                        parseMessage(it.trim())
                                    }
                                    // Die Nachricht ohne endendes Semicolon als Start vor den nächsten Empfang setzen
//                                    Log.e(TAG, "append: " + split_str[split_str.size-1])
                                    m_responseString = split_str[split_str.size-1]
                                }

                            }
                        }
                    }
                } catch (e: InterruptedException) {
                    Log.e(TAG, null, e)
                    keepRunning = false
                } catch (e: IOException) {
                    Log.e(TAG, null, e)
                    keepRunning = false
                } finally {
                    Log.d(TAG, "calling cancel() of " + t.getName())
                    t.cancel()
                }
            }
        }
        workerThread.start()
        return workerThread
    }

    private fun send(os: OutputStream, message: BluetoothMessage) {
        try {
            // Response-Listener registrieren
//            m_receiveMap.putAll(message.response)
            if(message.response != null)
                m_sentList.add(message)
            // Nachricht senden
            os.write((message.message + ';').toByteArray())
            Log.e(TAG, "Send: " + message.message + " (" + message.message.toByteArray() + ")")
            // Status der Nachricht auf gesendet setzen
            message.isSent()
            if(message.blocking)
                blocker = message
        } catch (e: IOException) {
            Log.e(TAG, "error while sending", e)
        }
    }

    private fun receive(inputStream: InputStream): String? {
        try {
            val num = inputStream.available()
            if (num > 0) {
                val buffer = ByteArray(num)
                val read = inputStream.read(buffer)
                if (read != -1) {
                    return String(buffer, 0, read)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "receive()", e)
        }
        return null
    }

    fun parseMessage(msg: String){
        if(m_sentList.find { btMessage ->
            var found = false;
            btMessage.response.forEach {
                // String-Gleichheit oder Übereinstimmung mit regulärem Ausdruck
                if(it.first == msg || Pattern.matches(it.first, msg)){
                    btMessage.onResponse(msg, it.second)
                    found = true
                }
            }
            found
        } == null){
            Log.e(TAG, "Kein passender Verarbeiter für Nachricht \"" + msg + "\"")
        }


/*
        val parser : (() -> Unit)? = m_receiveMap[msg]
        if(parser != null){
            parser()
        } else {
            var match = false
            // RegEx Prüfung
            m_receiveMap.forEach{
                if(Pattern.matches(it.key, msg)) {
                    Log.d(TAG, "Match " + it.key)
                    it.value()
                    match = true
                }
            }
            if(match == false)
            {
                Log.d(TAG, "Kein passender Verarbeiter für Nachricht \"" + msg + "\"")
            }
        }*/
    }

    fun unregisterMessageListener(bluetoothMessage : BluetoothMessage){
        m_sentList.remove(bluetoothMessage)
        if(bluetoothMessage == blocker)
            blocker = null
    }

}

