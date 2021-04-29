@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.example.test_bureau_library

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bureau.`interface`.*
import com.bureau.helpers.AllInstalledAppsHelper
import com.bureau.services.ASUrl
import com.bureau.services.ValidationService
import com.bureau.utils.*
import kotlinx.android.synthetic.main.activity_main.*


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

        AllInstalledAppsHelper().initAllInstalledApps(
            this,
            true,
            object : ApplicationFilterInterface {
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

        //url filter callbacks
        ASUrl.initCallbacks(object : UrlFilterInterface {
            override fun urlDetected(url: String) {
                Toast.makeText(this@MainActivity, "urlDetected", Toast.LENGTH_SHORT).show()
            }

            override fun safeUrl(url: String) {
                Toast.makeText(this@MainActivity, "safeUrl", Toast.LENGTH_SHORT).show()
            }

            override fun unSafeUrl(url: String) {
                Toast.makeText(this@MainActivity, "unSafeUrl", Toast.LENGTH_SHORT).show()
            }
        })
        switch_accessibility_service.apply {
            isChecked = isAccessibilityServiceEnabled(this@MainActivity, ASUrl::class.java)
            setOnCheckedChangeListener { button, boolean ->
                if (button.isPressed && boolean) {
                    if (!isAccessibilityServiceEnabled(this@MainActivity, ASUrl::class.java)){
                        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    }
                }
            }
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (marshMellowHelper != null) {
            marshMellowHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CALL_LOG -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // check READ_PHONE_STATE permission only when READ_CALL_LOG is granted
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.READ_PHONE_STATE
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_PHONE_STATE),
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE
                        )
                    }
                }
            }
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // check PROCESS_OUTGOING_CALLS permission only when READ_PHONE_STATE is granted
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.PROCESS_OUTGOING_CALLS
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        // We do not have this permission. Let's ask the user
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.PROCESS_OUTGOING_CALLS),
                            MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS
                        )
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