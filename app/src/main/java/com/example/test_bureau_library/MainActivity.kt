@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.example.test_bureau_library

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bureau.`interface`.*
import com.bureau.helpers.AllInstalledAppsHelper
import com.bureau.services.*
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
        CallFilteringService.initCallFilteringService(this, "12345","test@abc.com", object : CallFilterInterface {
            override fun warning(number: String, reason: String) {
            }

        })

        SmsFilteringService.initSmsFilterService(this,"12345",object : SMSFilterInterface {
            override fun warning(number: String, textBody: String, reason: String) {
            }
        })

        AppFilteringService.initAppFilteringService(this, object : ApplicationFilterInterface {
            override fun maliciousAppWarning(packageName: String, reason: String) {
            }

        })

        AllInstalledAppsHelper().initAllInstalledApps(
            this,
            true,
            object : ApplicationFilterInterface {
                override fun maliciousAppWarning(packageName: String, reason: String) {
                }

            })

        //url filter callbacks
        ASUrl.initCallbacks(object : UrlFilterInterface {
            override fun unSafeUrlWarning(url: String, reason: String) {
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