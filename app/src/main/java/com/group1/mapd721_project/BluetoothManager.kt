package com.group1.mapd721_project

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BluetoothManager(private val context: Context) {

    @SuppressLint("ServiceCast")
    private val bluetoothAdapter: BluetoothAdapter? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val bluetoothManager =
                context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
            bluetoothManager.adapter
        } else {
            @Suppress("DEPRECATION")
            BluetoothAdapter.getDefaultAdapter()
        }

    private val _isBluetoothEnabled = MutableStateFlow(false)
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled.asStateFlow()

    private val _showBluetoothDialog = MutableStateFlow(false)
    val showBluetoothDialog: StateFlow<Boolean> = _showBluetoothDialog.asStateFlow()

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_ON -> _isBluetoothEnabled.value = true
                    BluetoothAdapter.STATE_OFF -> _isBluetoothEnabled.value = false
                }
            }
        }
    }

    init {
        updateBluetoothState()
        context.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    fun cleanup() {
        context.unregisterReceiver(bluetoothStateReceiver)
    }

    fun updateBluetoothState() {
        _isBluetoothEnabled.value = bluetoothAdapter?.isEnabled == true
    }

    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }

    fun dismissDialog() {
        _showBluetoothDialog.value = false
    }

    private val _hasUserConsent = MutableStateFlow(false)
    val hasUserConsent: StateFlow<Boolean> = _hasUserConsent.asStateFlow()

    // Method to reset user consent on logout
    fun resetUserConsent() {
        _hasUserConsent.value = false
    }

    @SuppressLint("MissingPermission")
    fun enableBluetooth(activityResultLauncher: ActivityResultLauncher<Intent>) {
        if (bluetoothAdapter == null) {
            return
        }

        if (!hasBluetoothPermission()) {
            return
        }

        try {
            if (!bluetoothAdapter.isEnabled) {
                val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activityResultLauncher.launch(enableBTIntent)
                // Set user consent to true when they explicitly enable Bluetooth
                _hasUserConsent.value = true
            } else {
                // If Bluetooth is already enabled, still mark as user consented explicitly
                _hasUserConsent.value = true
            }
        } catch (securityException: SecurityException) {
            Toast.makeText(context, "Bluetooth permission required", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    fun disableBluetooth() {
        if (bluetoothAdapter == null) {
            return
        }

        if (!hasBluetoothPermission()) {
            Toast.makeText(context, "Bluetooth permission required", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && hasBluetoothAdminPermission()) {
                bluetoothAdapter.disable()
                Toast.makeText(context, "Turning Bluetooth off...", Toast.LENGTH_SHORT).show()
            } else {
                _showBluetoothDialog.value = true
            }
        } catch (securityException: SecurityException) {
            Toast.makeText(context, "Security permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun openBluetoothSettings() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        _showBluetoothDialog.value = false
    }

    fun hasBluetoothPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.BLUETOOTH_ADMIN
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasBluetoothAdminPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.BLUETOOTH_ADMIN
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }
    }
}