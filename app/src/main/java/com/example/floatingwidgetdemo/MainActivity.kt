package com.example.floatingwidgetdemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() , View.OnClickListener {
    private val SYSTEM_ALERT_WINDOW_PERMISSION = 2084

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            askPermission();
        }

        btn.setOnClickListener(this)

    }

    private fun askPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION)
    }

    override fun onClick(v:View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            startService(Intent(this@MainActivity, FloatingService::class.java))
            finish()
        }
        else if (Settings.canDrawOverlays(this))
        {
            startService(Intent(this@MainActivity, FloatingService::class.java))
            finish()
        }
        else
        {
            askPermission()
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            startService(Intent(this, FloatingService::class.java))
            finish()
        }
    }


}
