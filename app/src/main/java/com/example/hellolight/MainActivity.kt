package com.example.hellolight

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var toggleButton: Button
    private lateinit var lightStatusText: TextView
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var pairedDevice: BluetoothDevice
    private var isConnected: Boolean = false
    private val myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SerialPortService ID
    private var lightStatus: Boolean = false

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toggleButton = findViewById(R.id.light_button)
        lightStatusText = findViewById(R.id.light_status)

        if (!isConnected) {
            if (checkBluetoothPermissions()) {
                connectToBluetooth()
            }
        }

        toggleButton.setOnClickListener {
            if (!isConnected) {
                if (checkBluetoothPermissions()) {
                    connectToBluetooth()
                }
            } else {
                toggleLed()
            }
        }
    }

    private fun checkBluetoothPermissions(): Boolean {
        val hasConnectPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        val hasScanPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        val requiredPermissions = mutableListOf<String>()
        if (!hasConnectPermission) requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        if (!hasScanPermission) requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)

        return if (requiredPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions.toTypedArray(),
                REQUEST_BLUETOOTH_PERMISSIONS
            )
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                connectToBluetooth()
            } else {
                Log.e("Bluetooth", "Required permissions not granted")
            }
        }
    }

    private fun connectToBluetooth() {
        Thread {
            try {
                val deviceAddress = "" // Replace with your Bluetooth module's MAC address
                pairedDevice = bluetoothAdapter!!.getRemoteDevice(deviceAddress)
                bluetoothSocket = pairedDevice.createRfcommSocketToServiceRecord(myUUID)
                bluetoothAdapter.cancelDiscovery()
                bluetoothSocket.connect()
                isConnected = true
                runOnUiThread {
                    Log.d("Bluetooth", "Connected")
                }
            } catch (e: IOException) {
                runOnUiThread {
                    Log.e("Bluetooth", "Error connecting to device", e)
                }
            }
        }.start()

    }

    private fun toggleLed() {
        try {
            val outputStream = bluetoothSocket.outputStream
            if (!lightStatus) {
                outputStream.write('1'.code)
                lightStatusText.text = "Turned On"
                lightStatus = true
            } else {
                outputStream.write('0'.code)
                lightStatusText.text = "Turned Off"
                lightStatus = false
            }
        } catch (e: IOException) {
            Log.e("Bluetooth", "Error sending command", e)
        }
    }
}

/** Notes
 * Setup Udev rules -
 *      - lsusb - list USB devices connected to system. Also contains the product and vender IDs
 *      - Setting up udev rules for an Arduino Mega 2560 on a Linux system can help ensure that the
 *      device is consistently recognized and assigned the same device file every time it's plugged in.
 *      This can be especially useful if you're working with multiple USB devices.
 *      - /etc/udev/rules.d/99-arduino.rules
 *      - SUBSYSTEM=="tty", ATTRS{idVendor}=="2341", ATTRS{idProduct}=="0042", MODE="0666", GROUP="dialout", SYMLINK+="arduino_mega"
 *      - sudo udevadm control --reload (restarts the rule) 
 * Arduino Settings:
 *      - When uploading remove the RX and TX pins else there could be an interference with data transfer
 * Add user permissions in the Manifest
 * Use "hcitool scan" to get the module MAC address.
 */

/** TODO
 * - Get MAC address dynamically
 *
 */