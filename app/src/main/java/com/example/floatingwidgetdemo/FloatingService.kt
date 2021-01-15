package com.example.floatingwidgetdemo

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import kotlin.math.roundToInt

class FloatingService : Service() {

    /**Solution for handle layout flag because that devices whom Build version is
     * greater then Oreo that don't support WindowManager.LayoutParams.TYPE_PHONE
     * in that case we use WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY*/

    var LAYOUT_FLAG: Int = 0

    lateinit var floatingView: View
    lateinit var manager: WindowManager
    lateinit var params: WindowManager.LayoutParams
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        this.params = params
        //Specify the view position
        params.gravity =
            Gravity.TOP or Gravity.LEFT //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        floatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)
        manager.addView(floatingView, params)
        floatingView?.setOnTouchListener(object :
            View.OnTouchListener {
            var initialX: Int? = null
            var initialY: Int? = null
            var initialTouchX: Float? = null
            var initialTouchY: Float? = null

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
                when (motionEvent!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y

                        //get the touch location
                        initialTouchX = motionEvent!!.getRawX()
                        initialTouchY = motionEvent!!.getRawY()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val Xdiff = (motionEvent.getRawX() - initialTouchX!!)
                        val Ydiff = (motionEvent.getRawY() - initialTouchY!!)
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX!!.plus((motionEvent.getRawX() - initialTouchX!!)).roundToInt()
                        params.y = initialY!!.plus((motionEvent.getRawY() - initialTouchY!!).roundToInt())
                        manager.updateViewLayout(floatingView, params)
                        return true

                    }

                }
                return false
            }
        })
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.removeView(floatingView)
    }
}