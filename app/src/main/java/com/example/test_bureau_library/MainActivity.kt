package com.example.test_bureau_library

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bureau.`interface`.ApplicationFilterInterface
import com.bureau.`interface`.CallFilterInterface
import com.bureau.`interface`.SIMFilterInterface
import com.bureau.`interface`.SMSFilterInterface
import com.bureau.helpers.AllInstalledAppsHelper
import com.bureau.models.packageDetectorHelper.AllInstalledAppResponse
import com.bureau.services.ValidationService
import com.bureau.utils.*

class MainActivity : AppCompatActivity() {

    private var marshMellowHelper: MarshMellowHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Firstly, we check READ_CALL_LOG permission
        if (ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.READ_CALL_LOG
                ) !== PackageManager.PERMISSION_GRANTED
        ) {
            // We do not have this permission. Let's ask the user
            ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.READ_CALL_LOG),
                    MY_PERMISSIONS_REQUEST_READ_CALL_LOG
            )
        }
        initRequestPermission()
        ValidationService.init(this, "12345", object : CallFilterInterface {
            override fun existInContact(number: String?) {

            }

            override fun spam() {
            }

            override fun aggravated() {
            }

            override fun warning() {
            }

            override fun validNumber(number: String?) {
            }
        }, object : SMSFilterInterface {
            override fun existInContact(number: String?) {

            }

            override fun spam() {

            }

            override fun aggravated() {

            }

            override fun warning() {

            }

            override fun validNumber(number: String?) {

            }

        }, object : ApplicationFilterInterface {
            override fun maliciousApps(list: ArrayList<String>) {
                val commaSeparatedString = list.joinToString(separator = ", ")
                Toast.makeText(
                        this@MainActivity,
                        "Malicious Apps --> $commaSeparatedString ",
                        Toast.LENGTH_LONG
                ).show()
            }

            override fun safeApp(appName: String) {

            }
        }, object : SIMFilterInterface {
            override fun onSimChanged() {

            }

        })

        AllInstalledAppsHelper().initAllInstalledApps(this, true, object : ApplicationFilterInterface{
            override fun maliciousApps(list: ArrayList<String>) {
                Toast.makeText(
                        this@MainActivity,
                        "Malicious Apps Activity --> $list ",
                        Toast.LENGTH_LONG
                ).show()
            }

            override fun safeApp(appName: String) {
                Toast.makeText(
                        this@MainActivity,
                        "safeApp Apps Activity --> $appName ",
                        Toast.LENGTH_LONG
                ).show()
            }
        })


    }

    private fun initRequestPermission() {
        marshMellowHelper = MarshMellowHelper(this, phoneCallPermission, PERMISSIONS_REQUEST_CODE)
        marshMellowHelper!!.request(object : MarshMellowHelper.PermissionCallback {
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(permissionDeniedError: String) {

            }

            override fun onPermissionDeniedBySystem(permissionDeniedBySystem: String) {

            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (marshMellowHelper != null) {
            marshMellowHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CALL_LOG -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // check READ_PHONE_STATE permission only when READ_CALL_LOG is granted
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_STATE) !== PackageManager.PERMISSION_GRANTED) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_PHONE_STATE), MY_PERMISSIONS_REQUEST_READ_PHONE_STATE)
                    }
                }
            }
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // check PROCESS_OUTGOING_CALLS permission only when READ_PHONE_STATE is granted
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.PROCESS_OUTGOING_CALLS) !== PackageManager.PERMISSION_GRANTED) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS), MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS)
                    }
                }
            }
            MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                }
            }
        }
    }
}