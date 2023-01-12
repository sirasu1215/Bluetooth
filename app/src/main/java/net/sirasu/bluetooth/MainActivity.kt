package net.sirasu.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import pub.devrel.easypermissions.EasyPermissions


//esaypermissionで許可できなかった時、できた時の処理を分けれるようにするためにEasyPermissions
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    //bluetoothManager,AdapterはOn createの中に書かないといけないが、On create以外でも使えるようにnullで宣言しておく
    //?をつけるのはnullを許可するため
    //:のあとは型の宣言
    var bluetoothManager: BluetoothManager? = null
    var bluetoothAdapter: BluetoothAdapter? = null

    //SuppressLintをつけることで、バージョンが対応してない可能性があるというエラーをなくすことができる
    //これをつけることでエラーは減るが、エラーが特定できない原因にもなる
    @SuppressLint("InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //bluetoothManagerとbluetoothAdapterを取得、代入。
        //bluetoothManager?にしている理由はbluetoothManagerがnullになる可能性があるからそれを許可するため
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.getAdapter()


        //許可をしたいpermissionを配列にする。
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH
        )

        //許可したいpermissionを許可できるように
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            EasyPermissions.requestPermissions(this, "パーミッションに関する説明", 1, *permissions)
            return
        }

        //許可をするかを聞くif文は許可されている時はという処理
        findViewById<Button>(R.id.bluetooth_on_button).setOnClickListener {
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(enableBtIntent, 2)
                }
            }
        }


        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
        }

        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        val deviceName = device?.name
                        val deviceHardwareAddress = device?.address // MAC address
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }



    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }
}