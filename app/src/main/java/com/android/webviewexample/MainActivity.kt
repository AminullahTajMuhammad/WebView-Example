package com.android.webviewexample

import android.Manifest
import android.provider.Settings
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 1234
    private val permissions: Array<String> = arrayOf(
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CALL_PHONE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isPermissionGranted = checkAndRequestPermission()

        if(isPermissionGranted) {
            Toast.makeText(this, "isPermissionGranted is True", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkAndRequestPermission(): Boolean {
        val listPermissionNeeded = ArrayList<String>()

        for (perm in permissions) {
            if(ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(perm)
            }
        }

        if(!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                listPermissionNeeded.toArray(arrayOfNulls(listPermissionNeeded.size)),
                PERMISSION_REQUEST_CODE
            )
            return false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            val permissionResult = HashMap<String, Int>()
            var deniedCount = 0

            for(i in 0 until grantResults.size) {
                if(grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResult.put(permissions[i], grantResults[i])
                    deniedCount++
                }
            }

            if(deniedCount == 0) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {

                for(entry in permissionResult.entries) {
                    val permName = entry.key
                    val permResult = entry.value

                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                        showDialog("", "This application need some permissions to work without problem", "Yes, grantted permission",
                            DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                checkAndRequestPermission()
                            },
                            "No, Exit app", DialogInterface.OnClickListener { dialog, which ->
                                dialog.dismiss()
                                finish()
                            }, false)
                    } else {
                        finish()
                    }

//                    else {
//                        showDialog("", "You have denied some permissions. Allow permission at [Setting] -> [Permission]",
//                            "Go to setting", DialogInterface.OnClickListener { dialog, which ->
//                                dialog.dismiss()
//                                val intent = Intent()
//                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                startActivity(intent)
//                                finish()
//                            },
//                            "No, Exit app", DialogInterface.OnClickListener { dialog, which ->
//                                dialog.dismiss()
//                                finish()
//                            }, false)
//                        break
//                    }
                }
            }
        }
    }

    private fun showDialog(
        title: String,
        msg: String,
        pText: String,
        positiveOnClick: DialogInterface.OnClickListener,
        nText: String,
        negativeOnClick: DialogInterface.OnClickListener,
        isCancelAble: Boolean
    ): AlertDialog {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setCancelable(isCancelAble)
        builder.setMessage(msg)
        builder.setPositiveButton(pText, positiveOnClick)
        builder.setNegativeButton(nText, negativeOnClick)

        val alert = builder.create()
        alert.show()

        return alert

    }
}

