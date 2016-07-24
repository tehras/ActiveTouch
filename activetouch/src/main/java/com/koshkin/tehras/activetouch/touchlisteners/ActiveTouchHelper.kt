package com.koshkin.tehras.activetouch.touchlisteners

import android.os.Handler
import android.view.MotionEvent
import android.view.View
import com.koshkin.tehras.activetouch.fragments.ActiveTouchFragment

/**
 * Created by tehras on 7/23/16.
 *
 * Static Touch Helper
 */

private val LONG_TAP_THRESHOLD = 250L
var lastDialog: ActiveTouchFragment? = null

fun onTouch(ev: MotionEvent, v: View, a: ActiveTouchBehavior, mLongPressed: Runnable): Boolean {
    // this section will keep track of any hover views
    if (maxMovement == -1) {
        maxMovement = (v.measuredHeight.toDouble() * 0.25).toInt()
        if (maxMovement > 100 || maxMovement < 10) //have max/min
            maxMovement = 50
    }

    when (ev.action and MotionEvent.ACTION_MASK) {
        MotionEvent.ACTION_DOWN -> {
            originX = ev.rawX.toInt()
            originY = ev.rawY.toInt()

            isInside = true
            handler.postDelayed(mLongPressed, LONG_TAP_THRESHOLD)
            return true
        }
        MotionEvent.ACTION_UP -> {
            isInside = false
            handler.removeCallbacks(mLongPressed)
            a.hidePopup()
            return true
        }
        MotionEvent.ACTION_CANCEL -> {
            isInside = false
            a.hidePopup()
            if (!isShowing())
                handler.removeCallbacks(mLongPressed)
            return true
        }
        MotionEvent.ACTION_MOVE -> {
            val thisX = ev.rawX.toInt()
            val thisY = ev.rawY.toInt()

            if (Math.abs(thisX - originX) > maxMovement || Math.abs(thisY - originY) > maxMovement) {
                //reset
                reset(thisX, thisY, mLongPressed)
            }

            return true
        }
    }

    return false
}

private fun reset(x: Int, y: Int, mLongPressed: Runnable) {
    originX = x
    originY = y

    handler.removeCallbacks(mLongPressed)
    handler.postDelayed(mLongPressed, LONG_TAP_THRESHOLD)
}

private fun isShowing(): Boolean = lastDialog != null

val handler = Handler()

private var isInside: Boolean = false
private var maxMovement = -1
private var originX = 0
private var originY = 0