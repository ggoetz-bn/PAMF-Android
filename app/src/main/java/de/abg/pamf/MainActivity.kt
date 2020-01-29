package de.abg.pamf

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import de.abg.pamf.remote.BluetoothCommunicator

class MainActivity : AppCompatActivity() {


    lateinit var navController : NavController
    val handler : Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_cog, R.id.navigation_rudder, R.id.navigation_ewd, R.id.navigation_calibrate
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // Bluetooth
        BluetoothCommunicator.init(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == BluetoothCommunicator.REQUEST_ENABLE_BT){
            println("Bluetooth ist an")
            BluetoothCommunicator.connect()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO Bluetooth Verbindung beenden?
    }
}
